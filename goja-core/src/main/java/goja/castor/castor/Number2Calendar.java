
package goja.castor.castor;

import goja.castor.Castor;

import java.util.Calendar;


public class Number2Calendar extends Castor<Number, Calendar> {

    @Override
    public Calendar cast(Number src, Class<?> toType, String... args) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(src.longValue());
        return c;
    }

}
