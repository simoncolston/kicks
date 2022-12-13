package org.colston.lib.gui.task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;

/**
 * Glass pane that is put on a {@link RootPaneContainer} while a background chore is executing to stop any
 * mouse or keyboard input from the user.
 */
public class TaskGlassPane extends JPanel {
    public TaskGlassPane() {
        setOpaque(false);

        //capture all mouse activity
        MouseAdapter l = new MouseAdapter() {
        };
        addMouseListener(l);
        addMouseMotionListener(l);
        addMouseWheelListener(l);

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
}
