package goja.rapid.mvc.render;

import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import goja.rapid.excel.PoiExporter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> 导出POI工具</p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class PoiRender extends Render {

    private static final Logger logger = LoggerFactory.getLogger(PoiRender.class);

    private final static String CONTENT_TYPE = "application/msexcel;charset=" + getEncoding();
    private final List<?>[]  data;
    private       String[][] headers;
    private String[] sheetNames = new String[]{};
    private int cellWidth;
    private String[] columns  = new String[]{};
    private String   fileName = "file1.xls";
    private int    headerRow;
    private String version;

    public PoiRender(List<?>[] data) {
        this.data = data;
    }

    public static PoiRender me(List<?>... data) {
        return new PoiRender(data);
    }

    @Override
    public void render() {
        response.reset();
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        response.setContentType(CONTENT_TYPE);
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            PoiExporter.data(data)
                    .version(version)
                    .sheetNames(sheetNames)
                    .headerRow(headerRow)
                    .headers(headers)
                    .columns(columns)
                    .cellWidth(cellWidth)
                    .export()
                    .write(os);
        } catch (Exception e) {
            throw new RenderException(e);
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public PoiRender headers(String[]... headers) {
        this.headers = headers;
        return this;
    }

    public PoiRender headerRow(int headerRow) {
        this.headerRow = headerRow;
        return this;
    }

    public PoiRender columns(String... columns) {
        this.columns = columns;
        return this;
    }

    public PoiRender sheetName(String... sheetName) {
        this.sheetNames = sheetName;
        return this;
    }

    public PoiRender cellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
        return this;
    }

    public PoiRender fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public PoiRender version(String version) {
        this.version = version;
        return this;
    }
}
