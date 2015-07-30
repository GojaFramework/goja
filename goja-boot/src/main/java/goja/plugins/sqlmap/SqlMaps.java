package goja.plugins.sqlmap;

import com.google.common.collect.Maps;
import com.jfinal.plugin.activerecord.Model;
import goja.plugins.sqlmap.nodes.SqlNode;

import java.util.Map;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class SqlMaps {

    private SqlMaps() {
    }

    /**
     * 读取所有的配置内存管理Map
     */
    private static Map<Class<? extends Model>, Map<String, SqlNode>> sqlMapNodeMap = Maps.newHashMapWithExpectedSize(1);


    protected static void put(Class<? extends Model> cls, Map<String, SqlNode> nodeMap) {
        sqlMapNodeMap.put(cls, nodeMap);
    }

    protected static void clear() {
        sqlMapNodeMap.clear();
        sqlMapNodeMap = null;
    }


    public static SqlNode sqlNode(Class<? extends Model> mcls, String method) {
        Map<String, SqlNode> sqlNodeMap = sqlMapNodeMap.get(mcls);
        return sqlNodeMap.get(method);
    }

}
