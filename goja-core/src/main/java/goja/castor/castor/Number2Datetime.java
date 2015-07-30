
package goja.castor.castor;

import goja.castor.Castor;

import java.util.Date;


public class Number2Datetime extends Castor<Number, Date> {

    @Override
    public Date cast(Number src, Class<?> toType, String... args) {
        return new Date(src.longValue());
    }

}
