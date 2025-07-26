package org.colston.kicks.gui.canvas;

import org.colston.gui.actions.ActionManager;
import org.colston.gui.actions.ActionProvider;
import org.colston.kicks.KicksApp;
import org.colston.kicks.actions.Redo;
import org.colston.kicks.actions.Title;
import org.colston.kicks.actions.Undo;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.KicksDocumentEditor;
import org.colston.kicks.document.KicksDocumentListener;
import org.colston.kicks.document.Locatable;
import org.colston.kicks.document.Lyric;
import org.colston.kicks.document.Note;
import org.colston.kicks.document.Repeat;
import org.colston.kicks.document.Utou;

import javax.swing.*;
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

    private final CanvasActionProvider actionProvider = new CanvasActionProvider();

    private final JComponent container;
    private final CanvasPanel canvasPanel;
    private final CanvasModel model;
    private final InputComponent inputComponent;
    private final UndoManager undo = new UndoManager();
    private KicksDocument savedDocument = null;

    CanvasControl(JPanel container, CanvasPanel canvasPanel, CanvasModel model, InputComponent inputComponent) {
        this.container = container;
        this.canvasPanel = canvasPanel;

        this.model = model;
        KicksDocumentListener docListener = new KicksDocumentListener() {
            @Override
            public void documentUpdated() {
                updateUndoActions();
                canvasPanel.redraw();
            }

            @Override
            public void locationUpdated(int index, int offset) {
                canvasPanel.setCursorWithOnNote(index, offset, true);
            }
        };
        this.model.getEditor().addDocumentListener(docListener);
        UndoableEditListener undoListener = e -> undo.addEdit(e.getEdit());
        this.model.getEditor().addUndoableEditListener(undoListener);

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
    public KicksDocumentEditor getEditor() {
        return model.getEditor();
    }

    @Override
    public KicksDocument getDocument() {
        return model.getDocument();
    }

    @Override
    public void setDocument(KicksDocument doc) {
        undo.discardAllEdits();
        updateUndoActions();

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

    @Override
    public void zoomIn() {
        canvasPanel.zoomIn();
    }

    @Override
    public void zoomOut() {
        canvasPanel.zoomOut();
    }

    @Override
    public void zoomReset() {
        canvasPanel.zoomReset();
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
            model.getEditor().removeLyric(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset());
        } else {
            if (s.length() > 2) {
                s = s.substring(0, 2);
            }
            l = new Lyric(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset(), s);
            model.getEditor().addLyric(l);
        }
        canvasPanel.doAutoCursor();
    }

    void addNote(int string, int placement, boolean isSmall) {
        Note n = new Note(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset(), string, placement);
        model.getEditor().addNote(n);
        if (isSmall) {
            // override the input component setting (probably because user pressed Shift key)
            n.setSmall(true);
        } else if (inputComponent.isSmallNoteSelected()) {
            n.setSmall(true);
        }
        canvasPanel.doAutoCursor();
    }

    void moveCursorLeft(int modifiers) {
        boolean selecting = (modifiers & ActionEvent.SHIFT_MASK) > 0;
        canvasPanel.moveCursorLeft(selecting);
    }

    void moveCursorRight(int modifiers) {
        boolean selecting = (modifiers & ActionEvent.SHIFT_MASK) > 0;
        canvasPanel.moveCursorRight(selecting);
    }

    void moveCursorUp(int modifiers) {
        boolean selecting = (modifiers & ActionEvent.SHIFT_MASK) > 0;
        if ((modifiers & ActionEvent.ALT_MASK) > 0) {
            canvasPanel.moveCursorUpMinAmount(selecting);
        } else {
            canvasPanel.moveCursorUp(selecting);
        }
    }

    void moveCursorDown(int modifiers) {
        boolean selecting = (modifiers & ActionEvent.SHIFT_MASK) > 0;
        if ((modifiers & ActionEvent.ALT_MASK) > 0) {
            canvasPanel.moveCursorDownMinAmount(selecting);
        } else {
            canvasPanel.moveCursorDown(selecting);
        }
    }

    void addRest() {
        Note n = new Note(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset(),
                CanvasResources.REST_STRING, CanvasResources.REST_PLACEMENT);
        model.getEditor().addNote(n);
        canvasPanel.doAutoCursor();
    }

    void addRepeat(boolean end) {
        Repeat r = new Repeat(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset(), end);
        model.getEditor().addRepeat(r);
    }

    void setFlat() {
        model.getEditor().setFlat(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset());
    }

    void setUtou(Utou utou) {
        model.getEditor().setUtou(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset(), utou);
    }

    void setChord() {
        model.getEditor().setChord(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset());
    }

    void setSlur() {
        model.getEditor().setSlur(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset());
    }

    void delete() {
        if (!canvasPanel.getSelection().isEmpty()) {
            model.getEditor().remove(canvasPanel.getSelection());
        } else if (canvasPanel.isCursorOnNote()) {
            model.getEditor().removeNote(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset());
            model.getEditor().removeRepeat(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset());
        } else {
            model.getEditor().removeLyric(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset());
        }
    }

    @Override
    public void setNoteSizeNormal() {
        model.getEditor().setNoteSize(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset(), false);
    }

    @Override
    public void setNoteSizeSmall() {
        model.getEditor().setNoteSize(canvasPanel.getCursorIndex(), canvasPanel.getCursorOffset(), true);
    }

    /**
     * Move to the position of the current or previous note then delete it.
     * Ignored if not in the note column.
     */
    void backspace() {
        if (!canvasPanel.isCursorOnNote()) {
            return;
        }
        Locatable locatable = model.getEditor().findPreviousNote(canvasPanel.getCursorIndex(),
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
