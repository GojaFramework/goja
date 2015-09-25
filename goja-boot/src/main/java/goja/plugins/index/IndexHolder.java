/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.plugins.index;

import goja.app.GojaConfig;
import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-08-24 0:17
 * @since JDK 1.6
 */
public class IndexHolder {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(IndexHolder.class);

    private static final IKAnalyzer ANALYZER = new IKAnalyzer();

    private final static int MAX_COUNT = 1000;

    private String indexPath;

    private IndexHolder() {
    }


    public static IndexHolder getInstance() {
        return IndexPlugin.holder;
    }


    /**
     * 构造索引库管理实例
     *
     * @param idx_path 索引库地址
     * @return 索引库管理对象
     * @throws IOException
     */
    protected static IndexHolder init(String idx_path) throws IOException {
        ANALYZER.setUseSmart(GojaConfig.getPropertyToBoolean("index.smart", true));
        IndexHolder holder = new IndexHolder();
        idx_path = FilenameUtils.normalize(idx_path);
        File file = new File(idx_path);
        if (!file.exists() || !file.isDirectory())
            throw new FileNotFoundException(idx_path);
        if (!idx_path.endsWith(File.separator))
            idx_path += File.separator;
        holder.indexPath = idx_path;

        return holder;
    }

    private IndexWriter getWriter(Class<? extends Searchable> objClass) throws IOException {
        Directory dir = FSDirectory.open(new File(indexPath + objClass.getSimpleName()));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, ANALYZER);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        return new IndexWriter(dir, config);
    }

    private IndexSearcher getSearcher(Class<? extends Searchable> objClass) throws IOException {
        Directory dir = FSDirectory.open(new File(indexPath + objClass.getSimpleName()));
        return new IndexSearcher(DirectoryReader.open(dir));
    }


    /**
     * 多个资料库的搜索
     *
     * @param objClasses 对象数据类型
     * @return 多个资料库的搜索对象
     * @throws IOException
     */
    private IndexSearcher getSearchers(List<Class<? extends Searchable>> objClasses) throws IOException {
        IndexReader[] readers = new IndexReader[objClasses.size()];
        int idx = 0;
        for (Class<? extends Searchable> objClass : objClasses) {
            FSDirectory dir = FSDirectory.open(new File(indexPath + objClass.getSimpleName()));
            readers[idx++] = DirectoryReader.open(dir);
        }
        return new IndexSearcher(new MultiReader(readers, true));
    }

    /**
     * 优化索引库
     *
     * @param objClass 对象数据
     * @throws IOException
     */
    public void optimize(Class<? extends Searchable> objClass) throws IOException {
        IndexWriter writer = getWriter(objClass);
        try {
            writer.forceMerge(1);
            writer.commit();
        } finally {
            writer.close();
            writer = null;
        }
    }

    /**
     * 多库搜索
     *
     * @param objClasses 对象数据类型
     * @param query      查询条件
     * @param filter     过滤器
     * @param sort       排序
     * @param page       分页
     * @param count      总条数
     * @return 搜索结果
     * @throws IOException
     */
    public List<Searchable> find(List<Class<? extends Searchable>> objClasses, Query query, Filter filter, Sort sort, int page, int count) throws IOException {
        IndexSearcher searcher = getSearchers(objClasses);
        return find(searcher, query, filter, sort, page, count);
    }

    /**
     * 单库搜索
     *
     * @param objClass 对象数据类型
     * @param query    查询条件
     * @param filter   过滤器
     * @param sort     排序
     * @param page     分页
     * @param count    总条数
     * @return 搜索结果
     * @throws IOException
     */
    public List<Long> find(Class<? extends Searchable> objClass, Query query, Filter filter, Sort sort, int page, int count) throws IOException {
        IndexSearcher searcher = getSearcher(objClass);
        List<Searchable> results = find(searcher, query, filter, sort, page, count);
        List<Long> ids = new ArrayList<Long>();
        for (Searchable obj : results) {
            if (obj != null)
                ids.add(obj.id());
        }
        return ids;
    }

    /**
     * 多库搜索
     *
     * @param objClasses 对象数据类型
     * @param query      查询条件
     * @param filter     过滤器
     * @return 搜索结果
     * @throws IOException
     */
    public int count(List<Class<? extends Searchable>> objClasses, Query query, Filter filter) throws IOException {
        IndexSearcher searcher = getSearchers(objClasses);
        return count(searcher, query, filter);
    }

    /**
     * 搜索
     *
     * @param objClass 对象数据类型
     * @param query    查询条件
     * @param filter   过滤器
     * @return 搜索结果
     * @throws IOException
     */
    public int count(Class<? extends Searchable> objClass, Query query, Filter filter) throws IOException {
        IndexSearcher searcher = getSearcher(objClass);
        return count(searcher, query, filter);
    }

    /**
     * 搜索
     *
     * @param searcher 搜索条件
     * @param query    查询条件
     * @param filter   过滤器
     * @param sort     排序
     * @param page     分页
     * @param count    总条数
     * @return 搜索结果
     * @throws IOException
     */
    private List<Searchable> find(IndexSearcher searcher, Query query, Filter filter, Sort sort, int page, int count) throws IOException {
        try {
            TopDocs hits = null;
            if (filter != null && sort != null)
                hits = searcher.search(query, filter, MAX_COUNT, sort);
            else if (filter != null)
                hits = searcher.search(query, filter, MAX_COUNT);
            else if (sort != null)
                hits = searcher.search(query, MAX_COUNT, sort);
            else
                hits = searcher.search(query, MAX_COUNT);
            if (hits == null) return null;
            List<Searchable> results = new ArrayList<Searchable>();
            int nBegin = (page - 1) * count;
            int nEnd = Math.min(nBegin + count, hits.scoreDocs.length);
            for (int i = nBegin; i < nEnd; i++) {
                ScoreDoc s_doc = (ScoreDoc) hits.scoreDocs[i];
                Document doc = searcher.doc(s_doc.doc);
                Searchable obj = SearchHelper.doc2obj(doc);
                if (obj != null && !results.contains(obj)) {
                    results.add(obj);
                }
            }
            return results;

        } catch (IOException e) {
            logger.error("Unabled to find via query: " + query, e);
        }
        return null;
    }

    /**
     * 根据查询条件统计搜索结果数
     *
     * @param searcher 搜索条件
     * @param query    查询条件
     * @param filter   过滤器
     * @return 结果数
     * @throws IOException
     */
    private int count(IndexSearcher searcher, Query query, Filter filter) throws IOException {
        try {
            TotalHitCountCollector thcc = new TotalHitCountCollector();
            if (filter != null)
                searcher.search(query, filter, thcc);
            else
                searcher.search(query, thcc);
            return Math.min(MAX_COUNT, thcc.getTotalHits());
        } catch (IOException e) {
            logger.error("Unabled to find via query: " + query, e);
            return -1;
        }
    }

    /**
     * 批量添加索引
     *
     * @param objs 对象数据类型
     * @throws IOException
     */
    public int add(List<? extends Searchable> objs) throws IOException {
        if (objs == null || objs.size() == 0)
            return 0;
        int doc_count = 0;
        IndexWriter writer = getWriter(objs.get(0).getClass());
        try {
            for (Searchable obj : objs) {
                Document doc = SearchHelper.obj2doc(obj);
                writer.addDocument(doc);
                doc_count++;
            }
            writer.commit();
        } finally {
            writer.close();
            writer = null;
        }
        return doc_count;
    }

    /**
     * 批量删除索引
     *
     * @param objs 对象数据类型
     * @throws IOException
     */
    public int delete(List<? extends Searchable> objs) throws IOException {
        if (objs == null || objs.size() == 0)
            return 0;
        int doc_count = 0;
        IndexWriter writer = getWriter(objs.get(0).getClass());
        try {
            for (Searchable obj : objs) {
                writer.deleteDocuments(new Term("id", String.valueOf(obj.id())));
                doc_count++;
            }
            writer.commit();
        } finally {
            writer.close();
            writer = null;
        }
        return doc_count;
    }

    /**
     * 批量更新索引
     *
     * @param objs 对象数据类型
     * @throws IOException
     */
    public void update(List<? extends Searchable> objs) throws IOException {
        delete(objs);
        add(objs);
    }

    /**
     * 获取分词器
     *
     * @return 分词器
     */
    public IKAnalyzer getAnalyzer() {
        return ANALYZER;
    }
}
