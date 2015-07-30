package goja.jxls.controller;

import goja.jxls.transformer.Workbook;

import java.util.List;


/**
 * Defines interface to control workbook transformations
 * @author Leonid Vysochyn
 */
public interface WorkbookTransformationController {
    List getSheetTransformationControllers();
    void setSheetTransformationControllers(List sheetTransformationControllers);
    void addSheetTransformationController(SheetTransformationController sheetTransformationController);
    void removeSheetTransformationController(SheetTransformationController sheetTransformationController);
    Workbook getWorkbook();
}
