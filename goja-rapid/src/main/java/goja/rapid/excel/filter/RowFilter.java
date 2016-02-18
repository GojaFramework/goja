package goja.rapid.excel.filter;

import java.util.List;

public interface RowFilter {
    boolean doFilter(int rowNum, List<String> list);
}
