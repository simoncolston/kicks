package org.colston.kicks.gui.canvas;

import org.colston.gui.actions.ActionManager;
import org.colston.gui.actions.ActionProvider;
import org.colston.kicks.KicksApp;
import org.colston.kicks.actions.Copy;
import org.colston.kicks.actions.Delete;
import org.colston.kicks.actions.Paste;
import org.colston.kicks.actions.Redo;
import org.colston.kicks.actions.SongHeaderAdd;
import org.colston.kicks.actions.SongHeaderDelete;
import org.colston.kicks.actions.SongHeaderEdit;
import org.colston.kicks.actions.Undo;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.KicksDocumentEditor;
import org.colston.kicks.document.KicksDocumentListener;
import org.colston.kicks.document.Locatable;
import org.colston.kicks.document.Lyric;
import org.colston.kicks.document.Note;
import org.colston.kicks.document.Repeat;
import org.colston.kicks.document.Song;
import org.colston.kicks.document.Utou;
import org.colston.kicks.render.PageRenderer;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.print.Printable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class CanvasControl implements Canvas {

    private final CanvasActionProvider actionProvider = new CanvasActionProvider();

    private final CanvasPages canvasPages;
    private final CanvasModel model;
    private final InputComponent inputComponent;
    private final UndoManager undo = new UndoManager();
    private final JScrollPane scroller;
    private KicksDocument savedDocument = null;
    private final CanvasCursorModel cursorModel;
    private final CanvasZoomModel zoomModel;

    CanvasControl(JPanel container, CanvasPages canvasPages, CanvasModel model, CanvasCursorModel cursorModel, CanvasZoomModel zoomModel, InputComponent inputComponent) {
        this.canvasPages = canvasPages;
        this.model = model;
        this.inputComponent = inputComponent;
        this.cursorModel = cursorModel;
        this.zoomModel = zoomModel;

        this.scroller = new JScrollPane(container);
        scroller.getVerticalScrollBar().setUnitIncrement(16);
        scroller.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, InputEvent.SHIFT_DOWN_MASK), "scrollDown");
        scroller.getActionMap().put("scrollDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cursorModel.pageDown((e.getModifiers() & ActionEvent.SHIFT_MASK) > 0);
            }
        });
        scroller.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, InputEvent.SHIFT_DOWN_MASK), "scrollUp");
        scroller.getActionMap().put("scrollUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cursorModel.pageUp((e.getModifiers() & ActionEvent.SHIFT_MASK) > 0);
            }
        });

        // listen for changes on the document
        KicksDocumentListener docListener = new KicksDocumentListener() {
            @Override
            public void documentUpdated() {
                updateUndoActions();
                canvasPages.redraw();
            }

            @Override
            public void locationUpdated(int index, int offset) {
                cursorModel.setCursor(index, offset);
            }
        };
        this.model.getEditor().addDocumentListener(docListener);

        // listen for changes on the cursor model
        this.cursorModel.addListener(new CanvasCursorModelListener() {
            @Override
            public boolean vetoableCursorChanged(CanvasCursorModelEvent e) {
                int numberOfPages = PageRenderer.calculateNumberOfPages(e.newCursor().index());
                if (numberOfPages > canvasPages.getNumberOfPages()) {
                    return false;
                }
                cursorChanged(e);
                return true;
            }

            @Override
            public void cursorChanged(CanvasCursorModelEvent e) {
                int pageIndex = PageRenderer.calculatePageIndex(e.newCursor().index());
                if (pageIndex != PageRenderer.calculatePageIndex(e.oldCursor().index())) {
                    canvasPages.scrollPageToVisible(pageIndex);
                }
                canvasPages.handleText();
                canvasPages.redraw();
            }
        });

        // listen for zoom changes
        zoomModel.addListener(event -> {
            Dimension dimension = new Dimension();
            dimension.width = (int) (event.scale() * (PageRenderer.CANVAS_WIDTH + 2 * PageRenderer.BORDER_WIDTH));
            dimension.height = (int) (event.scale() * (PageRenderer.CANVAS_HEIGHT + 2 * PageRenderer.BORDER_WIDTH));
            canvasPages.setDimensions(dimension);
            canvasPages.redraw();
        });

        // listen for undoable edits
        UndoableEditListener undoListener = e -> undo.addEdit(e.getEdit());
        this.model.getEditor().addUndoableEditListener(undoListener);

        // listen for setting changing
        KicksApp.settings().addListener(canvasPages::redraw);
    }

    @Override
    public void requestFocusInWindow() {
        canvasPages.requestFocusInWindow();
    }

    @Override
    public JComponent getContainer() {
        return scroller;
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
        return new PageRenderer(model.getDocument(), KicksApp.settings());
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

        canvasPages.documentSet();
        zoomReset();

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
        zoomModel.zoomIn();
    }

    @Override
    public void zoomOut() {
        zoomModel.zoomOut();
    }

    @Override
    public void zoomReset() {
        zoomModel.zoomReset();
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
        String s = canvasPages.getText();
        int cursorIndex = cursorModel.getCursorIndex();
        int cursorOffset = cursorModel.getCursorOffset();
        Lyric l = model.getDocument().getLyric(cursorIndex,  cursorOffset);
        if (l == null && (s == null || s.isEmpty())) {
            // user hit enter, nothing to do, so just auto cursor (avoid creating undo edit)
            cursorModel.doAutoCursor();
            return;
        }
        if (s == null || s.isEmpty()) {
            model.getEditor().removeLyric(cursorIndex,  cursorOffset);
        } else {
            if (s.length() > 2) {
                s = s.substring(0, 2);
            }
            l = new Lyric(cursorIndex,  cursorOffset, s);
            model.getEditor().addLyric(l);
        }
        cursorModel.doAutoCursor();
    }

    void addNote(int string, int placement, boolean isSmall) {
        Note n = new Note(cursorModel.getCursorIndex(), cursorModel.getCursorOffset(), string, placement);
        model.getEditor().addNote(n);
        if (isSmall) {
            // override the input component setting (probably because user pressed Shift key)
            n.setSmall(true);
        } else if (inputComponent.isSmallNoteSelected()) {
            n.setSmall(true);
        }
        cursorModel.doAutoCursor();
    }

    void moveCursorLeft(int modifiers) {
        boolean selecting = (modifiers & ActionEvent.SHIFT_MASK) > 0;
        cursorModel.moveCursorLeft(selecting);
    }

    void moveCursorRight(int modifiers) {
        boolean selecting = (modifiers & ActionEvent.SHIFT_MASK) > 0;
        cursorModel.moveCursorRight(selecting);
    }

    void moveCursorUp(int modifiers) {
        boolean selecting = (modifiers & ActionEvent.SHIFT_MASK) > 0;
        if ((modifiers & ActionEvent.ALT_MASK) > 0) {
            cursorModel.moveCursorUpMinAmount(selecting);
        } else {
            cursorModel.moveCursorUp(selecting);
        }
    }

    void moveCursorDown(int modifiers) {
        boolean selecting = (modifiers & ActionEvent.SHIFT_MASK) > 0;
        if ((modifiers & ActionEvent.ALT_MASK) > 0) {
            cursorModel.moveCursorDownMinAmount(selecting);
        } else {
            cursorModel.moveCursorDown(selecting);
        }
    }

    void moveCursorHome(int modifiers) {
        boolean selecting = (modifiers & ActionEvent.SHIFT_MASK) > 0;
        cursorModel.moveCursorHome(selecting);
    }

    void moveCursorEnd(int modifiers) {
        boolean selecting = (modifiers & ActionEvent.SHIFT_MASK) > 0;
        cursorModel.moveCursorEnd(canvasPages.getNumberOfPages(), selecting);
    }

    void addRest() {
        Note n = new Note(cursorModel.getCursorIndex(), cursorModel.getCursorOffset(),
                CanvasResources.REST_STRING, CanvasResources.REST_PLACEMENT);
        model.getEditor().addNote(n);
        cursorModel.doAutoCursor();
    }

    void addRepeat(boolean end) {
        Repeat r = new Repeat(cursorModel.getCursorIndex(), cursorModel.getCursorOffset(), end);
        model.getEditor().addRepeat(r);
    }

    void setFlat() {
        model.getEditor().setFlat(cursorModel.getCursorIndex(), cursorModel.getCursorOffset());
    }

    void setUtou(Utou utou) {
        model.getEditor().setUtou(cursorModel.getCursorIndex(), cursorModel.getCursorOffset(), utou);
    }

    void setChord() {
        model.getEditor().setChord(cursorModel.getCursorIndex(), cursorModel.getCursorOffset());
    }

    void setSlur() {
        model.getEditor().setSlur(cursorModel.getCursorIndex(), cursorModel.getCursorOffset());
    }

    @Override
    public void copy() {
        if (cursorModel.getSelection().isEmpty()) {
            return;
        }
        KicksDocument copy = model.getEditor().copy(cursorModel.getSelection());
        if (copy == null) {
            return;
        }
        try (ByteArrayOutputStream is = new ByteArrayOutputStream()) {
            // convert to xml
            KicksApp.documentStore().save(copy, is);
            // stash in clipboard
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection data = new StringSelection(is.toString());
            cb.setContents(data, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paste() {
        String data = copyFromClipboard();
        if (data == null) {
            return;
        }
        try (ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes())) {
            KicksDocument doc = KicksApp.documentStore().load(is);
            model.getEditor().paste(cursorModel.getCursorIndex(), cursorModel.getCursorOffset(), doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String copyFromClipboard() {
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = cb.getContents(null);

        if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                return (String) transferable.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception e) {
                System.out.println("Couldn't get data from the clipboard");
                return null;
            }
        }
        System.out.println("Couldn't get data from the clipboard");
        return null;
    }

    @Override
    public void delete() {
        if (!cursorModel.getSelection().isEmpty()) {
            model.getEditor().remove(cursorModel.getAndClearSelection());
        } else if (cursorModel.isCursorOnNote()) {
            model.getEditor().removeNote(cursorModel.getCursorIndex(), cursorModel.getCursorOffset());
            model.getEditor().removeRepeat(cursorModel.getCursorIndex(), cursorModel.getCursorOffset());
        } else {
            model.getEditor().removeLyric(cursorModel.getCursorIndex(), cursorModel.getCursorOffset());
        }
    }

    @Override
    public void setNoteSizeNormal() {
        model.getEditor().setNoteSize(cursorModel.getCursorIndex(), cursorModel.getCursorOffset(), false);
    }

    @Override
    public void setNoteSizeSmall() {
        model.getEditor().setNoteSize(cursorModel.getCursorIndex(), cursorModel.getCursorOffset(), true);
    }

    @Override
    public void setFinger(int finger) {
        model.getEditor().setFinger(cursorModel.getCursorIndex(), cursorModel.getCursorOffset(), finger);
    }

    /**
     * Move to the position of the current or previous note then delete it.
     * Ignored if not in the note column.
     */
    void backspace() {
        if (!cursorModel.isCursorOnNote()) {
            return;
        }
        Locatable locatable = model.getEditor().findPreviousNote(cursorModel.getCursorIndex(), cursorModel.getCursorOffset());
        if (locatable == null) {
            cursorModel.initialiseCursor();
        } else {
            cursorModel.setCursor(locatable.getIndex(), locatable.getOffset());
        }
        delete();
    }

    void setAutoCursor(AutoCursor autoCursor) {
        cursorModel.setAutoCursor(autoCursor);
    }

    @Override
    public void editSongHeader() {
        int index = cursorModel.getCursorIndex();
        Optional<Song> song = model.getEditor().findSongBeforeIndex(index);
        if (song.isEmpty()) {
            return;
        }
        SongHeaderEditor sde = new SongHeaderEditor();
        sde.edit(song.get(), cursorModel.getCursorIndex(), cursorModel.getCursorOffset());
    }

    @Override
    public void addSongHeader() {
        int index = cursorModel.getCursorColumnIndex();
        if (index == -1) {
            // column not valid to receive song header
            return;
        }
        SongHeaderEditor sde = new SongHeaderEditor();
        sde.edit(new Song(index), cursorModel.getCursorIndex(), cursorModel.getCursorOffset());
    }

    @Override
    public void removeSongHeader() {
        int index = cursorModel.getCursorIndex();
        Optional<Song> song = model.getEditor().findSongBeforeIndex(index);
        if (song.isEmpty()) {
            return;
        }
        model.getEditor().removeSong(song.get().getIndex(), cursorModel.getCursorIndex(), cursorModel.getCursorOffset());
    }

    private static class CanvasActionProvider implements ActionProvider {
        private final List<Action> editActions = new ArrayList<>();
        private final List<Action> documentActions = new ArrayList<>();

        public CanvasActionProvider() {
            editActions.add(ActionManager.getAction(Copy.class));
            editActions.add(ActionManager.getAction(Paste.class));
            editActions.add(ActionManager.getAction(Delete.class));
            editActions.add(ActionManager.getAction(Undo.class));
            editActions.add(ActionManager.getAction(Redo.class));
            documentActions.add(ActionManager.getAction(SongHeaderAdd.class));
            documentActions.add(ActionManager.getAction(SongHeaderEdit.class));
            documentActions.add(ActionManager.getAction(SongHeaderDelete.class));
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
