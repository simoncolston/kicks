package org.colston.kicks.gui.canvas;

import org.colston.kicks.document.Locatable;
import org.colston.kicks.document.LocatableRange;
import org.colston.kicks.document.Lyric;
import org.colston.kicks.document.SimpleLocatableRange;
import org.colston.kicks.document.Song;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Optional;

/**
 * Canvas for drawing a page of the kunkunshi.
 *
 * @author simon
 */
class CanvasPanel extends JPanel {

    /*
     * Colours
     */
    private static final Color BACKGROUND_COLOUR = Color.WHITE;
    private static final Color SELECTION_COLOUR = UIManager.getDefaults().getColor("List.selectionBackground");

    /*
     * Text input
     */
    private final JTextComponent text;

    /*
     * Configurables
     */
    private final Dimension dimension = new Dimension();
    private double scale;

    /*
     * Cursor
     */
    private int cursorIndex = PageRenderer.CELLS_PER_COL;               // cell index from top right corner
    private int cursorOffset = Locatable.CELL_TICKS / 2; // offset within a cell (valid values: 0 -> CELL_TICKS)
    private boolean cursorOnNote = true;       // flag indicating whether cursor on the notes or the lyrics
    private Canvas.AutoCursor autoCursor = Canvas.AutoCursor.HALF; // where to move the cursor after input
    private final Stroke cursorStroke = new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    /*
     * Selection
     */
    private final SimpleLocatableRange selection = new SimpleLocatableRange();

    /*
     * The model.
     */
    private final CanvasModel model;
    private final EventListenerList listeners = new EventListenerList();

    private PageRenderer pageRenderer;

    public CanvasPanel(CanvasModel model, JTextComponent text) {
        // remove default layout manager - use absolute positioning for text field
        super(null);
        this.model = model;

        zoomReset();
        setBackground(BACKGROUND_COLOUR);
        setDimensions();
        setFocusable(true);
        addMouseListener(new ML());

        this.text = text;
        text.setFont(PageRenderer.lyricFont);
        add(text);
    }

    void addListener(CanvasPanelListener listener) {
        listeners.add(CanvasPanelListener.class, listener);
    }

    private void fireCursorChanged() {
        for (CanvasPanelListener l : listeners.getListeners(CanvasPanelListener.class)) {
            l.cursorChanged(cursorIndex, cursorOffset);
        }
    }

    private void setDimensions() {
        dimension.width = (int) (scale * (PageRenderer.CANVAS_WIDTH + 2 * PageRenderer.BORDER_WIDTH));
        dimension.height = (int) (scale * (PageRenderer.CANVAS_HEIGHT + 2 * PageRenderer.BORDER_WIDTH));
        setPreferredSize(dimension);
        setMinimumSize(dimension);
        setMaximumSize(dimension);
    }

    void documentSet() {
        selection.clear();
        initialiseCursor();
        cursorOnNote = true;
        text.setVisible(false);
        redraw();
    }

    void redraw() {
        revalidate();
        repaint();
    }

    void zoomIn() {
        scale = scale * 10.0 / 9.0; // divide by multiple of 3
        setDimensions();
        redraw();
    }

    void zoomOut() {
        scale = scale * 9.0 / 10.0;
        setDimensions();
        redraw();
    }

    void zoomReset() {
        int res = Toolkit.getDefaultToolkit().getScreenResolution();
        scale = (1.0f * res / 72f);
        setDimensions();
        redraw();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.scale(scale, scale);

        pageRenderer = new PageRenderer(model.getDocument(), selection, SELECTION_COLOUR,
                new CanvasCursor(cursorIndex, cursorOffset, cursorOnNote));
        pageRenderer.doPaint(g2);

        // draw the cursor
        g2.setColor(PageRenderer.CURSOR_COLOUR);
        g2.setStroke(cursorStroke);
        int x = pageRenderer.x(cursorIndex);
        int y = pageRenderer.y(cursorIndex, cursorOffset);
        if (!cursorOnNote) {
            x += PageRenderer.COLUMN_WIDTH / 2;
        }
        g2.drawLine(x, y, x + PageRenderer.COLUMN_WIDTH / 2, y);
        g2.dispose();
    }

