package org.colston.kicks.gui.canvas;

import org.colston.gui.actions.ActionManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

class NoteAction extends CanvasAction {
    private final int string;
    private final int placement;

    NoteAction(KeyStroke[] keyStrokes, String actionCommand, int string, int placement) {
        super(keyStrokes, actionCommand);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, "canvas.note." + string + "." + placement);
        this.string = string;
        this.placement = placement;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        control.addNote(string, placement, (e.getModifiers() & ActionEvent.SHIFT_MASK) > 0);
    }

    public int getString() {
        return string;
    }

    public int getPlacement() {
        return placement;
    }
}
