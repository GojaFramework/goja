package goja.core.sqlinxml;

import goja.core.sqlinxml.node.SqlNode;
import org.junit.Before;
import org.junit.Test;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public class SqlKitTest {
  @Before
  public void setUp() throws Exception {
    SqlKit.initWithTest();
  }

  @Test
  public void testWhere() throws Exception {
    final SqlNode sqlNode = SqlKit.sqlNode("test.test1");
    System.out.println("sqlNode = " + sqlNode);
  }
}