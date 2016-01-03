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

import com.jfinal.config.Constants;
import com.jfinal.config.JFinalConfig;
import com.jfinal.handler.Handler;
import com.jfinal.handler.HandlerFactory;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;
import com.jfinal.render.RenderFactory;
import com.jfinal.server.IServer;
import com.jfinal.server.ServerFactory;
import com.jfinal.token.ITokenCache;
import com.jfinal.token.TokenManager;
import com.jfinal.upload.OreillyCos;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * JFinal
 */
public final class JFinal {

    private static final JFinal me = new JFinal();
    private static IServer server;
    private Constants constants;
    private ActionMapping actionMapping;
    private Handler handler;
    private ServletContext servletContext;
    private String contextPath = "";

    private JFinal() {
    }

    public static JFinal me() {
        return me;
    }

    public static void start() {
        server = ServerFactory.getServer();
        server.start();
    }

    public static void start(String webAppDir, int port, String context, int scanIntervalSeconds) {
        server = ServerFactory.getServer(webAppDir, port, context, scanIntervalSeconds);
        server.start();
    }

    public static void stop() {
        server.stop();
    }

    /**
     * Run JFinal Server with Debug Configurations or Run Configurations in Eclipse JavaEE
     * args example: WebRoot 80 / 5
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            server = ServerFactory.getServer();
            server.start();
        } else {
            String webAppDir = args[0];
            int port = Integer.parseInt(args[1]);
            String context = args[2];
            int scanIntervalSeconds = Integer.parseInt(args[3]);
            server = ServerFactory.getServer(webAppDir, port, context, scanIntervalSeconds);
            server.start();
        }
    }

    boolean init(JFinalConfig jfinalConfig, ServletContext servletContext) {
        this.servletContext = servletContext;
        this.contextPath = servletContext.getContextPath();

        initPathUtil();

        Config.configJFinal(jfinalConfig);  // start plugin and init log factory in this method
        constants = Config.getConstants();

        initActionMapping();
        initHandler();
        initRender();
        initOreillyCos();
        initTokenManager();

        return true;
    }

    private void initTokenManager() {
        ITokenCache tokenCache = constants.getTokenCache();
        if (tokenCache != null) {
            TokenManager.init(tokenCache);
        }
    }

    private void initHandler() {
        Handler actionHandler = new ActionHandler(actionMapping, constants);
        handler = HandlerFactory.getHandler(Config.getHandlers().getHandlerList(), actionHandler);
    }

    private void initOreillyCos() {
        OreillyCos.init(constants.getBaseUploadPath(), constants.getMaxPostSize(),
                constants.getEncoding());
    }

    private void initPathUtil() {
        String path = servletContext.getRealPath("/");
        PathKit.setWebRootPath(path);
    }

    private void initRender() {
        RenderFactory.me().init(constants, servletContext);
    }

    private void initActionMapping() {
        actionMapping = new ActionMapping(Config.getRoutes(), Config.getInterceptors());
        actionMapping.buildActionMapping();
    }

    void stopPlugins() {
        List<IPlugin> plugins = Config.getPlugins().getPluginList();
        if (plugins != null) {
            for (int i = plugins.size() - 1; i >= 0; i--) {    // stop plugins
                boolean success = false;
                try {
                    success = plugins.get(i).stop();
                } catch (Exception e) {
                    success = false;
                    LogKit.error(e.getMessage(), e);
                }
                if (!success) {
                    System.err.println("Plugin stop error: " + plugins.get(i).getClass().getName());
                }
            }
        }
    }

    Handler getHandler() {
        return handler;
    }

    public Constants getConstants() {
        return Config.getConstants();
    }

    public String getContextPath() {
        return contextPath;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public Action getAction(String url, String[] urlPara) {
        return actionMapping.getAction(url, urlPara);
    }

    public List<String> getAllActionKeys() {
        return actionMapping.getAllActionKeys();
    }
}










