
package goja.castor.castor;

import java.sql.Time;
import java.sql.Timestamp;

import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;

public class Timestamp2SqlTime extends Castor<Timestamp, Time> {

    @Override
    public Time cast(Timestamp src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        return new Time(src.getTime());
    }

}
