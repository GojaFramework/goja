package goja.jxls.transformer;

import com.google.common.collect.Lists;
import goja.jxls.parser.Cell;
import goja.jxls.parser.Property;

import java.util.Iterator;
import java.util.List;


/**
 * Contains information about collections in a row of XLS template
 * @author Leonid Vysochyn
 */
public class RowCollection {
    /**
     * The number of dependent rows for this collection
     */
    private int dependentRowNumber = 0;

    /**
     * Collection property
     */
    private Property collectionProperty;
    /**
     * List of {@link Cell} objects related to the collection
     */
    private List<Cell> cells = Lists.newArrayList();

    /**
     * {@link Row} object containing this collection
     */
    private Row parentRow;

    private String collectionItemName;

    private int numberOfRows;

    public RowCollection(Row parentRow, Property collectionProperty, int dependentRowNumber) {
        this.parentRow = parentRow;
        setCollectionProperty(collectionProperty, dependentRowNumber);
    }

    public RowCollection(Property property) {
        setCollectionProperty(property, 0);
    }

    public RowCollection(Property collectionProperty, int dependentRowNumber) {
        this.dependentRowNumber = dependentRowNumber;
        setCollectionProperty( collectionProperty, dependentRowNumber );
    }

    public Row getParentRow() {
        return parentRow;
    }

    public void setParentRow(Row parentRow) {
        this.parentRow = parentRow;
    }

    public void addCell(Cell cell){
        cells.add( cell );
        cell.setRowCollection( this );
    }

    public Property getCollectionProperty() {
        return collectionProperty;
    }

    private void setCollectionProperty(Property collectionProperty, int dependentRowNumber){
        this.dependentRowNumber = dependentRowNumber;
        this.collectionProperty = collectionProperty;
        numberOfRows = (collectionProperty.getCollection().size() - 1)*(dependentRowNumber + 1);
    }

    public List<Cell> getCells() {
        return cells;
    }

    public int getDependentRowNumber() {
        return dependentRowNumber;
    }

    public void setDependentRowNumber(int dependentRowNumber) {
        this.dependentRowNumber = dependentRowNumber;
    }

    public boolean containsCell(Cell cell){
        return cell.getPoiCell() == null ||
                (cell.getRowCollection() != null &&
                        collectionProperty.getFullCollectionName().equals(cell.getRowCollection().getCollectionProperty().getFullCollectionName()));
    }

    public String getCollectionItemName() {
        return collectionItemName;
    }

    public void setCollectionItemName(String collectionItemName) {
        this.collectionItemName = collectionItemName;
    }

    public int getNumberOfRows(){
        return numberOfRows;
    }

    private Iterator iterator;
    private int iterateStep;
    private int iterateIndex;
    private Object iterateObject;

    /**
     * @return Current Collection Item being iterated
     */
    public Object getIterateObject() {
        return iterateObject;
    }

    /**
     * @return next object in the collection
     */
    public Object getNextObject(){
        if( (iterateIndex % (dependentRowNumber+1)) == 0 ){
            iterateObject = iterator.next();
        }
        iterateIndex += iterateStep + 1;
        return iterateObject;
    }

    /**
     *
     * @return true if the next invocation of getNextObject() will return object in the collection or false otherwise
     */
    public boolean hasNextObject(){
        return iterator.hasNext();
    }

    public void createIterator(int step){
        if( step != dependentRowNumber ){
            throw new IllegalArgumentException("Not supported yet");
            // todo: implement the case when iterateStep is lesser than dependentRowNumber
            //throw new IllegalArgumentException("IterateStep must be lesser or equal to dependentRowNumber, iterateStep=" + step + ", dependentRowNumber" + dependentRowNumber);
        }
        iterator = collectionProperty.getCollection().iterator();
        this.iterateStep = step;
        iterateIndex = 0;
    }

    public List<Cell> getRowCollectionCells(){
        List<Cell> columnNumbers = Lists.newArrayList();
        for (int i = 0, c = parentRow.getCells().size(); i < c; i++) {
            Cell cell = parentRow.getCells().get(i);
            if( containsCell( cell ) ){
                columnNumbers.add( cell );
            }
        }
        return columnNumbers;
    }

    /**
     * @return Collection Name associated with this row collection
     */
    public String toString() {
        return collectionProperty.getFullCollectionName();
    }

}
