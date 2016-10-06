package goja.test.mock;

import javax.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class MockServletInputStream extends ServletInputStream {
    private InputStream is;

    public MockServletInputStream(String str) throws UnsupportedEncodingException {
        is = new ByteArrayInputStream(str.getBytes("UTF-8"));
    }

    @Override
    public int read() throws IOException {
        return is.read();
    }
}
