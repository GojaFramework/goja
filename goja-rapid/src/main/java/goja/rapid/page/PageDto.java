/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.rapid.page;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import goja.core.app.GojaConfig;
import goja.core.StringPool;
import goja.core.kits.lang.Strs;
import goja.rapid.db.Condition;
import goja.rapid.db.RequestParam;
import goja.rapid.db.RequestParam.Direction;
import org.apache.commons.lang3.StringUtils;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * .
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-08-17 16:11
 * @since JDK 1.6
 */
public class PageDto {


    public static final Integer DEFAULT_PAGE_SIZE = GojaConfig.getPropertyToInt("app.page.defaultsize", 10);

    public final  int       page;
    public final  int       pageSize;
    private final String    sort;
    private final Direction direction;
    private final boolean   hasSort;

    public final List<RequestParam> params = Lists.newArrayListWithCapacity(3);

    private final Map<String, Object> fq = Maps.newHashMap();

    public final List<Object> query_params = Lists.newArrayListWithCapacity(3);

    private final StringBuilder filter_url = new StringBuilder();


    public static PageDto create(com.jfinal.core.Controller controller) {

        String dir = controller.getPara("dir", "desc").toUpperCase();
        final Direction direction = Direction.valueOf(dir);

        final Enumeration<String> paraNames = controller.getParaNames();
        // exmaple add app.page.size=pager.pageSize into application.conf
        final int current_page = controller.getParaToInt("p", 1);
        // pager.pageNo
        final int page_size = controller.getParaToInt("s", DEFAULT_PAGE_SIZE);

        String sort = controller.getPara("sort");
        PageDto pageDto = Strings.isNullOrEmpty(sort) ? new PageDto(current_page, page_size) : new PageDto(current_page, page_size, sort, direction);
        while (paraNames.hasMoreElements()) {
            String p_key = paraNames.nextElement();
            if (!Strings.isNullOrEmpty(p_key) && StringUtils.startsWith(p_key, "s-")) {
                final String req_val = controller.getPara(p_key);
                if (!Strings.isNullOrEmpty(req_val)) {
                    String[] param_array = StringUtils.split(p_key, StringPool.DASH);
                    if (param_array != null && param_array.length >= 2) {

                        String name = param_array[1];
                        String condition = param_array.length == 2 ? Condition.EQ.toString() : param_array[2];
                        condition = Strings.isNullOrEmpty(condition) ? Condition.EQ.toString() : condition.toUpperCase();
                        if (StringUtils.equals(condition, Condition.BETWEEN.toString())) {
                            String req_val2 = controller.getPara(StringUtils.replace(p_key, Condition.BETWEEN.toString(), "AND"));
                            pageDto.putTwoVal(name, req_val, req_val2, condition);
                        } else {
                            pageDto.put(name, req_val, condition);
                        }
                    }
                }
            }
        }
        return pageDto;
    }

    private PageDto(int pageNo, int pageSize,
                    String sort,
                    Direction direction) {
        this.page = pageNo;
        this.pageSize = pageSize;
        this.sort = sort;
        this.direction = direction;
        hasSort = true;
    }

    private PageDto(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
        this.sort = StringPool.EMPTY;
        this.direction = Direction.DESC;
        hasSort = false;
    }

    public void put(String key, String value, String condition) {
        putTwoVal(key, value, StringPool.EMPTY, condition);

        filter_url.append(StringPool.AMPERSAND).append(key).append(StringPool.EQUALS).append(value);
    }

    public void putTwoVal(String key, Object value, Object val2, String condition) {
        if (fq.containsKey(key)) {
            return;
        }
        final RequestParam reqParam = new RequestParam(key, condition);
        params.add(reqParam);
        switch (reqParam.condition) {
            case LIKE:
                query_params.add(Strs.like(String.valueOf(value)));
                break;
            case LLIKE:
                query_params.add(Strs.llike(String.valueOf(value)));
                break;
            case RLIKE:
                query_params.add(Strs.rlike(String.valueOf(value)));
                break;
            case BETWEEN:
                query_params.add(value);
                query_params.add(val2);
                break;
            default:
                query_params.add(value);
                break;
        }
        fq.put(key, value);

        filter_url.append(StringPool.AMPERSAND).append(key).append(StringPool.EQUALS).append(value);
        if (val2 != null) {
            final String key_two = key + "2";
            fq.put(key_two, val2);
            filter_url.append(StringPool.AMPERSAND).append(key_two).append(StringPool.EQUALS).append(val2);
        }
    }

    public String getQueryUrl() {
        return filter_url.toString();
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<RequestParam> getParams() {
        return params;
    }

    public Map<String, Object> getFq() {
        return fq;
    }

    public List<Object> getQuery_params() {
        return query_params;
    }

    public String getSort() {
        return sort;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isHasSort() {
        return hasSort;
    }


}
