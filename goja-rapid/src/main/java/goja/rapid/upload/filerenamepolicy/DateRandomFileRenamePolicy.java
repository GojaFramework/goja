/**
 * 
 */
package goja.rapid.upload.filerenamepolicy;

import com.jfinal.kit.HashKit;
import com.jfinal.kit.StrKit;
import goja.kits.base.Strs;
import org.joda.time.DateTime;

import java.io.File;

/**
 * @author BruceZCQ
 *  baseSaveDir/parentDir/2015/6/22/zzzz.jpg
 */
public class DateRandomFileRenamePolicy extends FileRenamePolicyWrapper {
	
	private String subSaveDirRefDate = null;
	private String parentDir = null;
	
	public DateRandomFileRenamePolicy() {
		this(null);
	}
	
	public DateRandomFileRenamePolicy(String parentDir) {
		this.parentDir = parentDir;
	}

	@Override
	public File nameProcess(File f, String name, String ext) {
		String rename = HashKit.md5(Strs.randomStr());;
		String fileName = rename + ext;
		// add "/" postfix
		StringBuilder path = new StringBuilder(this.appendFileSeparator(f.getParent()));

		// append parent dir
		if (StrKit.notBlank(this.parentDir)) {
			path.append(this.appendFileSeparator(this.parentDir));
		}
		
		//append year month day
		String ymdSubDir = DateTime.now().toString("yyyy"+File.separator+"M"+File.separator+"d");
		path.append(ymdSubDir);
		
		// yyyy/M/d/filename.jpg
		this.setSubSaveDirRefDate(this.appendFileSeparator(ymdSubDir)+File.separator+fileName);
		
		String _path = path.toString();
		File file = new File(_path);
		if (!file.exists()) {
			file.mkdirs();
		}
		this.setSaveDirectory(_path);
		return (new File(_path,fileName));
	}

	/**
	 * @return the subSaveDirRefDate
	 */
	public String getSubSaveDirRefDate() {
		return subSaveDirRefDate;
	}

	/**
	 * @param subSaveDirRefDate the subSaveDirRefDate to set
	 */
	private void setSubSaveDirRefDate(String subSaveDirRefDate) {
		this.subSaveDirRefDate = subSaveDirRefDate;
	}

}
