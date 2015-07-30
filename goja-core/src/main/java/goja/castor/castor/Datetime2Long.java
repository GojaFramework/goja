
package goja.castor.castor;

import goja.castor.Castor;

import java.util.Date;

public class Datetime2Long extends Castor<Date, Long> {

    @Override
    public Long cast(Date src, Class<?> toType, String... args) {
        return src.getTime();
    }

}
