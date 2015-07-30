/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc.render.ftl.layout;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import goja.mvc.render.ftl.kit.DirectiveKit;
import goja.mvc.render.ftl.kit.TemplateDirectiveBodyOverrideWraper;

import java.io.IOException;
import java.util.Map;

/**
 * <p>
 * Freemarker的覆盖自定义标记，用来覆盖模板中的指定区域.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-04-20 上午7:32
 * @since JDK 1.5
 */
public class OverrideDirective implements TemplateDirectiveModel {
    /** 覆盖模板的自定义指令名称 */
    public final static String DIRECTIVE_NAME = "override";

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars,
                        TemplateDirectiveBody body) throws TemplateException, IOException {
        String name = DirectiveKit.getRequiredParam(params, "name");
        String overrideVariableName = DirectiveKit.getOverrideVariableName(name);

        TemplateDirectiveBodyOverrideWraper override = DirectiveKit.getOverrideBody(env, name);
        TemplateDirectiveBodyOverrideWraper current = new TemplateDirectiveBodyOverrideWraper(body, env);
        if (override == null) {
            env.setVariable(overrideVariableName, current);
        } else {
            DirectiveKit.setTopBodyForParentBody(current, override);
        }
    }

}
