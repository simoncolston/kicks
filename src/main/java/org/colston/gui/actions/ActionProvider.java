package org.colston.gui.actions;

import java.util.List;

import javax.swing.Action;

public interface ActionProvider
{
	List<Action> getMenuActions(String menuName);

	List<Action> getToolBarActions(String menuName);
}
