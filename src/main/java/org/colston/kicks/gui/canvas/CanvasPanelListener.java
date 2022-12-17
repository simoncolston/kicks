package org.colston.kicks.gui.canvas;

import java.util.EventListener;

public interface CanvasPanelListener extends EventListener {
    void cursorChanged(int index, int offset);
}
