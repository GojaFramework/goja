/* Copyright 2013-2015 www.snakerflow.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.snaker.engine.access;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.snaker.engine.helper.ConfigHelper;

import java.util.List;

/**
 * 与具体DBAccess实现无关的分页参数及查询结果封装.
 *
 * @param <T> Page中对象的类型.
 * @author yuqs
 * @since 1.0
 */
public class Page<T> {
    public static final int NON_PAGE = -1;
    public static final int PAGE_SIZE = 15;

    //当前页
    private int pageNo = 1;
    //每页记录数
    private int pageSize = -1;
    //总记录数
    private long totalCount = 0;
    //查询结果集
    private List<T> result;

    public Page() {
        this.pageSize = ConfigHelper.getNumerProperty("jdbc.pageSize");
        if (pageSize <= 0) pageSize = PAGE_SIZE;
    }

    public Page(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 获得当前页的页号,默认为1.
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * 设置当前页的页号,小于1时自动设置为1.
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
        if (pageNo < 1) {
            this.pageNo = 1;
        }
    }

    /**
     * 返回Page对象自身的setPageNo函数,可用于连续设置。
     */
    public Page<T> pageNo(int thePageNo) {
        setPageNo(thePageNo);
        return this;
    }

    /**
     * 获得每页记录数.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置每页的记录数.
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 返回Page对象自身的setPageSize函数,用于连续设置。
     */
    public Page<T> pageSize(int thePageSize) {
        setPageSize(thePageSize);
        return this;
    }

    /**
     * 获得页内的记录列表.
     */
    public List<T> getResult() {
        return result;
    }

    /**
     * 设置页内的记录列表.
     */
    public void setResult(List<T> result) {
        this.result = result;
    }

    /**
     * 获得总记录数, 默认值为0.
     */
    public long getTotalCount() {
        return totalCount < 0 ? 0 : totalCount;
    }

    /**
     * 设置总记录数.
     */
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * 根据pageSize与totalCount计算总页数, 默认值为-1.
     */
    public long getTotalPages() {
        if (totalCount < 0) {
            return 0;
        }

        long count = totalCount / pageSize;
        if (totalCount % pageSize > 0) {
            count++;
        }
        return count;
    }

    /**
     * 是否还有下一页.
     */
    public boolean isHasNext() {
        return (pageNo + 1 <= getTotalPages());
    }

    /**
     * 取得下页的页号, 序号从1开始.
     * 当前页为尾页时仍返回尾页序号.
     */
    public int getNextPage() {
        if (isHasNext()) {
            return pageNo + 1;
        } else {
            return pageNo;
        }
    }

    /**
     * 是否还有上一页.
     */
    public boolean isHasPre() {
        return (pageNo - 1 >= 1);
    }

    /**
     * 取得上页的页号, 序号从1开始.
     * 当前页为首页时返回首页序号.
     */
    public int getPrePage() {
        if (isHasPre()) {
            return pageNo - 1;
        } else {
            return pageNo;
        }
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
