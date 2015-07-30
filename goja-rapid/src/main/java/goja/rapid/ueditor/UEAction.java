package goja.rapid.ueditor;

import com.alibaba.fastjson.JSON;
import com.jfinal.core.Controller;
import goja.rapid.ueditor.kit.FileManager;
import goja.rapid.ueditor.kit.ImageHunter;
import goja.rapid.ueditor.uploader.Base64Uploader;
import goja.rapid.ueditor.uploader.BinaryUploader;

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
     * 上传图片
     * <p/>
     * 返回示例：
     * <p/>
     * {"original":"demo.jpg","name":"demo.jpg","url":"\/server\/ueditor\/upload\/image\/demo.jpg",UEConst.SIZE:"99697",
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
                    UEConfig config = UEConfig.me;
                    String fieldName = config.getImageFieldName();
                    return BinaryUploader
                            .save(controller, fieldName,
                                    config.getImagePathFormat(),
                                    config.getImageMaxSize(),
                                    config.getImageAllowFiles())
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
                    return BinaryUploader
                            .save(controller, fieldName,
                                    config.getFilePathFormat(),
                                    config.getFileMaxSize(),
                                    config.getFileAllowFiles())
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
                    return BinaryUploader
                            .save(controller, fieldName,
                                    config.getVideoPathFormat(),
                                    config.getVideoMaxSize(),
                                    config.getVideoAllowFiles())
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
                    return Base64Uploader
                            .save(controller.getPara(fieldName))
                            .toJSONString();
                }
            },

    /**
     * 列出指定目录下的图片
     * <p/>
     * imageManagerActionName {String} [默认值："listimage"] //执行图片管理的action名称
     * imageManagerListPath {String} [默认值："/ueditor/php/upload/image/"] //指定要列出图片的目录
     * imageManagerListSize {String} [默认值：20] //每次列出文件数量
     * imageManagerUrlPrefix {String} [默认值：""] //图片访问路径前缀
     * imageManagerInsertAlign {String} [默认值："none"] //插入的图片浮动方式
     * imageManagerAllowFiles {Array}, //列出的文件类型
     * <p/>
     * example:
     * <p/>
     * {
     * "state": "SUCCESS",
     * "list": [
     * {
     * "url": "/server/ueditor/upload/image/3 2.jpg",
     * "mtime": 1400203383
     * },
     * {
     * "url": "/server/ueditor/upload/image/1.jpg",
     * "mtime": 1400203383
     * }
     * ],
     * "start": "0",
     * "total": 29
     * }
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
     * 列出指定目录下的文件
     * <p/>
     * fileManagerActionName {String} [默认值："listfile"] //执行文件管理的action名称
     * fileManagerListPath {String} [默认值："/ueditor/php/upload/file/"] //指定要列出文件的目录
     * fileManagerUrlPrefix {String} [默认值：""] //文件访问路径前缀
     * fileManagerListSize {String} [默认值：20] //每次列出文件数量
     * fileManagerAllowFiles {Array} //列出的文件类型
     * <p/>
     * 返回示例 ：
     * <p/>
     * {
     * "state": "SUCCESS",
     * "list": [
     * {
     * "url": "/server/ueditor/upload/file/7.pptx",
     * "mtime": 1400203383
     * }
     * ],
     * "start": "0",
     * "total": 7
     * }
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


    public abstract String invoke();

    public abstract String invoke(Controller controller);
}
