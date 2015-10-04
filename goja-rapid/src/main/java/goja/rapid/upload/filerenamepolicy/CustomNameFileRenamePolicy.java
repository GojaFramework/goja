/**
 * 
 */
package goja.rapid.upload.filerenamepolicy;

import java.io.File;

/**
 * @author BruceZCQ
 * 自定义文件名称
 */
public class CustomNameFileRenamePolicy extends FileRenamePolicyWrapper {
	
	private String customName = null;

	public CustomNameFileRenamePolicy(String customName) {
		this.customName = customName;
	}

	@Override
	public File nameProcess(File f, String name, String ext) {
		if (null == this.customName) {
			throw new IllegalArgumentException("Please Set Custom File Name!");
		}

		// add "/" postfix
		StringBuilder path = new StringBuilder(f.getParent());
		String _path = path.toString();
		this.setSaveDirectory(_path);
		
		String fileName = this.customName + ext;
		
		return (new File(_path, fileName));
	}

}
