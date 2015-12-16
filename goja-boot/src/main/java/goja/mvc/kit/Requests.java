/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc.kit;

import com.google.common.base.Strings;
import goja.core.StringPool;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Http Request Kit class.
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-06-01 20:47
 * @since JDK 1.6
 */
public abstract class Requests {


    /**
     * 获取客户端浏览器信息
     *
     * @param req
     * @return string
     */
    public static String browserInfo(HttpServletRequest req) {
        String browserInfo = "other";
        String ua = req.getHeader("User-Agent").toLowerCase();
        String s;
        String version;
        String msieP = "msie ([\\d.]+)";
        String ieheighP = "rv:([\\d.]+)";
        String firefoxP = "firefox\\/([\\d.]+)";
        String chromeP = "chrome\\/([\\d.]+)";
        String operaP = "opr.([\\d.]+)";
        String safariP = "version\\/([\\d.]+).*safari";

        Pattern pattern = Pattern.compile(msieP);
        Matcher mat = pattern.matcher(ua);
        if (mat.find()) {
            s = mat.group();
            if (s != null) {
                version = s.split(" ")[1];
                browserInfo = "ie " + version.substring(0, version.indexOf("."));
                return browserInfo;
            }
        }

        pattern = Pattern.compile(firefoxP);
        mat = pattern.matcher(ua);
        if (mat.find()) {
            s = mat.group();
            if (s != null) {
                version = s.split("/")[1];
                browserInfo = "firefox " + version.substring(0, version.indexOf("."));
                return browserInfo;
            }

        }

        pattern = Pattern.compile(ieheighP);
        mat = pattern.matcher(ua);
        if (mat.find()) {
            s = mat.group();
            if (s != null) {
                version = s.split(":")[1];
                browserInfo = "ie " + version.substring(0, version.indexOf("."));
                return browserInfo;
            }
        }

        pattern = Pattern.compile(operaP);
        mat = pattern.matcher(ua);
        if (mat.find()) {
            s = mat.group();
            if (s != null) {
                version = s.split("/")[1];
                browserInfo = "opera " + version.substring(0, version.indexOf("."));
                return browserInfo;
            }
        }

        pattern = Pattern.compile(chromeP);
        mat = pattern.matcher(ua);
        if (mat.find()) {
            s = mat.group();
            if (s != null) {
                version = s.split("/")[1];
                browserInfo = "chrome " + version.substring(0, version.indexOf("."));
                return browserInfo;
            }
        }

        pattern = Pattern.compile(safariP);
        mat = pattern.matcher(ua);
        if (mat.find()) {
            s = mat.group();
            if (s != null) {
                version = s.split("/")[1].split(" ")[0];
                browserInfo = "safari " + version.substring(0, version.indexOf("."));
                return browserInfo;
            }
        }
        return browserInfo;
    }

    /**
     * 获取客户端操作系统信息
     *
     * @param req http request.
     * @return string
     */
    public static String clientOS(HttpServletRequest req) {
        String userAgent = req.getHeader("User-Agent");
        String cos = "unknow os";
        Pattern p = Pattern.compile(".*(Windows NT 6\\.2).*");
        Matcher m = p.matcher(userAgent);
        if (m.find()) {
            cos = "Win 8";
            return cos;
        }

        p = Pattern.compile(".*(Windows NT 6\\.1).*");
        m = p.matcher(userAgent);
        if (m.find()) {
            cos = "Win 7";
            return cos;
        }

        p = Pattern.compile(".*(Windows NT 5\\.1|Windows XP).*");
        m = p.matcher(userAgent);
        if (m.find()) {
            cos = "WinXP";
            return cos;
        }

        p = Pattern.compile(".*(Windows NT 5\\.2).*");
        m = p.matcher(userAgent);
        if (m.find()) {
            cos = "Win2003";
            return cos;
        }

        p = Pattern.compile(".*(Win2000|Windows 2000|Windows NT 5\\.0).*");
        m = p.matcher(userAgent);
        if (m.find()) {
            cos = "Win2000";
            return cos;
        }

        p = Pattern.compile(".*(Mac|apple|MacOS8).*");
        m = p.matcher(userAgent);
        if (m.find()) {
            cos = "MAC";
            return cos;
        }

        p = Pattern.compile(".*(WinNT|Windows NT).*");
        m = p.matcher(userAgent);
        if (m.find()) {
            cos = "WinNT";
            return cos;
        }

        p = Pattern.compile(".*Linux.*");
        m = p.matcher(userAgent);
        if (m.find()) {
            cos = "Linux";
            return cos;
        }

        p = Pattern.compile(".*(68k|68000).*");
        m = p.matcher(userAgent);
        if (m.find()) {
            cos = "Mac68k";
            return cos;
        }

        p = Pattern
                .compile(".*(9x 4.90|Win9(5|8)|Windows 9(5|8)|95/NT|Win32|32bit).*");
        m = p.matcher(userAgent);
        if (m.find()) {
            cos = "Win9x";
            return cos;
        }

        return cos;
    }

