package goja.encry;


import goja.StringPool;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * <p>
 * .
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-01-04 13:41
 * @since JDK 1.6
 */
public class IdentitieKit {

    private static SecureRandom random = new SecureRandom();


    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static String uuid2() {
        return UUID.randomUUID().toString().replaceAll(StringPool.DASH, StringPool.EMPTY);
    }


    public static long randomLong() {
        return Math.abs(random.nextLong());
    }

    public static String randomBase62(int length) {
        byte[] randomBytes = new byte[length];
        random.nextBytes(randomBytes);
        return EncodeKit.encodeBase62(randomBytes);
    }
}
