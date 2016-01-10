/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.security.goja;

import com.google.common.base.Function;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.ehcache.CacheKit;
import goja.Goja;
import goja.core.StringPool;
import goja.core.encry.DigestsKit;
import goja.core.encry.EncodeKit;
import goja.rapid.mvc.kits.Requests;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Enumeration;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * <p> The Security Kit. </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-02-12 22:15
 * @since JDK 1.6
 */
public class SecurityKit {
    /**
     * 登录存储在客户端的用户Session标识
     */
    public final static String COOKIE_LOGIN = Goja.appName + "_session_id";
    /**
     * 登录的SESSION。KEY
     */
    private static final String LOGIN_SESSION_KEY = Goja.appName + "#$session$login_user";
    /**
     * 登录的会员IDSession信息
     */
    private static final String LOGIN_MEMBER_ID = Goja.appName + "@session#member%id";

    private static final String LOGIN_CACHE_SESSION = "login.session";

    private final static byte[] E_KEY = new byte[]{'1', '2', '3', '4', '5', '6', '7', '8'};
    private final static String DES = "DES";
    private final static int MAX_AGE = 86400 * 365;

    /**
     * Determine whether the user is logged in, if you have logged in, return <code> true </ code>
     *
     * @param request http request.
     * @return has loginned.
     */
    public static boolean isLogin(HttpServletRequest request) {
        return getLoginUser(request) != null;
    }

    /**
     * Check the user's password, and execute the login request
     *
     * @param user     user.
     * @param password password.
     * @param remember has remember.
     * @param request  request.
     * @param response http response.
     * @param <T>      User type.
     * @return True login Success.
     */
    public static <T extends Model> boolean login(T user, String password, boolean remember
            , HttpServletRequest request, HttpServletResponse response) {
        boolean matcher =
                SecurityKit.checkPassword(user.getStr("salt"), user.getStr("password"), password);
        if (matcher) {
            SecurityKit.setLoginMember(request, response, user, remember);
        }
        return matcher;
    }

    /**
     * logout
     *
     * @param req      http request
     * @param response http response
     */
    public static <T extends Model> void logout(HttpServletRequest req,
                                                HttpServletResponse response) {
        CookieUser cookie_user = getUserFromCookie(req);
        Requests.deleteCookie(req, response, COOKIE_LOGIN, true);
        // 清理Cache
        if (cookie_user != null) {
            CacheKit.remove(LOGIN_CACHE_SESSION, LOGIN_CACHE_SESSION + cookie_user.getId());
        } else {
            T user = getLoginUser(req);
            if (user != null) {
                CacheKit.remove(LOGIN_CACHE_SESSION,
                        LOGIN_CACHE_SESSION + user.getNumber(StringPool.PK_COLUMN));
            }
        }
        //清除session
        Enumeration<String> em = req.getSession().getAttributeNames();
        while (em.hasMoreElements()) {
            final String key = em.nextElement();
            req.getSession().removeAttribute(key);
        }
        req.getSession().invalidate();
    }

    /**
     * Get user login information, if the session does not exist, then try to obtain from the Cookie,
     * if cookie exists, then decrypt obtain user information.
     *
     * @param req      requeset
     * @param response http response
     * @param function call function.
     * @return user login information.
     */
    public static <T extends Model> T getLoginWithDb(HttpServletRequest req
            , HttpServletResponse response
            , Function<Long, T> function) {
        T loginUser = getLoginUser(req);
        if (loginUser == null) {
            //从Cookie中解析出用户id
            CookieUser cookie_user = getUserFromCookie(req);
            if (cookie_user == null) return null;
            T user = CacheKit.get(LOGIN_CACHE_SESSION, LOGIN_CACHE_SESSION + cookie_user.getId());
            if (user == null) {
                user = function.apply(cookie_user.getId());
                CacheKit.put(LOGIN_CACHE_SESSION, LOGIN_CACHE_SESSION + cookie_user.getId(), user);
            }
            // 用户密码和cookie中存储的密码一致
            if (user != null && StringUtils.equalsIgnoreCase(user.getStr("password"),
                    cookie_user.getPassword())) {
                setLoginMember(req, response, user, true);
                return user;
            } else {
                return null;
            }
        } else {
            return loginUser;
        }
    }

    /**
     * Obtain user information from the Session
     *
     * @param req request
     * @return user infomation.
     */
    public static <T extends Model> T getLoginUser(HttpServletRequest req) {
        return (T) req.getSession().getAttribute(LOGIN_SESSION_KEY);
    }

    /**
     * Sign settings
     *
     * @param request  request.
     * @param response response
     * @param user     The logged user.
     * @param remember is remember.
     */
    private static <T extends Model> void setLoginMember(HttpServletRequest request,
                                                         HttpServletResponse response,
                                                         T user, boolean remember) {
        request.getSession().setAttribute(LOGIN_SESSION_KEY, user);
        request.getSession().setAttribute(LOGIN_MEMBER_ID, user.getNumber(StringPool.PK_COLUMN));
        saveMemberInCookie(user, remember, request, response);
    }

