package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class SaveAs extends AbstractAction {
    public static final String ACTION_COMMAND = "action.save.as";

    private static final String MESSAGE_RESOURCE_PREFIX = "save.as";
    private static final String SMALL_ICON_NAME = "SaveAs24.png";
    private static final String LARGE_ICON_NAME = "SaveAs24.png";

    public SaveAs() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
        putValue(ActionManager.SMALL_ICON_NAME_KEY, SMALL_ICON_NAME);
        putValue(ActionManager.LARGE_ICON_NAME_KEY, LARGE_ICON_NAME);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionManager.getAction(Save.class).saveAs();
    }
}
