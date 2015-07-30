
package goja.castor.castor;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;

@SuppressWarnings({"rawtypes"})
public class String2Map extends Castor<String, Map> {

    @Override
    public Map cast(String src, Class<?> toType, String... args) throws FailToCastObjectException {
        return JSON.parseObject(src, new TypeReference<Map>(){});
    }

}
