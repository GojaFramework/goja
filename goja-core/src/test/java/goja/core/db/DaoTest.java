package goja.core.db;

import com.google.common.collect.Maps;

import goja.core.app.GojaConfig;
import goja.core.sqlinxml.SqlInXmlPlugin;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public class DaoTest {

    @Before
    public void setUp() throws Exception {
        GojaConfig.init();
        new SqlInXmlPlugin().start();

    }

    @Test
    public void testDynamicSql() throws Exception {
        final Map<String, Object> params = Maps.newHashMap();
        params.put("ab", 1);
        final Pair<String, List<Object>> sqlPair = Dao.dynamicSql(
                "test.test1",
                params);
        System.out.println("sqlPair = " + sqlPair);
    }
}