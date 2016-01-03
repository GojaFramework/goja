/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.kits.lang;

/**
 * <p>
 * Basic types of handling of null.
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-11-04 16:28
 * @since JDK 1.6
 */
@SuppressWarnings("UnnecessaryUnboxing")
public class DataKit {

    /**
     * Returns the primitve representation of the object, false if the object is null
     *
     * @param b - the object representation
     * @return the primitive representation
     */
    public static boolean getBoolean(Boolean b) {
        return b != null && b.booleanValue();
    }

    /**
     * Returns the primitve representation of the object, 0 if the object is null
     *
     * @param b - the object representation
     * @return the primitive representation
     */
    public static byte getByte(Byte b) {
        return b == null ? 0 : b.byteValue();
    }

    /**
     * Returns the primitve representation of the object, 0 if the object is null
     *
     * @param d - the object representation
     * @return the primitive representation
     */
    public static double getDouble(Double d) {
        return d == null ? 0 : d.doubleValue();
    }

    /**
     * Returns the primitve representation of the object, 0 if the object is null
     *
     * @param f - the object representation
     * @return the primitive representation
     */
    public static float getFloat(Float f) {
        return f == null ? 0 : f.floatValue();
    }

    /**
     * Returns the primitve representation of the object, 0 if the object is null
     *
     * @param i - the object representation
     * @return the primitive representation
     */
    public static int getInt(Integer i) {
        return i == null ? 0 : i.intValue();
    }

    /**
     * Returns the primitve representation of the object, 0 if the object is null
     *
     * @param l - the object representation
     * @return the primitive representation
     */
    public static long getLong(Long l) {
        return l == null ? 0 : l.longValue();
    }

    /**
     * Returns the primitve representation of the object, 0 if the object is null
     *
     * @param i - the object representation
     * @return the primitive representation
     */
    public static long getLong(Integer i) {
        return (i == null) ? 0 : i.longValue();
    }

    /**
     * Converts packing Long Int
     *
     * @param l the object representation
     * @return the primitive representation
     */
    public static int getInt(Long l) {
        return (l == null || l > Integer.MAX_VALUE) ? 0 : l.intValue();
    }
}
