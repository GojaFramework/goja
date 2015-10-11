package goja.rapid.ueditor.uploader;


import com.google.common.io.Files;
import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import goja.StringPool;
import goja.rapid.ueditor.UEConfig;
import goja.rapid.ueditor.UEConst;
import goja.rapid.ueditor.define.AppInfo;
import goja.rapid.ueditor.define.BaseState;
import goja.rapid.ueditor.define.State;
import goja.rapid.ueditor.kit.PathFormatKit;
import goja.rapid.ueditor.kit.StorageManager;
import org.apache.commons.io.FileUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;


public class BinaryUploader {

    /**
     * @param controller        请求控制器
     * @param fieldName         字段名称
     * @param imagePathFormat   上传保存路径,可以自定义保存路径和文件名格式
     *                          <p/>
     *                          {filename} 会替换成原文件名,配置这项需要注意中文乱码问题
     *                          {rand:6} 会替换成随机数,后面的数字是随机数的位数
     *                          {time} 会替换成时间戳
     *                          {yyyy} 会替换成四位年份
     *                          {yy} 会替换成两位年份
     *                          {mm} 会替换成两位月份
     *                          {dd} 会替换成两位日期
     *                          {hh} 会替换成两位小时
     *                          {ii} 会替换成两位分钟
     *                          {ss} 会替换成两位秒
     *                          非法字符 \ : * ? " < > |
     *                          具请体看线上文档: fex.baidu.com/ueditor/#use-format_upload_filename
     * @param config_max_size   配置的文件大小
     * @param config_allowFiles 可以上传的文件
     * @return 是否操作成功
     */
    public static State save(Controller controller, String fieldName,
                             String imagePathFormat,
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
                String savePath = imagePathFormat + suffix;
                savePath = PathFormatKit.parse(savePath, originFileName);

                final FileInputStream fileInputStream;
                fileInputStream = new FileInputStream(uploadFile.getFile());

                State storageState = StorageManager.saveFileByInputStream(fileInputStream,
                        savePath, config_max_size);
                if (storageState.isSuccess()) {
                    final String url = PathFormatKit.format(savePath);
                    storageState.putInfo(UEConst.URL, url);
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


    /**
     * @param controller 请求控制器
     * @param ueConfig   UE配置
     * @param image_flag 是否是上传图片，true表示是
     * @return 请求状态
     */
    public static State storageUploadFile(Controller controller, final UEConfig ueConfig, boolean image_flag) {
        final UploadFile uploadFile = controller.getFile(ueConfig.getImageFieldName());
        if (uploadFile == null) {
            return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
        } else {

            try {
                String originFileName = uploadFile.getOriginalFileName();
                String suffix = StringPool.DOT + Files.getFileExtension(originFileName);
                final List<String> allowFiles = image_flag ? ueConfig.getImageAllowFiles() : ueConfig.getFileAllowFiles();
                if (!allowFiles.contains(suffix)) {
                    return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
                }

                originFileName = Files.getNameWithoutExtension(originFileName);
                String savePath = image_flag ? ueConfig.getImagePathFormat() : ueConfig.getFilePathFormat() ;
                savePath = PathFormatKit.parse(savePath, originFileName) + suffix;

                final FileInputStream fileInputStream;
                fileInputStream = new FileInputStream(uploadFile.getFile());

                State storageState = StorageManager
                        .saveFileByInputStream(fileInputStream,
                                               savePath,
                                               image_flag ? ueConfig.getImageMaxSize() : ueConfig.getFileMaxSize());
                if (storageState.isSuccess()) {
                    final String url = PathFormatKit.format(savePath);
                    storageState.putInfo(UEConst.URL, url);
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
