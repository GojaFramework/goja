
package goja.castor.castor;

import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;

public class String2Character extends Castor<String, Character> {

    @Override
    public Character cast(String src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        return src.charAt(0);
    }

}
