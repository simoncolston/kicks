package org.colston.kicks.gui.canvas;

import org.colston.gui.actions.ActionManager;
import org.colston.gui.actions.ComponentAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

class CanvasAction extends ComponentAction {
    protected CanvasControl control;
    protected BiConsumer<CanvasControl, ActionEvent> handler;
    private Consumer<ActionEvent> listener;

    CanvasAction(BiConsumer<CanvasControl, ActionEvent> handler, KeyStroke[] keyStrokes, String actionCommand) {
        super(keyStrokes, actionCommand);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, actionCommand);
        this.handler = handler;
    }

    CanvasAction(KeyStroke[] keyStrokes, String actionCommand) {
        super(keyStrokes, actionCommand);
    }

    void setControl(CanvasControl cc) {
        this.control = cc;
    }

    public void setListener(Consumer<ActionEvent> listener) {
        this.listener = listener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (handler != null) {
            handler.accept(control, e);
            if (listener != null) {
                listener.accept(e);
            }
        }
    }
}
