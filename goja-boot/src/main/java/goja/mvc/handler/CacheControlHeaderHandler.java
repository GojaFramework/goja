/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.mvc.handler;

import com.jfinal.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CacheControlHeaderHandler extends Handler {
    private static final int invalidExpirationTime = -1;
    public final         int expires_hour          = 60;
    public final         int expires_day           = expires_hour * 24;
    public final         int expires_week          = expires_day * 7;
    public final         int expires_nocache       = -1;
    /** 缓存分钟数 */
    private final int expirationMinutes;

    public CacheControlHeaderHandler(int expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }

    @Override
    public void handle(String target, HttpServletRequest request,
                       HttpServletResponse response, boolean[] isHandled) {
        if (!response.isCommitted()) {
            if (expirationMinutes == expires_nocache) {
                response.setHeader("Cache-Control", "no-cache");
            } else if (expirationMinutes > invalidExpirationTime) {
                response.setDateHeader("Expires", System.currentTimeMillis() + (expirationMinutes * 60 * 1000));
                response.setHeader("Cache-Control", "private");
            }
        }
        nextHandler.handle(target, request, response, isHandled);
    }
}
