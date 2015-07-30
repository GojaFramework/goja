
package goja.castor.castor;

import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;

import java.sql.Time;
import java.util.Date;


public class Datetime2SqlTime extends Castor<Date, Time> {

    @Override
    public Time cast(Date src, Class<?> toType, String... args) throws FailToCastObjectException {
        return new Time(src.getTime());
    }

}
