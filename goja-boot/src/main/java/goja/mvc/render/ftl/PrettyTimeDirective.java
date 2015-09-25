/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc.render.ftl;

import com.google.common.base.Strings;
import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import goja.date.DateFormatter;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * <p>
 * 日期友好化显示.
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2013-12-18 10:34
 * @since JDK 1.6
 */
public class PrettyTimeDirective implements TemplateDirectiveModel {


    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        // 检查是否传递参数，此指令禁止传参！
        if (params.isEmpty()) {
            throw new TemplateModelException(
                    "This directive doesn't allow parameters.");
        }

        // 禁用循环变量
        /*
         * 循环变量
		        用户定义指令可以有循环变量，通常用于重复嵌套内容，基本用法是：作为nested指令的参数传递循环变量的实际值，而在调用用户定义指令时，在${"<@…>"}开始标记的参数后面指定循环变量的名字
		         例子：
			<#macro repeat count>
			  <#list 1..count as x>
			    <#nested x, x/2, x==count>
			  </#list>
			</#macro>
			<@repeat count=4 ; c, halfc, last>
			  ${c}. ${halfc}<#if last> Last!</#if>
			</@repeat>
		*/
        if (loopVars.length != 0) {
            throw new TemplateModelException(
                    "This directive doesn't allow loop variables.");
        }
        // 指令内容体没有任何内容的
        if (body == null) {
            SimpleScalar timeScalar = (SimpleScalar) params.get("time");
            String time = timeScalar.getAsString();
            String ftime = StringUtils.EMPTY;
            if (!Strings.isNullOrEmpty(time)) {
                final DateTime p_date = DateTime.parse(time, DateFormatter.DTP_YYYY_MM_DD_HH_MM_SS);
                final DateTime today = DateTime.now();
                Days d = Days.daysBetween(p_date, today);
                int days = d.getDays();
                if (days == 0) {
                    int hour = (int) ((today.getMillis() - p_date.getMillis()) / 3600000);
                    if (hour == 0)
                        ftime = Math.max((today.getMillis() - p_date.getMillis()) / 60000, 1) + "分钟前";
                    else
                        ftime = hour + "小时前";
                } else if (days == 1) {
                    ftime = "昨天";
                } else if (days == 2) {
                    ftime = "前天";
                } else if (days > 2 && days <= 10) {
                    ftime = days + "天前";
                } else {
                    ftime = p_date.toString(DateFormatter.YYYY_MM_DD);
                }
            }
            Writer out = env.getOut();
            out.write(ftime);
        } else {
            throw new RuntimeException("missing body");
        }
    }

}
