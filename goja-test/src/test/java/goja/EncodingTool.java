package goja;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.7
 */
public class EncodingTool {
    @Test
    public void convert() throws Exception {
        //GBK编码格式源码路径
        String srcDirPath = "/Users/sog/Downloads/basisPlatform/src";
        //转为UTF-8编码格式源码路径
        String utf8DirPath = "/Users/sog/Downloads/basisPlatform/utf8/src";

        //获取所有java文件
        Collection<File> javaGbkFileCol = FileUtils.listFiles(new File(srcDirPath), new
                String[]{"java","xml", "properties", "jsp", "jspa", "vm", "css", "js", "html",
                        "htm"},
                true);
        for (File javaGbkFile : javaGbkFileCol) {
            //UTF8格式文件路径
            String utf8FilePath = utf8DirPath + javaGbkFile.getAbsolutePath().substring(srcDirPath.length());
            //使用GBK读取数据，然后用UTF-8写入数据
            FileUtils.writeLines(new File(utf8FilePath), "UTF-8", FileUtils.readLines(javaGbkFile, "GBK"));
        }
    }
}
