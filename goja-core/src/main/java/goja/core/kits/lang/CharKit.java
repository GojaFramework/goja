/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.kits.lang;

import goja.core.StringPool;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;

/**
 * <p>
 * .
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-01-31 17:51
 * @since JDK 1.6
 */
public class CharKit {

    // ---------------------------------------------------------------- simple

    /**
     * Converts (signed) byte to (unsigned) char.
     */
    public static char toChar(byte b) {
        return (char) (b & 0xFF);
    }

    /**
     * Converts char array into byte array by stripping the high byte of each character.
     */
    public static byte[] toSimpleByteArray(char[] carr) {
        byte[] barr = new byte[carr.length];
        for (int i = 0; i < carr.length; i++) {
            barr[i] = (byte) carr[i];
        }
        return barr;
    }

    /**
     * Converts char sequence into byte array.
     *
     * @see #toSimpleByteArray(char[])
     */
    public static byte[] toSimpleByteArray(CharSequence charSequence) {
        byte[] barr = new byte[charSequence.length()];
        for (int i = 0; i < barr.length; i++) {
            barr[i] = (byte) charSequence.charAt(i);
        }
        return barr;
    }

    /**
     * Converts byte array to char array by simply extending bytes to chars.
     */
    public static char[] toSimpleCharArray(byte[] barr) {
        char[] carr = new char[barr.length];
        for (int i = 0; i < barr.length; i++) {
            carr[i] = (char) (barr[i] & 0xFF);
        }
        return carr;
    }

    // ---------------------------------------------------------------- ascii

    /**
     * Returns ASCII value of a char. In case of overload, 0x3F is returned.
     */
    public static int toAscii(char c) {
        if (c <= 0xFF) {
            return c;
        } else {
            return 0x3F;
        }
    }

    /**
     * Converts char array into {@link #toAscii(char) ASCII} array.
     */
    public static byte[] toAsciiByteArray(char[] carr) {
        byte[] barr = new byte[carr.length];
        for (int i = 0; i < carr.length; i++) {
            barr[i] = (byte) ((int) (carr[i] <= 0xFF ? carr[i] : 0x3F));
        }
        return barr;
    }

    /**
     * Converts char sequence into ASCII byte array.
     */
    public static byte[] toAsciiByteArray(CharSequence charSequence) {
        byte[] barr = new byte[charSequence.length()];
        for (int i = 0; i < barr.length; i++) {
            char c = charSequence.charAt(i);
            barr[i] = (byte) ((int) (c <= 0xFF ? c : 0x3F));
        }
        return barr;
    }

    // ---------------------------------------------------------------- raw arrays

    /**
     * Converts char array into byte array by replacing each character with two bytes.
     */
    public static byte[] toRawByteArray(char[] carr) {
        byte[] barr = new byte[carr.length << 1];
        for (int i = 0, bpos = 0; i < carr.length; i++) {
            char c = carr[i];
            barr[bpos++] = (byte) ((c & 0xFF00) >> 8);
            barr[bpos++] = (byte) (c & 0x00FF);
        }
        return barr;
    }

    public static char[] toRawCharArray(byte[] barr) {
        int carrLen = barr.length >> 1;
        if (carrLen << 1 < barr.length) {
            carrLen++;
        }
        char[] carr = new char[carrLen];
        int i = 0, j = 0;
        while (i < barr.length) {
            char c = (char) (barr[i] << 8);
            i++;

            if (i != barr.length) {
                c += barr[i] & 0xFF;
                i++;
            }
            carr[j++] = c;
        }
        return carr;
    }

    // ---------------------------------------------------------------- encoding

    /**
     * Converts char array to byte array using default Jodd encoding.
     */
    public static byte[] toByteArray(char[] carr) throws UnsupportedEncodingException {
        return new String(carr).getBytes(StringPool.UTF_8);
    }

    /**
     * Converts char array to byte array using provided encoding.
     */
    public static byte[] toByteArray(char[] carr, String charset) throws UnsupportedEncodingException {
        return new String(carr).getBytes(charset);
    }

    /**
     * Converts byte array of default Jodd encoding to char array.
     */
    public static char[] toCharArray(byte[] barr) throws UnsupportedEncodingException {
        return new String(barr, StringPool.UTF_8).toCharArray();
    }

    /**
     * Converts byte array of specific encoding to char array.
     */
    public static char[] toCharArray(byte[] barr, String charset) throws UnsupportedEncodingException {
        return new String(barr, charset).toCharArray();
    }

    // ---------------------------------------------------------------- find


