/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.libs;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * <p>
 * Images Utils.
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-04-04 10:16
 * @since JDK 1.6
 */
public class Images {

    /**
     * Resize an image
     *
     * @param originalImage The image file
     * @param to            The destination file
     * @param w             The new width (or -1 to proportionally resize)
     * @param h             The new height (or -1 to proportionally resize)
     */
    public static void resize(File originalImage, File to, int w, int h) {
        resize(originalImage, to, w, h, false);
    }

    /**
     * Resize an image
     *
     * @param originalImage The image file
     * @param to            The destination file
     * @param w             The new width (or -1 to proportionally resize) or the maxWidth if keepRatio is true
     * @param h             The new height (or -1 to proportionally resize) or the maxHeight if keepRatio is true
     * @param keepRatio     : if true, resize will keep the original image ratio and use w and h as max dimensions
     */
    public static void resize(File originalImage, File to, int w, int h, boolean keepRatio) {
        try {
            BufferedImage source = ImageIO.read(originalImage);
            int owidth = source.getWidth();
            int oheight = source.getHeight();
            double ratio = (double) owidth / oheight;

            int maxWidth = w;
            int maxHeight = h;

            if (w < 0 && h < 0) {
                w = owidth;
                h = oheight;
            }
            if (w < 0 && h > 0) {
                w = (int) (h * ratio);
            }
            if (w > 0 && h < 0) {
                h = (int) (w / ratio);
            }

            if (keepRatio) {
                h = (int) (w / ratio);
                if (h > maxHeight) {
                    h = maxHeight;
                    w = (int) (h * ratio);
                }
                if (w > maxWidth) {
                    w = maxWidth;
                    h = (int) (w / ratio);
                }
            }

            String mimeType = "image/jpeg";
            if (to.getName().endsWith(".png")) {
                mimeType = "image/png";
            }
            if (to.getName().endsWith(".gif")) {
                mimeType = "image/gif";
            }

            // out
            BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Image srcSized = source.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            Graphics graphics = dest.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, w, h);
            graphics.drawImage(srcSized, 0, 0, null);
            ImageWriter writer = ImageIO.getImageWritersByMIMEType(mimeType).next();
            ImageWriteParam params = writer.getDefaultWriteParam();
            FileImageOutputStream toFs = new FileImageOutputStream(to);
            writer.setOutput(toFs);
            IIOImage image = new IIOImage(dest, null, null);
            writer.write(null, image, params);
            toFs.flush();
            toFs.close();
            writer.dispose();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Crop an image
     *
     * @param originalImage The image file
     * @param to            The destination file
     * @param x1            The new x origin
     * @param y1            The new y origin
     * @param x2            The new x end
     * @param y2            The new y end
     */
    public static void crop(File originalImage, File to, int x1, int y1, int x2, int y2) {
        try {
            BufferedImage source = ImageIO.read(originalImage);

            String mimeType = "image/jpeg";
            if (to.getName().endsWith(".png")) {
                mimeType = "image/png";
            }
            if (to.getName().endsWith(".gif")) {
                mimeType = "image/gif";
            }
            int width = x2 - x1;
            int height = y2 - y1;

            // out
            BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Image croppedImage = source.getSubimage(x1, y1, width, height);
            Graphics graphics = dest.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.drawImage(croppedImage, 0, 0, null);
            ImageWriter writer = ImageIO.getImageWritersByMIMEType(mimeType).next();
            ImageWriteParam params = writer.getDefaultWriteParam();
            writer.setOutput(new FileImageOutputStream(to));
            IIOImage image = new IIOImage(dest, null, null);
            writer.write(null, image, params);
            writer.dispose();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Encode an image to base64 using a data: URI
     *
     * @param image The image file
     * @return The base64 encoded value
     * @throws java.io.IOException
     */
    public static String toBase64(File image) throws IOException {
        return "data:" + MimeTypes.getMimeType(image.getName()) + ";base64," + Codec.encodeBASE64(IO.readContent(image));
    }

    /**
     * 将指定的图片加入到当前图片中的指定位置. 即向图片中打水印.
     *
     * @param file     要添加的图片文件。
     * @param position 要添加的图片在当前图片中的位置. <br />
     *                 其取值范围为1-9之间的整数(默认为9)。分别代表：左上、上中、右上、左中、正中、右中、左下、中下、右下
     */
    public void add(File dest, File file, int position) {
        if (dest.exists() && !dest.isDirectory()) {
            String fn = file.getName().toLowerCase();
            int opacityType = fn.endsWith(".png") || fn.endsWith("gif") ?
                    BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
            Image destImg;
            try {
                destImg = ImageIO.read(dest);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            int width = destImg.getWidth(null);
            int height = destImg.getHeight(null);

            final BufferedImage image = new BufferedImage(width, height, opacityType);

            Image src;
            try {
                src = ImageIO.read(file);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            int ww = src.getWidth(null);
            int hh = src.getHeight(null);
            int WW, HH;
            switch (position) {
                case 1:
                    WW = 0;
                    HH = 0;
                    break;
                case 2:
                    WW = (width - ww) / 2;
                    HH = 0;
                    break;
                case 3:
                    WW = width - ww;
                    HH = 0;
                    break;
                case 4:
                    WW = 0;
                    HH = (height - hh) / 2;
                    break;
                case 5:
                    WW = (width - ww) / 2;
                    HH = (height - hh) / 2;
                    break;
                case 6:
                    WW = width - ww;
                    HH = (height - hh) / 2;
                    break;
                case 7:
                    WW = 0;
                    HH = height - hh;
                    break;
                case 8:
                    WW = (width - ww) / 2;
                    HH = height - hh;
                    break;
                default:
                    WW = width - ww;
                    HH = height - hh;
            }
            Graphics g = image.createGraphics();
            g.drawImage(src, WW, HH, ww, hh, null);
            g.dispose();
        }
    }

}
