package goja.core.app;

/**
 * <p>
 * 系统运行模式
 * </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public enum ApplicationMode {
    /**
     * 开发模式，框架默认
     */
    DEV,

    /**
     * 测试模式
     */
    TEST,
    /**
     * 生产运行模式
     */
    PROD;

    /**
     * Determine whether the current operation mode for the development pattern
     *
     * @return If returns true then said to development mode, or as an official running environment.
     */
    public boolean isDev() {
        return this == DEV;
    }

    public boolean isTest() {
        return this == TEST;
    }

    public boolean isProd() {
        return this == PROD;
    }
}
