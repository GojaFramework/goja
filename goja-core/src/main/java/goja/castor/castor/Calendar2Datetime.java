
package goja.castor.castor;

import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;

import java.util.Calendar;
import java.util.Date;


public class Calendar2Datetime extends Castor<Calendar, Date> {

    @Override
    public Date cast(Calendar src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        return src.getTime();
    }

}
