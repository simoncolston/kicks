package org.colston.kicks.gui.canvas;

import java.util.EventListener;

public interface CanvasZoomModelListener extends EventListener {
    void zoomChanged(CanvasZoomEvent event);
}
