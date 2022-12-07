package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksApp;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class New extends AbstractAction {
    public static final String ACTION_COMMAND = "action.new";

    private static final String MESSAGE_RESOURCE_PREFIX = "new";
    private static final String SMALL_ICON_NAME = "New24.png";
    private static final String LARGE_ICON_NAME = "New24.png";

    public New() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
        putValue(ActionManager.SMALL_ICON_NAME_KEY, SMALL_ICON_NAME);
        putValue(ActionManager.LARGE_ICON_NAME_KEY, LARGE_ICON_NAME);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!KicksApp.checkSaveChangesToCurrentDocument()) {
            return;
        }
        KicksApp.newDocument();
    }
}
