package goja.rapid.ueditor.kit;


import com.google.common.io.Files;

import com.jfinal.kit.PathKit;
import goja.core.StringPool;
import goja.core.app.GojaConfig;
import goja.core.app.GojaPropConst;
import goja.rapid.ueditor.UEConst;
import goja.rapid.ueditor.define.AppInfo;
import goja.rapid.ueditor.define.BaseState;
import goja.rapid.ueditor.define.State;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

public final class StorageManager {
    public static final int BUFFER_SIZE = 8192;

    private StorageManager() {

    }

    public static State saveBinaryFile(byte[] data, String path) {
        String saveFoloder = getUeFolder();

        File file = new File(saveFoloder + path);

        State state = valid(file);

        if (!state.isSuccess()) {
            return state;
        }

        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            bos.write(data);
            bos.flush();
            bos.close();
        } catch (IOException ioe) {
            return new BaseState(false, AppInfo.IO_ERROR);
        }

        state = new BaseState(true, file.getAbsolutePath());
        state.putInfo(UEConst.SIZE, data.length);
        state.putInfo(UEConst.TITLE, file.getName());
        return state;
    }

    /**
     * @return UE 文件存储路径
     */
    public static String getUeFolder() {
        // UE上传路径
        String saveFoloder = GojaConfig.getProperty(GojaPropConst.UE_UPLOAD_PATH, "upload");
        // 是否为绝对路径
        boolean absolute_flag = StringUtils.startsWith(saveFoloder, "://") || StringUtils.startsWith(saveFoloder, StringPool.SLASH);
        if (!absolute_flag) {
            //不是绝对路径，则设置根目录
            saveFoloder = PathKit.getWebRootPath() + File.separator + saveFoloder;
        }
        saveFoloder = (StringUtils.endsWith(saveFoloder, File.separator)) ? saveFoloder : saveFoloder + File.separator;
        try {
            Files.createParentDirs(new File(saveFoloder + ".ueconfig"));
        } catch (IOException e) {
            throw new RuntimeException("创建UE目录出现问题，无法存储上传的文件！");
        }
        return saveFoloder;
    }

    /**
     * @param is           文件流
     * @param relativePath 相对存储路径
     * @param maxSize      最大文件大小
     * @return 存储状态
     */
    public static State saveFileByInputStream(InputStream is, String relativePath, long maxSize) {
// UE上传路径
        String saveFoloder = getUeFolder();
        State state;

        File tmpFile = getTmpFile();

        byte[] dataBuf = new byte[2048];
        BufferedInputStream bis = new BufferedInputStream(is, StorageManager.BUFFER_SIZE);

        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(tmpFile), StorageManager.BUFFER_SIZE);

            int count;
            while ((count = bis.read(dataBuf)) != -1) {
                bos.write(dataBuf, 0, count);
            }
            bos.flush();
            bos.close();

            if (tmpFile.length() > maxSize) {
                FileUtils.deleteQuietly(tmpFile);
                return new BaseState(false, AppInfo.MAX_SIZE);
            }

            state = saveTmpFile(tmpFile, saveFoloder + relativePath);

            if (!state.isSuccess()) {
                FileUtils.deleteQuietly(tmpFile);
            }

            return state;

        } catch (IOException e) {
        }
        return new BaseState(false, AppInfo.IO_ERROR);
    }

    public static State saveFileByInputStream(InputStream is, String path) {
        State state;

        File tmpFile = getTmpFile();

        byte[] dataBuf = new byte[2048];
        BufferedInputStream bis = new BufferedInputStream(is, StorageManager.BUFFER_SIZE);

        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(tmpFile), StorageManager.BUFFER_SIZE);

            int count;
            while ((count = bis.read(dataBuf)) != -1) {
                bos.write(dataBuf, 0, count);
            }
            bos.flush();
            bos.close();

            state = saveTmpFile(tmpFile, path);

            if (!state.isSuccess()) {
                FileUtils.deleteQuietly(tmpFile);
            }

            return state;
        } catch (IOException ignored) {
        }
        return new BaseState(false, AppInfo.IO_ERROR);
    }

    private static File getTmpFile() {
        File tmpDir = FileUtils.getTempDirectory();
        String tmpFileName = (Math.random() * 10000 + StringPool.EMPTY).replace(StringPool.DOT, StringPool.EMPTY);
        return new File(tmpDir, tmpFileName);
    }

    private static State saveTmpFile(File tmpFile, String path) {
        State state;
        File targetFile = new File(path);

        if (targetFile.canWrite()) {
            return new BaseState(false, AppInfo.PERMISSION_DENIED);
        }
        try {
            FileUtils.moveFile(tmpFile, targetFile);
        } catch (IOException e) {
            return new BaseState(false, AppInfo.IO_ERROR);
        }

        state = new BaseState(true);
        state.putInfo(UEConst.SIZE, targetFile.length());
        state.putInfo(UEConst.TITLE, targetFile.getName());

        return state;
    }

    private static State valid(File file) {
        File parentPath = file.getParentFile();

        if ((!parentPath.exists()) && (!parentPath.mkdirs())) {
            return new BaseState(false, AppInfo.FAILED_CREATE_FILE);
        }

        if (!parentPath.canWrite()) {
            return new BaseState(false, AppInfo.PERMISSION_DENIED);
        }

        return new BaseState(true);
    }
}
