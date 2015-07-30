
package goja.castor.castor;

import goja.kits.base.Times;

import java.sql.Timestamp;


public class Timestamp2String extends DateTimeCastor<Timestamp, String> {

    @Override
    public String cast(Timestamp src, Class<?> toType, String... args) {
        return Times.sDT(Times.D(src.getTime()));
    }

}
