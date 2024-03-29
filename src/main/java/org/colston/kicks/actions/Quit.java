package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksApp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

public class Quit extends AbstractAction {
    public static final String ACTION_COMMAND = "action.quit";

    private static final String MESSAGE_RESOURCE_PREFIX = "quit";

    public Quit() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = KicksApp.frame();
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }
}
