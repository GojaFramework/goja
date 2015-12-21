package goja.core.kits;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public interface Mapper<FROM, TO> {

    TO map(FROM src) throws Exception;
}
