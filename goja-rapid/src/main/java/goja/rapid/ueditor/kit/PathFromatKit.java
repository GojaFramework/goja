package goja.rapid.ueditor.kit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class PathFromatKit {

    private static final String TIME      = "time";
    private static final String FULL_YEAR = "yyyy";
    private static final String YEAR      = "yy";
    private static final String MONTH     = "mm";
    private static final String DAY       = "dd";
    private static final String HOUR      = "hh";
    private static final String MINUTE    = "ii";
    private static final String SECOND    = "ss";
    private static final String RAND      = "rand";

    private static Date currentDate = null;

    public static String parse(String input) {

        Pattern pattern = Pattern.compile("\\{([^\\}]+)\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);

        PathFromatKit.currentDate = new Date();

            StringBuffer sb = new StringBuffer();

            while ( matcher.find() ) {

                matcher.appendReplacement(sb, PathFromatKit.getString( matcher.group( 1 ) ) );

            }

            matcher.appendTail(sb);

            return sb.toString();
        }

        /**
         * 格式化路径, 把windows路径替换成标准路径
         * @param input 待格式化的路径
         * @return 格式化后的路径
         */
        public static String format ( String input ) {

            return input.replace( "\\", "/" );

        }

        public static String parse ( String input, String filename ) {

            Pattern pattern = Pattern.compile( "\\{([^\\}]+)\\}", Pattern.CASE_INSENSITIVE  );
            Matcher matcher = pattern.matcher(input);
            String matchStr;

            PathFromatKit.currentDate = new Date();

            StringBuffer sb = new StringBuffer();

            while ( matcher.find() ) {

                matchStr = matcher.group( 1 );
                if (matchStr.contains("filename")) {
                    filename = filename.replace( "$", "\\$" ).replaceAll( "[\\/:*?\"<>|]", "" );
                    matcher.appendReplacement(sb, filename );
                } else {
                    matcher.appendReplacement(sb, PathFromatKit.getString( matchStr ) );
                }

            }

            matcher.appendTail(sb);

            return sb.toString();
        }

        private static String getString ( String pattern ) {

            pattern = pattern.toLowerCase();

            // time 处理
            if (pattern.contains(PathFromatKit.TIME)) {
                return PathFromatKit.getTimestamp();
            } else if (pattern.contains(PathFromatKit.FULL_YEAR)) {
                return PathFromatKit.getFullYear();
            } else if (pattern.contains(PathFromatKit.YEAR)) {
                return PathFromatKit.getYear();
            } else if (pattern.contains(PathFromatKit.MONTH)) {
                return PathFromatKit.getMonth();
            } else if (pattern.contains(PathFromatKit.DAY)) {
                return PathFromatKit.getDay();
            } else if (pattern.contains(PathFromatKit.HOUR)) {
                return PathFromatKit.getHour();
            } else if (pattern.contains(PathFromatKit.MINUTE)) {
                return PathFromatKit.getMinute();
            } else if (pattern.contains(PathFromatKit.SECOND)) {
                return PathFromatKit.getSecond();
            } else if (pattern.contains(PathFromatKit.RAND)) {
                return PathFromatKit.getRandom( pattern );
            }

            return pattern;

        }

        private static String getTimestamp () {
            return System.currentTimeMillis() + "";
        }

        private static String getFullYear () {
            return new SimpleDateFormat( "yyyy" ).format( PathFromatKit.currentDate );
        }

        private static String getYear () {
            return new SimpleDateFormat( "yy" ).format( PathFromatKit.currentDate );
        }

        private static String getMonth () {
            return new SimpleDateFormat( "MM" ).format( PathFromatKit.currentDate );
        }

        private static String getDay () {
            return new SimpleDateFormat( "dd" ).format( PathFromatKit.currentDate );
        }

        private static String getHour () {
            return new SimpleDateFormat( "HH" ).format( PathFromatKit.currentDate );
        }

        private static String getMinute () {
            return new SimpleDateFormat( "mm" ).format( PathFromatKit.currentDate );
        }

        private static String getSecond () {
            return new SimpleDateFormat( "ss" ).format( PathFromatKit.currentDate );
        }

        private static String getRandom ( String pattern ) {

            int length;
            pattern = pattern.split( ":" )[ 1 ].trim();

            length = Integer.parseInt( pattern );

            return ( Math.random() + "" ).replace( ".", "" ).substring( 0, length );

        }


}