    private boolean moveCursor(int ticks, boolean selecting) {
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
                return false;
            }
            index += sign * PageRenderer.CELLS_PER_COL;
        }
        if (cursorTicks < 0 || cursorTicks > PageRenderer.CELLS_PER_COL * PageRenderer.COLUMNS_PER_PAGE * Locatable.CELL_TICKS) {
            // cursor would move out of bounds so don't move cursor
            return false;
        }
        setCursor(cursorTicks, selecting);
        return true;
    }

    private void setCursor(int ticks, boolean selecting) {
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

        handleSelection(index,  offset, selecting);

        setCursor(index, offset);
    }

    /**
     * Set the cursor, also indicating whether it is on the note-side or the lyric-side of the column.
     * @param index cursor index
     * @param offset cursor offset
     * @param onNote true if the cursor should be on the note-side, false if lyric-side
     */
    void setCursorWithOnNote(int index, int offset, boolean onNote) {
        handleSelection(index, offset, false); // clear the selection
        setCursorOnNote(onNote);
        setCursor(findIndexWithoutTitle(index), offset);
    }

    private void setCursorOnNote(boolean newValue) {
        cursorOnNote = newValue;
        handleText();
        revalidate();
        repaint();
    }

    void setCursor(int index, int offset) {

        cursorIndex = index;
        cursorOffset = offset;
        /*
         * cursorOffset 12 and 0 are the same location EXCEPT on the split for a new column. For this case we favour the
         * end of the cell rather than the start.
         */
        if (cursorOffset == 0 && cursorIndex > 0) {
            cursorIndex--;
            cursorOffset = Locatable.CELL_TICKS;
        }

        handleText();

        revalidate();
        repaint();

        fireCursorChanged();
    }

    private void handleSelection(int index, int offset, boolean selecting) {
        if (!selecting) {
            selection.clear();
            return;
        }
        if (selection.isEmpty()) {
            selection.set(cursorIndex, cursorOffset, index, offset);
        } else {
            selection.adjust(index, offset);
        }
    }

    int getCursorIndex() {
        return cursorIndex;
    }

    int getCursorOffset() {
        return cursorOffset;
    }

    void initialiseCursor() {
        initialiseCursor(0);
    }

    public void initialiseCursor(int startIndex) {
        // set the cursor on the first column without a title
        setCursor(findIndexWithoutTitle(startIndex), Locatable.CELL_TICKS / 2);
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
     *
     * @return the index at the top of the column that the cursor is currently in
     * or -1 if title is not allowed in column.
     */
    int getCursorColumnIndex() {
        int col = cursorIndex / PageRenderer.CELLS_PER_COL;
        if (col >= PageRenderer.COLUMNS_PER_PAGE - 1) {
            // don't allow title in last column
            return -1;
        }
        return col * PageRenderer.CELLS_PER_COL;
    }

    boolean isCursorOnNote() {
        return cursorOnNote;
    }

    private void handleText() {
        if (cursorOnNote || !selection.isEmpty()) {
            text.setVisible(false);
        } else {
            int size = PageRenderer.COLUMN_WIDTH / 2;
            int x = pageRenderer.x(cursorIndex) + 7 * PageRenderer.COLUMN_WIDTH / 8;
            int y = pageRenderer.y(cursorIndex, cursorOffset) - size / 3;
            // convert to screen coordinates
            x = (int) ((x + PageRenderer.BORDER_WIDTH) * scale);
            y = (int) ((y + PageRenderer.BORDER_WIDTH) * scale);
            text.setBounds(x, y, size, size);

            Lyric l = model.getDocument().getLyric(cursorIndex, cursorOffset);
            text.setText(l != null ? l.getValue() : null);
            text.selectAll();

            text.setVisible(true);
            text.requestFocusInWindow();
        }
    }

    String getText() {
        return text.getText();
    }

    /**
     * Move on to the next cell midpoint or boundary, if enabled.
     */
    void doAutoCursor() {
        moveCursor(calcAutoCursorTicksDown(), false);
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

    public void moveCursorLeft(boolean selecting) {
        if (!cursorOnNote) {
            setCursorOnNote(true);
        } else if (moveCursor(PageRenderer.CELLS_PER_COL * Locatable.CELL_TICKS, selecting)) {
            setCursorOnNote(!cursorOnNote);
        }
    }

    public void moveCursorRight(boolean selecting) {
        if (cursorOnNote) {
            setCursorOnNote(false);
        } else if (moveCursor(-PageRenderer.CELLS_PER_COL * Locatable.CELL_TICKS, selecting)) {
            setCursorOnNote(!cursorOnNote);
        }
    }

    public void moveCursorUpMinAmount(boolean selecting) {
        moveCursor(-1, selecting);
    }

    public void moveCursorUp(boolean selecting) {
        if (autoCursor == Canvas.AutoCursor.OFF) {
            moveCursor(-1, selecting);
        } else {
            moveCursor(calcAutoCursorTicksUp(), selecting);
        }
    }

    public void moveCursorDownMinAmount(boolean selecting) {
        moveCursor(1, selecting);
    }

    public void moveCursorDown(boolean selecting) {
        if (autoCursor == Canvas.AutoCursor.OFF) {
            moveCursor(1, selecting);
        } else {
            moveCursor(calcAutoCursorTicksDown(), selecting);
        }
    }

    void setAutoCursor(Canvas.AutoCursor autoCursor) {
        this.autoCursor = autoCursor;
    }

    LocatableRange getSelection() {
        return selection;
    }

    LocatableRange getAndClearSelection() {
        LocatableRange range = new SimpleLocatableRange(selection);
        selection.clear();
        return range;
    }

    private void editSongDetails(Song song) {
        SongHeaderEditor sde = new SongHeaderEditor();
        sde.edit(song, cursorIndex, cursorOffset);
    }

    class ML extends MouseAdapter implements MouseListener {
        @Override
        public void mouseReleased(MouseEvent e) {
            /*
             * Allow for the transformations - scaling and translation for the border
             */
            int x = (int) Math.ceil(e.getX() / scale - PageRenderer.BORDER_WIDTH);
            int y = (int) Math.ceil(e.getY() / scale - PageRenderer.BORDER_WIDTH);

            if (y < 0 || y > PageRenderer.CANVAS_HEIGHT) {
                e.consume();
                return;
            }

            x = PageRenderer.CANVAS_WIDTH - x - PageRenderer.COLUMN_SPACE;
            //Which column?
            int col = x / (PageRenderer.COLUMN_WIDTH + PageRenderer.COLUMN_SPACE);
            if (col < 0 || col >= PageRenderer.COLUMNS_PER_PAGE) {
                e.consume();
                return;
            }

            // How far into the column?
            int colx = x % (PageRenderer.COLUMN_WIDTH + PageRenderer.COLUMN_SPACE);
            if (colx > PageRenderer.COLUMN_WIDTH) //click in column space!
            {
                e.consume();
                return;
            }
            boolean onNote = colx > PageRenderer.COLUMN_WIDTH / 2;

            int cells = y / PageRenderer.CELL_HEIGHT;
            int index = col * PageRenderer.CELLS_PER_COL + cells;
            int offset = (y % PageRenderer.CELL_HEIGHT) / (PageRenderer.CELL_HEIGHT / Locatable.CELL_TICKS);

            /*
             * Handle titles.
             */
            Optional<Song> song = model.getDocument().getSongAtIndex(index, PageRenderer.CELLS_PER_COL);
            if (song.isPresent()) {
                //invoke title edit
                editSongDetails(song.get());
                e.consume();
                return;
            }

            setCursorWithOnNote(index, offset, onNote);
        }
    }

}
