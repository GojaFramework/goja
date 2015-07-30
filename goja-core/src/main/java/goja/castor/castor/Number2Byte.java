
package goja.castor.castor;


import goja.castor.Castor;

public class Number2Byte extends Castor<Number, Byte> {

    @Override
    public Byte cast(Number src, Class<?> toType, String... args) {
        return src.byteValue();
    }

}
