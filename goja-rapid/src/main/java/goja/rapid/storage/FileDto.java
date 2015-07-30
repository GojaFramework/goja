package goja.rapid.storage;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.io.Files;
import goja.StringPool;
import org.apache.commons.lang3.StringUtils;

/**
 * <p> 附件文件处理 </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public final class FileDto {

    /**
     * 文件后缀
     */
    private final String ext;
    /**
     * 文件名称
     */
    private final String name;

    /**
     * 文件存放文件夹
     */
    private final String folder;
    /**
     * 文件存储相对路径
     */
    private final String storage;

    /**
     * 保存后的附件表ID
     */
    private long id;

    /**
     * 显示标题
     */
    private String title;

    @JSONCreator
    private FileDto(
            @JSONField(name = "extension") String ext,
            @JSONField(name = "name") String name,
            @JSONField(name = "floder") String folder,
            @JSONField(name = "path") String storage) {
        this.ext = ext;
        this.name = name;
        this.folder = StringUtils.endsWith(folder, StringPool.SLASH) ? folder : folder + StringPool.SLASH;
        this.storage = storage;
    }

    public static FileDto createFileInfo(String path, String folder, String storage) {
        return new FileDto(
                Files.getFileExtension(path),
                Files.getNameWithoutExtension(path),
                folder, storage
        );
    }



    public String getExt() {
        return ext;
    }

    public String getName() {
        return name;
    }

    public String getFolder() {
        return folder;
    }

    public String getStorage() {
        return storage;
    }

    @Override
    public String toString() {
        return folder + name + StringPool.DOT + ext;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
