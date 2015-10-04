package goja.rapid.ueditor.kit;


import com.jfinal.kit.PathKit;
import goja.rapid.ueditor.UEConfig;
import goja.rapid.ueditor.UEConst;
import goja.rapid.ueditor.define.AppInfo;
import goja.rapid.ueditor.define.BaseState;
import goja.rapid.ueditor.define.MIMEType;
import goja.rapid.ueditor.define.MultiState;
import goja.rapid.ueditor.define.State;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


/**
 * 图片抓取器
 *
 * @author hancong03@baidu.com
 */
public class ImageHunter {

    private final String       filename   = "remote";
    private String       savePath   = null;
    private String       rootPath   = null;
    private List<String> allowTypes = null;
    private long         maxSize    = -1;

    private List<String> filters = null;

    public ImageHunter() {

        this.savePath = UEConfig.me.getCatcherPathFormat();
        this.rootPath = PathKit.getWebRootPath() + File.separator;
        this.maxSize = UEConfig.me.getCatcherMaxSize();
        this.allowTypes = UEConfig.me.getCatcherAllowFiles();
        this.filters = UEConfig.me.getCatcherLocalDomain();

    }

    public State capture(String[] list) {

        MultiState state = new MultiState(true);

        for (String source : list) {
            state.addState(captureRemoteData(source));
        }

        return state;

    }

    public State captureRemoteData(String urlStr) {

        HttpURLConnection connection;
        URL url;
        String suffix;

        try {
            url = new URL(urlStr);

            if (!validHost(url.getHost())) {
                return new BaseState(false, AppInfo.PREVENT_HOST);
            }

            connection = (HttpURLConnection) url.openConnection();

            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(true);

            if (!validContentState(connection.getResponseCode())) {
                return new BaseState(false, AppInfo.CONNECTION_ERROR);
            }

            suffix = MIMEType.getSuffix(connection.getContentType());

            if (!validFileType(suffix)) {
                return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
            }

            if (!validFileSize(connection.getContentLength())) {
                return new BaseState(false, AppInfo.MAX_SIZE);
            }

            String savePath = this.getPath(this.savePath, this.filename, suffix);
            String physicalPath = this.rootPath + savePath;

            State state = StorageManager.saveFileByInputStream(connection.getInputStream(), physicalPath);

            if (state.isSuccess()) {
                state.putInfo(UEConst.URL, PathFromatKit.format(savePath));
                state.putInfo(UEConst.SOURCE, urlStr);
            }

            return state;

        } catch (Exception e) {
            return new BaseState(false, AppInfo.REMOTE_FAIL);
        }

    }

    private String getPath(String savePath, String filename, String suffix) {

        return PathFromatKit.parse(savePath + suffix, filename);

    }

    private boolean validHost(String hostname) {

        return !filters.contains(hostname);

    }

    private boolean validContentState(int code) {

        return HttpURLConnection.HTTP_OK == code;

    }

    private boolean validFileType(String type) {

        return this.allowTypes.contains(type);

    }

    private boolean validFileSize(int size) {
        return size < this.maxSize;
    }

    public String getFilename() {
        return filename;
    }
}
