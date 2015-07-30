
package goja.castor.castor;


import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;

@SuppressWarnings({"rawtypes"})
public class Object2Class extends Castor<Object, Class> {

    @Override
    public Class cast(Object src, Class<?> toType, String... args) throws FailToCastObjectException {
        return src.getClass();
    }

}
