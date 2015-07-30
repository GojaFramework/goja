
package goja.castor.castor;


import goja.castor.Castor;

public class Number2Boolean extends Castor<Number, Boolean> {

    @Override
    public Boolean cast(Number src, Class<?> toType, String... args) {
        return src.intValue() != 0;
    }

}
