package goja.rapid.ueditor;

import com.alibaba.fastjson.JSON;

import com.google.common.io.Files;

import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import goja.core.StringPool;
import goja.rapid.ueditor.define.AppInfo;
import goja.rapid.ueditor.define.BaseState;
import goja.rapid.ueditor.define.State;
import goja.rapid.ueditor.kit.FileManager;
import goja.rapid.ueditor.kit.ImageHunter;
import goja.rapid.ueditor.kit.PathFormatKit;
import goja.rapid.ueditor.kit.StorageManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
enum UEAction {

    CONFIG

            {
                @Override
                public String invoke() {
                    return JSON.toJSONString(UEConfig.me);
                }

                @Override
                public String invoke(Controller controller) {
                    return null;
                }
            },

    /**
     * 上传图片 <p/> 返回示例： <p/> {"original":"demo.jpg","name":"demo.jpg","url":"\/server\/ueditor\/upload\/image\/demo.jpg",UEConst.SIZE:"99697",
     * "type":".jpg","state":"SUCCESS"}
     */
    UPLOADIMAGE

            {
                @Override
                public String invoke() {
                    return null;
                }

                @Override
                public String invoke(Controller controller) {
                    final UEConfig config = UEConfig.me;
                    String imageFieldName = config.getImageFieldName();
                    return storageUploadFile(controller, config, true)
                            .toJSONString();
                }
            },

    UPLOADFILE

            {
                @Override
                public String invoke() {
                    return null;
                }

                @Override
                public String invoke(Controller controller) {
                    UEConfig config = UEConfig.me;
                    String fieldName = config.getFileFieldName();
                    return storageUploadFile(controller, config, false)
                            .toJSONString();
                }
            },

    UPLOADVIDEO

            {
                @Override
                public String invoke() {
                    return null;
                }

                @Override
                public String invoke(Controller controller) {
                    UEConfig config = UEConfig.me;
                    String fieldName = config.getVideoFieldName();
                    return storageUploadFile(controller, config, false)
                            .toJSONString();
                }
            },

    CATCHIMAGE

            {
                @Override
                public String invoke() {
                    return null;
                }

                @Override
                public String invoke(Controller controller) {
                    final ImageHunter imageHunter = new ImageHunter();
                    String[] list = controller.getParaValues(imageHunter.getFilename());
                    return imageHunter.capture(list).toJSONString();
                }
            },

    UPLOADSCRAWL

            {
                @Override
                public String invoke() {
                    return null;
                }

                @Override
                public String invoke(Controller controller) {
                    String fieldName = UEConfig.me.getScrawlFieldName();
                    return base64Save(controller.getPara(fieldName))
                            .toJSONString();
                }
            },

    /**
     * 列出指定目录下的图片 <p/> imageManagerActionName {String} [默认值："listimage"] //执行图片管理的action名称
     * imageManagerListPath {String} [默认值："/ueditor/php/upload/image/"] //指定要列出图片的目录
     * imageManagerListSize {String} [默认值：20] //每次列出文件数量 imageManagerUrlPrefix {String} [默认值：""]
     * //图片访问路径前缀 imageManagerInsertAlign {String} [默认值："none"] //插入的图片浮动方式 imageManagerAllowFiles
     * {Array}, //列出的文件类型 <p/> example: <p/> { "state": "SUCCESS", "list": [ { "url":
     * "/server/ueditor/upload/image/3 2.jpg", "mtime": 1400203383 }, { "url":
     * "/server/ueditor/upload/image/1.jpg", "mtime": 1400203383 } ], "start": "0", "total": 29 }
     */
    LISTIMAGE

            {
                @Override
                public String invoke() {
                    return "";
                }

                @Override
                public String invoke(Controller controller) {
                    int index = controller.getParaToInt(UEConst.START);
                    int size = controller.getParaToInt(UEConst.SIZE);

                    final UEConfig ueConfig = UEConfig.me;

                    return new FileManager(ueConfig.getImageManagerListPath(),
                            ueConfig.getImageManagerAllowFiles(),
                            ueConfig.getImageManagerListSize())
                            .listFile(index)
                            .toJSONString();
                }
            },

    /**
     * 列出指定目录下的文件 <p/> fileManagerActionName {String} [默认值："listfile"] //执行文件管理的action名称
     * fileManagerListPath {String} [默认值："/ueditor/php/upload/file/"] //指定要列出文件的目录
     * fileManagerUrlPrefix {String} [默认值：""] //文件访问路径前缀 fileManagerListSize {String} [默认值：20]
     * //每次列出文件数量 fileManagerAllowFiles {Array} //列出的文件类型 <p/> 返回示例 ： <p/> { "state": "SUCCESS",
     * "list": [ { "url": "/server/ueditor/upload/file/7.pptx", "mtime": 1400203383 } ], "start": "0",
     * "total": 7 }
     */
    LISTFILE

            {
                @Override
                public String invoke() {
                    return null;
                }

                @Override
                public String invoke(Controller controller) {
                    int index = controller.getParaToInt(UEConst.START);
                    int size = controller.getParaToInt(UEConst.SIZE);

                    final UEConfig ueConfig = UEConfig.me;

                    return new FileManager(
                            ueConfig.getFileManagerListPath(),
                            ueConfig.getFileAllowFiles(),
                            ueConfig.getFileManagerListSize())
                            .listFile(index)
                            .toJSONString();
                }
            };

    /**
     * @param controller 请求控制器
     * @param ueConfig   UE配置
     * @param image_flag 是否是上传图片，true表示是
     * @return 请求状态
     */
    private static State storageUploadFile(Controller controller, final UEConfig ueConfig,
                                           boolean image_flag) {
        final UploadFile uploadFile = controller.getFile(ueConfig.getImageFieldName());
        if (uploadFile == null) {
            return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
        } else {

            try {
                String originFileName = uploadFile.getOriginalFileName();
                String suffix = StringPool.DOT + Files.getFileExtension(originFileName);
                final List<String> allowFiles =
                        image_flag ? ueConfig.getImageAllowFiles() : ueConfig.getFileAllowFiles();
                if (!allowFiles.contains(suffix)) {
                    return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
                }

                originFileName = Files.getNameWithoutExtension(originFileName);
                String savePath = image_flag ? ueConfig.getImagePathFormat() : ueConfig.getFilePathFormat();
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

    /**
     * @param content Base64图片字符串
     * @return 存储状态
     */
    private static State base64Save(String content) {

        byte[] data = Base64.decodeBase64(content);

        UEConfig config = UEConfig.me;

        long maxSize = config.getScrawlMaxSize();
        if (!(data.length <= maxSize)) {
            return new BaseState(false, AppInfo.MAX_SIZE);
        }

        String suffix = ".jpg";

        String savePath =
                PathFormatKit.parse(config.getScrawlPathFormat(), config.getScrawlFieldName());

        savePath = savePath + suffix;

        State storageState = StorageManager.saveBinaryFile(data, savePath);

        if (storageState.isSuccess()) {
            storageState.putInfo(UEConst.URL, PathFormatKit.format(savePath));
            storageState.putInfo(UEConst.TYPE, suffix);
            storageState.putInfo(UEConst.ORIGINAL, StringPool.EMPTY);
        }

        return storageState;
    }

    public abstract String invoke();

    public abstract String invoke(Controller controller);

}
