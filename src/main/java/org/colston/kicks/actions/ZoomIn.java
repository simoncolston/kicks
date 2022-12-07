package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksApp;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.Tuning;
import org.colston.kicks.gui.util.JapaneseTextFocusListener;
import org.colston.sclib.i18n.Messages;
import org.colston.utils.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ZoomIn extends AbstractAction {
    public static final String ACTION_COMMAND = "action.zoom.in";

    private static final String MESSAGE_RESOURCE_PREFIX = "zoom.in";
    private static final String SMALL_ICON_NAME = "ZoomIn24.png";
    private static final String LARGE_ICON_NAME = "ZoomIn24.png";

    public ZoomIn() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
        putValue(ActionManager.SMALL_ICON_NAME_KEY, SMALL_ICON_NAME);
        putValue(ActionManager.LARGE_ICON_NAME_KEY, LARGE_ICON_NAME);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
            KicksApp.canvas().zoomIn();
    }
}
