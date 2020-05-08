package org.colston.gui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ActionProviders
{

	private static List<ActionProvider> providers = new ArrayList<>();
	
	public static void register(ActionProvider ap)
	{
		providers.add(ap);
	}
	
	public static List<ActionProvider> getAllProviders()
	{
		return Collections.unmodifiableList(providers);
	}
}
