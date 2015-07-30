package goja.date;

import java.util.Date;

/**
 * Date provider, use it rather than directly obtain the system time.
 *
 * @author calvin
 * @deprecated
 */
public interface DateProvider {

    /**
     * default implementation
     */
    static final DateProvider DEFAULT = new DefaultDateProvider();

    /**
     * Gets the current time.
     *
     * @return the current time.
     */
    Date getCurrentDate();

    /**
     * The current time of milliseconds.
     *
     * @return The current time of milliseconds.
     */
    long getCurrentTimeInMillis();

    /**
     * The default time provider, returns the current time, thread safety.
     */
    public static class DefaultDateProvider implements DateProvider {

        @Override
        public Date getCurrentDate() {
            return new Date();
        }

        @Override
        public long getCurrentTimeInMillis() {
            return System.currentTimeMillis();
        }
    }

}
