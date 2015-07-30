/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc.error;

import com.jfinal.render.IErrorRenderFactory;
import com.jfinal.render.Render;

/**
 * <p>
 * .
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-11-08 20:48
 * @since JDK 1.6
 */
public class GojaErrorRenderFactory implements IErrorRenderFactory {


    @Override
    public Render getRender(int errorCode, String view) {
        return new GojaErrorRender(errorCode, view);
    }
}
