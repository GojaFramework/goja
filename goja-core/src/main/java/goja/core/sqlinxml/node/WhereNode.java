package goja.core.sqlinxml.node;

import goja.core.sqlinxml.SqlParser;
import org.dom4j.Node;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public class WhereNode {

  public final String sql;

  private WhereNode(String sql) {
    this.sql = sql;
  }

  public static WhereNode create(Node whereNode) {
    WhereNode node = new WhereNode(SqlParser.cleanSqlText(whereNode.getText()));
    return node;
  }
}
