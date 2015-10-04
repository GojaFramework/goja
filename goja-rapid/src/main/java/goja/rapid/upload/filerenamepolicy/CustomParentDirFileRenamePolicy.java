/**
 * 
 */
package goja.rapid.upload.filerenamepolicy;

import java.io.File;

import com.jfinal.ext2.kit.RandomKit;

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
		StringBuilder path = new StringBuilder(f.getParent());
		
		path.append(this.parentDir);
		
		String _path = path.toString();
		this.setSaveDirectory(_path);

		if (this.namepolicy == NamePolicy.RANDOM_NAME) {
			name = RandomKit.randomMD5Str();
		} 
		
		String fileName = name + ext;
		
		File file = new File(_path);
		if (!file.exists()) {
			file.mkdirs();
		}
		
		return (new File(_path, fileName));
	}
}
