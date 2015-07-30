package goja.plugins.sqlmap.nodes;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import goja.StringPool;
import goja.lang.Lang;
import goja.plugins.sqlmap.exceptions.SqlMapException;
import goja.plugins.sqlmap.nodes.eval.IfEvalSqlNode;
import goja.plugins.sqlmap.nodes.eval.TrimEvalSqlNode;
import goja.plugins.sqlmap.nodes.eval.WhenEvalSqlNode;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * <p>XML解析处理 </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class XmlParseMap {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(XmlParseMap.class);
    public static final String TEST_XML_ATTR = "test";


    private final File sqlMapFile;

    public XmlParseMap(File sqlMapFile) {
        Preconditions.checkNotNull(sqlMapFile, "the sql map file has must not null!");
        Preconditions.checkArgument(sqlMapFile.exists(), "the sql map file has not exist!");

        if (!StringUtils.equalsIgnoreCase(Files.getFileExtension(sqlMapFile.getAbsolutePath()), "xml")) {
            throw new SqlMapException("The SqlMap file must xml file.");
        }

        this.sqlMapFile = sqlMapFile;
    }

    public SqlMapNode toNode() {
        try {
            String file_contents = Files.toString(this.sqlMapFile, Charsets.UTF_8);

            Document file_document = DocumentHelper.parseText(file_contents);

            final Element rootElement = file_document.getRootElement();

            final String modelName = rootElement.attributeValue("model");
            if (logger.isDebugEnabled()) {
                logger.debug("The sql mapping start init model with {}.", modelName);
            }
            final String packageName = rootElement.attributeValue("package");
            SqlMapNode sqlMapNode = new SqlMapNode(modelName, packageName);

            final List<Element> sqlElements = rootElement.elements("sql");

            SqlNode sqlNode;
            for (Element sqlElement : sqlElements) {
                final String id = sqlElement.attributeValue(StringPool.PK_COLUMN);
                if (Strings.isNullOrEmpty(id)) {
                    logger.warn("The sql map model {} id is empty!", modelName);
                    continue;
                }
                final String method = sqlElement.attributeValue("method");
                String sql = sqlElement.getTextTrim();
                sqlNode = new SqlNode(id, method, sql);
                sqlMapNode.addSqlNode(sqlNode);
                readSqlElement(sqlElement, sqlNode);

            }


            return sqlMapNode;

        } catch (IOException e) {
            throw new SqlMapException("The sql map file has read error!", e);
        } catch (DocumentException e) {
            throw new SqlMapException("The sql map file parse xml node has error!", e);
        }

    }

    /**
     * read parse sql element to sql node.
     *
     * @param sqlElement sql element xml.
     * @param sqlNode    sql node.
     */
    private void readSqlElement(Element sqlElement, final SqlNode sqlNode) {
        final List<Element> iflist = sqlElement.elements(IfEvalSqlNode.IF);
        final List<Element> chooselist = sqlElement.elements(ChooseSqlNode.CHOOSE);
        final List<Element> trimlist = sqlElement.elements(TrimEvalSqlNode.TRIM);


        if (!Lang.isEmpty(iflist)) {

            for (Element if_ele : iflist) {

                IfEvalSqlNode ifEvalSqlNode = parseIfNode(if_ele);
                if (ifEvalSqlNode == null) {
                    logger.warn("The if test is null!");
                    continue;
                }
                sqlNode.addEvalSqlNode(ifEvalSqlNode);
            }
        }
        if (!Lang.isEmpty(chooselist)) {
            for (Element choose_ele : chooselist) {
                ChooseSqlNode chooseSqlNode = parseChooseNode(choose_ele);
                if (chooseSqlNode == null) {
                    logger.warn("The when test is null!");
                    continue;
                }
                sqlNode.addEvalSqlNode(chooseSqlNode);
            }
        }
        if (!Lang.isEmpty(trimlist)) {
            for (Element trim_ele : trimlist) {
                parseTrimNode(sqlNode, trim_ele);
            }
        }

    }

    private void parseTrimNode(SqlNode sqlNode, Element trim_ele) {
        final String prefix = trim_ele.attributeValue(TrimEvalSqlNode.PREFIX);
        if (Strings.isNullOrEmpty(prefix)) {
            logger.warn("The prefix has error!");
            return;
        }

        final String prefixOverrides = trim_ele.attributeValue("prefixOverrides");

        TrimEvalSqlNode trimEvalSqlNode = new TrimEvalSqlNode(prefix, prefixOverrides);
        sqlNode.addEvalSqlNode(trimEvalSqlNode);

        //parse if and choosen sql node.
        final List<Element> ifeles = trim_ele.elements(IfEvalSqlNode.IF);
        final List<Element> chooseeles = trim_ele.elements(ChooseSqlNode.CHOOSE);
        for (Element chooseele : chooseeles) {
            final ChooseSqlNode chooseSqlNode = parseChooseNode(chooseele);
            if(chooseSqlNode == null){
                logger.warn("the when node test is null !");
                continue;
            }
            trimEvalSqlNode.addEvalSqlNode(chooseSqlNode);
        }
        for (Element ifele : ifeles) {
            final IfEvalSqlNode ifEvalSqlNode = parseIfNode(ifele);
            if(ifEvalSqlNode == null){
                logger.warn("the when if node test is null !");
                continue;
            }
            trimEvalSqlNode.addEvalSqlNode(ifEvalSqlNode);
        }
    }

    private ChooseSqlNode parseChooseNode(Element choose_ele) {
        final List<Element> whenElements = choose_ele.elements(WhenEvalSqlNode.WHEN);
        final Element otherwiseElement = choose_ele.element(OtherwiseSqlNode.OTHERWISE);

        WhenEvalSqlNode whenEvalSqlNode;
        final ChooseSqlNode chooseSqlNode = new ChooseSqlNode();
        for (Element whenElement : whenElements) {
            String test = whenElement.attributeValue(TEST_XML_ATTR);
            if (Strings.isNullOrEmpty(test)) {
                return null;
            }

            whenEvalSqlNode = new WhenEvalSqlNode(test, whenElement.getTextTrim());
            chooseSqlNode.addWhenEvalSqlNode(whenEvalSqlNode);
        }

        chooseSqlNode.setOtherwiseSqlNode(new OtherwiseSqlNode(otherwiseElement.getTextTrim()));
        return chooseSqlNode;
    }

    private IfEvalSqlNode parseIfNode(Element if_ele) {
        String test = if_ele.attributeValue(TEST_XML_ATTR);
        if (Strings.isNullOrEmpty(test)) {
            return null;
        }
        String ifsql = if_ele.getTextTrim();
        return new IfEvalSqlNode(test, ifsql);
    }


}
