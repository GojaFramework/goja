
package goja.castor.castor;

import java.util.Set;

import com.alibaba.fastjson.JSON;
import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;

@SuppressWarnings("rawtypes")
public class String2Set extends Castor<String, Set> {

    @Override
    public Set cast(String src, Class<?> toType, String... args) throws FailToCastObjectException {
        return JSON.parseObject(src,Set.class);
    }

}