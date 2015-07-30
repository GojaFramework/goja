
package goja.castor.castor;

import java.sql.Date;
import java.sql.Timestamp;

import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;

public class Timestamp2SqlDate extends Castor<Timestamp, Date> {

    @Override
    public Date cast(Timestamp src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        return new Date(src.getTime());
    }

}
