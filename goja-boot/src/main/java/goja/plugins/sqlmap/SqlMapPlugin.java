package goja.plugins.sqlmap;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;
import goja.app.GojaConfig;
import goja.StringPool;
import goja.plugins.sqlmap.nodes.SqlMapNode;
import goja.plugins.sqlmap.nodes.SqlNode;
import goja.plugins.sqlmap.nodes.XmlParseMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class SqlMapPlugin implements IPlugin {

    /**
     * 配置文件后缀名称
     */
    protected static final String CONFIG_SUFFIX = "dbmap.xml";

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(SqlMapPlugin.class);

    @Override
    public boolean start() {
        String sqlMapPath = GojaConfig.getProperty("app.sqlmaps", PathKit.getRootClassPath() + File.separator + "sqlmaps");
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(sqlMapPath));
        final List<File> sqlMapFiles = Lists.newArrayList();
        for (File f : iterable) {
            if (f.getName().endsWith(CONFIG_SUFFIX)) {
                sqlMapFiles.add(f);
            }
        }
        XmlParseMap xmlParseMap;
        SqlMapNode sqlMapNode;
        for (File sqlMapFile : sqlMapFiles) {
            xmlParseMap = new XmlParseMap(sqlMapFile);

            sqlMapNode = xmlParseMap.toNode();
            String model = sqlMapNode.getModel();
            try {
                String className = sqlMapNode.getPackageName() + StringPool.DOT + model;
                Class model_cls = Class.forName(className);
                if (model_cls == null) {
                    logger.error("The sqlmap xml model is null!");
                    continue;
                }

                List<SqlNode> sqlNodes = sqlMapNode.getSqlNodes();
                Map<String, SqlNode> sqlNodeMap = Maps.newHashMap();
                for (SqlNode sqlNode : sqlNodes) {
                    sqlNodeMap.put(sqlNode.getMethod(), sqlNode);
                }
                SqlMaps.put(model_cls, sqlNodeMap);
            } catch (ClassNotFoundException e) {
                logger.error("the model has class not found!", e);
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean stop() {
        SqlMaps.clear();
        return true;
    }

}
