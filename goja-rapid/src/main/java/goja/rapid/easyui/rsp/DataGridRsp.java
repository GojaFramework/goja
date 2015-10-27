package goja.rapid.easyui.rsp;

import com.jfinal.plugin.activerecord.Record;

import java.io.Serializable;
import java.util.List;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class DataGridRsp implements Serializable {

    private static final long serialVersionUID = -6577065924384706107L;

    public final int total;

    public final List<Record> rows;

    private DataGridRsp(int total, List<Record> rows) {
        this.total = total;
        this.rows = rows;
    }

    public static class Builder {
        private int          total;
        private List<Record> rows;


        public Builder total(int total) {
            this.total = total;
            return this;
        }

        public Builder rows(List<Record> rows) {
            this.rows = rows;
            return this;
        }


        public DataGridRsp build() {
            return new DataGridRsp(total, rows);
        }
    }

}
