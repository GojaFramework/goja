/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.rapid.qrcode;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-08-27 15:27
 * @since JDK 1.6
 */
public final class QRCode {


    /**
     * QRCode 生成器格式
     */
    private QRCodeFormat format = null;

    /**
     * 生成的 QRCode 图像对象
     */
    private BufferedImage qrcodeImage = null;

    /**
     * 生成的 QRCode 图片文件
     */
    private File qrcodeFile = null;

    /**
     * 返回生成的 QRCode 图像对象
     *
     * @return 生成的 QRCode 图像对象
     */
    public BufferedImage getQrcodeImage() {
        return qrcodeImage;
    }

    /**
     * 返回生成的 QRCode 图片文件
     *
     * @return 生成的 QRCode 图片文件
     */
    public File getQrcodeFile() {
        return qrcodeFile;
    }

    private QRCode() {

    }

    /**
     * 使用带默认值的「QRCode 生成器格式」来创建一个 QRCode 处理器。
     *
     * @param content 所要生成 QRCode 的内容
     * @return QRCode 处理器
     */
    public static QRCode create(final String content) {
        return create(content, QRCodeFormat.NEW());
    }

    /**
     * 使用指定的「QRCode 生成器格式」来创建一个 QRCode 处理器。
     *
     * @param content 所要生成 QRCode 的内容
     * @param format  QRCode 生成器格式
     * @return QRCode 处理器
     */
    public static QRCode create(final String content, QRCodeFormat format) {
        QRCode qrcode = new QRCode();
        qrcode.format = format;
        qrcode.qrcodeImage = toQRCode(content, format);
        return qrcode;
    }

    /**
     * 把指定的内容生成为一个 QRCode 的图片，之后保存到指定的文件中。
     *
     * @param f 指定的文件
     * @return QRCode 处理器
     */
    public QRCode toFile(String f) {
        return toFile(new File(f));
    }

    /**
     * 把指定的内容生成为一个 QRCode 的图片，之后保存到指定的文件中。
     *
     * @param qrcodeFile 指定的文件
     * @return QRCode 处理器
     */
    public QRCode toFile(File qrcodeFile) {
        try {
            if (!qrcodeFile.exists()) {
                Files.createParentDirs(qrcodeFile);
                qrcodeFile.createNewFile();
            }

            if (!ImageIO.write(this.qrcodeImage,
                    getSuffixName(qrcodeFile),
                    qrcodeFile)) {
                throw new RuntimeException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.qrcodeFile = qrcodeFile;
        return this;
    }

    /**
     * 把指定的内容生成为一个 QRCode 的图片，并在该图片中间添加上指定的图片；之后保存到指定的文件内。
     *
     * @param qrcodeFile QRCode 图片生成的指定的文件
     * @param appendFile 需要添加的图片
     * @return QRCode 处理器
     */
    public QRCode toFile(String qrcodeFile, String appendFile) {
        return toFile(new File(qrcodeFile), new File(appendFile));
    }

    /**
     * 把指定的内容生成为一个 QRCode 的图片，并在该图片中间添加上指定的图片；之后保存到指定的文件内。
     *
     * @param qrcodeFile QRCode 图片生成的指定的文件
     * @param appendFile 需要添加的图片
     * @return QRCode 处理器
     */
    public QRCode toFile(File qrcodeFile, File appendFile) {
        try {
            if (!qrcodeFile.exists()) {
                Files.createParentDirs(qrcodeFile);

                qrcodeFile.createNewFile();
            }

            appendImage(this.qrcodeImage, ImageIO.read(appendFile), this.format);
            if (!ImageIO.write(this.qrcodeImage,
                    getSuffixName(qrcodeFile),
                    qrcodeFile)) {
                throw new RuntimeException("Unexpected error writing image");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.qrcodeFile = qrcodeFile;
        return this;
    }

    private void appendImage(BufferedImage baseImage,
                             BufferedImage appendImage,
                             QRCodeFormat format) {
        Graphics gc = baseImage.getGraphics();
        gc.drawImage(appendImage,
                (baseImage.getWidth() - appendImage.getWidth()) / 2,
                (baseImage.getHeight() - appendImage.getHeight()) / 2,
                appendImage.getWidth(),
                appendImage.getHeight(),
                format.getBackGroundColor(),
                null);
    }

    /**
     * 使用带默认值的「QRCode 生成器格式」，把指定的内容生成为一个 QRCode 的图像对象。
     *
     * @param content 所需生成 QRCode 的内容
     * @return QRCode 的图像对象
     */
    public static BufferedImage toQRCode(String content) {
        return toQRCode(content, null);
    }

    /**
     * 使用指定的「QRCode生成器格式」，把指定的内容生成为一个 QRCode 的图像对象。
     *
     * @param content 所需生成 QRCode 的内容
     * @return QRCode 的图像对象
     */
    public static BufferedImage toQRCode(String content, QRCodeFormat format) {
        if (format == null) {
            format = QRCodeFormat.NEW();
        }

        content = new String(content.getBytes(Charset.forName(format.getEncode())));
        BitMatrix matrix;
        try {
            matrix = new QRCodeWriter().encode(content,
                    BarcodeFormat.QR_CODE,
                    format.getSize(),
                    format.getSize(),
                    format.getHints());
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int fgColor = format.getForeGroundColor().getRGB();
        int bgColor = format.getBackGroundColor().getRGB();
        BufferedImage image = new BufferedImage(width,
                height,
                ColorSpace.TYPE_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? fgColor : bgColor);
            }
        }
        return image;
    }

    /**
     * 从指定的 QRCode 图片文件中解析出其内容。
     *
     * @param qrcodeFile QRCode 文件
     * @return QRCode 中的内容
     */
    public static String from(String qrcodeFile) {
        if (qrcodeFile.startsWith("http://")
                || qrcodeFile.startsWith("https://")) {
            try {
                return from(new URL(qrcodeFile));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else {
            return from(new File(qrcodeFile));
        }
    }

    /**
     * 从指定的 QRCode 图片文件中解析出其内容。
     *
     * @param qrcodeFile QRCode 图片文件
     * @return QRCode 中的内容
     */
    public static String from(File qrcodeFile) {
        if(!qrcodeFile.exists()){
            return null;
        }
        try {
            BufferedImage image = ImageIO.read(qrcodeFile);
            return from(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从指定的 QRCode 图片链接中解析出其内容。
     *
     * @param qrcodeUrl QRCode 图片链接
     * @return QRCode 中的内容
     */
    public static String from(URL qrcodeUrl) {
        try {
            BufferedImage image = ImageIO.read(qrcodeUrl);
            return from(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从指定的 QRCode 图像对象中解析出其内容。
     *
     * @param qrcodeImage QRCode 图像对象
     * @return QRCode 中的内容
     */
    public static String from(BufferedImage qrcodeImage) {
        final LuminanceSource source = new BufferedImageLuminanceSource(qrcodeImage);
        final BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        String content;
        try {
            Result result = new QRCodeReader().decode(bitmap);
            content = result.getText();
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        } catch (ChecksumException e) {
            throw new RuntimeException(e);
        } catch (FormatException e) {
            throw new RuntimeException(e);
        }
        return content;
    }

    private String getSuffixName(File file) {
        String path = file.getAbsolutePath();

        if (Strings.isNullOrEmpty(path)) {
            return this.format.getImageFormat();
        }
        int pos = path.lastIndexOf('.');
        if (-1 == pos) {
            return this.format.getImageFormat();
        }
        return path.substring(pos + 1).toUpperCase();
    }
}
