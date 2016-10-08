/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.initialize.ctxbox;

/**
 * <p> The Class type . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-01-19 22:13
 * @since JDK 1.6
 */
public enum ClassType {

    /**
     * Controller Class
     */
    CONTROLLER,

    /**
     * Database activiteReocrd Class
     */
    MODEL,
    /**
     * Crob Job Class
     */
    JOB,
    /**
     * Interceptor System
     */
    AOP,

    /**
     * The System Event.
     */
    //EVENT,
    /**
     * THE Quartz job class.
     */
    QUARTZ,
    /**
     * The plugin class.
     */
    PLUGIN,
    //    /**
    //     * The handle class.
    //     */
    //    HANDLER,
    /**
     * Syslog Analysis
     */
    LOGPERCESSOR,

    /**
     * The shiro security data.
     */
    SECURITY_DATA
}
