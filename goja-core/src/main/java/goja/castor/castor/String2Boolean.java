
package goja.castor.castor;

import goja.castor.Castor;
import goja.kits.base.Strs;
import goja.lang.Lang;

public class String2Boolean extends Castor<String, Boolean> {

    @Override
    public Boolean cast(String src, Class<?> toType, String... args) {
        return !Strs.isBlank(src) && Lang.parseBoolean(src);
    }

}
