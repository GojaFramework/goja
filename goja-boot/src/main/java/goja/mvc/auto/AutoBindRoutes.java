/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.mvc.auto;

import goja.core.StringPool;
import goja.core.annotation.ControllerBind;
import goja.core.app.GojaConfig;
import goja.initialize.ctxbox.ClassBox;
import goja.initialize.ctxbox.ClassType;
import com.jfinal.config.Routes;
import com.jfinal.kit.StrKit;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings("unchecked")
public class AutoBindRoutes extends Routes {

    private static final Logger logger = LoggerFactory.getLogger(AutoBindRoutes.class);


    private static String controllerKey(Class clazz) {
        final String simpleName = clazz.getSimpleName();

        String controllerSuffix = "Controller";
        Preconditions.checkArgument(simpleName.endsWith(controllerSuffix),
                " does not has a @ControllerBind annotation and it's name is not end with " + controllerSuffix);
        // 得到 /helloController
        String controllerKey = StringPool.SLASH + StrKit.firstCharToLowerCase(simpleName);
        // 得到 /hello
        controllerKey = controllerKey.substring(0, controllerKey.indexOf(controllerSuffix));


        String packName = clazz.getPackage().getName();

        final String controllersFlag = "controllers";

        final String appPackPrefix = GojaConfig.getAppPackPrefix();


        if (StringUtils.startsWith(packName, appPackPrefix)) {

            // 增加一些新的路由机制
            // prefix        com.mo008
            // controller1   com.mo008.controllers
            // controller2   com.mo008.sys.controllers
            // controller3   com.mo008.sys.controllers.admin


            //  com.mo008.controllers.HelloController               -> /hello
            //  com.mo008.sys.controllers.HelloController           -> /sys/hello
            //  com.mo008.sys.controllers.admin.HelloController     -> sys/admin/hello
            //  com.mo008.sys.controllers.admin.me.HelloController  -> sys/admin/me/hello
            final String removePrefixPack = StringUtils.replace(packName, appPackPrefix, StringPool.EMPTY);
            final String removeControllerPack = StringUtils.replace(removePrefixPack, StringPool.DOT + controllersFlag, StringPool.EMPTY);
            return StringUtils.replace(removeControllerPack, StringPool.DOT, StringPool.SLASH) + controllerKey;
        } else {
            return null;
        }
    }

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
}
