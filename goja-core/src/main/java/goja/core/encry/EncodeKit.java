/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.encry;

import goja.core.StringPool;
import goja.core.kits.lang.ExceptionKit;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * <p>
 * .
 * </p>
 *
 * @author walter yang
 * @version 1.0 2013-10-30 10:53 AM
 * @since JDK 1.5
 */
public class EncodeKit {

    /**
     * sha-算法名称
     */
    public static final  String HASH_ALGORITHM   = "SHA-1";
    /**
     * 计算次数
     */
    public static final  int    HASH_INTERATIONS = 1024;
    /**
     * 长度
     */
    public static final  int    SALT_SIZE        = 8;
    private static final char[] BASE62           = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    /** Hex编码. */
    public static String encodeHex(byte[] input) {
        return Hex.encodeHexString(input);
    }

    /** Hex解码. */
    public static byte[] decodeHex(String input) {
        try {
            return Hex.decodeHex(input.toCharArray());
        } catch (DecoderException e) {
            throw ExceptionKit.unchecked(e);
        }
    }

    /** Base64编码. */
    public static String encodeBase64(byte[] input) {
        return Base64.encodeBase64String(input);
    }

    /** Base64编码, URL安全(将Base64中的URL非法字符'+'和'/'转为'-'和'_', 见RFC3548). */
    public static String encodeUrlSafeBase64(byte[] input) {
        return Base64.encodeBase64URLSafeString(input);
    }

    /** Base64解码. */
    public static byte[] decodeBase64(String input) {
        return Base64.decodeBase64(input);
    }

    /** Base62编码。 */
    public static String encodeBase62(byte[] input) {
        char[] chars = new char[input.length];
        for (int i = 0; i < input.length; i++) {
            chars[i] = BASE62[((input[i] & 0xFF) % BASE62.length)];
        }
        return new String(chars);
    }

    /** Html 转码. */
    public static String escapeHtml(String html) {
        return StringEscapeUtils.escapeHtml4(html);
    }

    /** Html 解码. */
    public static String unescapeHtml(String htmlEscaped) {
        return StringEscapeUtils.unescapeHtml4(htmlEscaped);
    }

    /** Xml 转码. */
    public static String escapeXml(String xml) {
        return StringEscapeUtils.escapeXml11(xml);
    }

    /** Xml 解码. */
    public static String unescapeXml(String xmlEscaped) {
        return StringEscapeUtils.unescapeXml(xmlEscaped);
    }

    /** URL 编码, Encode默认为UTF-8. */
    public static String urlEncode(String part) {
        try {
            return URLEncoder.encode(part, StringPool.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw ExceptionKit.unchecked(e);
        }
    }

    /** URL 解码, Encode默认为UTF-8. */
    public static String urlDecode(String part) {

        try {
            return URLDecoder.decode(part, StringPool.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw ExceptionKit.unchecked(e);
        }
    }
}
