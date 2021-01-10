package org.colston.kicks.gui.canvas;

import org.colston.gui.actions.ActionProvider;
import org.colston.gui.task.TaskListener;
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

    TaskListener getTaskListener();

    Printable getPrintable();

    KicksDocument getDocument();

    void setDocument(KicksDocument doc);

    boolean isDocumentChanged();

    void documentSaved();

    void undo();

    void redo();
}
