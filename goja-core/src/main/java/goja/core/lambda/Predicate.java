package goja.core.lambda;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public interface Predicate<T> {

    Predicate<?> ALWAYS_TRUE = new Predicate() {
        @Override
        public boolean eval(Object param) throws Exception {
            return true;
        }
    };

    Predicate<?> ALWAYS_FALSE = new Predicate() {
        @Override
        public boolean eval(Object param) throws Exception {
            return false;
        }
    };

    boolean eval(T param) throws Exception;
}
