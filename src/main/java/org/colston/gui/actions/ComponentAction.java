package org.colston.gui.actions;

import javax.swing.*;

public abstract class ComponentAction extends AbstractAction {

    private final KeyStroke[] keyStrokes;

    public ComponentAction(KeyStroke[] keyStrokes, String actionCommand) {
        this.keyStrokes = keyStrokes;
        putValue(ACTION_COMMAND_KEY, actionCommand);
    }

    public KeyStroke[] getKeyStrokes() {
        return keyStrokes;
    }

    public String getActionCommand() {
        return (String) getValue(ACTION_COMMAND_KEY);
    }
}
