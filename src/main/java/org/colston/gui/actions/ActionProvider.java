package org.colston.gui.actions;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

public interface ActionProvider {
    List<Action> getMenuActions(String menuName);

    List<Action> getToolBarActions(String menuName);

    Collection<? extends Action> getAllActions();
}
