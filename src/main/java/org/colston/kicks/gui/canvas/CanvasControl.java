package org.colston.kicks.gui.canvas;

import org.colston.gui.actions.ActionManager;
import org.colston.gui.actions.ActionProvider;
import org.colston.kicks.KicksApp;
import org.colston.kicks.actions.Redo;
import org.colston.kicks.actions.Title;
import org.colston.kicks.actions.Undo;
import org.colston.kicks.document.*;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class CanvasControl implements Canvas {
    private final KicksDocumentListener docListener = new KicksDocumentListener() {
        @Override
        public void documentUpdated() {
            updateUndoActions();
            canvasPanel.documentUpdated();
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
    private final CanvasModel model;
    private final JComponent inputComponent;
    private final UndoManager undo = new UndoManager();
    private KicksDocument savedDocument = null;

    CanvasControl(JPanel container, CanvasPanel canvasPanel, CanvasModel model, JComponent inputComponent) {
        this.container = container;
        this.canvasPanel = canvasPanel;
        this.model = model;
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
        return model.getDocument();
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

        model.setDocument(doc);
        documentSaved();

        canvasPanel.documentSet();

        requestFocusInWindow();
    }

    @Override
    public boolean isDocumentChanged() {
        return !savedDocument.equals(getDocument());
    }

    @Override
    public void documentSaved() {
        savedDocument = KicksApp.documentStore().clone(getDocument());
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
        u.putValue(Action.SHORT_DESCRIPTION, undo.getUndoPresentationName());
        Action r = ActionManager.getAction(Redo.class);
        r.setEnabled(undo.canRedo());
        r.putValue(Action.NAME, undo.getRedoPresentationName());
        r.putValue(Action.SHORT_DESCRIPTION, undo.getRedoPresentationName());
    }

    public void addLyric() {
        String s = canvasPanel.getText();
        Lyric l = model.getDocument().getLyric(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset());
        if (l == null && (s == null || s.isEmpty())) {
            // user hit enter, nothing to do, so just auto cursor (avoid creating undo edit)
            canvasPanel.doAutoCursor();
            return;
        }
        if (s == null || s.isEmpty()) {
            model.getDocument().removeLyric(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset());
        } else {
            if (s.length() > 2) {
                s = s.substring(0, 2);
            }
            l = new Lyric(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset(), s);
            model.getDocument().addLyric(l);
        }
        canvasPanel.doAutoCursor();
    }

    void addNote(int string, int placement, boolean isSmall) {
        Note n = new Note(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset(), string, placement);
        getDocument().addNote(n);
        if (isSmall) {
            n.setSmall(true);
        }
        canvasPanel.doAutoCursor();
    }

    void moveCursorLeft() {
        canvasPanel.moveCursorLeft();
    }

    void moveCursorRight() {
        canvasPanel.moveCursorRight();
    }

    void moveCursorUp(int modifiers) {
        if ((modifiers & ActionEvent.ALT_MASK) > 0) {
            canvasPanel.moveCursorUpMinAmount();
        } else {
            canvasPanel.moveCursorUp();
        }
    }

    void moveCursorDown(int modifiers) {
        if ((modifiers & ActionEvent.ALT_MASK) > 0) {
            canvasPanel.moveCursorDownMinAmount();
        } else {
            canvasPanel.moveCursorDown();
        }
    }

    void addRest() {
        Note n = new Note(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset(),
                NoteValues.REST_STRING, NoteValues.REST_PLACEMENT);
        getDocument().addNote(n);
        canvasPanel.doAutoCursor();
    }

    void addRepeat(boolean end) {
        Repeat r = new Repeat(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset(), end);
        model.getDocument().addRepeat(r);
    }

    void setFlat() {
        model.getDocument().setFlat(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset());
    }

    void setUtou(boolean isKaki) {
        model.getDocument().setUtou(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset(),
                isKaki ? Utou.KAKI : Utou.UCHI);
    }

    void setChord() {
        model.getDocument().setChord(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset());
    }

    void setSlur() {
        model.getDocument().setSlur(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset());
    }

    void delete() {
        if (canvasPanel.isCursorOnNote()) {
            model.getDocument().removeNote(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset());
            model.getDocument().removeRepeat(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset());
        } else {
            model.getDocument().removeLyric(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset());
        }
    }

    /**
     * Move to the position of the current or previous note then delete it.
     * Ignored if not in the note column.
     */
    void backspace() {
        if (!canvasPanel.isCursorOnNote()) {
            return;
        }
        Locatable locatable = model.getDocument().findPreviousNote(canvasPanel.getCursorIndex(),
                canvasPanel.getCursorOffset());
        if (locatable == null) {
            canvasPanel.initialiseCursor();
        } else {
            canvasPanel.setCursor(locatable.getIndex(), locatable.getOffset());
        }
        delete();
    }

    void setAutoCursor(AutoCursor autoCursor) {
        canvasPanel.setAutoCursor(autoCursor);
    }

    private static class CanvasActionProvider implements ActionProvider {
        private final List<Action> editActions = new ArrayList<>();
        private final List<Action> documentActions = new ArrayList<>();

        public CanvasActionProvider() {
            editActions.add(ActionManager.getAction(Undo.class));
            editActions.add(ActionManager.getAction(Redo.class));
            documentActions.add(ActionManager.getAction(Title.class));
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

        @Override
        public Collection<? extends Action> getAllActions() {
            List<Action> actions = new ArrayList<>(editActions);
            actions.addAll(documentActions);
            return actions;
        }
    }
}
