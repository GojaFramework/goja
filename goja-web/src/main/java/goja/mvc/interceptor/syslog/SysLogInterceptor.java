/**
 * Copyright (c) 2011-2013, kidzhou 周磊 (zhouleib1412@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package goja.mvc.interceptor.syslog;

import com.google.common.collect.Maps;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import goja.mvc.kits.Requests;


public class SysLogInterceptor implements Interceptor {
    private static final Map<String, LogConfig> acitonLogs = Maps.newHashMap();
    private LogProcessor logProcesser;

    public SysLogInterceptor setLogProcesser(LogProcessor logProcesser) {
        this.logProcesser = logProcesser;
        return this;
    }

    public SysLogInterceptor addConfig(String actionKey, LogConfig log) {
        acitonLogs.put(actionKey, log);
        return this;
    }

    @Override
    public void intercept(Invocation ai) {
        String actionKey = ai.getActionKey();
        Controller c = ai.getController();
        LogConfig log = acitonLogs.get(actionKey);
        if (log != null) {
            logFromConfig(c, log);
        }
        ai.invoke();
    }

    private void logFromConfig(Controller c, LogConfig log) {
        SysLog sysLog = new SysLog();
        final HttpServletRequest request = c.getRequest();
        final String remoteIP = Requests.remoteIP(request);
        sysLog.setIp(remoteIP);
        sysLog.setUser(logProcesser.getUsername(c));
        Map<String, String[]> parameterMap = request.getParameterMap();
        Set<Entry<String, String[]>> entrySet = parameterMap.entrySet();
        Map<String, String> paraMap = Maps.newHashMap();
        for (Entry<String, String[]> entry : entrySet) {
            String key = entry.getKey();
            String value = entry.getValue()[0];
            String result = log.params.get(key);
            if (StrKit.isBlank(result))
                continue;
            paraMap.put(result, value);
        }
        final String message = logProcesser.formatMessage(log.title, paraMap);
        sysLog.setMessage(message);
        sysLog.setTitle(log.getTitle());
        logProcesser.process(sysLog);
    }

}
