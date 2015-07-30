package goja.jxls.transformer;

import com.google.common.collect.Lists;
import goja.jxls.parser.Cell;
import goja.jxls.parser.Property;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

/**
 * Represents single row in excel transformation
 * @author Leonid Vysochyn
 */
public class Row {
    /**
     * POI {@link org.apache.poi.ss.usermodel.Row} object for the row
     */
    private org.apache.poi.ss.usermodel.Row hssfRow;

    private Sheet sheet;

    /**
     * List of {@link Cell} objects for this Row
     */
    private List<Cell> cells          = Lists.newArrayList();
    /**
     * List of {@link RowCollection} objects for this row
     */
    private List<RowCollection>       rowCollections = Lists.newArrayList();

    /**
     * List of all merged regions found in this row
     */
    private List<MergedRegion> mergedRegions = Lists.newArrayList();

    /**
     * Parent {@link Row} object if there is any
     */
    private Row parentRow;

    /**
     * @return Parent {@link Row} object if there is any
     */
    public Row getParentRow() {
        return parentRow;
    }

    public void setParentRow(Row parentRow) {
        this.parentRow = parentRow;
    }

    public Row(Sheet sheet, org.apache.poi.ss.usermodel.Row hssfRow) {
        this.sheet = sheet;
        this.hssfRow = hssfRow;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    /**
     * @return {@link RowCollection} in this row having maximum number of rows
     */
    public RowCollection getMaxNumberOfRowsCollection(){
        if( rowCollections.size() == 0 ){
            return null;
        }
        RowCollection maxNumberOfRowsCollection = (RowCollection) rowCollections.get(0);
        for (int i = 1, c = rowCollections.size(); i < c; i++) {
            RowCollection rowCollection = rowCollections.get(i);
            if( rowCollection.getNumberOfRows() > maxNumberOfRowsCollection.getNumberOfRows() ){
                maxNumberOfRowsCollection = rowCollection;
            }
        }
        return maxNumberOfRowsCollection;
    }

    /**
     * @return {@link RowCollection} in this row with maximum number of items
     */
    public RowCollection getMaxSizeCollection(){
        if( rowCollections.size() == 0 ){
            return null;
        }
        RowCollection maxSizeRowCollection = rowCollections.get(0);
        for (int i = 1, c = rowCollections.size(); i < c; i++) {
            RowCollection rowCollection = rowCollections.get(i);
            if( rowCollection.getCollectionProperty().getCollection().size() >
                    maxSizeRowCollection.getCollectionProperty().getCollection().size() ){
                maxSizeRowCollection = rowCollection;
            }
        }
        return maxSizeRowCollection;
    }

    /**
     * Founds {@link RowCollection} with given collection name in this row
     * @param collectionName - Collection name used to seek required RowCollection
     * @return {@link RowCollection} in this row having collection with required name
     */
    public RowCollection getRowCollectionByCollectionName(String collectionName){
        for (int i = 0, c = rowCollections.size(); i < c; i++) {
            RowCollection rowCollection = rowCollections.get(i);
            if( rowCollection.getCollectionProperty().getFullCollectionName().equals( collectionName )){
                return rowCollection;
            }
        }
        return null;
    }


    /**
     * Returns list with all {@link RowCollection} objects for this row
     * @return list of {@link RowCollection} objects
     */
    public List getRowCollections() {
        return rowCollections;
    }

    /**
     * Returns {@link RowCollection} corresponding to a collectionProperty.
     * Creates a new one if there is not any
     * @param collectionProperty - Collection property name corresponding RowCollection to find
     * @return {@link RowCollection} corresponding to the collectionProperty
     */
    private RowCollection getRowCollection(Property collectionProperty, int dependentRowNumber){
        for (int i = 0, c = rowCollections.size(); i < c; i++) {
            RowCollection rowCollection = rowCollections.get(i);
            if( rowCollection.getCollectionProperty().getFullCollectionName().equals(collectionProperty.getFullCollectionName()) ){
                return rowCollection;
            }
        }
        RowCollection rowCollection = new RowCollection( this, collectionProperty, dependentRowNumber );
        rowCollections.add( rowCollection );
        return rowCollection;
    }

    /**
     * Adds {@link RowCollection} object to the row collection list
     * @param rowCollection {@link RowCollection} to add
     */
    public void addRowCollection(RowCollection rowCollection){
        rowCollections.add( rowCollection );
    }

    /**
     * Adds {@link MergedRegion} to the list of merged regions in this row
     * @param mergedRegion {@link MergedRegion} to add
     */
    private void addMergedRegion(MergedRegion mergedRegion){
        mergedRegions.add( mergedRegion );
    }

    /**
     * Adds {@link Cell} object to the list of cells for this row
     * @param cell Cell to add
     * @return {@link RowCollection} object if given cell has row collection or null if it has not
     */
    public RowCollection addCell(Cell cell){
        RowCollection rowCollection = null;
        cells.add( cell );
        if (cell.getCollectionProperty() != null) {
            rowCollection = getRowCollection( cell.getCollectionProperty(), cell.getDependentRowNumber() );
            rowCollection.addCell( cell );
            if( cell.getMergedRegion()!=null ){
                MergedRegion mergedRegion = new MergedRegion( cell.getMergedRegion(), rowCollection );
                addMergedRegion( mergedRegion );
            }
        }else if( cell.getMergedRegion()!=null && cell.isEmpty()){
            rowCollection = getRowCollection( cell.getMergedRegion() );
            if( rowCollection!=null ){
                rowCollection.addCell( cell );
            }
        }
        return rowCollection;
    }

    private RowCollection getRowCollection(CellRangeAddress mergedRegion) {
        for (int i = 0, c = mergedRegions.size(); i < c; i++) {
            MergedRegion region = mergedRegions.get(i);
            if( region.getRegion().equals( mergedRegion ) ){
                return region.getRowCollection();
            }
        }
        return null;
    }

    /**
     * @return List of all {@link Cell} objects for this row
     */
    public List<Cell> getCells() {
        return cells;
    }

    public void setCells(List<Cell> cells) {
        this.cells = cells;
    }

    /**
     * @return POI {@link org.apache.poi.ss.usermodel.Row} object for the row
     */
    public org.apache.poi.ss.usermodel.Row getPoiRow() {
        return hssfRow;
    }

    public void setPoiRow(org.apache.poi.ss.usermodel.Row hssfRow) {
        this.hssfRow = hssfRow;
    }

    /**
     * @return The minimal dependent row number for this row
     */
    public int getMinDependentRowNumber() {
        if( rowCollections.size() == 0 ){
            return 0;
        }
        int minDependentRowNumber = rowCollections.get(0).getDependentRowNumber();
        for (int i = 1, c = rowCollections.size(); i < c; i++) {
            RowCollection rowCollection = rowCollections.get(i);
            if( rowCollection.getDependentRowNumber() < minDependentRowNumber ){
                minDependentRowNumber = rowCollection.getDependentRowNumber();
            }
        }
        return minDependentRowNumber;
    }
}
