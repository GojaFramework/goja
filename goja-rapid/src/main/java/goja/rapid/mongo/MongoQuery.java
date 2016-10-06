package goja.rapid.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;

import java.util.regex.Pattern;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public class MongoQuery {

    private BasicDBObject d = new BasicDBObject();

    private BasicDBList list = new BasicDBList();

    //数组

    public void add(String key, Object value) {
        list.add(new BasicDBObject(key, value));
    }

    public void add(String operator, String key, Object value) {
        list.add(new BasicDBObject(key, new BasicDBObject(operator, value)));
    }

    //普通

    public MongoQuery set(String key, Object value) {
        d.put(key, value);
        return this;
    }

    public MongoQuery set(String operator, String key, Object value) {
        d.put(key, new BasicDBObject(operator, value));
        return this;
    }

    public MongoQuery like(String key, String value) {
        Pattern pattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
        d.put(key, pattern);
        return this;
    }

    public DBObject get() {
        return d;
    }

    //or连接
    public DBObject getOrList() {
        d.put(QueryOperators.OR, list);
        return d;
    }

    //and连接
    public DBObject getAndList() {
        d.put(QueryOperators.AND, list);
        return d;
    }
}
