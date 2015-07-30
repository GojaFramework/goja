package goja.rapid.storage;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import static goja.StringPool.SLASH;
//import static goja.date.DateProvider.DEFAULT;

/**
 * <p>
 * 系统存储，负责每个上传文件的处理
 * </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public abstract class Storage {

    private static final Logger logger = LoggerFactory.getLogger(Storage.class);

    /**
     * 按年-月-日/小时/分钟的方式进行文件存储
     */
    private final static SimpleDateFormat FMT_FN = new SimpleDateFormat("yyyyMMdd/HH/mmss_");

    /**
     * 获取文件存储目录
     *
     * @return 目录
     */
    protected abstract String getFolder();

    /**
     * 获取文件存储相对路径.
     *
     * @return 文件存储相对路径
     */
    protected abstract String getPath();


    /**
     * 读取文件数据
     *
     * @param path 路径
     * @return 读取文件
     * @throws java.io.FileNotFoundException 文件不存在的异常
     */
    public FileInputStream read(String path) throws FileNotFoundException {
        return new FileInputStream(getFolder() + path);
    }

    /**
     * 读取文件
     *
     * @param path 路径
     * @return 读取文件
     * @throws java.io.FileNotFoundException 文件不存在的异常
     */
    public File readFile(String path) throws FileNotFoundException {
        return new File(getFolder() + path);
    }

    /**
     * 判断文件是否存在
     *
     * @param path 文件
     * @return 文件是否存在
     */
    public boolean exist(String path) {
        return new File(getFolder() + path).exists();
    }

    /**
     * 保存文件，并返回对应的文件URI
     *
     * @param f 文件
     * @return 文件URI
     */
    public Optional<FileDto> save(File f) {
        String ext = '.' + Files.getFileExtension(f.getName()).toLowerCase();
        String parent_dir = FMT_FN.format(DateTime.now().toDate());
        final String filename = RandomStringUtils.randomAlphanumeric(6);
        String file_folder_path = parent_dir + filename;
        String file_path = getFolder() + file_folder_path + ext;
        List<ImageResize> resizes = getResizes();
        if (resizes != null && !resizes.isEmpty()) {
            String resize_url;
            File dest;
            // 图片按照分辨率计算大小
            for (ImageResize resize : resizes) {
                resize_url = getFolder() + file_folder_path + '_' + resize + ext;
                try {
                    dest = createParentDirs(resize_url);
                    Thumbnails.of(f).size(resize.getWidth(), resize.getHeight()).toFile(dest);
                } catch (IOException e) {
                    logger.error("Generate images capture failure", e);
                    return Optional.absent();
                }
            }
        }
        // 增加如果已经登录的用户，将文件存储到指定用户的目录中。
        try {
            final File dest = createParentDirs(file_path);
            Files.copy(f, dest);
        } catch (IOException e) {
            logger.error("save file has error!", e);
            return Optional.absent();
        }

        final String folder = getPath() + file_folder_path.substring(0, file_folder_path.lastIndexOf(SLASH));
        return Optional.of(FileDto.createFileInfo(file_path, folder, file_folder_path + ext));
    }


    /**
     * 判断路径是否需要创建父级目录，如果路径不存在，则创建所有父级目录.
     *
     * @param pathname 路径地址
     * @return 文件句柄对象
     * @throws java.io.IOException 创建文件或者文件系统异常
     */
    private File createParentDirs(String pathname) throws IOException {
        File dest = new File(pathname);
        if (!dest.getParentFile().exists())
            Files.createParentDirs(dest);
        return dest;
    }

    /**
     * 根据路径来删除文件
     *
     * @param path 路径
     */
    public void delete(String path) {
        if (Strings.isNullOrEmpty(path)) {
            return;
        }
        String base_path = getFolder();
        File dest = new File(base_path + path);
        try {
            FileUtils.forceDelete(dest);
            List<ImageResize> resizes = getResizes();
            if (resizes != null && !resizes.isEmpty()) {
                // 图片按照分辨率计算大小
                for (ImageResize resize : resizes) {
                    String filename = Files.getNameWithoutExtension(path);
                    String resize_url = dest.getAbsolutePath().replace(filename, filename + '_' + resize);
                    FileUtils.forceDelete(new File(resize_url));
                }
            }
        } catch (IOException e) {
            logger.error("Failed to delete file", e);
        }

    }

    public abstract List<ImageResize> getResizes();
}
