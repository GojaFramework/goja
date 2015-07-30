
package goja.castor.castor;

import com.alibaba.fastjson.JSON;
import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;

import java.util.Collection;


@SuppressWarnings({"rawtypes"})
public class Collection2String extends Castor<Collection, String> {

    @Override
    public String cast(Collection src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        return JSON.toJSONString(src);
    }

}
