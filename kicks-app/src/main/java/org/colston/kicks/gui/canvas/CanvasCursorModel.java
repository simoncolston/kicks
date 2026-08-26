package org.colston.kicks.gui.canvas;

import org.colston.kicks.document.Locatable;
import org.colston.kicks.document.LocatableRange;
import org.colston.kicks.document.SimpleLocatableRange;
import org.colston.kicks.render.PageCursor;
import org.colston.kicks.render.PageRenderer;

import javax.swing.event.EventListenerList;

class CanvasCursorModel {

    private final EventListenerList listeners = new EventListenerList();

    private int cursorIndex = PageRenderer.CELLS_PER_COL;               // cell index from top right corner
    private int cursorOffset = Locatable.CELL_TICKS / 2; // offset within a cell (valid values: 0 -> CELL_TICKS)
    private boolean cursorOnNote = true;       // flag indicating whether cursor on the notes or the lyrics

    private Canvas.AutoCursor autoCursor = Canvas.AutoCursor.HALF; // where to move the cursor after input
    private final CanvasModel model;
    private final SimpleLocatableRange selection = new SimpleLocatableRange();


    public CanvasCursorModel(CanvasModel model) {
        this.model = model;
    }

    LocatableRange getSelection() {
        return selection;
    }

    LocatableRange getAndClearSelection() {
        LocatableRange range = new SimpleLocatableRange(selection);
        selection.clear();
        return range;
    }

    public void clearSelection() {
        selection.clear();
    }

    private void handleSelection(int index, int offset, boolean selecting) {
        if (!selecting) {
            selection.clear();
            return;
        }
        if (selection.isEmpty()) {
            selection.set(index, offset, cursorIndex, cursorOffset);
        } else {
            selection.adjust(cursorIndex, cursorOffset);
        }
    }

    void addListener(CanvasCursorModelListener listener) {
        listeners.add(CanvasCursorModelListener.class, listener);
    }

    private void fireCursorChanged(CanvasCursorModelEvent event) {

        CanvasCursorModelListener[] ls = listeners.getListeners(CanvasCursorModelListener.class);
        boolean vetoed = false;
        int i;
        for (i = 0; i < ls.length; i++) {
            CanvasCursorModelListener l = ls[i];
            if (!l.vetoableCursorChanged(event)) {
                vetoed = true;
                break;
            }
        }

        if (vetoed) {
            // rollback
            cursorIndex = event.oldCursor().index();
            cursorOffset = event.oldCursor().offset();
            cursorOnNote = event.oldCursor().onNote();

            // rollback listeners who have already accepted the change
            CanvasCursorModelEvent rollbackEvent = new CanvasCursorModelEvent(event);
            for (i = i - 1; i >= 0; i--) {
                ls[i].cursorChanged(rollbackEvent);
            }
        }
    }

    int getCursorIndex() {
        return cursorIndex;
    }

    int getCursorOffset() {
        return cursorOffset;
    }

    boolean isCursorOnNote() {
        return cursorOnNote;
    }

    int getCursorColumnIndex() {
        int col = cursorIndex / PageRenderer.CELLS_PER_COL;
        if (col >= PageRenderer.COLUMNS_PER_PAGE - 1) {
            // don't allow title in last column
            System.out.println("don't allow title in last column");
            return -1;
        }
        return col * PageRenderer.CELLS_PER_COL;
    }

    void setAutoCursor(Canvas.AutoCursor autoCursor) {
        this.autoCursor  = autoCursor;
    }

    void doAutoCursor() {
        moveCursor(calcAutoCursorTicksDown(), cursorOnNote, false);
    }

    void moveCursorLeft(boolean selecting) {
        if (!cursorOnNote) {
            setCursor(cursorIndex, cursorOffset, true, selecting);
        } else {
            moveCursor(PageRenderer.CELLS_PER_COL * Locatable.CELL_TICKS, selecting, selecting);
        }
    }

    void moveCursorRight(boolean selecting) {
        if (cursorOnNote && !selecting) {
            setCursor(cursorIndex, cursorOffset, false, false);
        } else {
            // the compiler warning is wrong - selecting can be false
            moveCursor(-PageRenderer.CELLS_PER_COL * Locatable.CELL_TICKS, true, cursorOnNote && selecting);
        }
    }

    void moveCursorUpMinAmount(boolean selecting) {
        moveCursor(-1, cursorOnNote, selecting);
    }

    void moveCursorUp(boolean selecting) {
        moveCursor(calcAutoCursorTicksUp(), cursorOnNote, selecting);
    }

    void pageUp(boolean selecting) {
        moveCursor(-PageRenderer.CELLS_PER_PAGE * Locatable.CELL_TICKS, cursorOnNote, selecting);
    }

    void moveCursorDownMinAmount(boolean selecting) {
        moveCursor(1, cursorOnNote, selecting);
    }

    void moveCursorDown(boolean selecting) {
        moveCursor(calcAutoCursorTicksDown(), cursorOnNote, selecting);
    }

    void pageDown(boolean selecting) {
        moveCursor(PageRenderer.CELLS_PER_PAGE * Locatable.CELL_TICKS, cursorOnNote, selecting);
    }