    public static List<String> acceptLanguage(HttpServletRequest request) {

        final Pattern qpattern = Pattern.compile("q=([0-9\\.]+)");
        final String acceptLanguage = request.getHeader("accept-language");
        if (Strings.isNullOrEmpty(acceptLanguage)) {
            return Collections.emptyList();
        }
        List<String> languages = Arrays.asList(acceptLanguage.split(","));
        Collections.sort(languages, new Comparator<String>() {

            public int compare(String lang1, String lang2) {
                double q1 = 1.0;
                double q2 = 1.0;
                Matcher m1 = qpattern.matcher(lang1);
                Matcher m2 = qpattern.matcher(lang2);
                if (m1.find()) {
                    q1 = Double.parseDouble(m1.group(1));
                }
                if (m2.find()) {
                    q2 = Double.parseDouble(m2.group(1));
                }
                return (int) (q2 - q1);
            }
        });
        List<String> result = new ArrayList<String>(10);
        for (String lang : languages) {
            result.add(lang.trim().split(";")[0]);
        }
        return result;
    }

    /**
     * 获取客户端IP地址，此方法用在proxy环境中
     *
     * @param request 当前请求
     * @return 客户端ip，如果获取失败，则返回 127.0.0.1
     */
    public static String remoteAddr(HttpServletRequest request) {
        String ipAddress = null;
        ipAddress = request.getHeader("x-forwarded-for");
        if ((ipAddress == null) || (ipAddress.length() == 0)
                || ("unknown".equalsIgnoreCase(ipAddress))) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if ((ipAddress == null) || (ipAddress.length() == 0)
                || ("unknown".equalsIgnoreCase(ipAddress))) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if ((ipAddress == null) || (ipAddress.length() == 0)
                || ("unknown".equalsIgnoreCase(ipAddress))) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1")) {
                InetAddress inet;
                try {
                    inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }

        }
        if ((ipAddress != null) && (ipAddress.length() > 15)) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    /**
     * 判断是否为搜索引擎
     *
     * @param request 当前请求
     * @return true 表示当前请求为搜索引擎发过来的请求。
     */
    public static boolean robot(HttpServletRequest request) {
        String ua = request.getHeader("user-agent");
        return !StringUtils.isBlank(ua) && ((ua.contains("Baiduspider") || ua.contains("Googlebot") || ua.contains("sogou") || ua.contains("sina") || ua.contains("iaskspider") || ua.contains("ia_archiver") || ua.contains("Sosospider") || ua.contains("YoudaoBot") || ua.contains("yahoo") || ua.contains("yodao") || ua.contains("MSNBot") || ua.contains("spider") || ua.contains("Twiceler") || ua.contains("Sosoimagespider") || ua.contains("naver.com/robots") || ua.contains("Nutch") || ua.contains("spider")));
    }

