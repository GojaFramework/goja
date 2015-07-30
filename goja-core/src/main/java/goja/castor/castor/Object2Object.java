
package goja.castor.castor;


import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;
import goja.lang.Mirror;

public class Object2Object extends Castor<Object, Object> {

    @Override
    public Object cast(Object src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        return Mirror.me(toType).born(src);
    }

}
