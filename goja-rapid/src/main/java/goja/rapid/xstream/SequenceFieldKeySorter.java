package goja.rapid.xstream;

import com.beust.jcommander.internal.Maps;
import com.thoughtworks.xstream.converters.reflection.FieldKey;
import com.thoughtworks.xstream.converters.reflection.FieldKeySorter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class SequenceFieldKeySorter implements FieldKeySorter {
    @Override
    public Map sort(Class type, Map keyedByFieldKey) {
        Annotation sequence = type.getAnnotation(XMLSequence.class);
        if (sequence != null) {
            final String[] fieldsOrder = ((XMLSequence) sequence).value();
            Map<Object, Object> result = Maps.newLinkedHashMap();
            Set<Map.Entry<FieldKey, Field>> fields = keyedByFieldKey.entrySet();
            for (String fieldName : fieldsOrder) {
                if (fieldName != null) {
                    for (Map.Entry<FieldKey, Field> fieldEntry : fields) {
                        if (fieldName.equals(fieldEntry.getKey().getFieldName())) {
                            result.put(fieldEntry.getKey(), fieldEntry.getValue());
                        }
                    }
                }
            }
            return result;
        } else {
            return keyedByFieldKey;
        }

    }
}
