/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;


/**
 * <p>
 * 验证帮助.
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-02-22 15:09
 * @since JDK 1.6
 */
public class Validator {


    /**
     * Matches the regular expression
     *
     * @param regex regex
     * @param value value
     * @return boolean
     */
    public static boolean match(String regex, String value) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(value).find();
    }

    /**
     * Matching the regular expression is case-sensitive
     *
     * @param regex regex
     * @param flags flags
     * @param value value
     * @return boolean
     */
    public static boolean match(String regex, int flags, String value) {
        Pattern pattern = Pattern.compile(regex, flags);
        return pattern.matcher(value).find();
    }

    /**
     * Email validation
     *
     * @param value value
     * @return boolean
     */
    public static boolean isEmail(String value) {
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * Phone number verification
     *
     * @param value value
     * @return boolean
     */
    public static boolean isMobile(String value) {
        String check = "^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\\d{8})$";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * Phone verification
     *
     * @param value value
     * @return boolean
     */
    public static boolean isTel(String value) {
        String check = "^\\d{3,4}-?\\d{7,9}$";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * Telephone number, including mobile phones and landlines
     *
     * @param value value
     * @return boolean
     */
    public static boolean isPhone(String value) {
        String telcheck = "^\\d{3,4}-?\\d{7,9}$";
        String mobilecheck = "^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\\d{8})$";
        return match(telcheck, Pattern.CASE_INSENSITIVE, value) || match(mobilecheck, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * @param value Input limited to English letters, numbers and underscores
     * @return boolean
     */
    public static boolean isGeneral(String value) {
        String check = "^\\w+$";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * @param value Input limited to English letters, numbers and underscores
     * @param min   Minimum length
     * @param max   Maximum length
     * @return boolean
     */
    public static boolean isGeneral(String value, int min, int max) {
        String check = "^\\w{" + min + "," + max + "}$";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * Judge whether it is birthday
     *
     * @param value value
     * @return boolean
     */
    public static boolean isBirthDay(String value) {
        String check = "(\\d{4})(/|-|\\.)(\\d{1,2})(/|-|\\.)(\\d{1,2})$";

        if (match(check, Pattern.CASE_INSENSITIVE, value)) {
            int year = Integer.parseInt(value.substring(0, 4));
            int month = Integer.parseInt(value.substring(5, 7));
            int day = Integer.parseInt(value.substring(8, 10));

            if (month < 1 || month > 12)
                return false;

            if (day < 1 || day > 31)
                return false;

            if ((month == 4 || month == 6 || month == 9 || month == 11)
                    && day == 31)
                return false;

            if (month == 2) {
                boolean isleap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
                if (day > 29 || (day == 29 && !isleap))
                    return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Identity verification
     *
     * @param value value
     * @return boolean
     */
    public static boolean isIdentityCard(String value) {
        String check = "(^\\d{15}$)|(^\\d{17}([0-9]|X)$)";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * Postal Code
     *
     * @param value value
     * @return boolean
     */
    public static boolean isZipCode(String value) {
        String check = "^[0-9]{6}$";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * Currency validation
     *
     * @param value value
     * @return boolean
     */
    public static boolean isCurrency(String value) {
        String check = "^(\\d+(?:\\.\\d{1,2})?)$";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * @param value Enter the numbers
     * @return boolean
     */
    public static boolean isNumber(String value) {
        String check = "^(\\+|\\-)?\\d+$";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * @param value Enter the numbers
     * @param min   Minimum length
     * @param max   Maximum length
     * @return boolean
     */
    public static boolean isNumber(String value, int min, int max) {
        String check = "^(\\+|\\-)?\\d{" + min + "," + max + "}$";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * @param value A positive integer
     * @return boolean
     */
    public static boolean isPositiveNumber(String value) {
        String check = "^\\d+$";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * @param value A positive integer
     * @param min   Minimum length
     * @param max   Maximum length
     * @return boolean
     */
    public static boolean isPositiveNumber(String value, int min, int max) {
        String check = "^\\d{" + min + "," + max + "}$";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * Chinese
     *
     * @param value value
     * @return boolean
     */
    public static boolean isChinese(String value) {
        String check = "^[\\u2E80-\\u9FFF]+$";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    public static boolean isChinese(String value, int min, int max) {
        String check = "^[\\u2E80-\\u9FFF]{" + min + "," + max + "}$";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * @param value Chinese characters, English letters, numbers and underscores
     * @return boolean
     */
    public static boolean isString(String value) {
        String check = "^[\\u0391-\\uFFE5\\w]+$";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * @param value Chinese characters, English letters, numbers and underscores
     * @param min   Minimum length
     * @param max   Maximum length
     * @return boolean
     */
    public static boolean isString(String value, int min, int max) {
        String check = "^[\\u0391-\\uFFE5\\w]{" + min + "," + max + "}$";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * @param value UUID
     * @return boolean
     */
    public static boolean isUUID(String value) {
        String check = "^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * Matches are linked
     *
     * @param value value
     * @return boolean
     */
    public static boolean isUrl(String value) {
        String check = "^((https?|ftp):\\/\\/)?(((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:)*@)?(((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]))|((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.?)(:\\d*)?)(\\/((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)+(\\/(([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)*)*)?)?(\\?((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)|[\\uE000-\\uF8FF]|\\/|\\?)*)?(\\#((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)|\\/|\\?)*)?$";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * Tell the time
     *
     * @param value value
     * @return boolean
     */
    public static boolean isDateTime(String value) {
        String check = "^(\\d{4})(/|-|\\.|年)(\\d{1,2})(/|-|\\.|月)(\\d{1,2})(日)?(\\s+\\d{1,2}(:|时)\\d{1,2}(:|分)?(\\d{1,2}(秒)?)?)?$";// check = "^(\\d{4})(/|-|\\.)(\\d{1,2})(/|-|\\.)(\\d{1,2})$";
        return match(check, Pattern.CASE_INSENSITIVE, value);
    }

    /**
     * Blank
     *
     * @param value value
     * @return boolean
     */
    public static boolean isBlank(Object value) {
        if (value instanceof Collection) {
            return ((Collection) value).isEmpty();
        } else if (value instanceof String) {
            return "".equals(value.toString().trim());
        } else {
            return value == null;
        }
    }

    public static boolean isNotBlank(Object value) {
        return !isBlank(value);
    }


    public static boolean isLength(String value, int min, int max) {
        int length = isBlank(value) ? 0 : value.length();
        return length >= min && length <= max;
    }

    public static boolean compareDate(String date1, String date2, String df) {
        SimpleDateFormat sdf = new SimpleDateFormat(df);
        try {
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);
            return d1.compareTo(d2) > 0;
        } catch (ParseException e) {
            return false;
        }
    }


    public static boolean compareDate(String date1, String date2) {
        return compareDate(date1, date2, "yyyy-MM-dd HH:mm:ss");
    }

}
