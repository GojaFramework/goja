/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.mvc.controller;

import goja.core.StringPool;
import goja.core.app.GojaConfig;
import goja.core.kits.reflect.ClassPathScanning;
import com.jfinal.config.Routes;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class ControllerBindRoutes extends Routes {

    private static final Logger logger = LoggerFactory.getLogger(ControllerBindRoutes.class);

    private static final String CONTROLLER_SUFFIX = "Controller";


    private static String controllerKey(Class<? extends Controller> clazz) {
        final String simpleName = clazz.getSimpleName();


        Preconditions.checkArgument(simpleName.endsWith(CONTROLLER_SUFFIX),
                " does not has a @ControllerBind annotation and it's name is not end with " + CONTROLLER_SUFFIX);
        // 得到 /helloController
        String controllerKey = StringPool.SLASH + StrKit.firstCharToLowerCase(simpleName);
        // 得到 /hello
        controllerKey = controllerKey.substring(0, controllerKey.indexOf(CONTROLLER_SUFFIX));


        String packName = clazz.getPackage().getName();

        final String controllersFlag = "controllers";

        final List<String> appScans = GojaConfig.getAppScans();

        for (String appScan : appScans) {
            if (StringUtils.startsWith(packName, appScan)) {

                // 增加一些新的路由机制
                // prefix        com.mo008
                // controller1   com.mo008.controllers
                // controller2   com.mo008.sys.controllers
                // controller3   com.mo008.sys.controllers.admin


                //  com.mo008.controllers.HelloController               -> /hello
                //  com.mo008.sys.controllers.HelloController           -> /sys/hello
                //  com.mo008.sys.controllers.admin.HelloController     -> sys/admin/hello
                //  com.mo008.sys.controllers.admin.me.HelloController  -> sys/admin/me/hello
                final String removePrefixPack     = StringUtils.replace(packName, appScan, StringPool.EMPTY);
                final String removeControllerPack = StringUtils.replace(removePrefixPack, StringPool.DOT + controllersFlag, StringPool.EMPTY);
                return StringUtils.replace(removeControllerPack, StringPool.DOT, StringPool.SLASH) + controllerKey;
            }
        }

        return null;

    }

    @Override
    public void config() {
        final Set<Class<? extends Controller>> controllerList = ClassPathScanning.scan(Controller.class);
        if (CollectionUtils.isNotEmpty(controllerList)) {
            ControllerBind controllerBind;
            for (Class<? extends Controller> controller : controllerList) {
                controllerBind = controller.getAnnotation(ControllerBind.class);
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
