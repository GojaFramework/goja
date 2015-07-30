/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.kits.io;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * <p>
 * The File Uitl.
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-11-04 11:16
 * @since JDK 1.6
 */
public final class FileEncodingKit {
    /**
     * The kit logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(FileEncodingKit.class);

    private FileEncodingKit() {

    }

    /**
     * Get coding format file.
     *
     * @param file the file.
     * @return File encoding format.
     */
    public static Optional<Charset> charset(File file) {
        if (!file.exists()) {
            logger.error("The file [ {} ] is not exist.", file.getAbsolutePath());
            return Optional.absent();
        }

        FileInputStream fileInputStream = null;
        BufferedInputStream bin = null;
        try {
            fileInputStream = new FileInputStream(file);
            bin = new BufferedInputStream(fileInputStream);
            int p = (bin.read() << 8) + bin.read();
            Optional<Charset> charset;
            //其中的 0xefbb、0xfffe、0xfeff、0x5c75这些都是这个文件的前面两个字节的16进制数
            switch (p) {
                case 0xefbb:
                    charset = Optional.of(Charsets.UTF_8);
                    break;
                case 0xfffe:
                    charset = Optional.of(Charset.forName("Unicode"));
                    break;
                case 0xfeff:
                    charset = Optional.of(Charsets.UTF_16BE);
                    break;
                case 0x5c75:
                    charset = Optional.of(Charsets.US_ASCII);
                    break;
                default:
                    charset = Optional.of(Charset.forName("GBK"));
            }

            return charset;
        } catch (FileNotFoundException e) {
            logger.error("The file [ {} ] is not exist.", file.getAbsolutePath(), e);
        } catch (IOException e) {
            logger.error("Read file has error, {}.", file.getAbsolutePath(), e);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(bin);
        }

        return Optional.absent();
    }

}
