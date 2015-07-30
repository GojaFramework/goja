
package goja.castor.castor;

import goja.castor.Castor;

import java.util.Calendar;


public class Calendar2Long extends Castor<Calendar, Long> {
    @Override
    public Long cast(Calendar src, Class<?> toType, String... args) {
        return src.getTimeInMillis();
    }
}
