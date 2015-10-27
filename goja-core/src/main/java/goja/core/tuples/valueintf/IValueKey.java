package goja.core.tuples.valueintf;

/** 
 * <p>
 * Marker interface for tuples with a "key" value.
 * </p> 

 * @since 1.1
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public interface IValueKey<X> {

    public X getKey();
    
}
