package goja.tuples.valueintf;

/** 
 * <p>
 * Marker interface for tuples with a "label" value.
 * </p> 

 * @since 1.1
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public interface IValueLabel<X> {

    public X getLabel();
    
}
