package goja.rapid.mvc.easyui.req;

import com.google.common.collect.Lists;

import goja.core.db.Condition;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

/**
 * <p> DataGrid EasyUI的分页表格传递的参数 </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public final class DataGridReq implements Serializable {
    private static final long serialVersionUID = -2179389393824520342L;


    /**
     * 页码
     */
    public final int page;

    /**
     * 每页显示数量
     */
    public final int rows;

    /**
     * 排序字段
     */
    public final String sortField;

    /**
     * 排序方向 desc 或者 asc
     */
    public final String order;

    /**
     * 搜索参数
     */
    public final List<Triple<String, Condition, Object>> params;

    private DataGridReq(int page, int rows, String sortField, String order, List<Triple<String, Condition, Object>> params) {
        this.page = page;
        this.rows = rows;
        this.sortField = sortField;
        this.order = order;
        this.params = params;
    }

    public static class Builder<M> {
        private int    page;
        private int    rows;
        private String sortField;

        private String order;
        private List<Triple<String, Condition, Object>> params = Lists.newArrayList();


        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder rows(int rows) {
            this.rows = rows;
            return this;
        }

        public Builder sortField(String sortField) {
            this.sortField = sortField;
            return this;
        }

        public Builder order(String order) {
            this.order = order;
            return this;
        }

        public Builder params(List<Triple<String, Condition, Object>> params) {
            this.params = params;
            return this;
        }


        public DataGridReq build() {
            return new DataGridReq(page, rows, sortField, order, params);
        }
    }
}
