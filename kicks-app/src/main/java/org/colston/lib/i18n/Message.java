package org.colston.lib.i18n;

/**
 * Internationalisation message.
 *
 * @author simon
 */
public class Message {
    private final String packageName;
    private final String property;
    private final Object[] args;

    /**
     * Constructor.
     * @param clz class to use to find package name to reference {@code messages.properties} file.
     * @param key  key to be used for lookup in the properties file
     * @param args arguments for the property
     */
    public Message(Class<?> clz, String key, Object... args) {
        this.packageName = clz.getPackage().getName();
        this.property = key;
        this.args = args;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getProperty() {
        return property;
    }

    public Object[] getArgs() {
        return args;
    }
}
