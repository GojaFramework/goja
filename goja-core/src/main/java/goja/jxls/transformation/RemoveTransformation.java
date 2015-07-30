package goja.jxls.transformation;

import com.google.common.collect.Lists;
import goja.jxls.formula.CellRef;
import goja.jxls.model.Block;
import goja.jxls.model.Point;

import java.util.List;


/**
 * Remove transformation
 * @author Leonid Vysochyn
 */
public class RemoveTransformation extends BlockTransformation {

    public RemoveTransformation(Block block) {
        super(block);
    }

    public Block getBlockAfterTransformation() {
        return null;
    }


    public List transformCell(Point p) {
        List<Point> cells = null;
        if( !block.contains( p ) ){
            cells = Lists.newArrayListWithCapacity(1);
            cells.add(p);
        }
        return cells;
    }

    public List transformCell(String sheetName, CellRef cellRef) {
        List<String> cells = null;
        String refSheetName = cellRef.getSheetName();
        if( block.getSheet().getSheetName().equalsIgnoreCase( refSheetName ) || (cellRef.getSheetName() == null && block.getSheet().getSheetName().equalsIgnoreCase( sheetName ))){
            if( !block.contains( cellRef.getRowNum(), cellRef.getColNum() ) ){
                cells = Lists.newArrayList();
                cells.add( cellRef.toString() );
            }
        }else{
            cells = Lists.newArrayListWithCapacity(1);
            cells.add( cellRef.toString() );
        }
        return cells;
    }

    public String toString() {
        return "RemoveTransformation: {" + super.toString() + "}";
    }
}
