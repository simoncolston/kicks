package org.colston.kicks.gui.canvas;

import java.awt.*;

interface CanvasPages {

    int PAGE_PADDING = 20;

    void requestFocusInWindow();

    void documentSet();

    void redraw();

    void handleText();

    String getText();

    int getNumberOfPages();

    void scrollPageToVisible(int pageIndex);

    void setDimensions(Dimension dimension);
}
