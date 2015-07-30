package goja.plugins.sqlmap;

import app.models.News;
import goja.plugins.sqlmap.nodes.SqlNode;
import org.junit.Before;
import org.junit.Test;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class SqlMapPluginTest {

    @Before
    public void setUp() throws Exception {
        new SqlMapPlugin().start();

    }

    @Test
    public void testGet() throws Exception {

        SqlNode findByTest = SqlMaps.sqlNode(News.class, "findByTest");
        System.out.println("findByTest = " + findByTest);

    }
}