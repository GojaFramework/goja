/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc.render;

import goja.core.StringPool;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import net.sf.jxls.transformer.XLSTransformer;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-03-29 14:12
 * @since JDK 1.6
 */
public class JxlsRender extends Render {
    private static final String CONTENT_TYPE = "application/vnd.ms-excel;charset=" + getEncoding();
    private final String templetFile;
    private Map<String, Object> beans = Maps.newHashMap();
    private String filename = "file1.xls";

    public JxlsRender(String templetFile) {
        this.templetFile = templetFile;
    }

    public static JxlsRender me(String templetFile) {
        return new JxlsRender(templetFile);
    }

    private static String encodeChineseDownloadFileName(HttpServletRequest request, String filename) {
        String agent = request.getHeader("USER-AGENT");
        try {
            if (!Strings.isNullOrEmpty(agent) && agent.contains("MSIE")) {
                filename = URLEncoder.encode(filename, StringPool.UTF_8);
            } else {
                filename = new String(filename.getBytes(StringPool.UTF_8), "iso8859-1");
            }
        } catch (UnsupportedEncodingException ignored) {

        }
        return filename;
    }

    public JxlsRender beans(Map<String, Object> beans) {
        this.beans = beans;
        return this;
    }

    private void buildBean() {
        Enumeration<String> attrs = request.getAttributeNames();
        while (attrs.hasMoreElements()) {
            String key = attrs.nextElement();
            Object value = request.getAttribute(key);
            beans.put(key, value);
        }
    }

    public JxlsRender filename(String filename) {
        this.filename = filename;
        return this;
    }

    @Override
    public void render() {
        if (beans.isEmpty()) {
            buildBean();
        }
        response.setContentType(CONTENT_TYPE);
        response.setHeader("Content-Disposition",
                "attachment;Filename=" + encodeChineseDownloadFileName(request, filename));
        try {
            OutputStream out = response.getOutputStream();
            InputStream is = new BufferedInputStream(new FileInputStream(templetFile));
            XLSTransformer transformer = new XLSTransformer();
            Workbook workBook = transformer.transformXLS(is, beans);
            workBook.write(out);
        } catch (Exception e) {
            throw new RenderException(e);
        }
    }
}
