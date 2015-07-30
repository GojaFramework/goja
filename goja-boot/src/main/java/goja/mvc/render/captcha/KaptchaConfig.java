package goja.mvc.render.captcha;

import goja.StringPool;
import kaptcha.Constants;

import java.util.Properties;

public class KaptchaConfig {

    private final Properties properties = new Properties();

    private KaptchaConfig() {
    }

    public static KaptchaConfig build() {
        return new KaptchaConfig();
    }

    public KaptchaConfig noBorder() {
        properties.setProperty(Constants.KAPTCHA_BORDER, StringPool.NO);
        return this;
    }

    public KaptchaConfig borderColor(String borderColor) {
        properties.setProperty(Constants.KAPTCHA_BORDER_COLOR, borderColor);
        return this;
    }

    public KaptchaConfig borderThickness(int borderThickness) {
        properties.setProperty(Constants.KAPTCHA_BORDER_THICKNESS, String.valueOf(borderThickness));
        return this;
    }

    public KaptchaConfig imageWidth(int imageWidth) {
        properties.setProperty(Constants.KAPTCHA_IMAGE_WIDTH, String.valueOf(imageWidth));
        return this;
    }

    public KaptchaConfig imageHeight(int imageHeight) {
        properties.setProperty(Constants.KAPTCHA_IMAGE_HEIGHT, String.valueOf(imageHeight));
        return this;
    }

    public KaptchaConfig charLength(int charLength) {
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, String.valueOf(charLength));
        return this;
    }

    public KaptchaConfig fontSize(int fontSize) {
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE, String.format("%spx", fontSize));
        return this;
    }

    public KaptchaConfig fontColor(String fontColor) {
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR, fontColor);
        return this;
    }

    public KaptchaConfig CharSpace(int charSpace) {
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_SPACE, String.valueOf(charSpace));
        return this;
    }

    public KaptchaConfig imageStyleFishEye() {
        properties.setProperty(Constants.KAPTCHA_OBSCURIFICATOR_IMPL, "kaptcha.impl.FishEyeGimpy");
        return this;
    }

    public KaptchaConfig imageStyleShadow() {
        properties.setProperty(Constants.KAPTCHA_OBSCURIFICATOR_IMPL, "kaptcha.impl.ShadowGimpy");
        return this;
    }

    public KaptchaConfig backgroundCleanForm(String formColor) {
        properties.setProperty(Constants.KAPTCHA_BACKGROUND_CLR_FROM, formColor);
        return this;
    }

    public KaptchaConfig backgroundCleanTo(String toColor) {
        properties.setProperty(Constants.KAPTCHA_BACKGROUND_CLR_TO, toColor);
        return this;
    }

    public Properties getProperties() {
        return properties;
    }
}
