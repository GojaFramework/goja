package goja.rapid.ueditor.uploader;

import com.jfinal.kit.PathKit;
import goja.StringPool;
import goja.rapid.ueditor.UEConfig;
import goja.rapid.ueditor.UEConst;
import goja.rapid.ueditor.UEHandler;
import goja.rapid.ueditor.define.AppInfo;
import goja.rapid.ueditor.define.BaseState;
import goja.rapid.ueditor.define.State;
import goja.rapid.storage.PathFormat;
import goja.rapid.ueditor.kit.StorageManager;
import org.apache.commons.codec.binary.Base64;

import static java.io.File.separator;

public final class Base64Uploader {

    public static State save(String content) {

        byte[] data = decode(content);

        UEConfig config = UEConfig.me;

        long maxSize = config.getScrawlMaxSize();

        if (!validSize(data, maxSize)) {
            return new BaseState(false, AppInfo.MAX_SIZE);
        }

        String suffix = ".jpg";

        String savePath = PathFormat.parse(config.getScrawlPathFormat(), config.getScrawlFieldName());

        savePath = savePath + suffix;
        String physicalPath = PathKit.getWebRootPath() + separator + savePath;

        State storageState = StorageManager.saveBinaryFile(data, physicalPath);

        if (storageState.isSuccess()) {
            storageState.putInfo(UEConst.URL, PathFormat.format(savePath));
            storageState.putInfo(UEConst.TYPE, suffix);
            storageState.putInfo(UEConst.ORIGINAL, StringPool.EMPTY);
        }

        return storageState;
    }

    private static byte[] decode(String content) {
        return Base64.decodeBase64(content);
    }

    private static boolean validSize(byte[] data, long length) {
        return data.length <= length;
    }

}