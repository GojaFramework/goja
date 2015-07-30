package goja.mvc.render.captcha;

import com.jfinal.render.Render;
import goja.mvc.Controller;
import kaptcha.Constants;
import kaptcha.Producer;
import kaptcha.util.Config;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Properties;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class KaptchaRender extends Render {

    private static final Logger logger = LoggerFactory.getLogger(KaptchaRender.class);

    private final Producer producer;


    public KaptchaRender(KaptchaConfig kaptchaConfig) {

        Properties properties = kaptchaConfig.getProperties();
        Config config = new Config(properties);
        this.producer = config.getProducerImpl();
    }


    @Override
    public void render() {
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        // return a jpeg
        response.setContentType("image/jpeg");

        // create the text for the image
        String capText = this.producer.createText();

        // create the image with the text
        BufferedImage bi = this.producer.createImage(capText);

        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            // write the data out
            ImageIO.write(bi, "jpg", out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(out);
        }


        // fixes issue #69: set the attributes after we write the image in case the image writing fails.

        // store the text in the session
        request.getSession().setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);

        // store the date in the session so that it can be compared
        // against to make sure someone hasn't taken too long to enter
        // their kaptcha
//        req.getSession().setAttribute(Constants.KAPTCHA_SESSION_DATE, new Date());
    }

    public static boolean verify(Controller controller, String captchaCode) {
        if (StringUtils.isBlank(captchaCode))
            return false;
        try {
            String kaptchaExpected = controller.getSessionAttr(Constants.KAPTCHA_SESSION_KEY);
            return !StringUtils.isBlank(kaptchaExpected) && StringUtils.equals(captchaCode, kaptchaExpected);
        } catch (Exception e) {
            logger.error("Kaptcha verify has error!", e);
            return false;
        } finally {
            controller.removeSessionAttr(Constants.KAPTCHA_SESSION_KEY);
        }
    }
}
