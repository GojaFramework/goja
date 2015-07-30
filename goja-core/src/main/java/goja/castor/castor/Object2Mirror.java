
package goja.castor.castor;


import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;
import goja.lang.Mirror;

@SuppressWarnings({"rawtypes"})
public class Object2Mirror extends Castor<Object, Mirror> {

    @Override
    public Mirror cast(Object src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        return Mirror.me(src.getClass());
    }

}
