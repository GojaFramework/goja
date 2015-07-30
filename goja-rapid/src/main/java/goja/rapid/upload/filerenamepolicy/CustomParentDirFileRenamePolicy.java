/**
 * 
 */
package goja.rapid.upload.filerenamepolicy;

import goja.kits.base.Strs;

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
		this.parentDir = parentDir;
		this.namepolicy = namepolicy;
	}
	
	@Override
	public File nameProcess(File f, String name, String ext) {
		if (null == this.parentDir) {
			throw new IllegalArgumentException("Please Set Custom ParentDir Name!");
		}
		
		// add "/" postfix
		StringBuilder path = new StringBuilder(this.appendFileSeparator(f.getParent()));
		
		path.append(this.parentDir);
		
		String _path = path.toString();
		File file = new File(_path);
		if (!file.exists()) {
			file.mkdirs();
		}
		this.setSaveDirectory(_path);
		
		if (this.namepolicy == NamePolicy.RANDOM_NAME) {
			name = Strs.randomMD5Str();
		} 
		return (new File(_path,name+ext));
	}

}
