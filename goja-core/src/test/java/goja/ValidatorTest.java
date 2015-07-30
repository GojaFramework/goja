package goja;

import goja.date.DateFormatter;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class ValidatorTest {

    @Test
    public void testIsEmail() throws Exception {

        Assert.assertTrue(Validator.isEmail("poplar1123@gmail.com"));
        Assert.assertFalse(Validator.isEmail("testx=1123"));

    }

    @Test
    public void testIsMobile() throws Exception {
        Assert.assertTrue(Validator.isMobile("18655121378"));
        Assert.assertFalse(Validator.isMobile("123456789"));
    }

    @Test
    public void testIsTel() throws Exception {

        Assert.assertFalse(Validator.isTel("123456789"));
        Assert.assertTrue(Validator.isTel("0551-12345678"));
    }

    @Test
    public void testIsPhone() throws Exception {
        Assert.assertTrue(Validator.isPhone("18655121378"));

    }

    @Test
    public void testIsGeneral() throws Exception {
        Assert.assertTrue(Validator.isGeneral("avc_123"));
        Assert.assertTrue(Validator.isGeneral("avc123"));
        Assert.assertTrue(Validator.isGeneral("abc"));
        Assert.assertTrue(Validator.isGeneral("123"));
        Assert.assertFalse(Validator.isGeneral("123@"));
        Assert.assertFalse(Validator.isGeneral("123@#123"));

    }

    @Test
    public void testIsGeneralLength() throws Exception {

        Assert.assertTrue(Validator.isGeneral("avc_123", 3, 8));
        Assert.assertFalse(Validator.isGeneral("avc123", 7, 10));
        Assert.assertFalse(Validator.isGeneral("abc", 4, 5));
        Assert.assertFalse(Validator.isGeneral("123##", 3, 5));
    }

    @Test
    public void testIsBirthDay() throws Exception {

        Assert.assertTrue(Validator.isBirthDay("1985-11-24"));
        Assert.assertFalse(Validator.isBirthDay("19851124"));
        Assert.assertFalse(Validator.isBirthDay("192121212"));
    }

    @Test
    public void testIsIdentityCard() throws Exception {
        Assert.assertTrue(Validator.isIdentityCard("340827198511230560"));
        Assert.assertTrue(Validator.isIdentityCard("34082719851123056X"));
        Assert.assertFalse(Validator.isIdentityCard("192121212"));

    }

    @Test
    public void testIsZipCode() throws Exception {
        Assert.assertTrue(Validator.isZipCode("230010"));
        Assert.assertFalse(Validator.isZipCode("123"));

    }

    @Test
    public void testIsCurrency() throws Exception {
        Assert.assertTrue(Validator.isCurrency("123"));
        Assert.assertTrue(Validator.isCurrency("123.25"));
        Assert.assertTrue(Validator.isCurrency("1123.12"));
        Assert.assertFalse(Validator.isCurrency("a1123.2511"));

    }

    @Test
    public void testIsNumber() throws Exception {
        Assert.assertTrue(Validator.isNumber("1123"));
        Assert.assertFalse(Validator.isNumber("a1123.2511"));
        Assert.assertFalse(Validator.isNumber("1123.2511"));

    }

    @Test
    public void testIsNumber1() throws Exception {
        Assert.assertTrue(Validator.isNumber("1123", 3, 10));
        Assert.assertFalse(Validator.isNumber("a1123.2511", 5, 10));


    }

    @Test
    public void testIsPositiveNumber() throws Exception {
        Assert.assertTrue(Validator.isPositiveNumber("1"));
        Assert.assertFalse(Validator.isPositiveNumber("-1"));
        Assert.assertFalse(Validator.isPositiveNumber("a"));

    }

    @Test
    public void testIsPositiveNumber1() throws Exception {
        Assert.assertTrue(Validator.isPositiveNumber("1", 0, 4));
        Assert.assertTrue(Validator.isPositiveNumber("12", 1, 4));
        Assert.assertFalse(Validator.isPositiveNumber("-1", 0, 1));
        Assert.assertFalse(Validator.isPositiveNumber("a", 0, 2));

    }

    @Test
    public void testIsChinese() throws Exception {
        Assert.assertTrue(Validator.isChinese("北京"));
        Assert.assertFalse(Validator.isChinese("a1123.2511"));

    }

    @Test
    public void testIsChinese1() throws Exception {
        Assert.assertTrue(Validator.isChinese("北京", 1, 3));
        Assert.assertFalse(Validator.isChinese("a1123.2511", 2, 4));

    }

    @Test
    public void testIsString() throws Exception {
        Assert.assertTrue(Validator.isString("北京"));
        Assert.assertFalse(Validator.isString("a1123.2511"));

    }

    @Test
    public void testIsString1() throws Exception {
        Assert.assertTrue(Validator.isString("北京", 2, 4));
        Assert.assertFalse(Validator.isString("a1123.2511", 2, 10));

    }

    @Test
    public void testIsUUID() throws Exception {

        Assert.assertTrue(Validator.isUUID(UUID.randomUUID().toString()));
        Assert.assertFalse(Validator.isUUID(UUID.randomUUID().toString().replace("-", "")));
    }

    @Test
    public void testIsUrl() throws Exception {
        Assert.assertTrue(Validator.isUrl("http://www.google.com"));
        Assert.assertTrue(Validator.isUrl("https://www.google.com"));
        Assert.assertTrue(Validator.isUrl("ftp://8.8.7.8"));
        Assert.assertFalse(Validator.isUrl("a1123.2511"));

    }

    @Test
    public void testIsDateTime() throws Exception {

        Assert.assertTrue(Validator.isDateTime("2015年11月23日"));
        Assert.assertTrue(Validator.isDateTime("2015年11月23日 01时02分22秒"));
        Assert.assertFalse(Validator.isDateTime("a1123.2511"));
    }

    @Test
    public void testIsBlank() throws Exception {

        Assert.assertTrue(Validator.isBlank(""));
        Assert.assertFalse(Validator.isBlank("a1123.2511"));
    }

    @Test
    public void testIsNotBlank() throws Exception {
        Assert.assertTrue(Validator.isNotBlank("not blank"));
        Assert.assertFalse(Validator.isNotBlank(""));

    }

    @Test
    public void testIsLength() throws Exception {
        Assert.assertTrue(Validator.isLength("not blank", 4, 12));
        Assert.assertFalse(Validator.isLength("", 1, 2));

    }

    @Test
    public void testCompareDate() throws Exception {
        Assert.assertTrue(Validator.compareDate("2015-01-24 01:01:23", "2015-01-23 01:01:23"));
        Assert.assertFalse(Validator.compareDate("2015-01-23 01:01:23", "2015-01-23 01:01:23"));

    }

    @Test
    public void testCompareDate1() throws Exception {
        Assert.assertTrue(Validator.compareDate("2015-01-24", "2015-01-23", DateFormatter.YYYY_MM_DD));
        Assert.assertFalse(Validator.compareDate("2015-01-23", "2015-01-23", DateFormatter.YYYY_MM_DD));

    }
}