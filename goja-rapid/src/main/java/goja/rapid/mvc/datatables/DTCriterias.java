package goja.rapid.mvc.datatables;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import goja.core.StringPool;
import goja.core.db.Condition;
import goja.core.kits.lang.Strs;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public final class DTCriterias implements Serializable {
    private static final long    serialVersionUID = -4728223524642774477L;
    /**
     * Matches the regular brackets
     */
    private static final Pattern BRACKETS_PATTERN = Pattern.compile("\\[(.*?)\\]");

    /**
     * jquery datatablse Control the query condition
     */
    private final DTSearch       search;
    /**
     * Display start number
     */
    private final int            start;
    /**
     * The page displays the number of.
     */
    private final int            length;
    /**
     * Which coloumns to display.
     */
    private final List<DTColumn> columns;
    /**
     * The order coloumn.
     */
    private final List<DTOrder>  order;
    private final int            draw;

    private final List<Triple<String, Condition, Object>> params;

    private DTCriterias(DTSearch search, int start, int length, List<DTColumn> columns,
                        List<DTOrder> order, int draw) {
        this.search = search;
        this.start = start;
        this.length = length;
        this.columns = columns;
        this.order = order;
        this.draw = draw;
        this.params = Lists.newArrayListWithCapacity(1);
    }

    public static DTCriterias criteriasWithRequest(HttpServletRequest request) {
        if (request != null) {

            String r_draw = request.getParameter(DTConstants.DT_DRAW);
            String r_start = request.getParameter(DTConstants.DT_START);
            String r_length = request.getParameter(DTConstants.DT_LENGTH);
            String r_search_value = request.getParameter(DTConstants.DT_SEARCH_VALUE);
            String r_search_regex = request.getParameter(DTConstants.DT_SEARCH_REGEX);

            boolean custom_search =
                    !Strings.isNullOrEmpty(request.getParameter(DTConstants.DT_SEARCH_FLAG));

            int draw = Strings.isNullOrEmpty(r_draw) ? 1 : Ints.tryParse(r_draw);
            int start = Strings.isNullOrEmpty(r_start) ? 1 : Ints.tryParse(r_start);
            int length = Strings.isNullOrEmpty(r_length) ? 1 : Ints.tryParse(r_length);
            start = start == 0 ? 1 : start;

            DTSearch dtSearch = DTSearch.create(r_search_value, BooleanUtils.toBoolean(r_search_regex));

            final List<DTColumn> dtColumns = Lists.newArrayList();
            final List<DTOrder> dtOrders = Lists.newArrayList();

            List<Pair<Integer, String>> _orders = Lists.newArrayList();
            List<String> processed = Lists.newArrayList();

            List<Triple<String, Condition, Object>> _params = Lists.newArrayListWithCapacity(3);
            try {
                Matcher matcher;
                String p_index = null;
                final Enumeration<String> parameterNames = request.getParameterNames();
                while (parameterNames.hasMoreElements()) {
                    String param_name = parameterNames.nextElement();
                    if (custom_search && StringUtils.startsWith(param_name,
                            DTConstants.DT_CUSTEM_SEARCH_PREFIX)) {
                        final String req_val = request.getParameter(param_name);
                        if (!Strings.isNullOrEmpty(req_val)) {
                            String[] param_array = StringUtils.split(param_name, StringPool.DASH);
                            if (param_array != null && param_array.length >= 2) {

                                String name = param_array[1];
                                String condition =
                                        param_array.length == 2 ? Condition.EQ.toString() : param_array[2];
                                Condition query_condition;
                                if (Strings.isNullOrEmpty(condition)) {
                                    query_condition = Condition.EQ;
                                } else {
                                    query_condition = Condition.valueOf(condition.toUpperCase());
                                }
                                switch (query_condition) {
                                    case BETWEEN:
                                        String two_param =
                                                StringUtils.replace(param_name, Condition.BETWEEN.toString(), "AND");
                                        String req_val2 = request.getParameter(two_param);
                                        _params.add(Triple.<String, Condition, Object>of(name, query_condition,
                                                new String[]{req_val, req_val2}));
                                        break;
                                    case LIKE:
                                        _params.add(Triple.<String, Condition, Object>of(name, query_condition,
                                                Strs.like(req_val)));
                                        break;
                                    case LLIKE:
                                        _params.add(Triple.<String, Condition, Object>of(name, query_condition,
                                                Strs.llike(req_val)));
                                        break;
                                    case RLIKE:
                                        _params.add(Triple.<String, Condition, Object>of(name, query_condition,
                                                Strs.rlike(req_val)));
                                        break;
                                    default:
                                        _params.add(
                                                Triple.<String, Condition, Object>of(name, query_condition, req_val));
                                }
                            }
                        }
                    }

                    // column setting
                    matcher = BRACKETS_PATTERN.matcher(param_name);
                    if (matcher.find()) {
                        p_index = matcher.group(1);
                    }
                    if (StringUtils.startsWithIgnoreCase(param_name, DTConstants.DT_COLUMNS)) {

                        if (!processed.isEmpty() && processed.contains(p_index)) {
                            continue;
                        }
                        processed.add(p_index);
                        String column_data = request.getParameter("columns[" + p_index + "][data]");
                        if (Strings.isNullOrEmpty(column_data)) {
                            p_index = null;
                            continue;
                        }
                        String column_name = request.getParameter("columns[" + p_index + "][name]");
                        String column_searchable = request.getParameter("columns[" + p_index + "][searchable]");
                        String column_orderable = request.getParameter("columns[" + p_index + "][orderable]");
                        String column_search_value =
                                request.getParameter("columns[" + p_index + "][search][value]");
                        String column_search_regex =
                                request.getParameter("columns[" + p_index + "][search][regex]");

                        dtColumns.add(new DTColumn(column_data, column_name
                                , BooleanUtils.toBoolean(column_searchable)
                                , BooleanUtils.toBoolean(column_orderable)
                                ,
                                DTSearch.create(column_search_value, BooleanUtils.toBoolean(column_search_regex))));
                    } else if (StringUtils.startsWithIgnoreCase(param_name, DTConstants.DT_ORDER)) {
                        int index = Ints.tryParse(p_index);
                        if (!_orders.isEmpty() && _orders.get(index) != null) {
                            continue;
                        }
                        String order_column_index = request.getParameter("order[" + p_index + "][column]");
                        String order_column_dir = request.getParameter("order[" + p_index + "][dir]");
                        Pair<Integer, String> _temp_order =
                                Pair.of(Ints.tryParse(order_column_index), order_column_dir);
                        _orders.add(_temp_order);
                    }
                }

                if (!_orders.isEmpty()) {
                    for (Pair<Integer, String> pair : _orders) {
                        DTColumn column = dtColumns.get(pair.getKey());
                        if (column == null) {
                            continue;
                        }
                        dtOrders.add(DTOrder.create(column.getData(), pair.getValue()));
                    }
                }
            } finally {
                _orders = null;
                processed = null;
            }

            final DTCriterias dtCriterias =
                    new DTCriterias(dtSearch, start, length, dtColumns, dtOrders, draw);
            dtCriterias.setAllParams(_params);
            return dtCriterias;
        } else {
            return null;
        }
    }

    public void setAllParams(List<Triple<String, Condition, Object>> _params) {
        this.params.addAll(_params);
    }

    /**
     * Adding custom query condition and value
     *
     * @param field     query condition
     * @param condition condition
     * @param value     query condition If it is a LIKE query, then you need to pass an array of objects,
     *                  the size of 2.
     */
    public void setParam(String field, Condition condition, Object value) {
        this.params.add(Triple.of(field, condition, value));
    }

    /**
     * Adding custom query equal  value.
     *
     * @param field query condition
     * @param value query condition
     */
    public void setParam(String field, Object value) {
        this.setParam(field, Condition.EQ, value);
    }

    public List<Triple<String, Condition, Object>> getParams() {
        return params;
    }

    /**
     * Support for a single entity with the integration of Datatables plugin。
     *
     * @param model The DB model.
     * @return response.
     */
    public DTResponse response(Class<? extends Model> model) {

        Preconditions.checkNotNull(this, "datatable criterias is must be not null.");
        final Page<Record> datas = DTDao.paginate(model, this);
        return DTResponse.build(this, datas.getList(), datas.getTotalRow(), datas.getTotalRow());
    }

    public DTResponse response(String whereSql, String columns, List<Object> params) {

        Preconditions.checkNotNull(this, "datatable criterias is must be not null.");
        final Page<Record> datas = DTDao.paginate(whereSql, columns, this, params);
        return DTResponse.build(this, datas.getList(), datas.getTotalRow(), datas.getTotalRow());
    }

    public DTSearch getSearch() {
        return search;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }

    public List<DTColumn> getColumns() {
        return columns;
    }

    public List<DTOrder> getOrder() {
        return order;
    }

    public int getDraw() {
        return draw;
    }
}
