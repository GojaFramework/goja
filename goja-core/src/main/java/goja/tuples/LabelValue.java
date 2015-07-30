package goja.tuples;

import java.util.Collection;
import java.util.Iterator;

import  goja.tuples.valueintf.IValueLabel;
import  goja.tuples.valueintf.IValueValue;

/**
 * <p>
 * A tuple of two elements, with positions 0 and 1 renamed as "label" and 
 * "value", respectively.
 * </p> 
 * 
 * @since 1.0
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public final class LabelValue<A,B> 
        extends Tuple
        implements IValueLabel<A>,
                   IValueValue<B> {

    private static final long serialVersionUID = 5055574980300695706L;

    private static final int SIZE = 2;

    private final A label;
    private final B value;
    
    
    
    public static <A,B> LabelValue<A,B> with(final A label, final B value) {
        return new LabelValue<A,B>(label,value);
    }

    
    /**
     * <p>
     * Create tuple from array. Array has to have exactly two elements.
     * </p>
     * 
     * @param <X> the array component type 
     * @param array the array to be converted to a tuple
     * @return the tuple
     */
    public static <X> LabelValue<X,X> fromArray(final X[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length != 2) {
            throw new IllegalArgumentException("Array must have exactly 2 elements in order to create a LabelValue. Size is " + array.length);
        }
        return new LabelValue<X,X>(array[0],array[1]);
    }

    
    public static <X> LabelValue<X,X> fromCollection(final Collection<X> collection) {
        return fromIterable(collection);
    }


    
    public static <X> LabelValue<X,X> fromIterable(final Iterable<X> iterable) {
        return fromIterable(iterable, 0, true);
    }

    
    
    public static <X> LabelValue<X,X> fromIterable(final Iterable<X> iterable, int index) {
        return fromIterable(iterable, index, false);
    }
    

    
    private static <X> LabelValue<X,X> fromIterable(final Iterable<X> iterable, int index, final boolean exactSize) {
        
        if (iterable == null) {
            throw new IllegalArgumentException("Iterable cannot be null");
        }

        boolean tooFewElements = false; 
        
        X element0 = null;
        X element1 = null;
        
        final Iterator<X> iter = iterable.iterator();
        
        int i = 0;
        while (i < index) {
            if (iter.hasNext()) {
                iter.next();
            } else {
                tooFewElements = true;
            }
            i++;
        }
        
        if (iter.hasNext()) {
            element0 = iter.next();
        } else {
            tooFewElements = true;
        }
        
        if (iter.hasNext()) {
            element1 = iter.next();
        } else {
            tooFewElements = true;
        }
        
        if (tooFewElements && exactSize) {
            throw new IllegalArgumentException("Not enough elements for creating a LabelValue (2 needed)");
        }
        
        if (iter.hasNext() && exactSize) {
            throw new IllegalArgumentException("Iterable must have exactly 2 available elements in order to create a LabelValue.");
        }
        
        return new LabelValue<X,X>(element0, element1);
        
    }
    
    
    
    
    public LabelValue(
            final A label, 
            final B value) {
        super(label, value);
        this.label = label;
        this.value = value;
    }


    public A getLabel() {
        return this.label;
    }


    public B getValue() {
        return this.value;
    }


    @Override
    public int getSize() {
        return SIZE;
    }
    
    
    
    public <X> LabelValue<X,B> setLabel(final X label) {
        return new LabelValue<X,B>(label, this.value);
    }
    
    
    public <Y> LabelValue<A,Y> setValue(final Y value) {
        return new LabelValue<A,Y>(this.label, value);
    }
    
    
    
    
    
}
