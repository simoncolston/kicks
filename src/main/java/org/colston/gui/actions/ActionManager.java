/*
 * Created on 2004/06/05
 *
 */
package org.colston.gui.actions;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.colston.i18n.Messages;

public class ActionManager
{

	public static final String MESSAGE_RESOURCE_PREFIX_KEY = "MessageResourcePrefixKey";
	public static final String SMALL_ICON_NAME_KEY = "SmallIconName";
	public static final String LARGE_ICON_NAME_KEY = "LargeIconName";

	private static Map<Class<? extends Action>, Action> map = new HashMap<>();

	/**
	 * Get an action from the action map.
	 * 
	 * @param cls class of the action
	 * @return the single instance of the action
	 */
	@SuppressWarnings("unchecked")
	public static synchronized <T extends Action> T getAction(Class<T> cls)
	{
		Action action = map.get(cls);
		if (action == null)
		{
			action = createAction(cls);
			if (action != null)
			{
				map.put(cls, action);
			}
		}
		return (T) action;
	}

	private static <T extends Action> T createAction(Class<T> cls)
	{
		try
		{
			T a = cls.getDeclaredConstructor().newInstance();
			initialiseResources(a);
			return a;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Initialises the following properties to resources in the <code>message.properties</code> from the same Java 
	 * package as the <code>Action</code>'s class:
	 * <ul>
	 *    <li><code>Action.NAME</code>              to <code>MESSAGE_RESOURCE_PREFIX_KEY.name</code>
	 *    <li><code>Action.SHORT_DESCRIPTION</code> to <code>MESSAGE_RESOURCE_PREFIX_KEY.tooltip</code>
	 *    <li><code>Action.MNEMONIC_KEY</code>      to <code>MESSAGE_RESOURCE_PREFIX_KEY.mnemonic</code>
	 *    <li><code>Action.ACCELERATOR_KEY</code>   to <code>MESSAGE_RESOURCE_PREFIX_KEY.shortcut.key</code>
	 * </ul>
	 * Also initialises the following properties with image icons from the specified image resources:
	 * <ul>
	 *    <li><code>Action.SMALL_ICON</code>        to <code>SMALL_ICON_NAME_KEY</code>
	 *    <li><code>Action.LARGE_ICON_KEY</code>    to <code>LARGE_ICON_NAME_KEY</code>
	 * </ul>
	 * 
	 * @param a action to initialise
	 */
	public static synchronized void initialiseResources(Action a)
	{
		String mPrefix = (String) a.getValue(MESSAGE_RESOURCE_PREFIX_KEY);
		if (mPrefix != null)
		{
			a.putValue(Action.NAME, Messages.get(a.getClass(), mPrefix + ".name"));
			a.putValue(Action.SHORT_DESCRIPTION, Messages.get(a.getClass(), mPrefix + ".tooltip"));
			String mnemonicName = mPrefix + ".mnemonic";
			String s = Messages.get(a.getClass(), mnemonicName);
			if (!mnemonicName.equalsIgnoreCase(s) && s.length() > 0)
			{
				// The mnemonic was found and is not zero length
				a.putValue(Action.MNEMONIC_KEY, Integer.valueOf(s.codePointAt(0)));
			}
			a.putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(Messages.get(a.getClass(), mPrefix + ".shortcut.key")));
		}
		String s = (String) a.getValue(SMALL_ICON_NAME_KEY);
		if (s != null)
		{
			URL url = a.getClass().getResource(s);
			if (url != null)
			{
				a.putValue(Action.SMALL_ICON, new ImageIcon(url));
			}
		}
		s = (String) a.getValue(LARGE_ICON_NAME_KEY);
		if (s != null)
		{
			URL url = a.getClass().getResource(s);
			if (url != null)
			{
				
				a.putValue(Action.LARGE_ICON_KEY, new ImageIcon(url));
			}
		}
	}
}
