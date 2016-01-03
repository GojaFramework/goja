/**
 * Copyright (c) 2011-2016, James Zhan 詹波 (jfinal@126.com).
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.jfinal.core;

import com.jfinal.config.*;
import com.jfinal.plugin.IPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class Config {

    private static final Constants constants = new Constants();
    private static final Routes routes = new Routes() {
        public void config() {
        }
    };
    private static final Plugins plugins = new Plugins();
    private static final Interceptors interceptors = new Interceptors();
    private static final Handlers handlers = new Handlers();
    //	private static Log log;

    private static final Logger log = LoggerFactory.getLogger(Config.class);

    // prevent new Config();
    private Config() {
    }

    /*
     * Config order: constant, route, plugin, interceptor, handler
     */
    static void configJFinal(JFinalConfig jfinalConfig) {
        jfinalConfig.configConstant(constants);
        //		initLogFactory();
        jfinalConfig.configRoute(routes);
        jfinalConfig.configPlugin(plugins);
        startPlugins();  // very important!!!
        jfinalConfig.configInterceptor(interceptors);
        jfinalConfig.configHandler(handlers);
    }

    public static final Constants getConstants() {
        return constants;
    }

    public static final Routes getRoutes() {
        return routes;
    }

    public static final Plugins getPlugins() {
        return plugins;
    }

    public static final Interceptors getInterceptors() {
        return interceptors;
    }

    public static Handlers getHandlers() {
        return handlers;
    }

    private static void startPlugins() {
        List<IPlugin> pluginList = plugins.getPluginList();
        if (pluginList == null) {
            return;
        }

        for (IPlugin plugin : pluginList) {
            try {
                // process ActiveRecordPlugin devMode
                if (plugin instanceof com.jfinal.plugin.activerecord.ActiveRecordPlugin) {
                    com.jfinal.plugin.activerecord.ActiveRecordPlugin arp =
                            (com.jfinal.plugin.activerecord.ActiveRecordPlugin) plugin;
                    if (arp.getDevMode() == null) {
                        arp.setDevMode(constants.getDevMode());
                    }
                }

                if (plugin.start() == false) {
                    String message = "Plugin start error: " + plugin.getClass().getName();
                    log.error(message);
                    throw new RuntimeException(message);
                }
            } catch (Exception e) {
                String message =
                        "Plugin start error: " + plugin.getClass().getName() + ". \n" + e.getMessage();
                log.error(message, e);
                throw new RuntimeException(message, e);
            }
        }
    }
}
