package org.colston.lib.i18n;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Localizing internationalized messages.
 */
public final class Messages {

    // Name of the properties files used for message internationalisation.
    private static final String MESSAGE_PROPERTIES_NAME = "messages";

    /**
     * Pseudonym for {@link #get(Class, String, Object...)}.
     * @param clz class in same package as properties file
     * @param property property key
     * @param args format string arguments
     * @return localised message
     */
    public static String message(Class<?> clz, String property, Object... args) {

        return get(clz, property, args);
    }

    public static String get(Class<?> clz, String property, Object... args) {

        return get(clz.getPackage().getName(), property, args);
    }

    public static String get(Message m) {

        return m == null ? "" : get(m.getPackageName(), m.getProperty(), m.getArgs());
    }

    private static String get(String packageName, String property, Object... args) {

        String resource = packageName + "." + MESSAGE_PROPERTIES_NAME;
        String value = null;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(resource);
            value = bundle.getString(property);
        } catch (MissingResourceException ignored) {
        }

        // TODO: Handle the case where one of the arguments is a Message object
        // (Recursively convert to string first then feed to MessageFormat.format(...)

        return value == null ? property : MessageFormat.format(value, args);
    }
}
