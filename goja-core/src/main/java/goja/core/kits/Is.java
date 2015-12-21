package goja.core.kits;

import goja.core.G;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class Is<T> {

    private final T value;

    public Is(T value) {
        this.value = value;
    }

    @SafeVarargs
    public final boolean in(T... candidates) {
        return G.isIn(value, candidates);
    }

    public boolean map() {
        return G.isMap(value);
    }

    public boolean list() {
        return G.isList(value);
    }

    public boolean set() {
        return G.isSet(value);
    }

    public boolean collection() {
        return G.isCollection(value);
    }

}
