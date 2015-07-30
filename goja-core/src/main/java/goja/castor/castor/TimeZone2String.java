
package goja.castor.castor;

import goja.castor.Castor;

import java.util.TimeZone;


public class TimeZone2String extends Castor<TimeZone, String> {

    @Override
    public String cast(TimeZone src, Class<?> toType, String... args) {
        return src.getID();
    }

}
