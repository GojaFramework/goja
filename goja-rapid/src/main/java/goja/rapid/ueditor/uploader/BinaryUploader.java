package goja.rapid.ueditor.uploader;


import com.google.common.io.Files;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;
import goja.StringPool;
import goja.rapid.ueditor.UEConst;
import goja.rapid.ueditor.define.AppInfo;
import goja.rapid.ueditor.define.BaseState;
import goja.rapid.ueditor.define.State;
import goja.rapid.storage.PathFormat;
import goja.rapid.ueditor.kit.StorageManager;
import org.apache.commons.io.FileUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import static java.io.File.separator;


public class BinaryUploader {

    public static State save(Controller controller, String fieldName,
                             String config_savePath,
                             long config_max_size,
                             List<String> config_allowFiles) {

        final UploadFile uploadFile = controller.getFile(fieldName);
        if (uploadFile == null) {
            return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
        } else {

            try {
                String originFileName = uploadFile.getOriginalFileName();
                String suffix = StringPool.DOT + Files.getFileExtension(originFileName);

                if (!config_allowFiles.contains(suffix)) {
                    return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
                }

                originFileName = Files.getNameWithoutExtension(originFileName);
                String savePath = config_savePath + suffix;
                savePath = PathFormat.parse(savePath, originFileName);
                String physicalPath = PathKit.getWebRootPath() + separator + savePath;

                final FileInputStream fileInputStream;
                fileInputStream = new FileInputStream(uploadFile.getFile());

                State storageState = StorageManager.saveFileByInputStream(fileInputStream,
                        physicalPath, config_max_size);
                if (storageState.isSuccess()) {
                    storageState.putInfo(UEConst.URL, PathFormat.format(savePath));
                    storageState.putInfo(UEConst.TYPE, suffix);
                    storageState.putInfo(UEConst.ORIGINAL, originFileName + suffix);
                }

                return storageState;
            } catch (FileNotFoundException e) {
                e.printStackTrace();

                return new BaseState(false, AppInfo.IO_ERROR);
            } finally {
                FileUtils.deleteQuietly(uploadFile.getFile());
            }
        }

    }


}
