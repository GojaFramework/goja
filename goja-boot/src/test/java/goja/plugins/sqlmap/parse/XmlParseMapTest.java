package goja.plugins.sqlmap.parse;

import goja.plugins.sqlmap.SqlMapParams;
import goja.plugins.sqlmap.nodes.SqlMapNode;
import goja.plugins.sqlmap.nodes.SqlNode;
import goja.plugins.sqlmap.nodes.XmlParseMap;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class XmlParseMapTest {

    @Test
    public void testParse() throws Exception {

        final XmlParseMap sqlMap = new XmlParseMap(new File("/Users/sog/iDo/iMe/GojaFramework/goja/goja-boot/src/test/resources/sqlmaps/new-dbmap.xml"));
        final SqlMapNode sqlMapNode = sqlMap.toNode();
        System.out.println("sqlMapNode = " + sqlMapNode);
        final List<SqlNode> sqlNodes = sqlMapNode.getSqlNodes();
        SqlMapParams sqlParams = new SqlMapParams();
        sqlParams.put("a", 2);
        sqlParams.put("b", 2);
        sqlParams.put("c", 2);
        for (SqlNode sqlNode : sqlNodes) {
            System.out.println("sqlNode = " + sqlNode.sql(sqlParams));
        }

    }
}
