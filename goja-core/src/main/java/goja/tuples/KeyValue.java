package goja.tuples;

import java.util.Collection;
import java.util.Iterator;

import  goja.tuples.valueintf.IValueKey;
import  goja.tuples.valueintf.IValueValue;

/**
 * <p>
 * A tuple of two elements, with positions 0 and 1 renamed as "key" and 
 * "value", respectively.
 * </p> 
 * 
 * @since 1.0
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public final class KeyValue<A,B> 
        extends Tuple
        implements IValueKey<A>, 
                   IValueValue<B> {

    private static final long serialVersionUID = 3460957157833872509L;

    private static final int SIZE = 2;

    private final A key;
    private final B value;
    
    
    
    public static <A,B> KeyValue<A,B> with(final A key, final B value) {
        return new KeyValue<A,B>(key,value);
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
    public static <X> KeyValue<X,X> fromArray(final X[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length != 2) {
            throw new IllegalArgumentException("Array must have exactly 2 elements in order to create a KeyValue. Size is " + array.length);
        }
        return new KeyValue<X,X>(array[0],array[1]);
    }

    
    public static <X> KeyValue<X,X> fromCollection(final Collection<X> collection) {
        return fromIterable(collection);
    }

    
    
    public static <X> KeyValue<X,X> fromIterable(final Iterable<X> iterable) {
        return fromIterable(iterable, 0, true);
    }

    
    
    public static <X> KeyValue<X,X> fromIterable(final Iterable<X> iterable, int index) {
        return fromIterable(iterable, index, false);
    }

    
    
    private static <X> KeyValue<X,X> fromIterable(final Iterable<X> iterable, int index, final boolean exactSize) {
        
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
            throw new IllegalArgumentException("Not enough elements for creating a KeyValue (2 needed)");
        }
        
        if (iter.hasNext() && exactSize) {
            throw new IllegalArgumentException("Iterable must have exactly 2 available elements in order to create a KeyValue.");
        }
        
        return new KeyValue<X,X>(element0, element1);
        
    }
    
    
    public KeyValue(
            final A key, 
            final B value) {
        super(key, value);
        this.key = key;
        this.value = value;
    }


    public A getKey() {
        return this.key;
    }


    public B getValue() {
        return this.value;
    }


    @Override
    public int getSize() {
        return SIZE;
    }
    
    
    
    public <X> KeyValue<X,B> setKey(final X key) {
        return new KeyValue<X,B>(key, this.value);
    }
    
    
    public <Y> KeyValue<A,Y> setValue(final Y value) {
        return new KeyValue<A,Y>(this.key, value);
    }
    
    
    
    
    
}
