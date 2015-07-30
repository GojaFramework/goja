
package goja.castor.castor;

import goja.castor.Castor;
import goja.castor.Castors;
import goja.exceptions.FailToCastObjectException;

import java.lang.reflect.Array;
import java.util.Collection;


@SuppressWarnings({"rawtypes"})
public class Collection2Array extends Castor<Collection, Object> {

    public Collection2Array() {
        this.fromClass = Collection.class;
        this.toClass = Array.class;
    }

    @Override
    public Object cast(Collection src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        Class<?> compType = toType.getComponentType();
        Object ary = Array.newInstance(compType, src.size());
        int index = 0;
        for (Object aSrc : src) {
            Array.set(ary, index++, Castors.me().castTo(aSrc, compType));
        }
        return ary;
    }

}
