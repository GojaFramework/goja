package goja.rapid.ueditor;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import goja.core.StringPool;
import goja.core.app.GojaConfig;
import goja.core.app.GojaPropConst;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * <p> 富文本配置 上传图片配置项 DTO</p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public final class UEConfig {


    /*上传图片配置项*/

    public static final UEConfig me = config();
    /**
     * 执行上传图片的action名称
     */
    private String imageActionName;
    /**
     * 提交的图片表单名称
     */
    private String imageFieldName;
    /**
     * 上传大小限制，单位B
     */
    private long imageMaxSize;
    /**
     * 上传图片格式显示
     */
    private List<String> imageAllowFiles;
    /**
     * 是否压缩图片
     */
    private boolean imageCompressEnable;
    /**
     * 图片压缩最长边限制
     */
    private int imageCompressBorder;
    /**
     * 插入的图片浮动方式
     */
    private String imageInsertAlign;
    /**
     * 图片访问路径前缀
     */
    private String imageUrlPrefix;

    /*涂鸦图片上传配置项*/
    /**
     * 上传保存路径,可以自定义保存路径和文件名格式 <p/> {filename} 会替换成原文件名,配置这项需要注意中文乱码问题 {rand:6} 会替换成随机数,后面的数字是随机数的位数
     * {time} 会替换成时间戳 {yyyy} 会替换成四位年份 {yy} 会替换成两位年份 {mm} 会替换成两位月份 {dd} 会替换成两位日期 {hh} 会替换成两位小时 {ii}
     * 会替换成两位分钟 {ss} 会替换成两位秒 非法字符 \ : * ? " < > | 具请体看线上文档: fex.baidu.com/ueditor/#use-format_upload_filename
     */
    private String imagePathFormat;
    /**
     * 执行上传涂鸦的action名称
     */
    private String scrawlActionName;
    /**
     * 提交的图片表单名称
     */
    private String scrawlFieldName;
    /**
     * 上传保存路径,可以自定义保存路径和文件名格式
     */
    private String scrawlPathFormat;
    /**
     * 上传大小限制，单位B
     */
    private long scrawlMaxSize;
    /**
     * 图片访问路径前缀
     */
    private String scrawlUrlPrefix;
    /**
     * 截图工具上传
     */
    private String scrawlInsertAlign;
    /**
     * 执行上传截图的action名称
     */
    private String snapscreenActionName;
    /**
     * 上传保存路径,可以自定义保存路径和文件名格式
     */
    private String snapscreenPathFormat;
    /**
     * 图片访问路径前缀
     */
    private String snapscreenUrlPrefix;

    /* 抓取远程图片配置*/
    /**
     * 插入的图片浮动方式
     */
    private String snapscreenInsertAlign;
    /**
     * 执行抓取远程图片的action名称
     */
    private List<String> catcherLocalDomain;
    /**
     * 提交的图片列表表单名称
     */
    private String catcherActionName;
    /**
     * 上传保存路径,可以自定义保存路径和文件名格式
     */
    private String catcherFieldName;
    /**
     * 图片访问路径前缀
     */
    private String catcherPathFormat;
    /**
     *
     */
    private String catcherUrlPrefix;
    /**
     * 上传大小限制 单位B
     */
    private int catcherMaxSize;

    /*上传视频配置*/
    /**
     * 抓取图片格式显示
     */
    private List<String> catcherAllowFiles;
    /**
     * 执行上传视频的action名称
     */
    private String videoActionName;
    /**
     * 提交的视频表单名称
     */
    private String videoFieldName;
    /**
     * 上传保存路径,可以自定义保存路径和文件名格式
     */
    private String videoPathFormat;
    /**
     * 视频访问路径前缀
     */
    private String videoUrlPrefix;
    /**
     * 上传大小限制，单位B，默认100MB
     */
    private long videoMaxSize;

    /*上传文件配置*/
    /**
     * 上传视频格式显示
     */
    private List<String> videoAllowFiles;
    /**
     * controller里,执行上传视频的action名称
     */
    private String fileActionName;
    /**
     * 提交的文件表单名称
     */
    private String fileFieldName;
    /**
     * 上传保存路径,可以自定义保存路径和文件名格式
     */
    private String filePathFormat;
    /**
     * 文件访问路径前缀
     */
    private String fileUrlPrefix;
    /**
     * 上传大小限制，单位B，默认50MB
     */
    private long fileMaxSize;

    /*指定要列出图片的目录*/
    /**
     * 上传文件格式显示
     */
    private List<String> fileAllowFiles;
    /**
     * 指定要列出图片的目录
     */
    private String imageManagerActionName;
    /**
     * 每次列出文件数量
     */
    private String imageManagerListPath;
    /**
     * 图片访问路径前缀
     */
    private int imageManagerListSize;
    /**
     * 插入的图片浮动方式
     */
    private String imageManagerUrlPrefix;
    /**
     *
     */
    private String imageManagerInsertAlign;

    /*列出指定目录下的文件*/
    /**
     * 列出的文件类型
     */
    private List<String> imageManagerAllowFiles;
    /**
     * 执行文件管理的action名称
     */
    private String fileManagerActionName;
    /**
     * 指定要列出文件的目录
     */
    private String fileManagerListPath;
    /**
     * 文件访问路径前缀
     */
    private String fileManagerUrlPrefix;
    /**
     * 每次列出文件数量
     */
    private int fileManagerListSize;
    /**
     * 列出的文件类型
     */
    private List<String> fileManagerAllowFiles;

    private static UEConfig config() {

        final UEConfig config = new UEConfig();

        String urlPrefix = GojaConfig.getProperty(GojaPropConst.UE_URLPREFIX);
        if (Strings.isNullOrEmpty(urlPrefix)) {

            final String appDomain = GojaConfig.getAppDomain();
            urlPrefix = appDomain + (StringUtils.endsWith(appDomain, StringPool.SLASH) ? StringPool.EMPTY
                    : StringPool.SLASH) + "upload/";
        }
        config.imageActionName = "uploadimage";
        config.imageFieldName = "upfile";
        config.imageMaxSize = 2048000;
        config.imageAllowFiles = Lists.newArrayList(".png", ".jpg", ".jpeg", ".gif", ".bmp");
        config.imageCompressEnable = true;
        config.imageCompressBorder = 1600;
        config.imageInsertAlign = "none";
        config.imageUrlPrefix = urlPrefix;
        config.imagePathFormat = UEConst.UE_FOLDER + "image/{yyyy}{mm}{dd}/{time}{rand:6}";

        config.scrawlActionName = "uploadscrawl";
        config.scrawlFieldName = "upfile";
        config.scrawlPathFormat = UEConst.UE_FOLDER + "image/{yyyy}{mm}{dd}/{time}{rand:6}";
        config.scrawlMaxSize = 2048000;
        config.scrawlUrlPrefix = urlPrefix;
        config.scrawlInsertAlign = "none";

        config.snapscreenActionName = "uploadimage";
        config.snapscreenPathFormat = UEConst.UE_FOLDER + "image/{yyyy}{mm}{dd}/{time}{rand:6}";
        config.snapscreenUrlPrefix = urlPrefix;
        config.snapscreenInsertAlign = "none";

        config.catcherLocalDomain = Lists.newArrayList("127.0.0.1", "localhost");
        config.catcherActionName = "catchimage";
        config.catcherFieldName = "source";
        config.catcherPathFormat = UEConst.UE_FOLDER + "image/{yyyy}{mm}{dd}/{time}{rand:6}";
        config.catcherUrlPrefix = urlPrefix;
        config.catcherMaxSize = 2048000;
        config.catcherAllowFiles = Lists.newArrayList(".png", ".jpg", ".jpeg", ".gif", ".bmp");

        config.videoActionName = "uploadvideo";
        config.videoFieldName = "upfile";
        config.videoPathFormat = UEConst.UE_FOLDER + "video/{yyyy}{mm}{dd}/{time}{rand:6}";
        config.videoUrlPrefix = urlPrefix;
        config.videoMaxSize = 102400000;
        config.videoAllowFiles =
                Lists.newArrayList(".flv", ".swf", ".mkv", ".avi", ".rm", ".rmvb", ".mpeg"
                        , ".mpg", ".ogg", ".ogv", ".mov", ".wmv", ".mp4", ".webm", ".mp3", ".wav", ".mid");

        config.fileActionName = "uploadfile";
        config.fileFieldName = "upfile";
        config.filePathFormat = UEConst.UE_FOLDER + "file/{yyyy}{mm}{dd}/{time}{rand:6}";
        config.fileUrlPrefix = urlPrefix;
        config.fileMaxSize = 51200000;
        config.fileAllowFiles =
                Lists.newArrayList(".png", ".jpg", ".jpeg", ".gif", ".bmp", ".flv", ".swf", ".mkv"
                        , ".avi", ".rm", ".rmvb", ".mpeg", ".mpg", ".ogg", ".ogv", ".mov", ".wmv", ".mp4",
                        ".webm", ".mp3"
                        , ".wav", ".mid", ".rar", ".zip", ".tar", ".gz", ".7z", ".bz2", ".cab", ".iso", ".doc",
                        ".docx"
                        , ".xls", ".xlsx", ".ppt", ".pptx", ".pdf", ".txt", ".md", ".xml");

        config.imageManagerActionName = "listimage";
        config.imageManagerListPath = UEConst.UE_FOLDER + "image/";
        config.imageManagerUrlPrefix = urlPrefix;
        config.imageManagerInsertAlign = "none";
        config.imageManagerListSize = 20;
        config.imageManagerAllowFiles = Lists.newArrayList(".png", ".jpg", ".jpeg", ".gif", ".bmp");

        config.fileManagerActionName = "listfile";
        config.fileManagerListPath = UEConst.UE_FOLDER + "file/";
        config.fileManagerUrlPrefix = urlPrefix;
        config.fileManagerListSize = 20;
        config.fileManagerAllowFiles =
                Lists.newArrayList(".png", ".jpg", ".jpeg", ".gif", ".bmp", ".flv", ".swf"
                        , ".mkv", ".avi", ".rm", ".rmvb", ".mpeg", ".mpg", ".ogg", ".ogv", ".mov", ".wmv",
                        ".mp4", ".webm"
                        , ".mp3", ".wav", ".mid", ".rar", ".zip", ".tar", ".gz", ".7z", ".bz2", ".cab", ".iso",
                        ".doc", ".docx"
                        , ".xls", ".xlsx", ".ppt", ".pptx", ".pdf", ".txt", ".md", ".xml");
        return config;
    }

    public String getImageActionName() {
        return imageActionName;
    }

    public void setImageActionName(String imageActionName) {
        this.imageActionName = imageActionName;
    }

    public String getImageFieldName() {
        return imageFieldName;
    }

    public void setImageFieldName(String imageFieldName) {
        this.imageFieldName = imageFieldName;
    }

    public long getImageMaxSize() {
        return imageMaxSize;
    }

    public void setImageMaxSize(long imageMaxSize) {
        this.imageMaxSize = imageMaxSize;
    }

    public List<String> getImageAllowFiles() {
        return imageAllowFiles;
    }

    public void setImageAllowFiles(List<String> imageAllowFiles) {
        this.imageAllowFiles = imageAllowFiles;
    }

    public boolean isImageCompressEnable() {
        return imageCompressEnable;
    }

    public void setImageCompressEnable(boolean imageCompressEnable) {
        this.imageCompressEnable = imageCompressEnable;
    }

    public int getImageCompressBorder() {
        return imageCompressBorder;
    }

    public void setImageCompressBorder(int imageCompressBorder) {
        this.imageCompressBorder = imageCompressBorder;
    }

    public String getImageInsertAlign() {
        return imageInsertAlign;
    }

    public void setImageInsertAlign(String imageInsertAlign) {
        this.imageInsertAlign = imageInsertAlign;
    }

    public String getImageUrlPrefix() {
        return imageUrlPrefix;
    }

    public void setImageUrlPrefix(String imageUrlPrefix) {
        this.imageUrlPrefix = imageUrlPrefix;
    }

    /**
     * @return 上传保存路径, 可以自定义保存路径和文件名格式 <p/> {filename} 会替换成原文件名,配置这项需要注意中文乱码问题 {rand:6}
     * 会替换成随机数,后面的数字是随机数的位数 {time} 会替换成时间戳 {yyyy} 会替换成四位年份 {yy} 会替换成两位年份 {mm} 会替换成两位月份 {dd} 会替换成两位日期
     * {hh} 会替换成两位小时 {ii} 会替换成两位分钟 {ss} 会替换成两位秒 非法字符 \ : * ? " < > | 具请体看线上文档:
     * fex.baidu.com/ueditor/#use-format_upload_filename
     */
    public String getImagePathFormat() {
        return imagePathFormat;
    }

    public void setImagePathFormat(String imagePathFormat) {
        this.imagePathFormat = imagePathFormat;
    }

    public String getScrawlActionName() {
        return scrawlActionName;
    }

    public void setScrawlActionName(String scrawlActionName) {
        this.scrawlActionName = scrawlActionName;
    }

    public String getScrawlFieldName() {
        return scrawlFieldName;
    }

    public void setScrawlFieldName(String scrawlFieldName) {
        this.scrawlFieldName = scrawlFieldName;
    }

    public String getScrawlPathFormat() {
        return scrawlPathFormat;
    }

    public void setScrawlPathFormat(String scrawlPathFormat) {
        this.scrawlPathFormat = scrawlPathFormat;
    }

    public long getScrawlMaxSize() {
        return scrawlMaxSize;
    }

    public void setScrawlMaxSize(long scrawlMaxSize) {
        this.scrawlMaxSize = scrawlMaxSize;
    }

    public String getScrawlUrlPrefix() {
        return scrawlUrlPrefix;
    }

    public void setScrawlUrlPrefix(String scrawlUrlPrefix) {
        this.scrawlUrlPrefix = scrawlUrlPrefix;
    }

    public String getScrawlInsertAlign() {
        return scrawlInsertAlign;
    }

    public void setScrawlInsertAlign(String scrawlInsertAlign) {
        this.scrawlInsertAlign = scrawlInsertAlign;
    }

    public String getSnapscreenActionName() {
        return snapscreenActionName;
    }

    public void setSnapscreenActionName(String snapscreenActionName) {
        this.snapscreenActionName = snapscreenActionName;
    }

    public String getSnapscreenPathFormat() {
        return snapscreenPathFormat;
    }

    public void setSnapscreenPathFormat(String snapscreenPathFormat) {
        this.snapscreenPathFormat = snapscreenPathFormat;
    }

    public String getSnapscreenUrlPrefix() {
        return snapscreenUrlPrefix;
    }

    public void setSnapscreenUrlPrefix(String snapscreenUrlPrefix) {
        this.snapscreenUrlPrefix = snapscreenUrlPrefix;
    }

    public String getSnapscreenInsertAlign() {
        return snapscreenInsertAlign;
    }

    public void setSnapscreenInsertAlign(String snapscreenInsertAlign) {
        this.snapscreenInsertAlign = snapscreenInsertAlign;
    }

    public List<String> getCatcherLocalDomain() {
        return catcherLocalDomain;
    }

    public void setCatcherLocalDomain(List<String> catcherLocalDomain) {
        this.catcherLocalDomain = catcherLocalDomain;
    }

    public String getCatcherActionName() {
        return catcherActionName;
    }

    public void setCatcherActionName(String catcherActionName) {
        this.catcherActionName = catcherActionName;
    }

    public String getCatcherFieldName() {
        return catcherFieldName;
    }

    public void setCatcherFieldName(String catcherFieldName) {
        this.catcherFieldName = catcherFieldName;
    }

    public String getCatcherPathFormat() {
        return catcherPathFormat;
    }

    public void setCatcherPathFormat(String catcherPathFormat) {
        this.catcherPathFormat = catcherPathFormat;
    }

    public String getCatcherUrlPrefix() {
        return catcherUrlPrefix;
    }

    public void setCatcherUrlPrefix(String catcherUrlPrefix) {
        this.catcherUrlPrefix = catcherUrlPrefix;
    }

    public int getCatcherMaxSize() {
        return catcherMaxSize;
    }

    public void setCatcherMaxSize(int catcherMaxSize) {
        this.catcherMaxSize = catcherMaxSize;
    }

    public List<String> getCatcherAllowFiles() {
        return catcherAllowFiles;
    }

    public void setCatcherAllowFiles(List<String> catcherAllowFiles) {
        this.catcherAllowFiles = catcherAllowFiles;
    }

    public String getVideoActionName() {
        return videoActionName;
    }

    public void setVideoActionName(String videoActionName) {
        this.videoActionName = videoActionName;
    }

    public String getVideoFieldName() {
        return videoFieldName;
    }

    public void setVideoFieldName(String videoFieldName) {
        this.videoFieldName = videoFieldName;
    }

    public String getVideoPathFormat() {
        return videoPathFormat;
    }

    public void setVideoPathFormat(String videoPathFormat) {
        this.videoPathFormat = videoPathFormat;
    }

    public String getVideoUrlPrefix() {
        return videoUrlPrefix;
    }

    public void setVideoUrlPrefix(String videoUrlPrefix) {
        this.videoUrlPrefix = videoUrlPrefix;
    }

    public long getVideoMaxSize() {
        return videoMaxSize;
    }

    public void setVideoMaxSize(long videoMaxSize) {
        this.videoMaxSize = videoMaxSize;
    }

    public List<String> getVideoAllowFiles() {
        return videoAllowFiles;
    }

    public void setVideoAllowFiles(List<String> videoAllowFiles) {
        this.videoAllowFiles = videoAllowFiles;
    }

    public String getFileActionName() {
        return fileActionName;
    }

    public void setFileActionName(String fileActionName) {
        this.fileActionName = fileActionName;
    }

    public String getFileFieldName() {
        return fileFieldName;
    }

    public void setFileFieldName(String fileFieldName) {
        this.fileFieldName = fileFieldName;
    }

    public String getFilePathFormat() {
        return filePathFormat;
    }

    public void setFilePathFormat(String filePathFormat) {
        this.filePathFormat = filePathFormat;
    }

    public String getFileUrlPrefix() {
        return fileUrlPrefix;
    }

    public void setFileUrlPrefix(String fileUrlPrefix) {
        this.fileUrlPrefix = fileUrlPrefix;
    }

    public long getFileMaxSize() {
        return fileMaxSize;
    }

    public void setFileMaxSize(long fileMaxSize) {
        this.fileMaxSize = fileMaxSize;
    }

    public List<String> getFileAllowFiles() {
        return fileAllowFiles;
    }

    public void setFileAllowFiles(List<String> fileAllowFiles) {
        this.fileAllowFiles = fileAllowFiles;
    }

    public String getImageManagerActionName() {
        return imageManagerActionName;
    }

    public void setImageManagerActionName(String imageManagerActionName) {
        this.imageManagerActionName = imageManagerActionName;
    }

    public String getImageManagerListPath() {
        return imageManagerListPath;
    }

    public void setImageManagerListPath(String imageManagerListPath) {
        this.imageManagerListPath = imageManagerListPath;
    }

    public int getImageManagerListSize() {
        return imageManagerListSize;
    }

    public void setImageManagerListSize(int imageManagerListSize) {
        this.imageManagerListSize = imageManagerListSize;
    }

    public String getImageManagerUrlPrefix() {
        return imageManagerUrlPrefix;
    }

    public void setImageManagerUrlPrefix(String imageManagerUrlPrefix) {
        this.imageManagerUrlPrefix = imageManagerUrlPrefix;
    }

    public String getImageManagerInsertAlign() {
        return imageManagerInsertAlign;
    }

    public void setImageManagerInsertAlign(String imageManagerInsertAlign) {
        this.imageManagerInsertAlign = imageManagerInsertAlign;
    }

    public List<String> getImageManagerAllowFiles() {
        return imageManagerAllowFiles;
    }

    public void setImageManagerAllowFiles(List<String> imageManagerAllowFiles) {
        this.imageManagerAllowFiles = imageManagerAllowFiles;
    }

    public String getFileManagerActionName() {
        return fileManagerActionName;
    }

    public void setFileManagerActionName(String fileManagerActionName) {
        this.fileManagerActionName = fileManagerActionName;
    }

    public String getFileManagerListPath() {
        return fileManagerListPath;
    }

    public void setFileManagerListPath(String fileManagerListPath) {
        this.fileManagerListPath = fileManagerListPath;
    }

    public String getFileManagerUrlPrefix() {
        return fileManagerUrlPrefix;
    }

    public void setFileManagerUrlPrefix(String fileManagerUrlPrefix) {
        this.fileManagerUrlPrefix = fileManagerUrlPrefix;
    }

    public int getFileManagerListSize() {
        return fileManagerListSize;
    }

    public void setFileManagerListSize(int fileManagerListSize) {
        this.fileManagerListSize = fileManagerListSize;
    }

    public List<String> getFileManagerAllowFiles() {
        return fileManagerAllowFiles;
    }

    public void setFileManagerAllowFiles(List<String> fileManagerAllowFiles) {
        this.fileManagerAllowFiles = fileManagerAllowFiles;
    }
}
