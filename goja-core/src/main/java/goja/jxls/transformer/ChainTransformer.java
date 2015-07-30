package goja.jxls.transformer;

import goja.jxls.controller.SheetTransformationController;
import goja.jxls.processor.RowProcessor;
import goja.jxls.model.Block;
import goja.jxls.transformation.ResultTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;


/**
 * Controls a list of transformers
 * @author Leonid Vysochyn
 */
public class ChainTransformer{
    protected static final Logger log = LoggerFactory.getLogger(ChainTransformer.class);

    List  transformers;
    Sheet sheet;
    List  rowProcessors;
    Row   parentRow;

    public ChainTransformer(List transformers, Sheet sheet, List rowProcessors, Row parentRow) {
        this.transformers = transformers;
        this.sheet = sheet;
        this.rowProcessors = rowProcessors;
        this.parentRow = parentRow;
    }

    public ChainTransformer(List transformers, List rowProcessors, Row parentRow) {
        this.transformers = transformers;
        this.rowProcessors = rowProcessors;
        this.parentRow = parentRow;
    }

    public ChainTransformer(List transformers, List rowProcessors) {
        this.transformers = transformers;
        this.rowProcessors = rowProcessors;
    }

    public ChainTransformer(List transformers) {
        this.transformers = transformers;
    }



    ResultTransformation transform(SheetTransformationController stc, SheetTransformer sheetTransformer, Map beans){
        ResultTransformation resultTransformation = new ResultTransformation();
        for (int i = 0, c=transformers.size(); i < c; i++) {
            RowTransformer rowTransformer = (RowTransformer) transformers.get(i);
            Block transformationBlock = rowTransformer.getTransformationBlock();
            transformationBlock = resultTransformation.transformBlock( transformationBlock );
            rowTransformer.setTransformationBlock( transformationBlock );
            log.debug(rowTransformer.getClass().getName() + ", " + rowTransformer.getTransformationBlock());
            //rowTransformer
            Row row = rowTransformer.getRow();
            row.setParentRow( parentRow );
            applyRowProcessors(row );
//            Util.writeToFile("beforeTransformBlock.xls", sheet.getPoiWorkbook());
            resultTransformation.add( rowTransformer.transform(stc, sheetTransformer, beans, resultTransformation) );
//            Util.writeToFile("afterTransformBlock.xls", sheet.getPoiWorkbook());

        }
        return resultTransformation;
    }

    /**
     * Applies all registered RowProcessors to a row
     * @param row - {@link Row} object with row information
     */
    private void applyRowProcessors(Row row) {
        for (int i = 0, c=rowProcessors.size(); i < c; i++) {
            RowProcessor rowProcessor = (RowProcessor) rowProcessors.get(i);
            rowProcessor.processRow(row, sheet.getNamedCells());
        }
    }


}
