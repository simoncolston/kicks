package org.colston.kicks.gui.canvas;

import javax.swing.event.EventListenerList;
import java.awt.*;

public class CanvasZoomModel {

    private final EventListenerList listeners = new EventListenerList();

    private double scale;

    void zoomIn() {
        scale = scale * 10.0 / 9.0; // divide by multiple of 3
        fireZoomChanged();
    }

    void zoomOut() {
        scale = scale * 9.0 / 10.0;
        fireZoomChanged();
    }

    void zoomReset() {
        int res = Toolkit.getDefaultToolkit().getScreenResolution();
        scale = (1.0f * res / 72f);
        fireZoomChanged();
    }

    void addListener(CanvasZoomModelListener listener) {
        listeners.add(CanvasZoomModelListener.class, listener);
    }

    private void fireZoomChanged() {
        CanvasZoomEvent event = new CanvasZoomEvent(scale);
        for (CanvasZoomModelListener l : listeners.getListeners(CanvasZoomModelListener.class)) {
            l.zoomChanged(event);
        }
    }

    public double getScale() {
        return scale;
    }
}
