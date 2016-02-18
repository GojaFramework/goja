package goja.core.sqlinxml.node;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public class SqlNodeTest {

  @Test
  public void testUseIf() throws Exception {
    Assert.assertTrue(new SqlNode("select * from where 1=1 -- #_if (abc==1) and abc=? -- #_endif", false,
        false, "", "", "").useIf);
  }
}