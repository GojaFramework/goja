
package goja.castor.castor;

import com.alibaba.fastjson.JSON;
import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;
import goja.kits.base.Strs;
import goja.lang.Mirror;

public class String2Object extends Castor<String, Object> {

    @Override
    public Object cast(String src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        if (Strs.isQuoteByIgnoreBlank(src, '{', '}'))
            return JSON.parseObject(src, toType);
        return Mirror.me(toType).born(src);
    }

}
