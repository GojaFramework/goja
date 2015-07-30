
package goja.castor.castor;

import goja.castor.Castor;
import goja.castor.Castors;
import goja.exceptions.FailToCastObjectException;

import java.util.Collection;


@SuppressWarnings({"rawtypes"})
public class Collection2Object extends Castor<Collection, Object> {

    @Override
    public Object cast(Collection src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        if (src.size() == 0)
            return null;
        return Castors.me().castTo(src.iterator().next(), toType);
    }

}
