
package goja.castor.castor;

import goja.castor.Castor;
import goja.lang.Mirror;

@SuppressWarnings({"rawtypes"})
public class String2Mirror extends Castor<String, Mirror> {

    private static final String2Class castor = new String2Class();

    @Override
    public Mirror<?> cast(String src, Class<?> toType, String... args) {
        return Mirror.me(castor.cast(src, toType));
    }

}
