
package goja.castor.castor;


import goja.date.DateFormatter;
import goja.kits.base.Times;

public class Datetime2String extends DateTimeCastor<java.util.Date, String> {
    private String format = DateFormatter.YYYY_MM_DD_HH_MM_SS;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String cast(java.util.Date src, Class<?> toType, String... args) {
        //return Times.sDT(src);
        return Times.format(format, src);
    }

}
