/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.plugins.index;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 可搜索对象.
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-08-24 0:23
 * @since JDK 1.6
 */
public interface Searchable extends Comparable<Searchable> {


    public String FN_TITLE  = "title";
    public String FN_DETAIL = "detail";

    /**
     * 文档的唯一编号
     *
     * @return 文档id
     */
    public long id();

    public void setId(long id);

    /**
     * 要存储的字段
     *
     * @return 返回字段名列表
     */
    public List<String> storeFields();

    /**
     * 要进行分词索引的字段
     *
     * @return 返回字段名列表
     */
    public List<String> indexFields();

    /**
     * 文档的优先级
     *
     * @return 文档的优先级
     */
    public float boost();

    /**
     * 扩展的存储数据
     *
     * @return 扩展数据K/V
     */
    public Map<String, String> extendStoreDatas();

    /**
     * 扩展的索引数据
     *
     * @return 扩展数据K/V
     */
    public Map<String, String> extendIndexDatas();

    /**
     * 列出id值大于指定值得所有对象
     *
     * @param id    id值
     * @param count count.
     * @return 大于制定值的所有对象
     */
    public List<? extends Searchable> ListAfter(long id, int count);
}
