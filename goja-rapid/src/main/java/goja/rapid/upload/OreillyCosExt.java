package goja.rapid.upload;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.oreilly.servlet.multipart.FileRenamePolicy;

import java.io.File;

/**
 * OreillyCosExt.
 */
public class OreillyCosExt {
	
	public static void init(String saveDirectory, int maxPostSize, String encoding) {
		try {
			Class.forName("com.oreilly.servlet.MultipartRequest");
			doInit(saveDirectory, maxPostSize, encoding);
		} catch (ClassNotFoundException e) {
			
		}
	}
	
	public static void setFileRenamePolicy(FileRenamePolicy fileRenamePolicy) {
		if (fileRenamePolicy == null) {
			fileRenamePolicy = new DefaultFileRenamePolicy();
		}
		MultipartRequestExt.fileRenamePolicy = fileRenamePolicy;
	}
	
	private static void doInit(String saveDirectory, int maxPostSize, String encoding) {
		String dir;
		if (StrKit.isBlank(saveDirectory)) {
			dir = PathKit.getWebRootPath() + File.separator + "upload";
		}
		else if (isAbsolutelyPath(saveDirectory)) {
			dir = saveDirectory;
		}
		else {
			dir = PathKit.getWebRootPath() + File.separator + saveDirectory;
		}
		
		// add "/" postfix
		if (dir.endsWith("/") == false && dir.endsWith("\\") == false) {
			dir = dir + File.separator;
		}
		
		MultipartRequestExt.init(dir, maxPostSize, encoding);
	}
	
	private static boolean isAbsolutelyPath(String saveDirectory) {
		return saveDirectory.startsWith("/") || saveDirectory.indexOf(":") == 1;
	}
}


