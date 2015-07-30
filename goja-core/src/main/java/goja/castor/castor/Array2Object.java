
package goja.castor.castor;

import goja.castor.Castor;
import goja.castor.Castors;
import goja.exceptions.FailToCastObjectException;

import java.lang.reflect.Array;


public class Array2Object extends Castor<Object, Object> {

    public Array2Object() {
        this.fromClass = Array.class;
        this.toClass = Object.class;
    }

    @Override
    public Object cast(Object src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        if (Array.getLength(src) == 0)
            return null;
        return Castors.me().castTo(Array.get(src, 0), toType);
    }

}
