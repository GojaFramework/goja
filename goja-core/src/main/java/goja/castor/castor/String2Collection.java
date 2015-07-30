package goja.castor.castor;

import java.util.Collection;

import com.alibaba.fastjson.JSON;
import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;

@SuppressWarnings({"rawtypes"})
public class String2Collection extends Castor<String, Collection> {

    @Override
    public Collection cast(String src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        return (Collection) JSON.parseObject(src, toType);
    }

}
