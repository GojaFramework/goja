
package goja.castor.castor;

import goja.castor.Castor;
import goja.kits.base.Strs;

import java.util.TimeZone;

public class String2TimeZone extends Castor<String, TimeZone> {

    @Override
    public TimeZone cast(String src, Class<?> toType, String... args) {
        if (Strs.isBlank(src))
            return null;
        return TimeZone.getTimeZone(src);
    }

}
