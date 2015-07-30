
package goja.castor.castor;

import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;

@SuppressWarnings({"rawtypes"})
public class String2Enum extends Castor<String, Enum> {

    @SuppressWarnings("unchecked")
    @Override
    public Enum cast(String src, Class<?> toType, String... args) throws FailToCastObjectException {
        return Enum.valueOf((Class<Enum>) toType, src);
    }

}
