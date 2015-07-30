
package goja.castor.castor;


import goja.castor.Castor;

public class Character2Number extends Castor<Character, Number> {

    @Override
    public Number cast(Character src, Class<?> toType, String... args) {
        return (int) src;
    }

}
