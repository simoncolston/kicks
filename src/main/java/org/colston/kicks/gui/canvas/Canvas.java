package org.colston.kicks.gui.canvas;

import org.colston.gui.actions.ActionProvider;
import org.colston.kicks.document.KicksDocument;

import javax.swing.*;
import java.awt.*;
import java.awt.print.Printable;

public interface Canvas {
    void requestFocusInWindow();

    JComponent getContainer();

    JComponent getComponent();

    Component getInputComponent();

    ActionProvider getActionProvider();

    Printable getPrintable();

    KicksDocument getDocument();

    void setDocument(KicksDocument doc);

    boolean isDocumentChanged();

    void documentSaved();

    void undo();

    void redo();

    void zoomIn();

    void zoomOut();

    void zoomReset();

    void setNoteSizeNormal();

    void setNoteSizeSmall();

    enum AutoCursor {
        OFF, HALF, ONE
    }
}
