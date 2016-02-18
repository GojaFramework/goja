package goja.core.sqlinxml;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import goja.core.StringPool;
import goja.core.sqlinxml.node.SqlNode;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public class SqlParser {

  private static final Logger logger = LoggerFactory.getLogger(SqlParser.class);

  public static List<Pair<String, SqlNode>> parseContent(String content, String filePath) {
    List<Pair<String, SqlNode>> sqlList = Lists.newArrayList();

    Set<String> sqlIdList = Sets.newHashSet();

    // 采用Dom4j来使用
    SAXReader reader = new SAXReader();
    try {
      Document document = reader.read(new ByteArrayInputStream(content.getBytes("UTF-8")));
      final Element root = document.getRootElement();
      String sqlGroupName = root.attributeValue("name");
      if (Strings.isNullOrEmpty(sqlGroupName)) {
        logger.error("sql配置文件[{}]中的SqlGroup name为空,自动忽略该文件的配置!", filePath);
        return sqlList;
      }
      final List<Element> elements = root.elements();

      for (Element sql : elements) {
        String sqlId = sql.attributeValue("id");
        final String sqlMapName = sqlGroupName + StringPool.DOT + sqlId;
        if (sqlIdList.contains(sqlMapName)) {
          logger.warn("sql配置文件[{}]中,已经存在[{}]的sql ID,请检查重复!", filePath, sqlId);
          continue;
        }
        //final Node whereNode = sql.selectSingleNode("where");
        //final List<Node> conditions = sql.selectNodes("where/condition");
        SqlBuilder sqlBuilder = new SqlBuilder();
        final String sqlNodeText = sql.getText();
        if (Strings.isNullOrEmpty(sqlNodeText)) {
          logger.warn("sql配置文件[{}]中,sql配置[{}]内容为空!", filePath, sqlId);
          continue;
        }
        String sqlSelect = cleanSqlText(sqlNodeText);
        if (Strings.isNullOrEmpty(sqlSelect)) {
          logger.warn("sql配置文件[{}]中,sql配置[{}]内容为空!", filePath, sqlId);
          continue;
        }
        String selectSql = StringUtils.substringBefore(sqlSelect, SqlNode.WHERE_MARKER);
        String whereSql = StringUtils.substringAfter(sqlSelect, SqlNode.WHERE_MARKER);

        final boolean hasCondition =
            StringUtils.containsIgnoreCase(sqlSelect, SqlNode.CONDITION_MARKER);

        if (hasCondition) {
          sqlBuilder.setConditionSql(
              StringUtils.substringAfter(whereSql, SqlNode.CONDITION_MARKER));

          whereSql = StringUtils.replace(whereSql, SqlNode.CONDITION_MARKER, StringPool.EMPTY);
          //  for (Node condition : conditions) {
          //    sqlBuilder.setConditionSql(cleanSqlText(condition.getText()));
          //  }
        }

        final boolean hasWhere = StringUtils.containsIgnoreCase(sqlSelect, SqlNode.WHERE_MARKER);
        sqlBuilder.setCondition(hasCondition)
            .setSelectSql(selectSql)
            .setWhere(hasWhere);

        if (hasWhere) {
          sqlBuilder.setWhereSql(whereSql);
          //.setWhereNode(WhereNode.create(whereNode));
        }
        //List<ConditionNode> conditionNodes = Lists.newArrayList();



        final SqlNode sqlNodeOO = sqlBuilder.createSql();
        //sqlNodeOO.addCondition(conditionNodes);
        sqlIdList.add(sqlMapName);
        Pair<String, SqlNode> sqlMap = Pair.of(sqlMapName, sqlNodeOO);
        sqlList.add(sqlMap);
      }
    } catch (DocumentException e) {
      logger.error("sql配置文件解析错误,请检查配置文件! 文件地址:{}", filePath, e);
    } catch (UnsupportedEncodingException e) {
      logger.error("sql配置文件解析错误,请检查配置文件! 文件地址:{}", filePath, e);
    } finally {
      sqlIdList.clear();
      sqlIdList = null;
    }
    return sqlList;
  }

  public static List<Pair<String, SqlNode>> parseInJar(String jarResource) {
    try {
      String xmlContent =
          Resources.toString(Resources.getResource(jarResource), Charsets.UTF_8);

      return parseContent(xmlContent, jarResource);
    } catch (IOException e) {
      throw new RuntimeException("读取Jar文件错误!", e);
    }
  }

  public static List<Pair<String, SqlNode>> parseFile(File xmlFile) {

    Preconditions.checkNotNull(xmlFile, "xml sql配置文件不能为空!");
    try {
      return parseContent(Files.toString(xmlFile, Charsets.UTF_8), xmlFile.getAbsolutePath());
    } catch (IOException e) {
      throw new RuntimeException("读取文件错误!", e);
    }
  }

  public static String cleanSqlText(String sqlText) {
    if (Strings.isNullOrEmpty(sqlText)) {
      return StringPool.EMPTY;
    }
    return sqlText.replace('\r', ' ').replace('\n', ' ').replaceAll(" {2,}", " ");
  }
}
