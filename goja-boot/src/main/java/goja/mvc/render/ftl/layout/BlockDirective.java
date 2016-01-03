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
 * 定义一个被填充的模板自定义指令，一般在模板中使用，表示这个区域将要被子画面给填充掉.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-04-20 上午7:36
 * @since JDK 1.5
 */
public class BlockDirective implements TemplateDirectiveModel {
    /**
     * 自定义指令名称
     */
    public final static String DIRECTIVE_NAME = "block";

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env,
                        Map params, TemplateModel[] loopVars,
                        TemplateDirectiveBody body) throws TemplateException, IOException {
        String name = DirectiveKit.getRequiredParam(params, "name");
        TemplateDirectiveBodyOverrideWraper overrideBody = DirectiveKit.getOverrideBody(env, name);
        if (overrideBody == null) {
            if (body != null) {
                body.render(env.getOut());
            }
        } else {
            DirectiveKit.setTopBodyForParentBody(
                    new TemplateDirectiveBodyOverrideWraper(body, env),
                    overrideBody);
            overrideBody.render(env.getOut());
        }
    }

}
