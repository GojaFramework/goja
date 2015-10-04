/**
 * 
 */
package goja.rapid.upload.filerenamepolicy;

import java.io.File;

import com.jfinal.ext2.config.JFinalConfigExt;
import com.jfinal.ext2.kit.DateTimeKit;
import com.jfinal.ext2.kit.RandomKit;
import com.jfinal.kit.StrKit;

/**
 * @author BruceZCQ
 *  baseSaveDir/parentDir/2015/6/22/zzzz.jpg
 */
public class DateRandomFileRenamePolicy extends FileRenamePolicyWrapper {
	
	private String parentDir = null;
	private String appParentDateDir = null;
	private String parentDateDir = null;
	private String customName = null;
	
	public DateRandomFileRenamePolicy() {
		this(null);
	}
	
	public DateRandomFileRenamePolicy(String parentDir) {
		this.parentDir = this.appendFileSeparator(parentDir);
	}
	
	public DateRandomFileRenamePolicy(String parentDir, String customName) {
		this.parentDir = this.appendFileSeparator(parentDir);
		this.customName = customName;
	}
	
	public void setCutomName(String customName) {
		this.customName = customName;
	}

	@Override
	public File nameProcess(File f, String name, String ext) {
		String rename = null; 
		if (StrKit.notBlank(this.customName)) {
			rename = this.customName;
		}else{
			rename = RandomKit.randomMD5Str();
		}
		// add "/" postfix
		StringBuilder path = new StringBuilder(f.getParent());

		// append parent dir
		if (StrKit.notBlank(this.parentDir)) {
			path.append(this.parentDir);
		}
		
		//append year month day
		String ymdSubDir = DateTimeKit.formatNowToStyle("yyyy"+File.separator+"M"+File.separator+"d");
		path.append(ymdSubDir);
		
		String parentDateDir = this.parentDir+ymdSubDir+File.separator;
		this.setParentDateDir(parentDateDir);

		this.setAppParentDateDir(File.separator+JFinalConfigExt.WEB_APP_NAME+parentDateDir);
		
		String _path = path.toString();
		this.setSaveDirectory(_path);
		
		String fileName = rename + ext;
		
		File file = new File(_path);
		if (!file.exists()) {
			file.mkdirs();
		}

		return (new File(_path, fileName));
	}

	/**
	 * 返回时间目录 /WebAppName/ParentDir/2015/7/22/
	 * @return the appParentDateDir
	 */
	public String getAppParentDateDir() {
		return appParentDateDir;
	}

	protected void setAppParentDateDir(String appParentDateDir) {
		this.appParentDateDir = appParentDateDir;
	}

	/**
	 * 返回时间目录 /ParentDir/2015/7/22/
	 * @return the parentDateDir
	 */
	public String getParentDateDir() {
		return parentDateDir;
	}

	protected void setParentDateDir(String parentDateDir) {
		this.parentDateDir = parentDateDir;
	}

}
