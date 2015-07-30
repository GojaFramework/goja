
package goja.castor.castor;


import goja.kits.base.Strs;

public class String2Datetime extends DateTimeCastor<String, java.util.Date> {

    @Override
    public java.util.Date cast(String src, Class<?> toType, String... args) {
        // 处理空白
        if (Strs.isBlank(src))
            return null;
        return toDate(src);
    }

}
