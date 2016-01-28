package goja.core.sqlinxml;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */

@XmlRootElement
public class SqlCondition {

  @XmlValue
  String value;

}
