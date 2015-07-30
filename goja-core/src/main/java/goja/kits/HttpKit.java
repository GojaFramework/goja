package goja.kits;

import goja.libs.IO;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class HttpKit {


    public static class ContentTypeWithEncoding {
        public final String contentType;
        public final String encoding;

        public ContentTypeWithEncoding(String contentType, String encoding) {
            this.contentType = contentType;
            this.encoding = encoding;
        }
    }

    public static ContentTypeWithEncoding parseContentType( String contentType ) {
        if( contentType == null ) {
            return new ContentTypeWithEncoding("text/html".intern(), null);
        } else {
            String[] contentTypeParts = contentType.split(";");
            String _contentType = contentTypeParts[0].trim().toLowerCase();
            String _encoding = null;
            // check for encoding-info
            if( contentTypeParts.length >= 2 ) {
                String[] encodingInfoParts = contentTypeParts[1].split(("="));
                if( encodingInfoParts.length == 2 && encodingInfoParts[0].trim().equalsIgnoreCase("charset")) {
                    // encoding-info was found in request
                    _encoding = encodingInfoParts[1].trim();

                    if (StringUtils.isNotBlank(_encoding) &&
                            ((_encoding.startsWith("\"") && _encoding.endsWith("\""))
                                    || (_encoding.startsWith("'") && _encoding.endsWith("'")))
                            ) {
                        _encoding = _encoding.substring(1, _encoding.length() - 1).trim();
                    }
                }
            }
            return new ContentTypeWithEncoding(_contentType, _encoding);
        }

    }

    public static class StatusCode {

        public static final int OK = 200;
        public static final int CREATED = 201;
        public static final int ACCEPTED = 202;
        public static final int PARTIAL_INFO = 203;
        public static final int NO_RESPONSE = 204;
        public static final int MOVED = 301;
        public static final int FOUND = 302;
        public static final int METHOD = 303;
        public static final int NOT_MODIFIED = 304;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int PAYMENT_REQUIRED = 402;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int INTERNAL_ERROR = 500;
        public static final int NOT_IMPLEMENTED = 501;
        public static final int OVERLOADED = 502;
        public static final int GATEWAY_TIMEOUT = 503;

        public static boolean success(int code) {
            return code / 100 == 2;
        }

        public static boolean redirect(int code) {
            return code / 100 == 3;
        }

        public static boolean error(int code) {
            return code / 100 == 4 || code / 100 == 5;
        }
    }


    private static final Map<String, String> lower2UppercaseHttpHeaders = initLower2UppercaseHttpHeaders();

    private static Map<String, String> initLower2UppercaseHttpHeaders() {
        Map<String, String> map = new HashMap<String, String>();

        String path = "/libs/http_headers.properties";
        InputStream in = HttpKit.class.getResourceAsStream(path);
        if (in == null) {
            throw new RuntimeException("Error reading " + path);
        }
        List<String> lines = IO.readLines(in);
        for (String line : lines) {
            line = line.trim();
            if ( !line.startsWith("#")) {
                map.put(line.toLowerCase(), line);
            }
        }

        return Collections.unmodifiableMap(map);
    }

    /**
     * Use this method to make sure you have the correct caseing of a http header name.
     * eg: fixes 'content-type' to 'Content-Type'
     */
    public static String fixCaseForHttpHeader( String headerName) {
        if (headerName == null) {
            return null;
        }
        String correctCase = lower2UppercaseHttpHeaders.get(headerName.toLowerCase());
        if ( correctCase != null) {
            return correctCase;
        }
        // Didn't find it - return it as it is
        return headerName;
    }
}
