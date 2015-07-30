/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.encry;

import goja.kits.base.ExceptionKit;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * <p>
 * .
 * </p>
 *
 * @author walter yang
 * @version 1.0 2013-10-30 10:53 AM
 * @since JDK 1.5
 */
public class DigestsKit {
    private static final String       MD5    = "MD5";
    private static       SecureRandom random = new SecureRandom();

    /** 对输入字符串进行sha1散列. */
    public static byte[] sha1(byte[] input) {
        return digest(input, EncodeKit.HASH_ALGORITHM, null, 1);
    }

    public static byte[] sha1(byte[] input, byte[] salt) {
        return digest(input, EncodeKit.HASH_ALGORITHM, salt, 1);
    }

    public static byte[] sha1(byte[] input, byte[] salt, int iterations) {
        return digest(input, EncodeKit.HASH_ALGORITHM, salt, iterations);
    }

    /** 对字符串进行散列, 支持md5与sha1算法. */
    private static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);

            if (salt != null) {
                digest.update(salt);
            }

            byte[] result = digest.digest(input);

            for (int i = 1; i < iterations; i++) {
                digest.reset();
                result = digest.digest(result);
            }
            return result;
        } catch (GeneralSecurityException e) {
            throw ExceptionKit.unchecked(e);
        }
    }

    /**
     * 生成随机的Byte[]作为salt.
     *
     * @param numBytes byte数组的大小
     */
    public static byte[] generateSalt(int numBytes) {
        Validate.isTrue(numBytes > 0, "numBytes argument must be a positive integer (1 or larger)", numBytes);

        byte[] bytes = new byte[numBytes];
        random.nextBytes(bytes);
        return bytes;
    }

    /** 对文件进行md5散列. */
    public static byte[] md5(InputStream input) throws IOException {
        return digest(input, MD5);
    }

    /** 对文件进行sha1散列. */
    public static byte[] sha1(InputStream input) throws IOException {
        return digest(input, EncodeKit.HASH_ALGORITHM);
    }

    private static byte[] digest(InputStream input, String algorithm) throws IOException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            int bufferLength = 8 * 1024;
            byte[] buffer = new byte[bufferLength];
            int read = input.read(buffer, 0, bufferLength);

            while (read > -1) {
                messageDigest.update(buffer, 0, read);
                read = input.read(buffer, 0, bufferLength);
            }

            return messageDigest.digest();
        } catch (GeneralSecurityException e) {
            throw ExceptionKit.unchecked(e);
        }
    }
}
