package goja.el.opt.custom;

import goja.el.opt.RunMethod;

import java.util.HashMap;
import java.util.Map;


/**
 * 自定义函数工厂类
 *
 * @author juqkai(juqkai@gmail.com)
 */
public class CustomMake {
    private static Map<String, RunMethod> runms = new HashMap<String, RunMethod>();

    static {

        final Max max = new Max();
        final Min min = new Min();
        final Trim trim = new Trim();
        final MakeUUID makeUUID = new MakeUUID();
        runms.put(max.fetchSelf(), max);
        runms.put(min.fetchSelf(), min);
        runms.put(trim.fetchSelf(), trim);
        runms.put(makeUUID.fetchSelf(), makeUUID);
    }

    /**
     * 加载插件
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void init() {


        final MakeUUID makeUUID = new MakeUUID();
        runms.put(makeUUID.fetchSelf(), makeUUID);
    }

    /**
     * 自定义方法 工厂方法
     */
    public static RunMethod make(String val) {
        return runms.get(val);
    }

}
