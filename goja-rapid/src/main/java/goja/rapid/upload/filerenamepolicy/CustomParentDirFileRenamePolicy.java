/**
 * 
 */
package goja.rapid.upload.filerenamepolicy;

import com.jfinal.kit.HashKit;
import goja.core.kits.lang.Strs;

import java.io.File;

/**
 * @author BruceZCQ
 * 自定义资源父目录名称
 *
 */
public class CustomParentDirFileRenamePolicy extends
		FileRenamePolicyWrapper {

	private String parentDir = null;
	private NamePolicy namepolicy = NamePolicy.ORIGINAL_NAME;
	
	public CustomParentDirFileRenamePolicy(String parentDir, NamePolicy namepolicy) {
		this.parentDir = this.appendFileSeparator(parentDir);
		this.namepolicy = namepolicy;
	}
	
	@Override
	public File nameProcess(File f, String name, String ext) {
		if (null == this.parentDir) {
			throw new IllegalArgumentException("Please Set Custom ParentDir Name!");
		}
		
		// add "/" postfix

        String _path = f.getParent() + this.parentDir;
		this.setSaveDirectory(_path);

		if (this.namepolicy == NamePolicy.RANDOM_NAME) {
			name = HashKit.md5(Strs.randomStr());
		} 
		
		String fileName = name + ext;
		
		File file = new File(_path);
		if (!file.exists()) {
			file.mkdirs();
		}
		
		return (new File(_path, fileName));
	}
}
