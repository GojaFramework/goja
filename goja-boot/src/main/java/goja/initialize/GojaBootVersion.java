package goja.initialize;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public final class GojaBootVersion {

    private GojaBootVersion() {
    }

    /**
     * Return the full version string of the present Goja Boot codebase, or {@code null}
     * if it cannot be determined.
     * @return the version of Goja Boot or {@code null}
     * @see Package#getImplementationVersion()
     */
    public static String getVersion() {
        Package pkg = GojaBootVersion.class.getPackage();
        return (pkg != null ? pkg.getImplementationVersion() : null);
    }

}
