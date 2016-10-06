package goja.rapid.ueditor.kit;

import goja.core.StringPool;
import goja.rapid.ueditor.UEConst;
import goja.rapid.ueditor.define.AppInfo;
import goja.rapid.ueditor.define.BaseState;
import goja.rapid.ueditor.define.MultiState;
import goja.rapid.ueditor.define.State;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class FileManager {
    private final String   dir;
    private final String[] allowFiles;
    private final int      count;

    /**
     * @param dir         文件目录
     * @param allow_files 合法的文件后缀名
     * @param count       总共获取多少个文件
     */
    public FileManager(String dir, List<String> allow_files, int count) {
        this.dir = StorageManager.getUeFolder() + dir;
        final int size = allow_files.size();
        this.allowFiles = new String[size];
        for (int i = 0; i < size; i++) {
            allowFiles[i] = allow_files.get(i).replace(StringPool.DOT, StringPool.EMPTY);
        }
        this.count = count;

    }

    public State listFile(int index) {

        File dir = new File(this.dir);
        State state;

        if (!dir.exists()) {
            return new BaseState(false, AppInfo.NOT_EXIST);
        }

        if (!dir.isDirectory()) {
            return new BaseState(false, AppInfo.NOT_DIRECTORY);
        }


        Collection<File> list = FileUtils.listFiles(dir, this.allowFiles, true);

        if (index < 0 || index > list.size()) {
            state = new MultiState(true);
        } else {
            Object[] fileList = Arrays.copyOfRange(list.toArray(), index, index + this.count);
            state = this.getState(fileList);
        }

        state.putInfo(UEConst.START, index);
        state.putInfo(UEConst.TOTAL, list.size());

        return state;

    }

    private State getState(Object[] files) {

        MultiState state = new MultiState(true);
        BaseState fileState;

        File file;

        for (Object obj : files) {
            if (obj == null) {
                break;
            }
            file = (File) obj;
            fileState = new BaseState(true);
            fileState.putInfo(UEConst.URL, this.getPath(file));
            state.addState(fileState);
        }

        return state;

    }

    private String getPath(File file) {
        String path = file.getAbsolutePath();
        return PathFormatKit.format(StringUtils.substring(path, StringUtils.indexOf(path, UEConst.UE_FOLDER)));
    }


}