    void moveCursorHome(boolean selecting) {
        setCursor(findIndexWithoutTitle(0), Locatable.CELL_TICKS / 2, true, selecting);
    }

    void moveCursorEnd(int pageCount, boolean selecting) {
        setCursor(((PageRenderer.CELLS_PER_PAGE * pageCount) - 1) * Locatable.CELL_TICKS
                + Locatable.CELL_TICKS / 2, // so we end up in the middle of a cell
                cursorOnNote,
                selecting);
    }

    void initialiseCursor() {
        initialiseCursor(0);
    }

    public void initialiseCursor(int startIndex) {
        // set the cursor on the first column without a title
        setCursor(findIndexWithoutTitle(startIndex), Locatable.CELL_TICKS / 2, true, false);
    }

    void setCursor(int index, int offset) {
        // this is a reset of the cursor so clear the selection
        setCursor(findIndexWithoutTitle(index), offset, true, false);
    }

    private int findIndexWithoutTitle(int startIndex) {
        for (int index = startIndex; index < PageRenderer.CELLS_PER_COL * PageRenderer.COLUMNS_PER_PAGE; index += PageRenderer.CELLS_PER_COL) {
            if (model.getDocument().getSongAtIndex(index, PageRenderer.CELLS_PER_COL).isEmpty()) {
                return index;
            }
        }
        // this should not happen ;-)
        return -1;
    }

    /**
     * Calculate number of ticks required to move on to next boundary set by the auto cursor setting.
     * @return number of ticks
     */
    private int calcAutoCursorTicksDown() {
        return switch (autoCursor) {
            case OFF -> 1;
            case HALF -> Locatable.CELL_TICKS / 2 - cursorOffset % (Locatable.CELL_TICKS / 2);
            case ONE -> (Locatable.CELL_TICKS / 2) * (((cursorOffset % 12) / (Locatable.CELL_TICKS / 2)) + 1)
                    - cursorOffset % (Locatable.CELL_TICKS / 2);
        };
    }

    /**
     * Calculate number of ticks required to move back to next boundary set by the auto cursor setting.
     * @return number of ticks
     */
    private int calcAutoCursorTicksUp() {
        return switch (autoCursor) {
            case OFF -> -1;
            case HALF -> -((cursorOffset - 1) % (Locatable.CELL_TICKS / 2) + 1);
            case ONE -> ((Locatable.CELL_TICKS / 2) * (((cursorOffset % 12) / (Locatable.CELL_TICKS / 2)) + 1)
                    - cursorOffset % (Locatable.CELL_TICKS / 2)) % Locatable.CELL_TICKS - Locatable.CELL_TICKS;
        };
    }

    private void moveCursor(int ticks, boolean onNote, boolean selecting) {
        int cursorTicks = cursorIndex * Locatable.CELL_TICKS + cursorOffset;
        cursorTicks += ticks;
        // find the next column that does not contain a title - need to use index rather than ticks
        int index = cursorTicks / Locatable.CELL_TICKS;
        int offset = cursorTicks % Locatable.CELL_TICKS;
        if (offset == 0) {
            index--;
        }
        while (model.getDocument().getSongAtIndex(index, PageRenderer.CELLS_PER_COL).isPresent()) {
            // skip over a title by adding/subtracting a whole column of ticks
            int sign = (int) Math.signum(ticks);
            cursorTicks += sign * Locatable.CELL_TICKS * PageRenderer.CELLS_PER_COL;
            if (cursorTicks <= 0) {
                // edge case - 0 is usually allowed, but not if there is a title there!
                return;
            }
            index += sign * PageRenderer.CELLS_PER_COL;
        }
        setCursor(cursorTicks, onNote, selecting);
    }

    private void setCursor(int ticks, boolean onNote, boolean selecting) {
        int index = ticks / Locatable.CELL_TICKS;
        int offset = ticks % Locatable.CELL_TICKS;

        /*
         * cursorOffset 12 and 0 are the same location EXCEPT on the split for a new column. For this case we favour the
         * end of the cell rather than the start.
         */
        if (offset == 0 && index > 0) {
            index--;
            offset = Locatable.CELL_TICKS;
        }

        setCursor(index, offset, onNote, selecting);
    }

    void setCursor(int index, int offset, boolean onNote, boolean selecting) {

        if (index < 0) {
            return;
        }

        PageCursor oldCursor = new PageCursor(cursorIndex, cursorOffset, cursorOnNote);

        cursorIndex = index;
        cursorOffset = offset;
        cursorOnNote = onNote;

        if (oldCursor.index() != cursorIndex && oldCursor.offset() != cursorOffset) {
            /*
             * cursorOffset 12 and 0 are the same location EXCEPT on the split for a new column. For this case we favour the
             * end of the cell rather than the start.
             */
            if (cursorOffset == 0 && cursorIndex > 0) {
                cursorIndex--;
                cursorOffset = Locatable.CELL_TICKS;
            }
        }

        handleSelection(oldCursor.index(), oldCursor.offset(), selecting);

        PageCursor newCursor = new PageCursor(cursorIndex, cursorOffset, cursorOnNote);
        fireCursorChanged(new CanvasCursorModelEvent(oldCursor, newCursor));
    }
}
