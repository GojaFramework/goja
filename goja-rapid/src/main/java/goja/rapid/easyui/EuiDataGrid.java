package goja.rapid.easyui;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import goja.core.StringPool;
import goja.core.app.GojaPropConst;
import goja.core.date.DateFormatter;
import goja.core.kits.base.Strs;
import goja.core.sqlinxml.SqlKit;
import goja.core.tuples.Triplet;
import goja.rapid.datatables.DTDao;
import goja.rapid.db.Condition;
import goja.rapid.easyui.req.DataGridReq;
import goja.rapid.easyui.rsp.DataGridRsp;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static goja.core.StringPool.COMMA;
import static goja.core.StringPool.SPACE;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class EuiDataGrid {

    public static final DataGridRsp EMPTY_DATAGRID = new DataGridRsp.Builder().rows(Collections.EMPTY_LIST).total(0).build();


    private EuiDataGrid() {
    }

    private static final Logger logger = LoggerFactory.getLogger(EuiDataGrid.class);

    public static Optional<DataGridReq> req(HttpServletRequest request) {
        if (request != null) {

            String r_page = request.getParameter("page");
            String r_rows = request.getParameter("rows");
            String r_sortField = request.getParameter("sort");
            String r_orderBy = request.getParameter("order");

            int page = Strings.isNullOrEmpty(r_page) ? 1 : Ints.tryParse(r_page);
            page = (page == 0) ? 1 : page;
            int rows = Strings.isNullOrEmpty(r_rows) ? 10 : Ints.tryParse(r_rows);
            rows = (rows == 0) ? 10 : rows;

            String orderBy = "asc";
            if (!Strings.isNullOrEmpty(r_orderBy) && StringUtils.equals("desc", r_orderBy)) {
                orderBy = "desc";
            }

            DataGridReq.Builder builder = new DataGridReq.Builder();
            builder.sortField(r_sortField).order(orderBy).page(page).rows(rows);

            List<Triplet<String, Condition, Object>> _params = Lists.newArrayListWithCapacity(3);
            String p_index = null;
            final Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String param_name = parameterNames.nextElement();
                if (logger.isDebugEnabled()) {
                    logger.debug("请求参数名称： {}, 参数值: {}", param_name, request.getParameter(param_name));
                }
                // param_name 类似这样
                // s-field_name-ne,s-field_name-eq,
                // s-field_name-inn,s-field_name-in,
                // s-field_name-lt,s-field_name-lteq,
                // s-field_name-gt,s-field_name-gteq,s-field_name-between,
                // s-field_name-like,s-field_name-llike,s-field_name-rlike
                if (StringUtils.startsWith(param_name, "s-")) {
                    // 如果传递的参数以日期(date)开头，则表示需要转换为日期
                    final String req_val = request.getParameter(param_name);
                    if (!Strings.isNullOrEmpty(req_val)) {
                        String[] param_array = StringUtils.split(param_name, StringPool.DASH);
                        if (param_array != null && param_array.length >= 2) {

                            String name = param_array[1];
                            String condition = param_array.length == 2 ? Condition.EQ.toString() : param_array[2];
                            Condition query_condition
                                    = Strings.isNullOrEmpty(condition) ? Condition.EQ : Condition.valueOf(condition.toUpperCase());
                            switch (query_condition) {
                                case BETWEEN:

                                    if (StringUtils.startsWith(req_val, "date-")) {
                                        //需要转让为日期 示例 date-2015-10-10~2015-10-10 或者 date-2015-10-10 12:12:12~2015-10-10 12:12:12
                                        final String realValue = StringUtils.replace(req_val, "date-", StringPool.EMPTY);
                                        String[] dataValues = StringUtils.split(realValue, "~");
                                        try {
                                            final Date startDate = DateUtils.parseDate(dataValues[0], DateFormatter.YYYY_MM_DD, DateFormatter.YYYY_MM_DD_HH_MM_SS);
                                            final Date endDate = DateUtils.parseDate(dataValues[1], DateFormatter.YYYY_MM_DD, DateFormatter.YYYY_MM_DD_HH_MM_SS);
                                            _params.add(Triplet.<String, Condition, Object>with(name, query_condition, new Date[]{startDate, endDate}));
                                        } catch (ParseException e) {
                                            logger.error("日期转换出错！", e);
                                        }
                                    } else {
                                        String[] dataValues = StringUtils.split(req_val, "~");
                                        _params.add(Triplet.<String, Condition, Object>with(name, query_condition, new String[]{dataValues[0], dataValues[1]}));
                                    }

                                    break;
                                case LIKE:
                                    _params.add(Triplet.<String, Condition, Object>with(name, query_condition, Strs.like(req_val)));
                                    break;
                                case LLIKE:
                                    _params.add(Triplet.<String, Condition, Object>with(name, query_condition, Strs.llike(req_val)));
                                    break;
                                case RLIKE:
                                    _params.add(Triplet.<String, Condition, Object>with(name, query_condition, Strs.rlike(req_val)));
                                    break;
                                default:
                                    _params.add(Triplet.<String, Condition, Object>with(name, query_condition, req_val));
                            }
                        }
                    }
                }

            }

            builder.params(_params);
            return Optional.of(builder.build());
        }

        return Optional.absent();
    }


    public static DataGridRsp rsp(DataGridReq req, Class<? extends Model> model) {

        Preconditions.checkNotNull(req);

        int pageSize = req.rows;
        int page = req.page;

        final Table table = TableMapping.me().getTable(model);
        final String tableName = table.getName();

        final Map<String, Class<?>> columnTypeMap = table.getColumnTypeMap();

        String sql_columns = "SELECT " + StringUtils.join(columnTypeMap.keySet(), COMMA);


        StringBuilder where = new StringBuilder(" FROM ");
        where.append(tableName).append(SPACE);


        final List<Triplet<String, Condition, Object>> custom_params = req.params;
        final List<Object> params = Lists.newArrayList();
        DTDao.appendWhereSql(params, where, custom_params);


        if (!Strings.isNullOrEmpty(req.sortField)) {
            where.append(" ORDER BY ").append(req.sortField).append(StringPool.SPACE).append(req.order);
        }


        final Page<Record> paginate = Db.paginate(page, pageSize, sql_columns, where.toString(), params.toArray());
        DataGridRsp.Builder builder = new DataGridRsp.Builder();
        builder.rows(paginate.getList()).total(paginate.getTotalRow());
        return builder.build();


    }


    public static DataGridRsp rsp(DataGridReq req, String sqlGroupName) {

        Preconditions.checkNotNull(req);

        List<Object> params = Lists.newArrayList();
        return rsp(req, sqlGroupName, params);

    }
    public static DataGridRsp rsp(DataGridReq req, String sqlGroupName, List<Object> params) {

        Preconditions.checkNotNull(req);

        int pageSize = req.rows;
        int page = req.page;

        String sqlId = sqlGroupName + ".easyui.datagrid";
        String sql = SqlKit.sql(sqlId);
        if (Strings.isNullOrEmpty(sql)) {
            logger.error("约定的配置sql 不存在，约定sql名称为 {}", sqlId);
            return EMPTY_DATAGRID;
        }

        if (!StringUtils.containsIgnoreCase(sql, GojaPropConst.WHERESPLIT)) {
            logger.error("约定的分页SQL 切割标志符 [--where--] 不存在，请检查 SQLID为{} 的sql语句", sqlId);
            return EMPTY_DATAGRID;
        }


        String sql_columns = StringUtils.substringBefore(sql, GojaPropConst.WHERESPLIT);
        StringBuilder where = new StringBuilder(StringUtils.substringAfter(sql, GojaPropConst.WHERESPLIT));

        final List<Triplet<String, Condition, Object>> custom_params = req.params;
        DTDao.appendWhereSql(params, where, custom_params);


        if (!Strings.isNullOrEmpty(req.sortField)) {
            where.append(" ORDER BY ").append(req.sortField).append(StringPool.SPACE).append(req.order);
        }


        final Page<Record> paginate = Db.paginate(page, pageSize, sql_columns, where.toString(), params.toArray());
        DataGridRsp.Builder builder = new DataGridRsp.Builder();
        builder.rows(paginate.getList()).total(paginate.getTotalRow());
        return builder.build();


    }

}