    /**
     * Check Password , salt, password, planpassword.
     *
     * @param salt          salt
     * @param password      password.
     * @param plainPassword plain password.
     * @return true, in user.
     */
    public static boolean checkPassword(String salt, String password, String plainPassword) {
        byte[] saltHex = EncodeKit.decodeHex(salt);
        byte[] hashPassword =
                DigestsKit.sha1(plainPassword.getBytes(), saltHex, EncodeKit.HASH_INTERATIONS);
        return StringUtils.equals(EncodeKit.encodeHex(hashPassword), password);
    }

    /**
     * Store user information in a cookie logged in.
     *
     * @param user     The user has logged on.
     * @param save     has save.
     * @param request  request
     * @param response http response
     */
    public static <T extends Model> void saveMemberInCookie(T user, boolean save,
                                                            HttpServletRequest request, HttpServletResponse response) {
        String new_value =
                getLoginKey(user, Requests.remoteIP(request), request.getHeader("user-agent"));
        int max_age = save ? MAX_AGE : -1;
        Requests.deleteCookie(request, response, COOKIE_LOGIN, true);
        Requests.setCookie(request, response, COOKIE_LOGIN, new_value, max_age, true);
    }

    /**
     * Generating system user login ID string.
     *
     * @param user       The user has logged on.
     * @param ip         The login ip.
     * @param user_agent http agent.
     * @return User login identification string
     */
    private static <T extends Model> String getLoginKey(T user, String ip, String user_agent) {
        return encrypt(String.valueOf(user.getNumber(StringPool.PK_COLUMN)) + '|'
                + user.getStr("password") + '|' + ip + '|' + ((user_agent == null) ? 0
                : user_agent.hashCode()) + '|' + System.currentTimeMillis());
    }

    /**
     * encrypt
     *
     * @param src src
     * @param key key
     * @return encrypt string
     * @throws RuntimeException error.
     */
    private static byte[] encrypt(byte[] src, byte[] key) throws RuntimeException {
        //		DES算法要求有一个可信任的随机数源
        try {
            SecureRandom sr = new SecureRandom();
            // 从原始密匙数据创建DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey securekey = keyFactory.generateSecret(dks);
            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance(DES);
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
            // 现在，获取数据并加密
            // 正式执行加密操作
            return cipher.doFinal(src);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从cookie中读取保存的用户信息
     *
     * @param req requeset 请求
     * @return 用户信息
     */
    private static CookieUser getUserFromCookie(HttpServletRequest req) {
        try {
            Cookie cookie = Requests.getCookie(req, COOKIE_LOGIN);
            if (cookie != null && StringUtils.isNotBlank(cookie.getValue())) {
                return userForCookie(cookie.getValue(), req);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * encrypt
     *
     * @param value encry string
     * @return ciphertext.
     */
    private static String encrypt(String value) {
        byte[] data = encrypt(value.getBytes(), E_KEY);
        try {
            return URLEncoder.encode(new String(Base64.encodeBase64(data)), StringPool.UTF_8);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * Obtain user information from the Cookie
     *
     * @param uuid    ciphertext.
     * @param request request.
     * @return cookie user.
     */
    private static CookieUser userForCookie(String uuid, HttpServletRequest request) {
        if (StringUtils.isBlank(uuid)) {
            return null;
        }
        String ck = decrypt(uuid);
        final String[] items = StringUtils.split(ck, '|');
        if (items.length == 5) {
            String ua = request.getHeader("user-agent");
            int ua_code = (ua == null) ? 0 : ua.hashCode();
            int old_ua_code = Integer.parseInt(items[3]);
            if (ua_code == old_ua_code) {
                return new CookieUser(NumberUtils.toLong(items[0], -1L), items[1], false);
            }
        }
        return null;
    }

    /**
     * decrypt
     *
     * @param value ciphertext
     * @return Plaintext.
     */
    private static String decrypt(String value) {
        try {
            value = URLDecoder.decode(value, StringPool.UTF_8);
            if (StringUtils.isBlank(value)) return null;
            byte[] data = Base64.decodeBase64(value.getBytes());
            return new String(decrypt(data, E_KEY));
        } catch (UnsupportedEncodingException excp) {
            return null;
        }
    }

    /**
     * decrypt
     *
     * @param src ciphertext
     * @param key Key.
     * @return Plaintext
     * @throws RuntimeException cuowu
     */
    private static byte[] decrypt(byte[] src, byte[] key) throws RuntimeException {
        try {
            //		DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();
            // 从原始密匙数据创建一个DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);
            // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey securekey = keyFactory.generateSecret(dks);
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance(DES);
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
            // 现在，获取数据并解密
            // 正式执行解密操作
            return cipher.doFinal(src);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
