/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.test.ci;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import goja.logging.Logger;

/**
 * <p>
 * .
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-02-24 16:51
 * @since JDK 1.6
 */
public class ProfilerInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation ai) {
        try {
            String actionKey = ai.getActionKey();
            Profiler.start("profiler " + actionKey + " start...");

            ai.invoke();

        } finally {
            Profiler.release();
            Logger.info(Profiler.dump());
        }
    }
}