    /**
     * 获取COOKIE
     *
     * @param name    cookie 名称
     * @param request 当前请求
     * @return cookie 信息
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie ck : cookies) {
            if (StringUtils.equalsIgnoreCase(name, ck.getName()))
                return ck;
        }
        return null;
    }

    /**
     * 获取COOKIE
     *
     * @param request 当前请求
     * @param name    cokie名称
     * @return 字符串形式的cookie值
     */
    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie ck : cookies) {
            if (StringUtils.equalsIgnoreCase(name, ck.getName()))
                return ck.getValue();
        }
        return null;
    }

    /**
     * 设置COOKIE
     *
     * @param name   cookie's name.
     * @param value  cooke value.
     * @param maxAge age time.
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response,
                                 String name, String value, int maxAge) {
        setCookie(request, response, name, value, maxAge, true);
    }

    /**
     * 设置COOKIE
     *
     * @param name   cookie's name.
     * @param value  cooke value.
     * @param maxAge age time.
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name,
                                 String value, int maxAge, boolean all_sub_domain) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        if (all_sub_domain) {
            String serverName = request.getServerName();
            String domain = domainOfServerName(serverName);
            if (domain != null && domain.indexOf('.') != -1) {
                cookie.setDomain('.' + domain);
            }
        }
        cookie.setPath(StringPool.SLASH);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request,
                                    HttpServletResponse response, String name, boolean all_sub_domain) {
        setCookie(request, response, name, "", 0, all_sub_domain);
    }

    /**
     * 获取用户访问URL中的根域名
     * 例如: www.dlog.cn -> dlog.cn
     *
     * @param host 访问地址
     * @return 根域名
     */
    public static String domainOfServerName(String host) {
        if (isIPAddr(host))
            return null;
        String[] names = StringUtils.split(host, '.');
        if (names == null) return null;
        int len = names.length;
        if (len == 1) return null;
        if (len == 3) {
            return makeup(names[len - 2], names[len - 1]);
        }
        if (len > 3) {
            String dp = names[len - 2];
            if (dp.equalsIgnoreCase("com") || dp.equalsIgnoreCase("gov") || dp.equalsIgnoreCase("net") || dp.equalsIgnoreCase("edu") || dp.equalsIgnoreCase("org"))
                return makeup(names[len - 3], names[len - 2], names[len - 1]);
            else
                return makeup(names[len - 2], names[len - 1]);
        }
        return host;
    }

    /**
     * 判断字符串是否是一个IP地址
     *
     * @param addr 字符串
     * @return true 表示ip地址，否则反之
     */
    public static boolean isIPAddr(String addr) {
        if (StringUtils.isEmpty(addr))
            return false;
        String[] ips = StringUtils.split(addr, '.');
        if (ips == null || ips.length != 4)
            return false;
        try {
            int ipa = Integer.parseInt(ips[0]);
            int ipb = Integer.parseInt(ips[1]);
            int ipc = Integer.parseInt(ips[2]);
            int ipd = Integer.parseInt(ips[3]);
            return ipa >= 0 && ipa <= 255 && ipb >= 0 && ipb <= 255 && ipc >= 0
                    && ipc <= 255 && ipd >= 0 && ipd <= 255;
        } catch (Exception ignored) {
        }
        return false;
    }

    private static String makeup(String... ps) {
        StringBuilder s = new StringBuilder();
        for (int idx = 0; idx < ps.length; idx++) {
            if (idx > 0)
                s.append('.');
            s.append(ps[idx]);
        }
        return s.toString();
    }

    /**
     * 获取HTTP端口
     *
     * @param request 当前请求
     * @return http端口
     */
    public static int httpPort(HttpServletRequest request) {
        try {
            return new URL(request.getRequestURL().toString()).getPort();
        } catch (MalformedURLException excp) {
            return 80;
        }
    }

    /**
     * 获取浏览器提交的整形参数
     *
     * @param request      当前请求
     * @param param        参数
     * @param defaultValue 默认值
     * @return 值
     */
    public static int param(HttpServletRequest request, String param, int defaultValue) {
        return NumberUtils.toInt(request.getParameter(param), defaultValue);
    }

    /**
     * 获取浏览器提交的字符串参
     *
     * @param request      当前请求
     * @param param        参数
     * @param defaultValue 默认值
     * @return 值
     */
    public static String param(HttpServletRequest request, String param, String defaultValue) {
        String value = request.getParameter(param);
        return (StringUtils.isEmpty(value)) ? defaultValue : value;
    }

    /**
     * Get shaping parameters submitted by the browser
     *
     * @param request      Request
     * @param param        Param
     * @param defaultValue Default Value
     * @return Param Values.
     */
    public static long param(HttpServletRequest request, String param, long defaultValue) {
        return NumberUtils.toLong(request.getParameter(param), defaultValue);
    }

    public static long[] paramValues(HttpServletRequest req, String name) {
        String[] values = req.getParameterValues(name);
        if (values == null) return null;
        return (long[]) ConvertUtils.convert(values, long.class);
    }

    /**
     * 判断当前Request是否位Ajax请求，通过消息头来进行判断.
     *
     * @param request 当前请求对象
     * @return 如果返回<code>true</code>则，表示当前的Request是由Ajax请求，否则反之
     */
    public static boolean ajax(HttpServletRequest request) {
        String x_requested = request.getHeader("x-requested-with");
        return !Strings.isNullOrEmpty(x_requested) && "XMLHttpRequest".equals(x_requested);
    }

    /**
     * 去html
     *
     * @param src HTMl
     * @return 去掉HTML后的字符串
     */
    public static String replaceTagHTML(String src) {
        String regex = "\\<(.+?)\\>";
        return StringUtils.isNotEmpty(src) ? src.replaceAll(regex, "") : "";
    }
}
