
package goja.castor.castor;

import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;

import java.io.File;


public class File2String extends Castor<File, String> {

    @Override
    public String cast(File src, Class<?> toType, String... args) throws
            FailToCastObjectException {
        return src.getAbsolutePath();
    }

}
