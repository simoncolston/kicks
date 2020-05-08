package org.colston.i18n;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Internationlised messages.
 * 
 * @author simon
 */
public final class Messages
{

	// Name of the properties files used for message internationalisation.
	private static final String MESSAGE_PROPERTIES_NAME = "messages";

	public static final String get(Class<?> clz, String property, Object... args)
	{

		return get(clz.getPackage().getName(), property, args);
	}

	public static final String get(Message m)
	{

		return m == null ? "" : get(m.getPackageName(), m.getProperty(), m.getArgs());
	}

	private static final String get(String packageName, String property, Object... args)
	{

		String resname = String.format("%s.%s", packageName, MESSAGE_PROPERTIES_NAME);
		String value = null;
		try
		{

			ResourceBundle bundle = ResourceBundle.getBundle(resname);
			value = bundle.getString(property);
		} catch (MissingResourceException e)
		{
		}

		// TODO: Handle the case where one of the arguments is a Message object
		// (Recursively convert to string first then feed to MessageFormat.format(...)

		return value == null ? property : MessageFormat.format(value, args);
	}
}
