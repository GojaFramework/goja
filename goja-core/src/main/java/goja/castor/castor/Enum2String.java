
package goja.castor.castor;


import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;

@SuppressWarnings({"rawtypes"})
public class Enum2String extends Castor<Enum, String> {

    @Override
    public String cast(Enum src, Class<?> toType, String... args) throws FailToCastObjectException {
        return src.name();
    }
}
