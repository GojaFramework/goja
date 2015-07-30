
package goja.castor.castor;

import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;
import goja.lang.Lang;

import java.lang.reflect.Array;
import java.util.Map;


@SuppressWarnings({"unchecked", "rawtypes"})
public class Array2Map extends Castor<Object, Map> {

    public Array2Map() {
        this.fromClass = Array.class;
        this.toClass = Map.class;
    }

    @Override
    public Map cast(Object src, Class<?> toType, String... args) throws FailToCastObjectException {
        if (null == args || args.length == 0)
            throw Lang.makeThrow(FailToCastObjectException.class,
                    "For the elements in array %s[], castors don't know which one is the key field.",
                    src.getClass().getComponentType().getName());
        return Lang.array2map((Class<Map<Object, Object>>) toType, src, args[0]);
    }

}
