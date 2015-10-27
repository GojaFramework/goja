/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc.render.captcha;

import com.jfinal.core.Const;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.render.Render;
import goja.core.StringPool;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Random;

/**
 * 验证码Render，这个验证码Render在构造函数里就已经创建好了随机码以及md5散列后的随机码。
 * 调用方式如下：
 * CaptchaRender captchaRender = new CaptchaRender();
 * String md5RandonCode = captchaRender.getMd5RandonCode();
 * 保存md5RandonCode到session、cookie或者其他地方
 * render(captchaRender);
 * 基于JFinal的版本修改。
 */
public class CaptchaRender extends Render {

    /**
     * 默认存储时使用的key,将md5散列后的随机码保存至session，cookie时使用。
     */
    public static final  String   DEFAULT_CAPTCHA_MD5_CODE_KEY = "_CAPTCHA_MD5_CODE_";
    private static final long     serialVersionUID             = -7599510915228560611L;
    /**
     * 随机码生成字典
     */
    private static final String[] strArr                       = {"3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y"};
    /**
     * 图片宽度
     */
    private final int imgWidth;

    /**
     * 图片高度
     */
    private final int imgHeight;

    /**
     * 随机生成字符数量
     */
    private final int imgRandNumber;

    /**
     * 生成的随机码
     */
    private final String randonCode;

    /**
     * md5散列后的随机码
     */
    private final String md5RandonCode;

    /**
     * 构造函数,随机生成6个字符。
     */
    public CaptchaRender() {
        this(6);
    }

    /**
     * 构造函数
     *
     * @param imgRandNumber 随机生成多少个字符,最少4个字符。
     */
    public CaptchaRender(int imgRandNumber) {
        if (imgRandNumber < 4) {
            imgRandNumber = 4;
        }
        this.imgWidth = 16 * imgRandNumber + 12;
        this.imgHeight = 26;
        this.imgRandNumber = imgRandNumber;
        this.randonCode = generateRandonCode();
        this.md5RandonCode = encrypt(randonCode);
    }

    /**
     * 使用md5散列字符串
     *
     * @param srcStr 输入的字符串
     * @return 加密后的字符串
     */
    private static String encrypt(String srcStr) {
        try {
            String result = "";
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(srcStr.getBytes(Const.DEFAULT_ENCODING));
            for (byte b : bytes) {
                String hex = Integer.toHexString(b & 0xFF).toUpperCase();
                result += ((hex.length() == 1) ? StringPool.ZERO : StringPool.EMPTY) + hex;
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 验证码检查
     *
     * @param controller      控制器
     * @param inputRandomCode 用户输入的验证码
     * @return 若二者一致，返回true，否则返回false
     */
    public static boolean validate(Controller controller, String inputRandomCode) {
        if (StrKit.isBlank(inputRandomCode))
            return false;
        try {
            inputRandomCode = inputRandomCode.toUpperCase();
            inputRandomCode = encrypt(inputRandomCode);
            return inputRandomCode.equals(controller.getCookie(DEFAULT_CAPTCHA_MD5_CODE_KEY));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取md5散列后的验证码，调用发需妥善保存此验证码。
     *
     * @return md5散列后的验证码
     */
    public String getMd5RandonCode() {
        return this.md5RandonCode;
    }

    /**
     * 依据字典生成随即码
     *
     * @return 随机码
     */
    private String generateRandonCode() {
        // 生成随机类
        Random random = new Random();
        String sRand = StringPool.EMPTY;
        for (int i = 0; i < imgRandNumber; i++) {
            String rand = String.valueOf(strArr[random.nextInt(strArr.length)]);
            sRand += rand;
        }
        return sRand;
    }

    /**
     * 渲染图片
     */
    public void render() {
        BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
        drawGraphic(image);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        Cookie cookie = new Cookie(DEFAULT_CAPTCHA_MD5_CODE_KEY, getMd5RandonCode());
        cookie.setMaxAge(-1);
        cookie.setPath(StringPool.SLASH);
        response.addCookie(cookie);
        ServletOutputStream sos = null;
        try {
            sos = response.getOutputStream();
            ImageIO.write(image, "jpeg", sos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (sos != null)
                try {
                    sos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 绘制验证码
     *
     * @param image BufferedImage对象
     */
    private void drawGraphic(BufferedImage image) {
        // 获取图形上下文
        Graphics g = image.createGraphics();
        // 生成随机类
        Random random = new Random();
        // 设定背景色
        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, imgWidth, imgHeight);
        // 设定字体
        g.setFont(new Font("Times New Roman", Font.PLAIN, 18));

        // 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(imgWidth);
            int y = random.nextInt(imgHeight);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }

        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        //取随机产生的认证码(img_randNumber位数字)
        for (int i = 0; i < imgRandNumber; i++) {
            String rand = String.valueOf(this.randonCode.charAt(i));
            // 将认证码显示到图象中
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            // 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
            g.drawString(rand, 16 * i + 6, 21);
        }
        // 图象生效
        g.dispose();
    }

    /**
     * 生成随机颜色
     *
     * @param fc 颜色值
     * @param bc 颜色值2
     * @return 颜色对象
     */
    private static Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}


