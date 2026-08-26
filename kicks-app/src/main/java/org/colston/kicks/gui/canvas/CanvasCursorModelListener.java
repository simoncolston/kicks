package org.colston.kicks.gui.canvas;

import java.util.EventListener;

public interface CanvasCursorModelListener extends EventListener {

    default boolean vetoableCursorChanged(CanvasCursorModelEvent e) {
        cursorChanged(e);
        return true;
    }

    void cursorChanged(CanvasCursorModelEvent e);
}
