package goja.rapid.mvc.kisso;

import com.baomidou.kisso.web.WebKissoConfigurer;
import com.jfinal.plugin.IPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public class KissoJfinalPlugin implements IPlugin {

  protected static final Logger logger = LoggerFactory.getLogger("WebKissoConfigurer");


  /**

   * 初始化

   */
  public boolean start() {
    new WebKissoConfigurer("sso.properties").initKisso();
    return true;
  }


  /**

   * 销毁

   */
  public boolean stop() {
    logger.info("Uninstalling Kisso ");
    return true;
  }
}
