/**
 *
 */
package goja.rapid.upload.filerenamepolicy;

import com.jfinal.kit.HashKit;
import goja.core.kits.lang.Strs;

import java.io.File;


/**
 * @author BruceZCQ
 *         随机文件名
 *         baseSaveDir/xxxxxx.jpg
 */
public class RandomFileRenamePolicy extends FileRenamePolicyWrapper {

    @Override
    public File nameProcess(File f, String name, String ext) {
        String path = f.getParent();
        this.setSaveDirectory(path);

        String fileName = HashKit.md5(Strs.randomStr()) + ext;

        return (new File(path, fileName));
    }
}