    /**
     * Match if one character equals to any of the given character.
     *
     * @return <code>true</code> if characters match any character from given array,
     * otherwise <code>false</code>
     */
    public static boolean equalsOne(char c, char[] match) {
        for (char aMatch : match) {
            if (c == aMatch) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds index of the first character in given array the matches any from the
     * given set of characters.
     *
     * @return index of matched character or -1
     */
    public static int findFirstEqual(char[] source, int index, char[] match) {
        for (int i = index; i < source.length; i++) {
            if (equalsOne(source[i], match) == true) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds index of the first character in given array the matches any from the
     * given set of characters.
     *
     * @return index of matched character or -1
     */
    public static int findFirstEqual(char[] source, int index, char match) {
        for (int i = index; i < source.length; i++) {
            if (source[i] == match) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Finds index of the first character in given array the differs from the
     * given set of characters.
     *
     * @return index of matched character or -1
     */
    public static int findFirstDiff(char[] source, int index, char[] match) {
        for (int i = index; i < source.length; i++) {
            if (equalsOne(source[i], match) == false) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds index of the first character in given array the differs from the
     * given set of characters.
     *
     * @return index of matched character or -1
     */
    public static int findFirstDiff(char[] source, int index, char match) {
        for (int i = index; i < source.length; i++) {
            if (source[i] != match) {
                return i;
            }
        }
        return -1;
    }

    // ---------------------------------------------------------------- is

    /**
     * Returns <code>true</code> if character is a white space (<= ' ').
     * White space definition is taken from String class (see: <code>trim()</code>).
     */
    public static boolean isWhitespace(char c) {
        return c <= ' ';
    }

    /**
     * Returns <code>true</code> if specified character is lowercase ASCII.
     * If user uses only ASCIIs, it is much much faster.
     */
    public static boolean isLowercaseAlpha(char c) {
        return (c >= 'a') && (c <= 'z');
    }

    /**
     * Returns <code>true</code> if specified character is uppercase ASCII.
     * If user uses only ASCIIs, it is much much faster.
     */
    public static boolean isUppercaseAlpha(char c) {
        return (c >= 'A') && (c <= 'Z');
    }

    public static boolean isAlphaOrDigit(char c) {
        return isDigit(c) || isAlpha(c);
    }

    public static boolean isWordChar(char c) {
        return isDigit(c) || isAlpha(c) || (c == '_');
    }

    public static boolean isPropertyNameChar(char c) {
        return isDigit(c) || isAlpha(c) || (c == '_') || (c == '.') || (c == '[') || (c == ']');
    }

    // ---------------------------------------------------------------- RFC

    /**
     * Indicates whether the given character is in the {@code ALPHA} set.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
     */
    public static boolean isAlpha(char c) {
        return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'));
    }

    /**
     * Indicates whether the given character is in the {@code DIGIT} set.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
     */
    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Indicates whether the given character is in the <i>gen-delims</i> set.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
     */
    public static boolean isGenericDelimiter(int c) {
        return c == ':' || c == '/' || c == '?' || c == '#' || c == '[' || c == ']' || c == '@';
    }

    /**
     * Indicates whether the given character is in the <i>sub-delims</i> set.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
     */
    protected static boolean isSubDelimiter(int c) {
        return c == '!' || c == '$' || c == '&' || c == '\'' || c == '(' || c == ')' || c == '*' || c == '+' ||
                c == ',' || c == ';' || c == '=';
    }

    /**
     * Indicates whether the given character is in the <i>reserved</i> set.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
     */
    protected static boolean isReserved(char c) {
        return isGenericDelimiter(c) || isReserved(c);
    }

    /**
     * Indicates whether the given character is in the <i>unreserved</i> set.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
     */
    protected static boolean isUnreserved(char c) {
        return isAlpha(c) || isDigit(c) || c == '-' || c == '.' || c == '_' || c == '~';
    }

    /**
     * Indicates whether the given character is in the <i>pchar</i> set.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
     */
    protected static boolean isPchar(char c) {
        return isUnreserved(c) || isSubDelimiter(c) || c == ':' || c == '@';
    }


    // ---------------------------------------------------------------- conversions

    /**
     * Uppers lowercase ASCII char.
     */
    public static char toUpperAscii(char c) {
        if (isLowercaseAlpha(c)) {
            c -= (char) 0x20;
        }
        return c;
    }


    /**
     * Lowers uppercase ASCII char.
     */
    public static char toLowerAscii(char c) {
        if (isUppercaseAlpha(c)) {
            c += (char) 0x20;
        }
        return c;
    }


    /**
     * @see #indexOfChars(String, String, int)
     */
    public static int indexOfChars(String string, String chars) {
        return indexOfChars(string, chars, 0);
    }

    /**
     * Returns the very first index of any char from provided string, starting from specified index offset.
     * Returns index of founded char, or <code>-1</code> if nothing found.
     */
    public static int indexOfChars(String string, String chars, int startindex) {
        int stringLen = string.length();
        int charsLen = chars.length();
        if (startindex < 0) {
            startindex = 0;
        }
        for (int i = startindex; i < stringLen; i++) {
            char c = string.charAt(i);
            for (int j = 0; j < charsLen; j++) {
                if (c == chars.charAt(j)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int indexOfChars(String string, char[] chars) {
        return indexOfChars(string, chars, 0);
    }

    /**
     * Returns the very first index of any char from provided string, starting from specified index offset.
     * Returns index of founded char, or <code>-1</code> if nothing found.
     */
    public static int indexOfChars(String string, char[] chars, int startindex) {
        int stringLen = string.length();
        for (int i = startindex; i < stringLen; i++) {
            char c = string.charAt(i);
            for (char aChar : chars) {
                if (c == aChar) {
                    return i;
                }
            }
        }
        return -1;
    }


    /**
     * Returns if string starts with given character.
     */
    public static boolean startsWithChar(String s, char c) {
        return s.length() != 0 && s.charAt(0) == c;
    }


    /**
     * Returns <code>true</code> if string contains only digits.
     */
    public static boolean containsOnlyDigits(String string) {
        int size = string.length();
        for (int i = 0; i < size; i++) {
            char c = string.charAt(i);
            if (!CharKit.isDigit(c)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Returns <code>true</code> if string {@link #containsOnlyDigits(String) contains only digits}
     * or signs plus or minus.
     */
    public static boolean containsOnlyDigitsAndSigns(String string) {
        int size = string.length();
        for (int i = 0; i < size; i++) {
            char c = string.charAt(i);
            if ((!CharKit.isDigit(c)) && (c != '-') && (c != '+')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Splits a string in several parts (tokens) that are separated by delimiter
     * characters. Delimiter may contains any number of character and it is
     * always surrounded by two strings.
     *
     * @param src source to examine
     * @param d   string with delimiter characters
     * @return array of tokens
     */
    public static String[] splitc(String src, String d) {
        if ((d.length() == 0) || (src.length() == 0)) {
            return new String[]{src};
        }
        char[] delimiters = d.toCharArray();
        char[] srcc = src.toCharArray();

        int maxparts = srcc.length + 1;
        int[] start = new int[maxparts];
        int[] end = new int[maxparts];

        int count = 0;

        start[0] = 0;
        int s = 0, e;
        if (CharKit.equalsOne(srcc[0], delimiters)) {    // string starts with delimiter
            end[0] = 0;
            count++;
            s = CharKit.findFirstDiff(srcc, 1, delimiters);
            if (s == -1) {                            // nothing after delimiters
                return new String[]{StringUtils.EMPTY, StringUtils.EMPTY};
            }
            start[1] = s;                            // new start
        }
        while (true) {
            // find new end
            e = CharKit.findFirstEqual(srcc, s, delimiters);
            if (e == -1) {
                end[count] = srcc.length;
                break;
            }
            end[count] = e;

            // find new start
            count++;
            s = CharKit.findFirstDiff(srcc, e, delimiters);
            if (s == -1) {
                start[count] = end[count] = srcc.length;
                break;
            }
            start[count] = s;
        }
        count++;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = src.substring(start[i], end[i]);
        }
        return result;
    }

    /**
     * Splits a string in several parts (tokens) that are separated by single delimiter
     * characters. Delimiter is always surrounded by two strings.
     *
     * @param src       source to examine
     * @param delimiter delimiter character
     * @return array of tokens
     */
    public static String[] splitc(String src, char delimiter) {
        if (src.length() == 0) {
            return new String[]{StringUtils.EMPTY};
        }
        char[] srcc = src.toCharArray();

        int maxparts = srcc.length + 1;
        int[] start = new int[maxparts];
        int[] end = new int[maxparts];

        int count = 0;

        start[0] = 0;
        int s = 0, e;
        if (srcc[0] == delimiter) {    // string starts with delimiter
            end[0] = 0;
            count++;
            s = CharKit.findFirstDiff(srcc, 1, delimiter);
            if (s == -1) {                            // nothing after delimiters
                return new String[]{StringUtils.EMPTY, StringUtils.EMPTY};
            }
            start[1] = s;                            // new start
        }
        while (true) {
            // find new end
            e = CharKit.findFirstEqual(srcc, s, delimiter);
            if (e == -1) {
                end[count] = srcc.length;
                break;
            }
            end[count] = e;

            // find new start
            count++;
            s = CharKit.findFirstDiff(srcc, e, delimiter);
            if (s == -1) {
                start[count] = end[count] = srcc.length;
                break;
            }
            start[count] = s;
        }
        count++;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = src.substring(start[i], end[i]);
        }
        return result;
    }


    /**
     * Returns if string ends with provided character.
     */
    public static boolean endsWithChar(String s, char c) {
        return s.length() != 0 && s.charAt(s.length() - 1) == c;
    }

}
