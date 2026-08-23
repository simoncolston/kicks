package org.colston.kicks.gui.canvas;

import org.colston.kicks.render.PageCursor;

public record CanvasCursorModelEvent(PageCursor oldCursor, PageCursor newCursor) {
    public CanvasCursorModelEvent(CanvasCursorModelEvent event) {
        this(event.oldCursor(), event.newCursor());
    }
}
