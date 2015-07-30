
package goja.castor;

import goja.castor.castor.*;
import goja.exceptions.FailToCastObjectException;
import goja.lang.Lang;
import goja.lang.Mirror;
import goja.lang.TypeExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一个创建 Castor 的工厂类。它的使用方式是：
 * <p/>
 * <pre>
 * Castors.me().cast(obj, fromType, toType);
 * </pre>
 *
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public class Castors {

    private static final Logger log = LoggerFactory.getLogger(Castors.class);

    /**
     * @return 单例
     */
    public static Castors me() {
        return one;
    }


    /**
     * 如何抽取对象的类型级别
     */
    private TypeExtractor extractor;

    /**
     * Castor 的配置
     */
    private Object                    setting;
    private HashMap<Class<?>, Method> settingMap;

    /**
     * 设置转换的配置
     * <p/>
     * 配置对象所有的共有方法都会被遍历。只要这个方法有一个且只有一个参数，并且该参数 是一个 org.nutz.castor.Castor
     * 接口的实现类。那么就会被认为是对该种 Castor 的一个 配置。
     * <p/>
     * 当初始化这个 Castor 之前，会用这个方法来设置一下你的 Castor （如果你的 Castor 需要配置的话）
     *
     * @param obj 配置对象。可以是任意的 Java 对象。
     */
    public synchronized Castors setSetting(Object obj) {
        if (obj != null) {
            setting = obj;
            this.reload();
        }
        return this;
    }

    /**
     * 设置自定义的对象类型提取器逻辑
     *
     * @param te 类型提取器
     */
    public synchronized Castors setTypeExtractor(TypeExtractor te) {
        extractor = te;
        return this;
    }

    private Castors() {
        setting = new DefaultCastorSetting();
        reload();
    }

    private void reload() {
        buildSettingMap();
        //this.map = 
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        classes.addAll(defaultCastorList);
        for (Class<?> klass : classes) {
            try {
                if (Modifier.isAbstract(klass.getModifiers()))
                    continue;
                if (!Castor.class.isAssignableFrom(klass))
                    continue;
                fillMap(klass, settingMap, false);
            } catch (Throwable e) {
                if (log.isWarnEnabled())
                    log.warn("Fail to create castor [%s] because: %s",
                            klass,
                            e.getMessage());
            }
        }
        if (log.isDebugEnabled())
            log.debug("Using %s castor for Castors", map.size());
    }

    private void buildSettingMap() throws SecurityException {
        settingMap = new HashMap<Class<?>, Method>();
        for (Method m1 : setting.getClass().getMethods()) {
            Class<?>[] pts = m1.getParameterTypes();
            if (pts.length == 1 && Castor.class.isAssignableFrom(pts[0])) {
                settingMap.put(pts[0], m1);
            }
        }
    }

    public void addCastor(Class<?> klass) {
        try {
            fillMap(klass, settingMap, true);
        } catch (Throwable e) {
            throw Lang.wrapThrow(Lang.unwrapThrow(e));
        }
    }

    /**
     * 填充 map .<br>
     * 在map中使用hash值来做为key来进行存储
     *
     * @param klass
     * @param settingMap
     * @param replace
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws java.lang.reflect.InvocationTargetException
     */
    private void fillMap(Class<?> klass, HashMap<Class<?>, Method> settingMap, boolean replace)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Castor<?, ?> castor = (Castor<?, ?>) klass.newInstance();
        if (!map.containsKey(castor.toString()) || replace) {
            map.put(castor.toString(), castor);
        } else {
            castor = map.get(castor.toString());
        }
        Method m = settingMap.get(castor.getClass());
        if (null == m) {
            for (Entry<Class<?>, Method> entry : settingMap.entrySet()) {
                Class<?> cc = entry.getKey();
                if (cc.isAssignableFrom(klass)) {
                    m = settingMap.get(cc);
                    break;
                }
            }
        }
        if (null != m)
            m.invoke(setting, castor);
    }

    /**
     * First index is "from" (source) The second index is "to" (target)
     */
    // private Map<String, Map<String, Castor<?, ?>>> map;
    // private Map<Integer, Castor<?,?>> map;
    private Map<String, Castor<?, ?>> map = new ConcurrentHashMap<String, Castor<?, ?>>();
    ;

    /**
     * 转换一个 POJO 从一个指定的类型到另外的类型
     *
     * @param src      源对象
     * @param fromType 源对象类型
     * @param toType   目标类型
     * @param args     转换时参数。有些 Castor 可能需要这个参数，比如 Array2Map
     * @return 目标对象
     * @throws goja.exceptions.FailToCastObjectException 如果没有找到转换器，或者转换失败
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <F, T> T cast(Object src,
                         Class<F> fromType,
                         Class<T> toType,
                         String... args) throws FailToCastObjectException {
        if (null == src) {
            // 原生数据的默认值
            if (toType.isPrimitive()) {
                if (toType == int.class)
                    return (T) Integer.valueOf(0);
                else if (toType == long.class)
                    return (T) Long.valueOf(0L);
                else if (toType == byte.class)
                    return (T) Byte.valueOf((byte) 0);
                else if (toType == short.class)
                    return (T) Short.valueOf((short) 0);
                else if (toType == float.class)
                    return (T) Float.valueOf(.0f);
                else if (toType == double.class)
                    return (T) Double.valueOf(.0);
                else if (toType == boolean.class)
                    return (T) Boolean.FALSE;
                else if (toType == char.class)
                    return (T) Character.valueOf(' ');
                throw Lang.impossible();
            }
            // 是对象，直接返回 null
            return null;
        }

        if (fromType == toType || toType == null || fromType == null)
            return (T) src;

        Class<?> componentType = toType.getComponentType();
        if (null != componentType && fromType != String.class && componentType.isAssignableFrom(fromType)) {
            Object array = Array.newInstance(componentType, 1);
            Array.set(array, 0, src);
            return (T) array;
        }

        if (fromType.getName().equals(toType.getName()))
            return (T) src;
        if (toType.isAssignableFrom(fromType))
            return (T) src;
        Mirror<?> from = Mirror.me(fromType, extractor);
        Castor c = find(from, toType);
        if (null == c)
            throw new FailToCastObjectException(String.format("Can not find castor for '%s'=>'%s' in (%d) because:\n%s",
                    fromType.getName(),
                    toType.getName(),
                    map.size(),
                    "Fail to find matched castor"));
        if (Object2Object.class.getName().equals(c.getClass().getName())
                && from.canCastToDirectly(toType)) { // Use language built-in cases
            return (T) src;
        }
        try {
            return (T) c.cast(src, toType, args);
        } catch (FailToCastObjectException e) {
            throw e;
        } catch (Exception e) {
            throw new FailToCastObjectException(String.format("Fail to cast from <%s> to <%s> for {%s} because:\n%s:%s",
                    fromType.getName(),
                    toType.getName(),
                    src,
                    e.getClass()
                            .getSimpleName(),
                    e.getMessage()),
                    Lang.unwrapThrow(e));
        }
    }

    /**
     * 获取一个转换器
     *
     * @param from 源类型
     * @param to   目标类型
     * @return 转换器
     */
    public <F, T> Castor<F, T> find(Class<F> from, Class<T> to) {
        return find(Mirror.me(from), to);
    }

    @SuppressWarnings("unchecked")
    private <F, T> Castor<F, T> find(Mirror<F> from, Class<T> toType) {
        String key = Castor.key(from.getType(), toType);
        // 哈，这种类型以前转过，直接返回转换器就行了
        if (map.containsKey(key)) {
            return (Castor<F, T>) map.get(key);
        }

        Mirror<T> to = Mirror.me(toType, extractor);
        Class<?>[] fets = from.extractTypes();
        Class<?>[] tets = to.extractTypes();
        for (Class<?> ft : fets) {
            for (Class<?> tt : tets) {
                if (map.containsKey(Castor.key(ft, tt))) {
                    Castor<F, T> castor = (Castor<F, T>) map.get(Castor.key(ft,
                            tt));
                    // 缓存转换器，加速下回转换速度
                    map.put(key, castor);
                    return castor;
                }
            }
        }
        return null;
    }

    /**
     * 转换一个 POJO 到另外的类型
     *
     * @param src    源对象
     * @param toType 目标类型
     * @return 目标对象
     * @throws FailToCastObjectException 如果没有找到转换器，或者转换失败
     */
    public <T> T castTo(Object src, Class<T> toType)
            throws FailToCastObjectException {
        return cast(src, null == src ? null : src.getClass(), toType);
    }

    /**
     * 判断一个类型是否可以被转换成另外一个类型
     * <p/>
     * 判断的依据就是看是否可以直接被转型，以及能不能找到一个专有的转换器
     *
     * @param fromType 起始类型
     * @param toType   目标类型
     * @return 是否可以转换
     */
    public boolean canCast(Class<?> fromType, Class<?> toType) {
        if (Mirror.me(fromType).canCastToDirectly(toType))
            return true;

        if (toType.isArray() && toType.getComponentType().isAssignableFrom(fromType)) {
            return true;
        }

        Castor<?, ?> castor = this.find(fromType, toType);
        return !(castor instanceof Object2Object);
    }

    /**
     * 将一个 POJO 转换成字符串
     *
     * @param src 源对象
     * @return 字符串
     */
    public String castToString(Object src) {
        try {
            return castTo(src, String.class);
        } catch (FailToCastObjectException e) {
            return String.valueOf(src);
        }
    }

    private static List<Class<?>> defaultCastorList = new ArrayList<Class<?>>(120);

    static {

        defaultCastorList.add(Array2Array.class);
        defaultCastorList.add(Array2Collection.class);
        defaultCastorList.add(Array2Map.class);
        defaultCastorList.add(Array2Object.class);
        defaultCastorList.add(Boolean2Number.class);
        defaultCastorList.add(Boolean2String.class);
        defaultCastorList.add(Calendar2Datetime.class);
        defaultCastorList.add(Calendar2Long.class);
        defaultCastorList.add(Calendar2String.class);
        defaultCastorList.add(Calendar2Timestamp.class);
        defaultCastorList.add(Character2Number.class);
        defaultCastorList.add(Class2Mirror.class);
        defaultCastorList.add(Class2String.class);
        defaultCastorList.add(Collection2Array.class);
        defaultCastorList.add(Collection2Collection.class);
        defaultCastorList.add(Collection2Map.class);
        defaultCastorList.add(Collection2Object.class);
        defaultCastorList.add(Collection2String.class);
        defaultCastorList.add(DateTimeCastor.class);
        defaultCastorList.add(Datetime2Calendar.class);
        defaultCastorList.add(Datetime2Long.class);
        defaultCastorList.add(Datetime2SqlDate.class);
        defaultCastorList.add(Datetime2SqlTime.class);
        defaultCastorList.add(Datetime2String.class);
        defaultCastorList.add(Datetime2Timpestamp.class);
        defaultCastorList.add(Enum2Number.class);
        defaultCastorList.add(Enum2String.class);
        defaultCastorList.add(File2String.class);
        defaultCastorList.add(Map2Array.class);
        defaultCastorList.add(Map2Collection.class);
        defaultCastorList.add(Map2Enum.class);
        defaultCastorList.add(Map2String.class);
        defaultCastorList.add(Mirror2Class.class);
        defaultCastorList.add(Mirror2String.class);
        defaultCastorList.add(Number2Boolean.class);
        defaultCastorList.add(Number2Byte.class);
        defaultCastorList.add(Number2Calendar.class);
        defaultCastorList.add(Number2Char.class);
        defaultCastorList.add(Number2Character.class);
        defaultCastorList.add(Number2Datetime.class);
        defaultCastorList.add(Number2Double.class);
        defaultCastorList.add(Number2Enum.class);
        defaultCastorList.add(Number2Float.class);
        defaultCastorList.add(Number2Integer.class);
        defaultCastorList.add(Number2Long.class);
        defaultCastorList.add(Number2Short.class);
        defaultCastorList.add(Number2String.class);
        defaultCastorList.add(Number2Timestamp.class);
        defaultCastorList.add(Object2Class.class);
        defaultCastorList.add(Object2List.class);
        defaultCastorList.add(Object2Mirror.class);
        defaultCastorList.add(Object2Object.class);
        defaultCastorList.add(Object2String.class);
        defaultCastorList.add(SqlDate2String.class);
        defaultCastorList.add(SqlDate2Timestamp.class);
        defaultCastorList.add(SqlTime2String.class);
        defaultCastorList.add(SqlTime2Timestamp.class);
        defaultCastorList.add(String2Array.class);
        defaultCastorList.add(String2BigDecimal.class);
        defaultCastorList.add(String2Boolean.class);
        defaultCastorList.add(String2Byte.class);
        defaultCastorList.add(String2Calendar.class);
        defaultCastorList.add(String2Character.class);
        defaultCastorList.add(String2Class.class);
        defaultCastorList.add(String2Collection.class);
        defaultCastorList.add(String2Datetime.class);
        defaultCastorList.add(String2Double.class);
        defaultCastorList.add(String2Enum.class);
        defaultCastorList.add(String2Float.class);
        defaultCastorList.add(String2Integer.class);
        defaultCastorList.add(String2Long.class);
        defaultCastorList.add(String2Map.class);
        defaultCastorList.add(String2Mirror.class);
        defaultCastorList.add(String2Number.class);
        defaultCastorList.add(String2Object.class);
        defaultCastorList.add(String2Pattern.class);
        defaultCastorList.add(String2Set.class);
        defaultCastorList.add(String2Short.class);
        defaultCastorList.add(String2SqlDate.class);
        defaultCastorList.add(String2SqlTime.class);
        defaultCastorList.add(String2TimeZone.class);
        defaultCastorList.add(String2Timestamp.class);
        defaultCastorList.add(TimeZone2String.class);
        defaultCastorList.add(Timestamp2Calendar.class);
        defaultCastorList.add(Timestamp2Datetime.class);
        defaultCastorList.add(Timestamp2Long.class);
        defaultCastorList.add(Timestamp2SqlDate.class);
        defaultCastorList.add(Timestamp2SqlTime.class);
        defaultCastorList.add(Timestamp2String.class);

    }

    private static Castors one = new Castors();
}
