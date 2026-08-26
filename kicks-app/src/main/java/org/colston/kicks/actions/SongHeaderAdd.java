package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksApp;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SongHeaderAdd extends AbstractAction {
    public static final String ACTION_COMMAND = "action.song.header.add";

    private static final String MESSAGE_RESOURCE_PREFIX = "song.header.add";

    public SongHeaderAdd() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
        setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        KicksApp.canvas().addSongHeader();
    }
}
