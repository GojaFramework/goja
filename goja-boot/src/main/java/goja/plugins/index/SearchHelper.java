/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.plugins.index;

import com.google.common.collect.Lists;
import goja.core.StringPool;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p> 搜索工具类. </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-08-24 0:25
 * @since JDK 1.6
 */
@SuppressWarnings("UnusedDeclaration")
public class SearchHelper {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(IndexHolder.class);

    private static final IKAnalyzer ANALYZER;

    private final static BooleanQuery nullQuery = new BooleanQuery();

    private final static Formatter highlighter_formatter = new SimpleHTMLFormatter("<span class=\"highlight\">", "</span>");

    public final static String FN_ID        = "___id";
    public final static String FN_CLASSNAME = "___class";

    static {
        ANALYZER = IndexHolder.getInstance().getAnalyzer();
    }

    /**
     * 重整搜索关键短语
     *
     * @param key 关键词
     * @return 关键短语
     */
    public static String cleanupKey(String key) {
        if (DefaultConfig.getInstance().getExtDictionarys().contains(key.trim().toLowerCase()))
            return key;
        StringBuilder sb = new StringBuilder();
        List<String> keys = splitKeywords(key);
        for (String word : keys) {
            if (sb.length() > 0)
                sb.append(' ');
            sb.append(word);
        }

        return sb.toString();
    }

    /**
     * 生成查询条件
     *
     * @param field field
     * @param q     q
     * @param boost boost
     * @return query
     */
    public static Query makeQuery(String field, String q, float boost) {
        if (StringUtils.isBlank(q) || StringUtils.isBlank(field))
            return nullQuery;
        final QueryParser parser = new QueryParser(Version.LUCENE_4_9, field, ANALYZER);
        parser.setDefaultOperator(QueryParser.AND_OPERATOR);
        try {
            Query querySinger = parser.parse(q);
            querySinger.setBoost(boost);
            return querySinger;
        } catch (Exception e) {
            TermQuery queryTerm = new TermQuery(new Term(field, q));
            queryTerm.setBoost(boost);
            return queryTerm;
        }
    }


    /**
     * 关键字切分
     *
     * @param sentence 要分词的句子
     * @return 返回分词结果
     */
    public static List<String> splitKeywords(String sentence) {
        List<String> keys = Lists.newArrayList();

        if (StringUtils.isNotBlank(sentence)) {
            StringReader reader = new StringReader(sentence);
            IKSegmenter ikseg = new IKSegmenter(reader, true);
            try {
                do {
                    final Lexeme me = ikseg.next();
                    if (me == null)
                        break;
                    String term = me.getLexemeText();
                    if (DefaultConfig.getInstance().getExtStopWordDictionarys().contains(term.toLowerCase()))
                        continue;
                    keys.add(term);
                } while (true);
            } catch (IOException e) {
                logger.error("Unable to split keywords", e);
            }
        }

        return keys;
    }

    /**
     * 对一段文本执行语法高亮处理
     *
     * @param text 要处理高亮的文本
     * @param key  高亮的关键字
     * @return 返回格式化后的HTML文本
     */
    public static String highlight(String text, String key) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(text))
            return text;
        String result = null;
        try {
            final PhraseQuery pquery = new PhraseQuery();
            for (String sk : splitKeywords(key)) {
                pquery.add(new Term(StringPool.EMPTY, QueryParser.escape(sk)));
            }
            final QueryScorer scorer = new QueryScorer(pquery);
            final Highlighter hig = new Highlighter(highlighter_formatter, scorer);
            final TokenStream tokens = ANALYZER.tokenStream(null, new StringReader(text));
            result = hig.getBestFragment(tokens, text);
        } catch (Exception e) {
            logger.error("Unabled to hightlight text", e);
        }
        return (result != null) ? result : text;
    }

    /**
     * 返回文档中保存的对象 id
     *
     * @param doc Document
     * @return 保存的对象 id
     */
    public static long docid(Document doc) {
        return NumberUtils.toLong(doc.get(FN_ID), 0);
    }

    /**
     * 获取文档对应的对象类
     *
     * @param doc Document
     * @return Searchable
     */
    public static Searchable doc2obj(Document doc) {
        try {
            long id = docid(doc);
            if (id <= 0)
                return null;
            Searchable obj = (Searchable) Class.forName(doc.get(FN_CLASSNAME)).newInstance();
            obj.setId(id);
            return obj;
        } catch (Exception e) {
            logger.error("Unabled generate object from document#id=" + doc.toString(), e);
            return null;
        }
    }

    /**
     * 将对象转成 Lucene 的文档
     *
     * @param obj Java 对象
     * @return 返回Lucene文档
     */
    public static Document obj2doc(Searchable obj) {
        if (obj == null)
            return null;

        Document doc = new Document();
        doc.add(new LongField(FN_ID, obj.id(), Field.Store.YES));
        doc.add(new StoredField(FN_CLASSNAME, obj.getClass().getName()));

        //存储字段
        List<String> fields = obj.storeFields();
        if (fields != null)
            for (String fn : fields) {
                Object fv = readField(obj, fn);
                if (fv != null)
                    doc.add(obj2field(fn, fv, true));
            }

        //扩展存储字段
        Map<String, String> eDatas = obj.extendStoreDatas();
        if (eDatas != null)
            for (String fn : eDatas.keySet()) {
                if (fields != null && fields.contains(fn))
                    continue;
                String fv = eDatas.get(fn);
                if (fv != null)
                    doc.add(obj2field(fn, fv, true));
            }

        //索引字段
        fields = obj.indexFields();
        if (fields != null)
            for (String fn : fields) {
                String fv = (String) readField(obj, fn);
                if (fv != null) {
                    TextField tf = new TextField(fn, fv, Field.Store.NO);
                    tf.setBoost(obj.boost());
                    doc.add(tf);
                }
            }

        //扩展索引字段
        eDatas = obj.extendIndexDatas();
        if (eDatas != null)
            for (String fn : eDatas.keySet()) {
                if (fields != null && fields.contains(fn))
                    continue;
                String fv = eDatas.get(fn);
                if (fv != null) {
                    TextField tf = new TextField(fn, fv, Field.Store.NO);
                    tf.setBoost(obj.boost());
                    doc.add(tf);
                }
            }

        return doc;
    }

    /**
     * 访问对象某个属性的值
     *
     * @param obj   对象
     * @param field 属性名
     * @return Lucene 文档字段
     */
    private static Object readField(Object obj, String field) {
        try {
            return PropertyUtils.getProperty(obj, field);
        } catch (Exception e) {
            logger.error("Unabled to get property '" + field + "' of " + obj.getClass().getName(), e);
            return null;
        }

    }

    private static Field obj2field(String field, Object fieldValue, boolean store) {
        if (fieldValue == null)
            return null;

        if (fieldValue instanceof Date) //日期
            return new LongField(field, ((Date) fieldValue).getTime(), store ? Field.Store.YES : Field.Store.NO);
        if (fieldValue instanceof Number) //其他数值
            return new StringField(field, String.valueOf(((Number) fieldValue).longValue()), store ? Field.Store.YES : Field.Store.NO);
        //其他默认当字符串处理
        return new StringField(field, (String) fieldValue, store ? Field.Store.YES : Field.Store.NO);
    }
}
