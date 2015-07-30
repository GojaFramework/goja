package goja.plugins.sqlmap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.plugin.activerecord.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class SqlMapParams extends HashMap<String, Object> {
    private static final long serialVersionUID = 4587304172441070501L;


    private final Map<String, Object> _params = Maps.newHashMap();

    private final List<Object> _paramVals = Lists.newArrayList();


    public <T> SqlMapParams add(String field, T param) {
        this._params.put(field, param);
        this._paramVals.add(param);
        return this;
    }

    public <T extends Model> SqlMapParams add(T model) {
        for (String filed : model.getAttrNames()) {
            this._params.put(filed, model.get(filed));
        }
        return this;
    }


    public SqlMapParams remove(String field) {
        Object obv = _params.get(field);
        this._params.remove(field);
        this._paramVals.remove(obv);
        return this;
    }

    public SqlMapParams clr() {
        this._params.clear();
        this._paramVals.clear();
        return this;
    }

    public Object[] getParamVals() {
        return _paramVals.toArray();
    }
}
