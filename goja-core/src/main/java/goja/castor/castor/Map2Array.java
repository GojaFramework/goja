
package goja.castor.castor;

import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;
import goja.lang.Lang;

import java.lang.reflect.Array;
import java.util.Map;


@SuppressWarnings({"rawtypes"})
public class Map2Array extends Castor<Map, Object> {

    public Map2Array() {
        this.fromClass = Map.class;
        this.toClass = Array.class;
    }

    @Override
    public Object cast(Map src, Class<?> toType, String... args) throws FailToCastObjectException {
        return Lang.collection2array(src.values(), toType.getComponentType());
    }

}
