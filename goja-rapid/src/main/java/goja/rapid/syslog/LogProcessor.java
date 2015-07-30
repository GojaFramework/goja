/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.rapid.syslog;

import com.jfinal.core.Controller;

import java.util.Map;

/**
 * 日志处理器
 */
public interface LogProcessor {

    void process(SysLog sysLog);

    String getUsername(Controller c);

    String formatMessage(final LogConfig config, Map<String, String> message);
}
