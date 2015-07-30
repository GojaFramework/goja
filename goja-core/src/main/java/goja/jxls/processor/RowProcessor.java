package goja.jxls.processor;

import goja.jxls.transformer.Row;

import java.util.Map;


/**
 * Allows dynamic processing of rows
 * @author <a href="mailto:Lvissochin@db.luxoft.com">Leonid Vysochin</a>
 */
public interface RowProcessor {
    void processRow(Row row, Map namedCells);
}
