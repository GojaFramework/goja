package goja.libs;

import org.junit.Test;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class ExpressionTest {

    @Test
    public void testName() throws Exception {
        System.out.println("Expression.evaluate(\"cron.(abc)\") = " + Expression.evaluate("0 0 12 * * ?","*.*.*1"));

    }
}