package org.colston.kicks.gui.canvas;

import org.colston.gui.actions.ActionManager;
import org.colston.gui.actions.ActionProvider;
import org.colston.kicks.KicksMain;
import org.colston.kicks.actions.Redo;
import org.colston.kicks.actions.Title;
import org.colston.kicks.actions.Tuning;
import org.colston.kicks.actions.Undo;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.KicksDocumentListener;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class CanvasControl implements Canvas {
    private final KicksDocumentListener docListener = new KicksDocumentListener() {
        @Override
        public void documentUpdated() {
            updateUndoActions();
            canvasPanel.revalidate();
            canvasPanel.repaint();
        }

        @Override
        public void locationUpdated(int index, int offset) {
            canvasPanel.setCursor(index, offset, true);
        }
    };

    private final UndoableEditListener undoListener = new UndoableEditListener() {

        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            undo.addEdit(e.getEdit());
        }
    };
    private final CanvasActionProvider actionProvider = new CanvasActionProvider();

    private final JComponent container;
    private final CanvasPanel canvasPanel;
    private final JComponent inputComponent;
    private final UndoManager undo = new UndoManager();
    private KicksDocument savedDocument = null;

    CanvasControl(JPanel container, CanvasPanel canvasPanel, JComponent inputComponent) {
        this.container = container;
        this.canvasPanel = canvasPanel;
        this.inputComponent = inputComponent;
    }

    @Override
    public void requestFocusInWindow() {
        canvasPanel.requestFocusInWindow();
    }

    @Override
    public JComponent getContainer() {
        return container;
    }

    @Override
    public JComponent getComponent() {
        return canvasPanel;
    }

    @Override
    public Component getInputComponent() {
        return inputComponent;
    }

    @Override
    public ActionProvider getActionProvider() {
        return actionProvider;
    }

    @Override
    public Printable getPrintable() {
        return canvasPanel;
    }

    @Override
    public KicksDocument getDocument() {
        return canvasPanel.getDocument();
    }

    @Override
    public void setDocument(KicksDocument doc) {
        undo.discardAllEdits();
        updateUndoActions();

        if (getDocument() != null) {
            getDocument().removeDocumentListener(docListener);
            getDocument().removeUndoableEditListener(undoListener);
        }
        doc.addDocumentListener(docListener);
        doc.addUndoableEditListener(undoListener);

        canvasPanel.setDocument(doc);
        documentSaved();

        canvasPanel.revalidate();
        canvasPanel.repaint();

        requestFocusInWindow();
    }

    @Override
    public boolean isDocumentChanged() {
        return !savedDocument.equals(getDocument());
    }

    @Override
    public void documentSaved() {
        savedDocument = KicksMain.getDocumentStore().clone(getDocument());
    }

    @Override
    public void undo() {
        undo.undo();
        updateUndoActions();
    }

    @Override
    public void redo() {
        undo.redo();
        updateUndoActions();
    }

    private void updateUndoActions() {
        Action u = ActionManager.getAction(Undo.class);
        u.setEnabled(undo.canUndo());
        u.putValue(Action.NAME, undo.getUndoPresentationName());
        Action r = ActionManager.getAction(Redo.class);
        r.setEnabled(undo.canRedo());
        r.putValue(Action.NAME, undo.getRedoPresentationName());
    }

    public void addLyric() {
        canvasPanel.addLyric();
    }

    void addNote(int string, int placement, boolean isSmall) {
        canvasPanel.addNote(string, placement, isSmall);
    }

    void moveCursorLeft() {
        canvasPanel.moveCursorLeft();
    }

    void moveCursorRight() {
        canvasPanel.moveCursorRight();
    }

    void moveCursorUp(int modifiers) {
        canvasPanel.moveCursorUp(modifiers);

    }

    void moveCursorDown(int modifiers) {
        canvasPanel.moveCursorDown(modifiers);
    }

    void addRest() {
        canvasPanel.addRest();
    }

    void addRepeat(boolean end) {
        canvasPanel.addRepeat(end);
    }

    void setFlat() {
        canvasPanel.setFlat();
    }

    void setUtou(boolean isKaki) {
        canvasPanel.setUtou(isKaki);
    }

    void delete() {
        canvasPanel.delete();
    }

    void setChord() {
        canvasPanel.setChord();
    }

    void setSlur() {
        canvasPanel.setSlur();
    }

    private static class CanvasActionProvider implements ActionProvider {
        private final List<Action> editActions = new ArrayList<>();
        private final List<Action> documentActions = new ArrayList<>();

        public CanvasActionProvider() {
            editActions.add(ActionManager.getAction(Undo.class));
            editActions.add(ActionManager.getAction(Redo.class));
            documentActions.add(ActionManager.getAction(Title.class));
            documentActions.add(ActionManager.getAction(Tuning.class));
        }

        @Override
        public List<Action> getMenuActions(String menuName) {
            if ("menu.edit".equals(menuName)) {
                return Collections.unmodifiableList(editActions);
            }
            if ("menu.document".equals(menuName)) {
                return Collections.unmodifiableList(documentActions);
            }
            return null;
        }

        @Override
        public List<Action> getToolBarActions(String menuName) {
            if ("menu.edit".equals(menuName)) {
                return Collections.unmodifiableList(editActions);
            }
            return null;
        }
    }
}
