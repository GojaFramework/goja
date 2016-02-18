package goja.core.libs;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public class Freemarkers {

  private static Logger log = LoggerFactory.getLogger(Freemarkers.class);

  /**
   * 渲染模板
   *
   * @throws IOException
   * @throws TemplateException
   */
  public static String render(String templateContent, Map<String, Object> paramMap) {
    StringWriter writer = new StringWriter();
    try {
      Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
      cfg.setTemplateLoader(new StringTemplateLoader(templateContent));
      cfg.setDefaultEncoding("UTF-8");

      Template template = cfg.getTemplate("");

      template.process(paramMap, writer);
    } catch (IOException e) {
      e.printStackTrace();
      log.error(e.getMessage());
    } catch (TemplateException e) {
      e.printStackTrace();
      log.error(e.getMessage());
    }
    return writer.toString();
  }



  static  class StringTemplateLoader implements TemplateLoader {

    private String template;

    public StringTemplateLoader(String template) {
      this.template = template;
      if (template == null) {
        this.template = "";
      }
    }

    public void closeTemplateSource(Object templateSource) throws IOException {
      ((StringReader) templateSource).close();
    }

    public Object findTemplateSource(String name) throws IOException {
      return new StringReader(template);
    }

    public long getLastModified(Object templateSource) {
      return 0;
    }

    public Reader getReader(Object templateSource, String encoding) throws IOException {
      return (Reader) templateSource;
    }
  }

}

