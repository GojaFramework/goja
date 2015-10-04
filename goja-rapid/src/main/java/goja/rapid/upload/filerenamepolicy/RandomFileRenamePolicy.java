/**
 * 
 */
package goja.rapid.upload.filerenamepolicy;

import java.io.File;

import com.jfinal.ext2.kit.RandomKit;


/**
 * @author BruceZCQ
 * 随机文件名
 * baseSaveDir/xxxxxx.jpg
 */
public class RandomFileRenamePolicy extends FileRenamePolicyWrapper {

	@Override
	public File nameProcess(File f, String name, String ext) {
		String path = f.getParent();
		this.setSaveDirectory(path);
		
		String fileName = RandomKit.randomMD5Str() + ext;
		
		return (new File(path, fileName));
	}
}
