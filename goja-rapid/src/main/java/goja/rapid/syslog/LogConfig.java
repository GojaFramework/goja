/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.rapid.syslog;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 系统日志
 */
public class LogConfig {
    /**
     * 日志描述
     */
    final String title;

    /**
     * key 请求参数 value 参数描述
     */
    final Map<String, String> params = Maps.newHashMap();

    /**
     * 内容模板
     */
    private final String format;

    public LogConfig(String title) {
        this.title = title;
        this.format = StringUtils.EMPTY;
    }

    public LogConfig(String title, String format) {
        this.title = title;
        this.format = format;
    }

    public LogConfig addPara(String key, String value) {
        params.put(key, value);
        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getFormat() {
        return format;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
