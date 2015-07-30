
package goja.castor.castor;

import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;

import java.util.Calendar;
import java.util.Date;


public class Datetime2Calendar extends Castor<Date, Calendar> {

    @Override
    public Calendar cast(Date src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        Calendar c = Calendar.getInstance();
        c.setTime(src);
        return c;
    }

}
