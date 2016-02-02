/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.rapid.email;

import goja.core.StringPool;
import goja.core.app.GojaConfig;
import goja.core.exceptions.MailException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mail utils
 */
public class EMail {

  private static final Logger logger = LoggerFactory.getLogger(EMail.class);
  public static Session session;
  public static boolean asynchronousSend = true;
  public static ExecutorService executor = Executors.newCachedThreadPool();

  private EMail() {
  }

  /**
   * Send an email
   */
  public static Future<Boolean> send(Email email) {
    try {
      email = buildMessage(email);

      if (GojaConfig.getProperty("mail.smtp", StringPool.EMPTY).equals("mock")
          && GojaConfig.getApplicationMode().isDev()) {
        Mock.send(email);
        return new Future<Boolean>() {
          @Override
          public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
          }

          @Override
          public boolean isCancelled() {
            return false;
          }

          @Override
          public boolean isDone() {
            return true;
          }

          @Override
          public Boolean get() throws InterruptedException, ExecutionException {
            return true;
          }

          @Override
          public Boolean get(long timeout, final TimeUnit unit)
              throws InterruptedException, ExecutionException, TimeoutException {
            return true;
          }
        };
      }

      email.setMailSession(getSession());
      return sendMessage(email);
    } catch (EmailException ex) {
      throw new MailException("Cannot send email", ex);
    }
  }

  /**
   *
   */
  public static Email buildMessage(Email email) throws EmailException {

    String from = GojaConfig.getProperty("mail.smtp.from");
    if (email.getFromAddress() == null && !StringUtils.isEmpty(from)) {
      email.setFrom(from);
    } else if (email.getFromAddress() == null) {
      throw new MailException("Please define a 'from' email address", new NullPointerException());
    }
    if ((email.getToAddresses() == null || email.getToAddresses().size() == 0) &&
        (email.getCcAddresses() == null || email.getCcAddresses().size() == 0) &&
        (email.getBccAddresses() == null || email.getBccAddresses().size() == 0)) {
      throw new MailException("Please define a recipient email address",
          new NullPointerException());
    }
    if (email.getSubject() == null) {
      throw new MailException("Please define a subject", new NullPointerException());
    }
    if (email.getReplyToAddresses() == null || email.getReplyToAddresses().size() == 0) {
      email.addReplyTo(email.getFromAddress().getAddress());
    }

    return email;
  }

  public static Session getSession() {
    if (session == null) {
      Properties props = new Properties();
      // Put a bogus value even if we are on dev mode, otherwise JavaMail will complain
      props.put("mail.smtp.host", GojaConfig.getProperty("mail.smtp.host", "localhost"));

      String channelEncryption;
      if (GojaConfig.containsKey("mail.smtp.protocol") && GojaConfig.getProperty(
          "mail.smtp.protocol", "smtp").equals("smtps")) {
        // Backward compatibility before stable5
        channelEncryption = "starttls";
      } else {
        channelEncryption = GojaConfig.getProperty("mail.smtp.channel", "clear");
      }

      if (channelEncryption.equals("clear")) {
        props.put("mail.smtp.port", "25");
      } else if (channelEncryption.equals("ssl")) {
        // port 465 + setup yes ssl socket factory (won't verify that the server certificate is signed with a root ca.)
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "play.utils.YesSSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
      } else if (channelEncryption.equals("starttls")) {
        // port 25 + enable starttls + ssl socket factory
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.starttls.enable", "true");
        // can't install our socket factory. will work only with server that has a signed certificate
        // story to be continued in javamail 1.4.2 : https://glassfish.dev.java.net/issues/show_bug.cgi?id=5189
      }

      if (GojaConfig.containsKey("mail.smtp.localhost")) {
        props.put("mail.smtp.localhost",
            GojaConfig.get("mail.smtp.localhost"));            //override defaults
      }
      if (GojaConfig.containsKey("mail.smtp.socketFactory.class")) {
        props.put("mail.smtp.socketFactory.class", GojaConfig.get("mail.smtp.socketFactory.class"));
      }
      if (GojaConfig.containsKey("mail.smtp.port")) {
        props.put("mail.smtp.port", GojaConfig.get("mail.smtp.port"));
      }
      String user = GojaConfig.getProperty("mail.smtp.user");
      String password = GojaConfig.getProperty("mail.smtp.pass");
      if (password == null) {
        // Fallback to old convention
        password = GojaConfig.getProperty("mail.smtp.password");
      }
      session = null;

      if (user != null && password != null) {
        props.put("mail.smtp.auth", "true");
        session = Session.getInstance(props, new SMTPAuthenticator(user, password));
      } else {
        props.remove("mail.smtp.auth");
        session = Session.getInstance(props);
      }

      if (Boolean.parseBoolean(GojaConfig.getProperty("mail.debug", "false"))) {
        session.setDebug(true);
      }
    }
    return session;
  }

  /**
   * Send a JavaMail message
   *
   * @param msg An Email message
   */
  public static Future<Boolean> sendMessage(final Email msg) {
    if (asynchronousSend) {
      return executor.submit(new Callable<Boolean>() {

        public Boolean call() {
          try {
            msg.setSentDate(new Date());
            msg.send();
            return true;
          } catch (Throwable e) {
            MailException me = new MailException("Error while sending email", e);
            logger.error("The email has not been sent", me);
            return false;
          }
        }
      });
    } else {
      final StringBuffer result = new StringBuffer();
      try {
        msg.setSentDate(new Date());
        msg.send();
      } catch (Throwable e) {
        MailException me = new MailException("Error while sending email", e);
        logger.error("The email has not been sent", me);
        result.append("oops");
      }
      return new Future<Boolean>() {

        public boolean cancel(boolean mayInterruptIfRunning) {
          return false;
        }

        public boolean isCancelled() {
          return false;
        }

        public boolean isDone() {
          return true;
        }

        public Boolean get() throws InterruptedException, ExecutionException {
          return result.length() == 0;
        }

        public Boolean get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
          return result.length() == 0;
        }
      };
    }
  }

  public static class SMTPAuthenticator extends Authenticator {

    private String user;
    private String password;

    public SMTPAuthenticator(String user, String password) {
      this.user = user;
      this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(user, password);
    }
  }

  public static class Mock {

    static Map<String, String> emails = new HashMap<String, String>();

    public static String getContent(Part message) throws MessagingException,
        IOException {

      if (message.getContent() instanceof String) {
        return message.getContentType() + ": " + message.getContent() + " \n\t";
      } else if (message.getContent() != null && message.getContent() instanceof Multipart) {
        Multipart part = (Multipart) message.getContent();
        String text = "";
        for (int i = 0; i < part.getCount(); i++) {
          BodyPart bodyPart = part.getBodyPart(i);
          if (!Message.ATTACHMENT.equals(bodyPart.getDisposition())) {
            text += getContent(bodyPart);
          } else {
            text += "attachment: \n" +
                "\t\t name: " + (StringUtils.isEmpty(bodyPart.getFileName()) ? "none"
                : bodyPart.getFileName()) + "\n" +
                "\t\t disposition: " + bodyPart.getDisposition() + "\n" +
                "\t\t description: " + (StringUtils.isEmpty(bodyPart.getDescription()) ? "none"
                : bodyPart.getDescription()) + "\n\t";
          }
        }
        return text;
      }
      if (message.getContent() != null && message.getContent() instanceof Part) {
        if (!Message.ATTACHMENT.equals(message.getDisposition())) {
          return getContent((Part) message.getContent());
        } else {
          return "attachment: \n" +
              "\t\t name: " + (StringUtils.isEmpty(message.getFileName()) ? "none"
              : message.getFileName()) + "\n" +
              "\t\t disposition: " + message.getDisposition() + "\n" +
              "\t\t description: " + (StringUtils.isEmpty(message.getDescription()) ? "none"
              : message.getDescription()) + "\n\t";
        }
      }

      return "";
    }

    static void send(Email email) {

      try {
        final StringBuilder content = new StringBuilder();
        Properties props = new Properties();
        props.put("mail.smtp.host", "myfakesmtpserver.com");

        Session session = Session.getInstance(props);
        email.setMailSession(session);

        email.buildMimeMessage();

        MimeMessage msg = email.getMimeMessage();
        msg.saveChanges();

        String body = getContent(msg);

        content.append("From Mock Mailer\n\tNew email received by");

        content.append("\n\tFrom: ").append(email.getFromAddress().getAddress());
        content.append("\n\tReplyTo: ").append(email.getReplyToAddresses().get(0).getAddress());
        content.append("\n\tTo: ");
        for (Object add : email.getToAddresses()) {
          content.append(add.toString()).append(", ");
        }
        // remove the last ,
        content.delete(content.length() - 2, content.length());
        if (email.getCcAddresses() != null && !email.getCcAddresses().isEmpty()) {
          content.append("\n\tCc: ");
          for (Object add : email.getCcAddresses()) {
            content.append(add.toString()).append(", ");
          }
          // remove the last ,
          content.delete(content.length() - 2, content.length());
        }
        if (email.getBccAddresses() != null && !email.getBccAddresses().isEmpty()) {
          content.append("\n\tBcc: ");
          for (Object add : email.getBccAddresses()) {
            content.append(add.toString()).append(", ");
          }
          // remove the last ,
          content.delete(content.length() - 2, content.length());
        }
        content.append("\n\tSubject: ").append(email.getSubject());
        content.append("\n\t").append(body);

        content.append("\n");
        logger.info(content.toString());

        for (Object add : email.getToAddresses()) {
          content.append(", ").append(add.toString());
          emails.put(((InternetAddress) add).getAddress(), content.toString());
        }
      } catch (Exception e) {
        logger.error("error sending mock email", e);
      }
    }

    public static String getLastMessageReceivedBy(String email) {
      return emails.get(email);
    }

    public static void reset() {
      emails.clear();
    }
  }
}

