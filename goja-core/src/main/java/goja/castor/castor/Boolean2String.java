
package goja.castor.castor;


import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;

public class Boolean2String extends Castor<Boolean, String> {

    @Override
    public String cast(Boolean src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        return String.valueOf(src);
    }

}
