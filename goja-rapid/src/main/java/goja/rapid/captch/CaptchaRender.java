package goja.rapid.captch;

import com.github.bingoohuang.patchca.background.BackgroundFactory;
import com.github.bingoohuang.patchca.color.ColorFactory;
import com.github.bingoohuang.patchca.color.RandomColorFactory;
import com.github.bingoohuang.patchca.custom.ConfigurableCaptchaService;
import com.github.bingoohuang.patchca.filter.predefined.CurvesRippleFilterFactory;
import com.github.bingoohuang.patchca.filter.predefined.DiffuseRippleFilterFactory;
import com.github.bingoohuang.patchca.filter.predefined.DoubleRippleFilterFactory;
import com.github.bingoohuang.patchca.filter.predefined.MarbleRippleFilterFactory;
import com.github.bingoohuang.patchca.filter.predefined.WobbleRippleFilterFactory;
import com.github.bingoohuang.patchca.font.RandomFontFactory;
import com.github.bingoohuang.patchca.service.Captcha;
import com.github.bingoohuang.patchca.text.renderer.BestFitTextRenderer;
import com.github.bingoohuang.patchca.text.renderer.TextRenderer;
import com.github.bingoohuang.patchca.word.RandomWordFactory;
import com.jfinal.core.Controller;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.StrKit;
import com.jfinal.render.Render;
import goja.core.StringPool;
import goja.core.app.GojaConfig;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class CaptchaRender extends Render {
    private static final Logger logger = LoggerFactory.getLogger(CaptchaRender.class);


    private final ConfigurableCaptchaService configurableCaptchaService = new ConfigurableCaptchaService();


    public static String captchStore = GojaConfig.getProperty("app.captch.store", "cookie");


    /**
     * @param backgroundFactory 背景颜色工厂
     * @param filter 滤镜特效
     * @param bgColor 背景色
     * @param drawColor 验证码字符颜色
     * @param drawBgColor 背景元素的颜色
     * @param randomColor 随机颜色
     * @param artifactNum 噪点数量
     * @param lineNum 线条数量
     * @param fontMinNum 最小字数
     * @param fontMaxNum 自大字数
     * @param fontMinSize 最小字体大小
     * @param fontMaxSize 最大字体大小
     * @param width 宽
     * @param height 高
     * @param topMargin 顶部边距
     * @param bottomMargin 底步边距
     * @param code 字符
     */
    private CaptchaRender(BackgroundFactory backgroundFactory, FilterFactory filter,
                          Color bgColor, final Color drawColor, Color drawBgColor,
                          boolean randomColor, int artifactNum, int lineNum,
                          int fontMinNum, int fontMaxNum, int fontMinSize, int fontMaxSize,
                          int width, int height, int topMargin, int bottomMargin, String code) {


        // 颜色创建工厂,使用一定范围内的随机色

        ColorFactory colorFactory;
        if (randomColor)
            colorFactory = new RandomColorFactory();
        else
            colorFactory = new ColorFactory() {

                public Color getColor(int index) {
                    return drawColor;//new Color(118,102,102);
                }
            };

        configurableCaptchaService.setColorFactory(colorFactory);

        // 随机字体生成器
        RandomFontFactory fontFactory = new RandomFontFactory();
        fontFactory.setMaxSize(fontMaxSize);
        fontFactory.setMinSize(fontMinSize);
        configurableCaptchaService.setFontFactory(fontFactory);

        // 随机字符生成器,去除掉容易混淆的字母和数字,如o和0等
        RandomWordFactory wordFactory = new RandomWordFactory();
        wordFactory.setCharacters(code);
        wordFactory.setMaxLength(fontMaxNum);
        wordFactory.setMinLength(fontMinNum);
        configurableCaptchaService.setWordFactory(wordFactory);

        // 自定义验证码图片背景
        if (backgroundFactory == null) {
            backgroundFactory = new SimpleBackgroundFactory(bgColor, randomColor ? null : drawBgColor, artifactNum, lineNum);
        }
        configurableCaptchaService.setBackgroundFactory(backgroundFactory);

        // 图片滤镜设置
        int filterNum = filter == null ? RandomUtils.nextInt(4) : filter.value();

        switch (filterNum) {
            case 0:
                configurableCaptchaService.setFilterFactory(new CurvesRippleFilterFactory(configurableCaptchaService.getColorFactory()));
                break;
            case 1:
                configurableCaptchaService.setFilterFactory(new MarbleRippleFilterFactory());
                break;
            case 2:
                configurableCaptchaService.setFilterFactory(new DoubleRippleFilterFactory());
                break;
            case 3:
                configurableCaptchaService.setFilterFactory(new WobbleRippleFilterFactory());
                break;
            case 4:
                configurableCaptchaService.setFilterFactory(new DiffuseRippleFilterFactory());
                break;
            default:
                //默认效果
                configurableCaptchaService.setFilterFactory(new CurvesRippleFilterFactory(configurableCaptchaService.getColorFactory()));
                break;
        }

        // 文字渲染器设置
        TextRenderer textRenderer = new BestFitTextRenderer();
        textRenderer.setBottomMargin(bottomMargin);
        textRenderer.setTopMargin(topMargin);
        configurableCaptchaService.setTextRenderer(textRenderer);

        // 验证码图片的大小
        configurableCaptchaService.setWidth(width);
        configurableCaptchaService.setHeight(height);

    }

    /**
     * you can  rewrite this  render
     * 输出
     */
    public void render() {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        //初始化
        ServletOutputStream outputStream = null;

        // 得到验证码对象,有验证码图片和验证码字符串
        Captcha captcha = configurableCaptchaService.getCaptcha();
        // 取得验证码字符串放入Session
        String captchaCode = captcha.getChallenge();
        if (logger.isDebugEnabled()) {
            logger.debug("captcha:" + captchaCode);
        }
        if (StringUtils.equals(captchStore, "cookie")) {
            Cookie cookie = new Cookie(GojaConfig.getAppName() + "_CAPTCHA_CODE_", HashKit.md5(captchaCode.toLowerCase()));
            cookie.setMaxAge(-1);
            cookie.setPath(StringPool.SLASH);
            response.addCookie(cookie);
        } else {

            HttpSession session = request.getSession();
            session.setAttribute(GojaConfig.getAppName() + "_CAPTCHA_CODE_", HashKit.md5(captchaCode.toLowerCase()));
        }


        // 取得验证码图片并输出
        BufferedImage bufferedImage = captcha.getImage();

        try {
            outputStream = response.getOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (outputStream != null)
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }


    public static boolean validate(Controller controller, String inputRandomCode, String randomCodeKey) {

        if (StrKit.isBlank(inputRandomCode))
            return false;
        inputRandomCode = HashKit.md5(inputRandomCode.toLowerCase());

        if (StringUtils.equals(captchStore, "cookie")) {
            try {
                return inputRandomCode.equals(controller.getCookie(GojaConfig.getAppName() + "_CAPTCHA_MD5_CODE_"));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            try {
                String captchCode = controller.getSessionAttr(GojaConfig.getAppName() + "_CAPTCHA_CODE_");
                return StringUtils.equals(captchCode, inputRandomCode);
            } catch (Exception e) {
                return false;
            } finally {
                controller.removeSessionAttr(GojaConfig.getAppName() + "_CAPTCHA_CODE_");
            }
        }
    }


    public static class Builder {
        private BackgroundFactory backgroundFactory;

        private Color drawColor   = new Color(0, 0, 0);
        private Color drawBgColor = new Color(102, 102, 102);
        private Color bgColor;


        private String code         = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789";
        private int    fontMinNum   = 4;
        private int    fontMaxNum   = 4;
        private int    fontMinSize  = 20;
        private int    fontMaxSize  = 20;
        private int    topMargin    = 1;
        private int    bottomMargin = 1;
        private int    width        = 118;
        private int    height       = 41;

        private int artifactNum = 50;
        private int lineNum     = 0;


        private FilterFactory filter;

        private boolean randomColor = false;

        public Builder backgroundFactory(BackgroundFactory backgroundFactory) {
            this.backgroundFactory = backgroundFactory;
            return this;
        }

        public Builder drawColor(Color drawColor) {
            this.drawColor = drawColor;
            return this;
        }

        public Builder drawBgColor(Color drawBgColor) {
            this.drawBgColor = drawBgColor;
            return this;
        }

        public Builder bgColor(Color bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public Builder fontMinNum(int fontMinNum) {
            this.fontMinNum = fontMinNum;
            return this;
        }

        public Builder fontMaxNum(int fontMaxNum) {
            this.fontMaxNum = fontMaxNum;
            return this;
        }

        public Builder fontMinSize(int fontMinSize) {
            this.fontMinSize = fontMinSize;
            return this;
        }

        public Builder fontMaxSize(int fontMaxSize) {
            this.fontMaxSize = fontMaxSize;
            return this;
        }

        public Builder topMargin(int topMargin) {
            this.topMargin = topMargin;
            return this;
        }

        public Builder bottomMargin(int bottomMargin) {
            this.bottomMargin = bottomMargin;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        /**
         * @param artifactNum 噪点数量
         * @return 噪点数量
         */
        public Builder artifactNum(int artifactNum) {
            this.artifactNum = artifactNum;
            return this;
        }

        public Builder lineNum(int lineNum) {
            this.lineNum = lineNum;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder filter(FilterFactory filter) {
            this.filter = filter;
            return this;
        }

        public Builder randomColor(boolean randomColor) {
            this.randomColor = randomColor;
            return this;
        }

        public CaptchaRender build() {
            return new CaptchaRender(backgroundFactory, filter, bgColor, drawColor, drawBgColor, randomColor,
                                     artifactNum, lineNum,
                                     fontMinNum, fontMaxNum, fontMinSize, fontMaxSize,
                                     width, height, topMargin, bottomMargin, code);
        }


    }


    public enum FilterFactory {
        //曲面
        Curves(0),
        //大理石纹
        Marble(1),
        //对折
        Double(2),
        //颤动
        Wobble(3),
        //扩散
        Diffuse(4);
        private int value;

        FilterFactory(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }

}
