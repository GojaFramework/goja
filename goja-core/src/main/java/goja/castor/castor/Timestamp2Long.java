
package goja.castor.castor;

import java.sql.Timestamp;

import goja.castor.Castor;

public class Timestamp2Long extends Castor<Timestamp, Long> {

    @Override
    public Long cast(Timestamp src, Class<?> toType, String... args) {
        return src.getTime();
    }

}
