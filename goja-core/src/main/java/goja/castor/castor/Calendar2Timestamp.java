
package goja.castor.castor;

import goja.castor.Castor;

import java.util.Calendar;
import java.sql.Timestamp;


public class Calendar2Timestamp extends Castor<Calendar, Timestamp> {

    @Override
    public Timestamp cast(Calendar src, Class<?> toType, String... args) {
        long ms = src.getTimeInMillis();
        return new Timestamp(ms);
    }
}
