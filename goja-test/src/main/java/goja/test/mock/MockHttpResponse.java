package goja.test.mock;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Locale;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class MockHttpResponse implements HttpServletResponse {

    private StringWriter resp;

    public MockHttpResponse(StringWriter resp) {
        this.resp = resp;
    }

    @Override
    public void addCookie(Cookie arg0) {

    }

    @Override
    public void addDateHeader(String arg0, long arg1) {

    }

    @Override
    public void addHeader(String arg0, String arg1) {

    }

    @Override
    public void addIntHeader(String arg0, int arg1) {

    }

    @Override
    public boolean containsHeader(String arg0) {

        return false;
    }

    @Override
    public String encodeRedirectUrl(String arg0) {

        return null;
    }

    @Override
    public String encodeRedirectURL(String arg0) {

        return null;
    }

    @Override
    public String encodeUrl(String arg0) {

        return null;
    }

    @Override
    public String encodeURL(String arg0) {

        return null;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public int getBufferSize() {

        return 0;
    }

    @Override
    public String getCharacterEncoding() {

        return null;
    }

    @Override
    public String getContentType() {

        return null;
    }

    @Override
    public String getHeader(String arg0) {

        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {

        return null;
    }

    @Override
    public Collection<String> getHeaders(String arg0) {

        return null;
    }

    @Override
    public Locale getLocale() {

        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        // OutputStream os = new FileOutputStream(new File("/home/kid/mock"));
        // return new MockServletOutputStream(os);
        return null;
    }

    @Override
    public int getStatus() {

        return 0;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        //        PrintWriter writer = new PrintWriter(System.out);
        return new PrintWriter(resp);
    }

    @Override
    public boolean isCommitted() {

        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public void sendError(int arg0) throws IOException {

    }

    @Override
    public void sendError(int arg0, String arg1) throws IOException {

    }

    @Override
    public void sendRedirect(String arg0) throws IOException {

    }

    @Override
    public void setBufferSize(int arg0) {

    }

    @Override
    public void setCharacterEncoding(String arg0) {

    }

    @Override
    public void setContentLength(int arg0) {

    }

    @Override
    public void setContentType(String arg0) {

    }

    @Override
    public void setDateHeader(String arg0, long arg1) {

    }

    @Override
    public void setHeader(String arg0, String arg1) {

    }

    @Override
    public void setIntHeader(String arg0, int arg1) {

    }

    @Override
    public void setLocale(Locale arg0) {

    }

    @Override
    public void setStatus(int arg0) {

    }

    @Override
    public void setStatus(int arg0, String arg1) {

    }
}
