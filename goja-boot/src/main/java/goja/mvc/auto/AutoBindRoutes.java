/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.mvc.auto;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.jfinal.config.Routes;
import com.jfinal.kit.StrKit;
import goja.core.StringPool;
import goja.annotation.ControllerBind;
import goja.core.app.GojaConfig;
import goja.core.app.GojaPropConst;
import goja.initialize.ctxbox.ClassBox;
import goja.initialize.ctxbox.ClassType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings("unchecked")
public class AutoBindRoutes extends Routes {

    private static final Logger logger = LoggerFactory.getLogger(AutoBindRoutes.class);


    private static final String suffix = "Controller";

    @Override
    public void config() {
        List<Class> controllerClasses = ClassBox.getInstance().getClasses(ClassType.CONTROLLER);
        if (controllerClasses != null && !controllerClasses.isEmpty()) {
            ControllerBind controllerBind;
            for (Class controller : controllerClasses) {
                controllerBind = (ControllerBind) controller.getAnnotation(ControllerBind.class);
                if (controllerBind == null) {
                    final String controllerKey = controllerKey(controller);
                    if (Strings.isNullOrEmpty(controllerKey)) {
                        logger.warn("控制器类{},路由生成失败!", controller.getName());
                        continue;
                    }
                    this.add(controllerKey, controller);
                } else if (StrKit.isBlank(controllerBind.viewPath())) {
                    this.add(controllerBind.value(), controller);
                } else {
                    this.add(controllerBind.value(), controller, controllerBind.viewPath());
                }
            }
        }
    }

    private static String controllerKey(Class clazz) {
        final String simpleName = clazz.getSimpleName();
        Preconditions.checkArgument(simpleName.endsWith(suffix), " does not has a @ControllerBind annotation and it's name is not end with " + suffix);
        String controllerKey = StringPool.SLASH + StrKit.firstCharToLowerCase(simpleName);
        controllerKey = controllerKey.substring(0, controllerKey.indexOf(suffix));
        String packName = clazz.getPackage().getName();

        // 增加一种新的路由机制
        // prefix       com.mo008
        // controller   com.mo008.controllers
        // controller   com.mo008.sys.controllers
        // controller2  com.mo008.member.controllers
        final String packPrefix = GojaConfig.getProperty(GojaPropConst.APP_PACKAGE_PREFIX, "app");
        if (StringUtils.startsWith(packName, packPrefix)) {
            // 如果是配置的包,则进行路由生成
            if (StringUtils.startsWith(packName, packPrefix + StringPool.DOT + "controllers")) { // 包名正好为 com.mo008.controllers 开头
                if (StringUtils.endsWith(packName, "controllers")) {
                    return controllerKey;
                } else {
                    String biz_pk = StringUtils.substringAfter(packName, ".controllers.");
                    controllerKey = StringPool.SLASH
                            + biz_pk.replace(StringPool.DOT, StringPool.SLASH)
                            + controllerKey;
                    return controllerKey;
                }
            } else {
                // 取得中间的信息
                String moduleRoute = StringUtils.replace(packName.replace(packName, packPrefix + StringPool.DOT)
                        .replace(".controllers", StringPool.EMPTY), StringPool.DOT, StringPool.SLASH); //得到中间区域的配置
                // sys/user
                if (StringUtils.endsWith(packName, "controllers")) {
                    return StringPool.SLASH + moduleRoute + controllerKey;
                } else {
                    String biz_pk = StringUtils.substringAfter(packName, ".controllers.");
                    controllerKey = StringPool.SLASH
                            + moduleRoute
                            + biz_pk.replace(StringPool.DOT, StringPool.SLASH)
                            + controllerKey;
                    return controllerKey;
                }

            }
        } else {
            return null;
        }


    }


}
