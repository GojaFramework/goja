/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc.error;

import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.jfinal.core.Const;
import com.jfinal.core.JFinal;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.jfinal.render.RenderFactory;
import goja.core.StringPool;
import goja.core.app.GojaConfig;
import goja.mvc.Freemarkers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * <p>
 * .
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-11-08 20:54
 * @since JDK 1.6
 */
public class GojaErrorRender extends Render {

    protected static final String contentType = "text/html; charset=" + getEncoding();

    protected static final String version = "<center><a href='http://www.jfinal.com?f=ev'><b>Powered by JFinal " + Const.JFINAL_VERSION + "</b></a></center>";

    protected static final String html404 = "<html><head><title>404 Not Found</title></head><body bgcolor='white'><center><h1>404 Not Found</h1></center><hr>" + version + "</body></html>";
    protected static final String html500 = "<html><head><title>500 Internal Server Error</title></head><body bgcolor='white'><center><h1>500 Internal Server Error</h1></center><hr>" + version + "</body></html>";

    protected static final String html401 = "<html><head><title>401 Unauthorized</title></head><body bgcolor='white'><center><h1>401 Unauthorized</h1></center><hr>" + version + "</body></html>";
    protected static final String html403 = "<html><head><title>403 Forbidden</title></head><body bgcolor='white'><center><h1>403 Forbidden</h1></center><hr>" + version + "</body></html>";


    protected int errorCode;


    public GojaErrorRender(int errorCode, String view) {
        this.errorCode = errorCode;
        this.view = view;
    }

    public void render() {
        response.setStatus(getErrorCode());    // HttpServletResponse.SC_XXX_XXX

        // render with view
        String view = getView();
        if (view != null) {
            RenderFactory.me().getRender(view).setContext(request, response).render();
            return;
        }


        // render with html content
        PrintWriter writer = null;
        try {
            response.setContentType(contentType);
            writer = response.getWriter();
            writer.write(getErrorHtml());
            writer.flush();
        } catch (IOException e) {
            throw new RenderException(e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    public String getErrorHtml() {
        if (GojaConfig.getApplicationMode().isDev()) {
            int errorCode = getErrorCode();
            Map<String, Object> pdata = Maps.newHashMap();
            final String requestURI = request.getRequestURI();
            pdata.put("requestURI", requestURI);
            switch (errorCode) {
                case SC_INTERNAL_SERVER_ERROR:
                    Throwable te = (Throwable) request.getAttribute("goja_error");
                    String error_msg = Throwables.getStackTraceAsString(te);
                    List<String> error_lines = Splitter.on(StringPool.NEWLINE).splitToList(error_msg);
                    pdata.put("title", error_lines.get(0));
                    pdata.put("errors", error_lines);
                    return Freemarkers.renderStrTemplate("<!DOCTYPE html><html><head><title> Application 500 Eroor </title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head><style type=\"text/css\">html, body {margin: 0;padding: 0;font-family : Helvetica, Arial, Sans;background: #EEEEEE;}.block {padding: 20px;border-bottom : 1px solid #aaa;}#header h1 {font-weight : normal;font-size: 28px;margin: 0;}#more {color: #666;font-size : 80%;border: none;}#header {background : #fcd2da;}#header p {color : #333;}#source {background : #f6f6f6;}#source h2 {font-weight : normal;font-size: 18px;margin: 0 0 10px 0;}#source .lineNumber {float: left;display: block;width: 40px;text-align: right;margin-right : 10px;font-size: 14px;font-family: monospace;background: #333;color: #fff;}#source .line {clear: both;color: #333;margin-bottom : 1px;}#source pre {font-size: 14px;margin: 0;overflow-x : hidden;}#source .error {color : #c00 !important;}#source .error .lineNumber {background : #c00;}#source a {text-decoration : none;}#source a:hover * {cursor : pointer !important;}#source a:hover pre {background : #FAFFCF !important;}#source em {font-style: normal;text-decoration : underline;font-weight: bold;}#source strong {font-style: normal;font-weight : bold;}</style><body><div id=\"header\" class=\"block\"><h1>Execution exception</h1><p> ${title!} </p></div><div id=\"source\" class=\"block\"><h2>Exception information in detail </h2><#list errors as er><div class=\"line <#if er?starts_with(\"\tat app.\")>error</#if>\"><span class=\"lineNumber\">${er_index + 1}:</span><pre>${er}</pre></div></#list></div></body></html>", pdata);
                case SC_NOT_FOUND:
                    final List<String> allActionKeys = JFinal.me().getAllActionKeys();
                    pdata.put("routes", allActionKeys);
                    return Freemarkers.renderStrTemplate("<!DOCTYPE html><html><head><title>Not found</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head><style type=\"text/css\">html, body {margin: 0;padding: 0;font-family: Helvetica, Arial, Sans;background: #EEEEEE;}.block {padding: 20px;border-bottom: 1px solid #aaa;}#header h1 {font-weight: normal;font-size: 28px;margin: 0;}#more {color: #666;font-size: 80%;border: none;}#header {background: #FFFFCC;}#header p {color: #333;}#routes {background: #f6f6f6;}#routes h2 {font-weight: normal;font-size: 18px;margin: 0 0 10px 0;}#routes ol {}#routes li {font-size: 14px;font-family: monospace;color: #333;}</style><body><div id=\"header\" class=\"block\"><h1>${requestURI!}  Not found</h1></div><div id=\"routes\" class=\"block\"><h2>These routes have been tried, in this order :</h2><ol><#list routes as r><li> ${r} </li></#list></ol></div></body></html>", pdata);
                default:
                    return "<html><head><title>" + errorCode + " Error</title></head><body bgcolor='white'><center><h1>" + errorCode + " Error</h1></center><hr>" + version + "</body></html>";
            }

        } else {
            int errorCode = getErrorCode();
            if (errorCode == SC_NOT_FOUND)
                return html404;
            if (errorCode == SC_INTERNAL_SERVER_ERROR)
                return html500;
            if (errorCode == 401)
                return html401;
            if (errorCode == 403)
                return html403;
            return "<html><head><title>" + errorCode + " Error</title></head><body bgcolor='white'><center><h1>" + errorCode + " Error</h1></center><hr>" + version + "</body></html>";
        }


    }

    public int getErrorCode() {
        return errorCode;
    }
}