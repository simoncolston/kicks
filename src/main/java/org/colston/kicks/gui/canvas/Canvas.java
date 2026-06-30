package org.colston.kicks.gui.canvas;

import org.colston.gui.actions.ActionProvider;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.KicksDocumentEditor;

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

    KicksDocumentEditor getEditor();

    void setDocument(KicksDocument doc);

    boolean isDocumentChanged();

    void documentSaved();

    void copy();

    void paste();

    void delete();

    void undo();

    void redo();

    void zoomIn();

    void zoomOut();

    void zoomReset();

    void setNoteSizeNormal();

    void setNoteSizeSmall();

    void setFinger(int finger);

    void editSongHeader();

    void addSongHeader();

    void removeSongHeader();

    enum AutoCursor {
        OFF, HALF, ONE
    }
}
