/**
 * 
 */
package goja.rapid.upload.filerenamepolicy;

import java.io.File;

import com.oreilly.servlet.multipart.FileRenamePolicy;

/**
 * @author BruceZCQ
 *
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
		return this.nameProcess(f,body, ext);
	}
	
	/**
	 * 文件名字处理
	 * @param f 文件
	 * @param name 原名称
	 * @param ext 文件扩展名
	 * @return
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
	 * @param path
	 */
	public String appendFileSeparator(String path){
		if (null == path) {
			return File.separator;
		}
		// add "/" prefix
		if (!path.startsWith("/") && !path.startsWith("\\")) {
			path = File.separator + path;
		}
				
		// add "/" postfix
		if (!path.endsWith("/") && !path.endsWith("\\")) {
			path = path + File.separator;
		}
		return path;
	}
}
