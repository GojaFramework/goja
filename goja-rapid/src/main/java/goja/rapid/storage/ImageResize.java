package goja.rapid.storage;

/**
 * <p> 图片切割对象</p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class ImageResize {

    private final int width;
    private final int height;

    private ImageResize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static ImageResize builder(int w, int h) {
        return new ImageResize(w, h);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return width + "x" + height;
    }
}
