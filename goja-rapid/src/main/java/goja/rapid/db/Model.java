package goja.rapid.db;

import goja.castor.Castors;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.util.Date;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class Model<M extends Model> extends com.jfinal.plugin.activerecord.Model<M> {
    private static final long serialVersionUID = -4236614897933149382L;


    public <T> T getData(String attr, Class<T> cls) {
        Object value = get(attr);
        return Castors.me().castTo(value, cls);
    }

    public int valInt(String attr) {
        return getData(attr, Integer.class);
    }

    public long valLong(String attr) {
        return getData(attr, Long.class);
    }

    public double valDouble(String attr) {
        return getData(attr, Double.class);
    }

    public float valFloat(String attr) {
        return getData(attr, Float.class);
    }

    public Date valDate(String attr) {
        return getData(attr, Date.class);
    }

    public DateTime valDateTime(String attr) {
        Timestamp timestamp = getTimestamp(attr);
        return new DateTime(timestamp.getTime());
    }

    public boolean valBoolean(String attr) {
        Object val = get(attr);
        if (val == null) {
            return false;
        }
        if (val instanceof Boolean) {
            return (Boolean) val;
        } else if (val instanceof Number) {
            Number val_num = (Number) val;
            return val_num.intValue() == 0;
        } else if (val instanceof String) {
            String val_str = (String) val;
            return StringUtils.equalsIgnoreCase(val_str, "Y")
                    || StringUtils.equalsIgnoreCase(val_str, "1")
                    || StringUtils.equalsIgnoreCase(val_str, "ON");
        } else {
            return false;
        }
    }

}
