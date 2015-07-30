
package goja.castor.castor;

import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;

import java.util.Map;


@SuppressWarnings("rawtypes")
public class Map2Enum extends Castor<Map, Enum> {

    @Override
    public Enum cast(Map src, Class<?> toType, String... args) throws FailToCastObjectException {
        return null;
    }

}
