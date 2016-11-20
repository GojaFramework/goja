/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc.render.ftl.shiro.permission;

/**
 * <p> . </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-10-27 10:39 AM
 * @since JDK 1.5
 */
public class LacksPermissionTag extends PermissionTag {
    @Override
    protected boolean showTagBody(String p) {
        return !isPermitted(p);
    }
}
