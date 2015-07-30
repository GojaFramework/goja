/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc.render.ftl.layout;

import freemarker.cache.TemplateCache;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import goja.StringPool;
import goja.mvc.render.ftl.kit.DirectiveKit;

import java.io.IOException;
import java.util.Map;

/**
 * <p>
 * 定义模板继承的自定义指令.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-04-20 上午7:36
 * @since JDK 1.5
 */
public class ExtendsDirective implements TemplateDirectiveModel {
    /**
     * 自定义指令名称
     */
    public final static String DIRECTIVE_NAME = "extends";

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params,
                        TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        String name = DirectiveKit.getRequiredParam(params, "name");
        params.remove("name");

        if (!name.endsWith(".ftl")) {
            name = name + ".ftl";
        }
        String encoding = DirectiveKit.getParam(params, "encoding", StringPool.UTF_8);
        String includeTemplateName = TemplateCache.getFullTemplatePath(env, getTemplatePath(env), name);
        Configuration configuration = env.getConfiguration();
        final Template template = configuration.getTemplate(includeTemplateName, env.getLocale(), encoding, true);
        for (Object key : params.keySet()) {
            TemplateModel paramModule = new SimpleScalar(params.get(key).toString());
            env.setVariable(key.toString(), paramModule);
        }
        env.include(template);
    }

    /**
     * 取得模板路径的地址
     *
     * @param env Freemarker的运行环境
     * @return 模板路径地址
     */
    private String getTemplatePath(Environment env) {
        String templateName = env.getTemplate().getName();
        return templateName.lastIndexOf('/') == -1 ? "" : templateName.substring(0, templateName.lastIndexOf('/') + 1);
    }

}
