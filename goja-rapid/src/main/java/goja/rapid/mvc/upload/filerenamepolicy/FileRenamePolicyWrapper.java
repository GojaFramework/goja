/**
 *
 */
package goja.rapid.mvc.upload.filerenamepolicy;

import goja.core.StringPool;

import com.oreilly.servlet.multipart.FileRenamePolicy;

import java.io.File;

/**
 * @author BruceZCQ
 */
public abstract class FileRenamePolicyWrapper implements FileRenamePolicy {

    private String saveDirectory;

    @Override
    public File rename(File f) {
        if (null == f) {
            return null;
        }
        String name = f.getName();
        String body = "";
        String ext = "";
        int dot = name.lastIndexOf(".");
        if (dot != -1) {
            body = name.substring(0, dot);
            ext = name.substring(dot);
        }
        return this.nameProcess(f, body, ext);
    }

    /**
     * 文件名字处理
     *
     * @param f    文件
     * @param name 原名称
     * @param ext  文件扩展名
     */
    public abstract File nameProcess(File f, String name, String ext);

    public String getSaveDirectory() {
        return saveDirectory;
    }

    protected void setSaveDirectory(String saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

    /**
     * Add File Separator
     */
    public String appendFileSeparator(String path) {
        if (null == path) {
            return File.separator;
        }
        // add "/" prefix
        if (!path.startsWith(StringPool.SLASH) && !path.startsWith(StringPool.BACK_SLASH)) {
            path = File.separator + path;
        }

        // add "/" postfix
        if (!path.endsWith(StringPool.SLASH) && !path.endsWith(StringPool.BACK_SLASH)) {
            path = path + File.separator;
        }
        return path;
    }
}
