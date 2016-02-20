/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc.render.ftl.kit;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

import java.io.IOException;
import java.io.Writer;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-11-01 21:58
 * @since JDK 1.6
 */
public class TemplateDirectiveBodyOverrideWraper implements TemplateDirectiveBody, TemplateModel {

    /**
     * 运行环境
     */
    public final Environment env;
    /**
     * 当前内容渲染
     */
    private final TemplateDirectiveBody body;

    /**
     * 内容渲染包装器
     */
    public TemplateDirectiveBodyOverrideWraper parentBody;

    /**
     * 构造一个包装器
     *
     * @param body 内容渲染
     * @param env  运行环境
     */
    public TemplateDirectiveBodyOverrideWraper(TemplateDirectiveBody body,
                                               Environment env) {
        super();
        this.body = body;
        this.env = env;
    }

    @Override
    public void render(Writer out) throws TemplateException, IOException {
        if (body == null) return;
        TemplateDirectiveBodyOverrideWraper preOverridy = (TemplateDirectiveBodyOverrideWraper) env.getVariable(DirectiveKit.OVERRIDE_CURRENT_NODE);
        try {
            env.setVariable(DirectiveKit.OVERRIDE_CURRENT_NODE, this);
            body.render(out);
        } finally {
            env.setVariable(DirectiveKit.OVERRIDE_CURRENT_NODE, preOverridy);
        }
    }
}
