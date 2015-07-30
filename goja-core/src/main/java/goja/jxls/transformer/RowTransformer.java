package goja.jxls.transformer;

import goja.jxls.controller.SheetTransformationController;
import goja.jxls.model.Block;
import goja.jxls.transformation.ResultTransformation;

import java.util.Map;


/**
 * Defines row transformation methods
 */
public interface RowTransformer {
    Row getRow();
    ResultTransformation transform(SheetTransformationController stc, SheetTransformer sheetTransformer, Map beans, ResultTransformation previousTransformation);
    Block getTransformationBlock();
    void setTransformationBlock(Block block);
    ResultTransformation getTransformationResult();

}
