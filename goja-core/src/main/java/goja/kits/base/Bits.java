/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.kits.base;

/**
 * Part a copy of <code>java.io.Bits</code>, which is for unknown reason package local.
 * Utility methods for packing/unpacking primitive values in/out of byte arrays
 * using big-endian byte ordering.
 */
public class Bits {

    public static boolean getBoolean(byte[] b, int off) {
        return b[off] != 0;
    }

    public static char getChar(byte[] b, int off) {
        return (char) (((b[off + 1] & 0xFF)) +
                ((b[off]) << 8));
    }

    public static short getShort(byte[] b, int off) {
        return (short) (((b[off + 1] & 0xFF)) +
                ((b[off]) << 8));
    }

    public static int getInt(byte[] b, int off) {
        return ((b[off + 3] & 0xFF)) +
                ((b[off + 2] & 0xFF) << 8) +
                ((b[off + 1] & 0xFF) << 16) +
                ((b[off]) << 24);
    }

    public static float getFloat(byte[] b, int off) {
        int i = getInt(b, off);
        return Float.intBitsToFloat(i);
    }

    public static long getLong(byte[] b, int off) {
        return ((b[off + 7] & 0xFFL)) +
                ((b[off + 6] & 0xFFL) << 8) +
                ((b[off + 5] & 0xFFL) << 16) +
                ((b[off + 4] & 0xFFL) << 24) +
                ((b[off + 3] & 0xFFL) << 32) +
                ((b[off + 2] & 0xFFL) << 40) +
                ((b[off + 1] & 0xFFL) << 48) +
                (((long) b[off]) << 56);
    }

    public static double getDouble(byte[] b, int off) {
        long j = getLong(b, off);
        return Double.longBitsToDouble(j);
    }

    public static void putBoolean(byte[] b, int off, boolean val) {
        b[off] = (byte) (val ? 1 : 0);
    }

    public static void putChar(byte[] b, int off, char val) {
        b[off + 1] = (byte) (val);
        b[off] = (byte) (val >>> 8);
    }

    public static void putShort(byte[] b, int off, short val) {
        b[off + 1] = (byte) (val);
        b[off] = (byte) (val >>> 8);
    }

    public static void putInt(byte[] b, int off, int val) {
        b[off + 3] = (byte) (val);
        b[off + 2] = (byte) (val >>> 8);
        b[off + 1] = (byte) (val >>> 16);
        b[off] = (byte) (val >>> 24);
    }

    public static void putFloat(byte[] b, int off, float val) {
        int i = Float.floatToIntBits(val);
        putInt(b, off, i);
    }

    public static void putLong(byte[] b, int off, long val) {
        b[off + 7] = (byte) (val);
        b[off + 6] = (byte) (val >>> 8);
        b[off + 5] = (byte) (val >>> 16);
        b[off + 4] = (byte) (val >>> 24);
        b[off + 3] = (byte) (val >>> 32);
        b[off + 2] = (byte) (val >>> 40);
        b[off + 1] = (byte) (val >>> 48);
        b[off] = (byte) (val >>> 56);
    }

    public static void putDouble(byte[] b, int off, double val) {
        long j = Double.doubleToLongBits(val);
        putLong(b, off, j);
    }

    // ---------------------------------------------------------------- mask

    public static boolean isSet(final byte value, final byte mask) {
        return (value & mask) == mask;
    }

    public static boolean isSet(final int value, final int mask) {
        return (value & mask) == mask;
    }

    public static boolean notSet(final int value, final int mask) {
        return (value & mask) != mask;
    }

    /**
     * Returns value with the bit corresponding to the mask set
     * (if setBit is true) or cleared (if setBit is false).
     */
    public static int set(int value, int mask, boolean setBit) {
        return setBit ? value | mask : value & ~mask;
    }

    /**
     * Returns value with the bit corresponding to the mask set
     * (if setBit is true) or cleared (if setBit is false).
     */
    public static byte set(byte value, byte mask, boolean setBit) {
        return (byte) (setBit ? value | mask : value & ~mask);
    }

}