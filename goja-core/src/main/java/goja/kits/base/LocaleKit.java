/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.kits.base;

import com.google.common.collect.Maps;
import goja.StringPool;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * .
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-01-31 18:17
 * @since JDK 1.6
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class LocaleKit {

    protected static Map<String, LocaleData> locales = Maps.newHashMap();


    // ---------------------------------------------------------------- locale cache

    /**
     * Lookups for locale data and creates new if it doesn't exist.
     */
    protected static LocaleData lookupLocaleData(String code) {
        LocaleData localeData = locales.get(code);
        if (localeData == null) {
            String[] data = decodeLocaleCode(code);
            localeData = new LocaleData(new Locale(data[0], data[1], data[2]));
            locales.put(code, localeData);
        }
        return localeData;
    }

    protected static LocaleData lookupLocaleData(Locale locale) {
        return lookupLocaleData(resolveLocaleCode(locale));
    }

    /**
     * Returns Locale from cache.
     */
    public static Locale getLocale(String language, String country, String variant) {
        LocaleData localeData = lookupLocaleData(resolveLocaleCode(language, country, variant));
        return localeData.locale;
    }


    // ---------------------------------------------------------------- get cached locales

    /**
     * Returns Locale from cache.
     */
    public static Locale getLocale(String language, String country) {
        return getLocale(language, country, null);
    }

    /**
     * Returns Locale from cache where Locale may be specified also using language code.
     * Converts a locale string like "en", "en_US" or "en_US_win" to <b>new</b> Java locale object.
     */
    public static Locale getLocale(String languageCode) {
        LocaleData localeData = lookupLocaleData(languageCode);
        return localeData.locale;
    }

    /**
     * Transforms locale data to locale code. <code>null</code> values are allowed.
     */
    public static String resolveLocaleCode(String lang, String country, String variant) {
        StringBuilder code = new StringBuilder(lang);
        if (!StringUtils.isEmpty(country)) {
            code.append('_').append(country);
            if (!StringUtils.isEmpty(variant)) {
                code.append('_').append(variant);
            }
        }
        return code.toString();
    }

    // ---------------------------------------------------------------- convert

    /**
     * Resolves locale code from locale.
     */
    public static String resolveLocaleCode(Locale locale) {
        return resolveLocaleCode(locale.getLanguage(), locale.getCountry(), locale.getVariant());
    }

    /**
     * Decodes locale code in string array that can be used for <code>Locale</code> constructor.
     */
    public static String[] decodeLocaleCode(String localeCode) {
        String result[] = new String[3];
        String[] data = CharKit.splitc(localeCode, '_');
        result[0] = data[0];
        result[1] = result[2] = StringPool.EMPTY;
        if (data.length >= 2) {
            result[1] = data[1];
            if (data.length >= 3) {
                result[2] = data[2];
            }
        }
        return result;
    }

    /**
     * Returns cached <code>NumberFormat</code> instance for specified locale.
     */
    public static NumberFormat getNumberFormat(Locale locale) {
        LocaleData localeData = lookupLocaleData(locale);
        NumberFormat nf = localeData.numberFormat;
        if (nf == null) {
            nf = NumberFormat.getInstance(locale);
            localeData.numberFormat = nf;
        }
        return nf;
    }

    // ---------------------------------------------------------------- locale elements

    /**
     * Holds all per-Locale data.
     */
    static class LocaleData {
        final Locale locale;
        NumberFormat numberFormat;

        LocaleData(Locale locale) {
            this.locale = locale;
        }
    }
}
