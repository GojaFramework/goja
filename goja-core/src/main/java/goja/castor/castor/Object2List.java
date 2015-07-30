
package goja.castor.castor;

import goja.castor.Castor;
import goja.exceptions.FailToCastObjectException;
import goja.lang.Lang;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings({"rawtypes"})
public class Object2List extends Castor<Object, List> {

    @Override
    @SuppressWarnings("unchecked")
    public List cast(Object src, Class<?> toType, String... args) throws FailToCastObjectException {
        try {
            List<Object> list = (List<Object>) (toType == List.class ? new ArrayList<Object>(1)
                                                                    : toType.newInstance());
            list.add(src);
            return list;
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

}
