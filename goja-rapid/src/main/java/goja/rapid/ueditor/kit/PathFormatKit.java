package goja.rapid.ueditor.kit;

import goja.core.StringPool;

import org.joda.time.DateTime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class PathFormatKit {

    public static final  String   PATH_TWO_ZERO = "%02d";
    private static final String   TIME          = "time";
    private static final String   FULL_YEAR     = "yyyy";
    private static final String   YEAR          = "yy";
    private static final String   MONTH         = "mm";
    private static final String   DAY           = "dd";
    private static final String   HOUR          = "hh";
    private static final String   MINUTE        = "ii";
    private static final String   SECOND        = "ss";
    private static final String   RAND          = "rand";
    private static final Pattern  PARSE_PATTERN = Pattern.compile("\\{([^\\}]+)\\}", Pattern.CASE_INSENSITIVE);
    private static       DateTime currentDate   = null;

    public static String parse(String input) {

        Matcher matcher = PARSE_PATTERN.matcher(input);

        PathFormatKit.currentDate = DateTime.now();

        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {

            matcher.appendReplacement(sb, PathFormatKit.getString(matcher.group(1)));

        }

        matcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * 格式化路径, 把windows路径替换成标准路径
     *
     * @param input 待格式化的路径
     * @return 格式化后的路径
     */
    public static String format(String input) {

        return input.replace("\\", "/");

    }

    /**
     * @param savePath 存储路径
     * @param filename 文件名
     * @return 格式化后的存储路径
     */
    public static String parse(String savePath, String filename) {

        Matcher matcher = PARSE_PATTERN.matcher(savePath);
        String matchStr;

        PathFormatKit.currentDate = DateTime.now();

        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {

            matchStr = matcher.group(1);
            if (matchStr.contains("filename")) {
                filename = filename.replace("$", "\\$").replaceAll("[\\/:*?\"<>|]", "");
                matcher.appendReplacement(sb, filename);
            } else {
                matcher.appendReplacement(sb, PathFormatKit.getString(matchStr));
            }

        }

        matcher.appendTail(sb);

        return sb.toString();
    }

    private static String getString(String pattern) {

        pattern = pattern.toLowerCase();

        // time 处理
        if (pattern.contains(PathFormatKit.TIME)) {
            return PathFormatKit.getTimestamp();
        } else if (pattern.contains(PathFormatKit.FULL_YEAR)) {
            return PathFormatKit.getFullYear();
        } else if (pattern.contains(PathFormatKit.YEAR)) {
            return PathFormatKit.getYear();
        } else if (pattern.contains(PathFormatKit.MONTH)) {
            return PathFormatKit.getMonth();
        } else if (pattern.contains(PathFormatKit.DAY)) {
            return PathFormatKit.getDay();
        } else if (pattern.contains(PathFormatKit.HOUR)) {
            return PathFormatKit.getHour();
        } else if (pattern.contains(PathFormatKit.MINUTE)) {
            return PathFormatKit.getMinute();
        } else if (pattern.contains(PathFormatKit.SECOND)) {
            return PathFormatKit.getSecond();
        } else if (pattern.contains(PathFormatKit.RAND)) {
            return PathFormatKit.getRandom(pattern);
        }

        return pattern;

    }

    private static String getTimestamp() {
        return System.currentTimeMillis() + StringPool.EMPTY;
    }

    private static String getFullYear() {
        return String.format("%04d", PathFormatKit.currentDate.getYear());
    }

    private static String getYear() {
        return String.format(PATH_TWO_ZERO, PathFormatKit.currentDate.getYearOfCentury());
    }

    private static String getMonth() {
        return String.format(PATH_TWO_ZERO, PathFormatKit.currentDate.getMonthOfYear());
    }

    private static String getDay() {

        return String.format(PATH_TWO_ZERO, PathFormatKit.currentDate.getDayOfMonth());
    }

    private static String getHour() {

        return String.format(PATH_TWO_ZERO, PathFormatKit.currentDate.getHourOfDay());
    }

    private static String getMinute() {

        return String.format(PATH_TWO_ZERO, PathFormatKit.currentDate.getMinuteOfHour());
    }

    private static String getSecond() {
        return String.format(PATH_TWO_ZERO, PathFormatKit.currentDate.getSecondOfMinute());
    }

    private static String getRandom(String pattern) {

        int length;
        pattern = pattern.split(":")[1].trim();

        length = Integer.parseInt(pattern);

        return (Math.random() + "").replace(".", "").substring(0, length);

    }


}
