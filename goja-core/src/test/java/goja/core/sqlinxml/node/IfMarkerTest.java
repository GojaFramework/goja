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
public class IfMarkerTest {

  @Test
  public void testParam() throws Exception {
    IfMarker ifMarker =
        new IfMarker("select * from where 1=1 -- #_if (abc==1) and abc=? -- #_endif");
    Assert.assertEquals("and abc=?", ifMarker.expressionSqlFragments.get(0).getValue());
  }

  @Test
  public void testParamMore() throws Exception {
    IfMarker ifMarker =
        new IfMarker("select * from where 1=1 --   #_if  ( abc == 1 ) and abc=? -- #_endif");
    Assert.assertEquals("and abc=?", ifMarker.expressionSqlFragments.get(0).getValue());
  }
}