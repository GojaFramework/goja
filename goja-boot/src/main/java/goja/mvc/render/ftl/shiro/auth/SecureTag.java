/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc.render.ftl.shiro.auth;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * <p>
 * 基本权限验证.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-10-27 10:29 AM
 * @since JDK 1.5
 */
public abstract class SecureTag implements TemplateDirectiveModel {

    /** 日志信息 */
    protected final Logger _logger;

    /** 构造函数，初始化日志 */
    protected SecureTag() {
        _logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        //检查参数
        verifyParameters(params);
        verifyLoopVars(loopVars);
        render(env, params, body);
    }

    /**
     * 检查循环变量
     * <p>
     * 循环变量：用户定义指令可以有循环变量，通常用于重复嵌套内容，
     * 基本用法是：作为nested指令的参数传递循环变量的实际值，而在调用用户定义指令时，在${"<@…>"}开始标记的参数后面指定循环变量的名字
     * </p>
     *
     * @param loopVars 循环变量参数
     */
    protected void verifyLoopVars(TemplateModel[] loopVars) throws TemplateModelException {
        if (loopVars.length != 0) {
            throw new TemplateModelException("Shiro directive doesn't allow loop variables!");
        }
    }

    public abstract void render(Environment env, Map params, TemplateDirectiveBody body) throws IOException, TemplateException;

    protected String getParam(Map params, String name) {
        Object value = params.get(name);

        if (value instanceof SimpleScalar) {
            return ((SimpleScalar) value).getAsString();
        }

        return null;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    protected Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    /**
     * 检验参数
     *
     * @param params 参数信息
     * @throws freemarker.template.TemplateModelException 参数异常
     */
    protected void verifyParameters(Map params) throws TemplateModelException {
    }

    protected void renderBody(Environment env, TemplateDirectiveBody body) throws IOException, TemplateException {
        if (body != null) {
            body.render(env.getOut());
        }
    }
}
