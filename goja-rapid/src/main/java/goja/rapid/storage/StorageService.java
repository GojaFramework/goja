package goja.rapid.storage;

import com.google.common.collect.Lists;
import com.jfinal.kit.PathKit;
import goja.StringPool;

import java.io.File;
import java.util.List;

import static java.io.File.separator;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class StorageService extends Storage {

    public static final String FS_DIR = "files";

    /**
     * 资源文件
     */
    public final static StorageService RES_STORAGE    = new StorageService("res", null);
    /**
     * 普通文件
     */
    public final static StorageService NORMAL_STORAGE = new StorageService("normal", null);
    /**
     * 视频文件
     */
    public final static StorageService IMAGE_STORAGE  = new StorageService("image",
            Lists.newArrayList(ImageResize.builder(200, 200), ImageResize.builder(120, 120)));


    private final String            file_path;
    private final String            file_url;
    private final List<ImageResize> file_resizes;

    /**
     * 私有构造函数,确保对象只能通过单例方法来调用.
     */
    private StorageService(String ext, List<ImageResize> file_resizes) {
        this.file_resizes = file_resizes;
        this.file_path = PathKit.getWebRootPath() + separator + FS_DIR + separator + ext + separator;
        this.file_url = FS_DIR + StringPool.SLASH + ext + StringPool.SLASH;
    }

    /**
     * 构建用户存储服务
     *
     * @param shortName 用户别名，唯一
     * @return 存储服务
     */
    public static StorageService createUserStorageService(String shortName) {
        return new StorageService("home" + separator + shortName, null);
    }


    @Override
    protected String getFolder() {
        return file_path;
    }

    @Override
    protected String getPath() {
        return file_url;
    }


    @Override
    public List<ImageResize> getResizes() {
        return file_resizes;
    }

}
