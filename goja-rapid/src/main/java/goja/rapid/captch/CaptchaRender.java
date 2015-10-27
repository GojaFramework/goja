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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class CaptchaRender extends Render {
    private static final Logger logger = LoggerFactory.getLogger(CaptchaRender.class);


    private String code          = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789";
    private int    font_min_num  = 4;
    private int    font_max_num  = 4;
    private int    font_min_size = 20;
    private int    font_max_size = 20;
    private double x_amplitude   = 1.6;
    private double y_amplitude   = 0.8;
    private int    top_margin    = 1;
    private int    bottom_margin = 1;
    private int    width         = 118;
    private int    height        = 41;

    private        ConfigurableCaptchaService configurableCaptchaService = new ConfigurableCaptchaService();
    private        BackgroundFactory          backgroundFactory          = null;
    //滤镜特效
    private        FilterFactory              filter                     = null;
    private static Random                     random                     = new Random();
    /**
     * 背景色
     */
    private        Color                      bgColor                    = null;

    /**
     * 验证码字符颜色
     */
    private Color drawColor   = new Color(0, 0, 0);
    /**
     * 背景元素的颜色
     */
    private Color drawBgColor = new Color(102, 102, 102);

    private boolean randomColor = false;
    /**
     * 噪点数量
     */
    private int     artifactNum = 50;

    private int lineNum = 0;


    public static String captchStore = GojaConfig.getProperty("app.captch.store", "cookie");

    private void initCaptchService() {

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
        fontFactory.setMaxSize(font_max_size);
        fontFactory.setMinSize(font_min_size);
        configurableCaptchaService.setFontFactory(fontFactory);

        // 随机字符生成器,去除掉容易混淆的字母和数字,如o和0等
        RandomWordFactory wordFactory = new RandomWordFactory();
        wordFactory.setCharacters(code);
        wordFactory.setMaxLength(font_max_num);
        wordFactory.setMinLength(font_min_num);
        configurableCaptchaService.setWordFactory(wordFactory);

        // 自定义验证码图片背景
        if (backgroundFactory == null) {
            backgroundFactory = new SimpleBackgroundFactory(bgColor, randomColor ? null : drawBgColor, artifactNum, lineNum);
        }
        configurableCaptchaService.setBackgroundFactory(backgroundFactory);

        // 图片滤镜设置
        int filterNum;
        if (filter == null) {
            filterNum = random.nextInt(4);
        } else
            filterNum = filter.value();

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
        textRenderer.setBottomMargin(bottom_margin);
        textRenderer.setTopMargin(top_margin);
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
        initCaptchService();
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


    public BackgroundFactory getBackgroundFactory() {
        return backgroundFactory;
    }

    public void setBackgroundFactory(BackgroundFactory backgroundFactory) {
        this.backgroundFactory = backgroundFactory;
    }

    public Color getDrawColor() {
        return drawColor;
    }

    public void setDrawColor(Color drawColor) {
        this.drawColor = drawColor;
    }

    public Color getDrawBgColor() {
        return drawBgColor;
    }

    public void setDrawBgColor(Color drawBgColor) {
        this.drawBgColor = drawBgColor;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    public void setFontNum(int font_min_num, int font_max_num) {
        this.font_min_num = font_min_num;
        this.font_max_num = font_max_num;
    }


    public void setFontSize(int font_min_size, int font_max_size) {
        this.font_min_size = font_min_size;
        this.font_max_size = font_max_size;
    }

    public void setFontMargin(int top_margin, int bottom_margin) {
        this.top_margin = top_margin;
        this.bottom_margin = bottom_margin;
    }

    public void setImgSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setArtifactNum(int artifactNum) {
        this.artifactNum = artifactNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setFilter(FilterFactory filter) {
        this.filter = filter;
    }

    public void setRandomColor(boolean randomColor) {
        this.randomColor = randomColor;
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
