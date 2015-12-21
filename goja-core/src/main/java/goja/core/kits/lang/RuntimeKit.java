/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.kits.lang;

/**
 * Runtime utilities.
 */
public class RuntimeKit {

    public static final Runtime RUNTIME = Runtime.getRuntime();

    // ---------------------------------------------------------------- execution

    /**
     * Returns current method signature.
     */
    public static String currentMethod() {
        StackTraceElement[] ste = new Exception().getStackTrace();
        int ndx = (ste.length > 1) ? 1 : 0;
        return new Exception().getStackTrace()[ndx].toString();
    }

    // ---------------------------------------------------------------- memory

    /**
     * Returns the amount of available memory (free memory plus never allocated memory).
     */
    public static long availableMemory() {
        return RUNTIME.freeMemory() + (RUNTIME.maxMemory() - RUNTIME.totalMemory());
    }

    /**
     * Returns the amount of available memory in percents.
     */
    public static float availableMemoryPercent() {
        return availableMemory() * 100.0f / RUNTIME.maxMemory();
    }

    /**
     * Compacts memory as much as possible by allocating huge memory block
     * and then forcing garbage collection.
     */
    public static void compactMemory() {
        try {
            final byte[][] unused = new byte[128][];
            for (int i = unused.length; i-- != 0; ) {
                unused[i] = new byte[2000000000];
            }
        } catch (OutOfMemoryError ignore) {
        }
        System.gc();
    }

    // ---------------------------------------------------------------- location

    /**
     * Returns location of the class. If class is not in a jar, it's classpath
     * is returned; otherwise the jar location.
     */
    public static String classLocation(Class clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    /**
     * Returns jodd location.
     *
     * @see #classLocation
     */
    public static String joddLocation() {
        return classLocation(RuntimeKit.class);
    }

}
