/**
 * MultipartRequestExt
 * <br/>
 * 参考com.jfinal.upload.MultipartRequest <br/>
 * 完善了自定义的FileRenamePolicy处理 <br/>
 */
package goja.rapid.upload;

import com.jfinal.upload.UploadFile;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.oreilly.servlet.multipart.FileRenamePolicy;
import goja.rapid.upload.filerenamepolicy.FileRenamePolicyWrapper;
import org.apache.commons.io.FileUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MultipartRequestExt.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class MultipartRequestExt extends HttpServletRequestWrapper {
	
	private static String saveDirectory;
	private static int maxPostSize;
	private static String encoding;
	static FileRenamePolicy fileRenamePolicy = new DefaultFileRenamePolicy();
	
	private List<UploadFile> uploadFiles;
	private com.oreilly.servlet.MultipartRequest multipartRequest;
	
	static void init(String saveDirectory, int maxPostSize, String encoding) {
		MultipartRequestExt.saveDirectory = saveDirectory;
		MultipartRequestExt.maxPostSize = maxPostSize;
		MultipartRequestExt.encoding = encoding;
	}
	
	public MultipartRequestExt(HttpServletRequest request, String saveDirectory, int maxPostSize, String encoding) {
		super(request);
		wrapMultipartRequest(request, saveDirectory, maxPostSize, encoding);
	}
	
	public MultipartRequestExt(HttpServletRequest request, String saveDirectory, int maxPostSize) {
		super(request);
		wrapMultipartRequest(request, saveDirectory, maxPostSize, encoding);
	}
	
	public MultipartRequestExt(HttpServletRequest request, String saveDirectory) {
		super(request);
		wrapMultipartRequest(request, saveDirectory, maxPostSize, encoding);
	}
	
	public MultipartRequestExt(HttpServletRequest request) {
		super(request);
		wrapMultipartRequest(request, saveDirectory, maxPostSize, encoding);
	}

	/**
	 * 添加对相对路径的支持
	 * 1: 以 "/" 开头或者以 "x:开头的目录被认为是绝对路径
	 * 2: 其它路径被认为是相对路径, 需要 JFinalConfig.uploadedFileSaveDirectory 结合
	 */
	private String handleSaveDirectory(String saveDirectory) {
		if (saveDirectory.startsWith("/") || saveDirectory.indexOf(":") == 1)
			return saveDirectory;
		else 
			return MultipartRequestExt.saveDirectory + saveDirectory;
	}
	
	private void wrapMultipartRequest(HttpServletRequest request, String saveDirectory, int maxPostSize, String encoding) {
		saveDirectory = handleSaveDirectory(saveDirectory);
		
		File dir = new File(saveDirectory);
		if ( !dir.exists()) {
			if (!dir.mkdirs()) {
				throw new RuntimeException("Directory " + saveDirectory + " not exists and can not create directory.");
			}
		}
		
        uploadFiles = new ArrayList<UploadFile>();
		
		try {
			if (null == fileRenamePolicy) {
				fileRenamePolicy = new DefaultFileRenamePolicy();
			}
			
			multipartRequest = new com.oreilly.servlet.MultipartRequest(request, saveDirectory, maxPostSize, encoding, fileRenamePolicy);
			
			String saveDirectoryPath = saveDirectory;
			// 使用了FileRenamePolicyWrapper，获取新的saveDirectory
			if (fileRenamePolicy instanceof FileRenamePolicyWrapper){
				saveDirectoryPath = ((FileRenamePolicyWrapper)fileRenamePolicy).getSaveDirectory();
			}
			
			Enumeration files = multipartRequest.getFileNames();
			while (files.hasMoreElements()) {
				String name = (String)files.nextElement();
				String filesystemName = multipartRequest.getFilesystemName(name);
				
				// 文件没有上传则不生成 UploadFile, 这与 cos的解决方案不一样
				if (filesystemName != null) {
					String originalFileName = multipartRequest.getOriginalFileName(name);
					String contentType = multipartRequest.getContentType(name);
					UploadFile uploadFile = new UploadFile(name, saveDirectoryPath, filesystemName, originalFileName, contentType);
					if (isSafeFile(uploadFile))
						uploadFiles.add(uploadFile);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			fileRenamePolicy = null;
		}
	}
	
	private boolean isSafeFile(UploadFile uploadFile) {
		String fileName = uploadFile.getFileName().trim().toLowerCase();
		if (fileName.endsWith(".jsp") || fileName.endsWith(".jspx")) {
            FileUtils.deleteQuietly(uploadFile.getFile());
			return false;
		}
		return true;
	}
	
	public List<UploadFile> getFiles() {
		return uploadFiles;
	}
	
	/**
	 * Methods to replace HttpServletRequest methods
	 */
	public Enumeration getParameterNames() {
		return multipartRequest.getParameterNames();
	}
	
	public String getParameter(String name) {
		return multipartRequest.getParameter(name);
	}
	
	public String[] getParameterValues(String name) {
		return multipartRequest.getParameterValues(name);
	}
	
	public Map getParameterMap() {
		Map map = new HashMap();
		Enumeration enumm = getParameterNames();
		while (enumm.hasMoreElements()) {
			String name = (String) enumm.nextElement();
			map.put(name, multipartRequest.getParameterValues(name));
		}
		return map;
	}
}






