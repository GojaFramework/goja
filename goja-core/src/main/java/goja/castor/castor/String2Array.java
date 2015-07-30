
package goja.castor.castor;

import com.alibaba.fastjson.JSON;
import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;
import goja.kits.base.Strs;
import goja.lang.Lang;

import java.lang.reflect.Array;

public class String2Array extends Castor<String, Object> {

    public String2Array() {
        this.fromClass = String.class;
        this.toClass = Array.class;
    }

    @Override
    public Object cast(String src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        if (Strs.isQuoteByIgnoreBlank(src, '[', ']')) {
            return JSON.parseObject(src, toType);
        }
        String[] ss = Strs.splitIgnoreBlank(src);
        return Lang.array2array(ss, toType.getComponentType());
    }

}
