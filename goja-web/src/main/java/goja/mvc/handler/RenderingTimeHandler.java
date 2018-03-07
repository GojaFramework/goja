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
package goja.rapid.mvc.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;
import com.jfinal.log.Log;

/**
 * <a href = http://www.oschina.net/question/173052_62229 />
 */
public class RenderingTimeHandler extends Handler {

    protected final Log logger = Log.getLog(getClass());

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        long start = System.currentTimeMillis();
        next.handle(target, request, response, isHandled);
        long end = System.currentTimeMillis();
        logger.debug("rending time:" + (end - start) + "ms");
    }

}