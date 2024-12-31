package org.colston.kicks.gui.canvas;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksApp;
import org.colston.kicks.actions.Title;
import org.colston.kicks.document.*;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * Canvas for drawing a page of the kunkunshi.
 *
 * @author simon
 */
class CanvasPanel extends JPanel implements Printable {
    /*
     * Dimensions
     */
    private static final int TITLE_WIDTH = 56;
    private static final int TITLE_MARGIN = 9;
    private static final int COLUMN_WIDTH = 56;
    private static final int COLUMN_SPACE = 9;
    private static final int CELL_HEIGHT = 36;
    private static final int DEFAULT_BORDER_WIDTH = 20;

    private static final int COLUMNS_PER_PAGE = 10;
    private static final int CELLS_PER_COL = 12;

    //TODO: Rebase everything as eleven columns

    //NOTE: This is now the same as 11 columns, so we could replace any column with a title
    //CANVAS_WIDTH = 706;
    private static final int CANVAS_WIDTH = COLUMN_WIDTH * COLUMNS_PER_PAGE + COLUMN_SPACE * (COLUMNS_PER_PAGE - 1)
            + TITLE_WIDTH + COLUMN_SPACE;
    //CANVAS_HEIGHT = 432;
    private static final int CANVAS_HEIGHT = CELL_HEIGHT * CELLS_PER_COL;

    private static final int REPEAT_HEAD_WIDTH = 6;
    private static final int REPEAT_HEAD_HEIGHT = 8;

    private static final int X_OFFSET_CHORD = COLUMN_WIDTH / 2 - 4;
    private static final int X_OFFSET_SLUR = 4;

    /*
     * Colours
     */
    private static final Color BORDER_BOX_COLOUR = new Color(150, 150, 150);
    private static final Color FOREGROUND_COLOUR = Color.BLACK;
    private static final Color BACKGROUND_COLOUR = Color.WHITE;
    //TODO: check the visibility of this
    static final Color CURSOR_COLOUR = Color.BLUE;
    private static final Color SELECTION_COLOUR = UIManager.getDefaults().getColor("List.selectionBackground");

    /*
     * Fonts
     */
    private final Font titleFont = new Font(KicksApp.FONT_NAME, Font.PLAIN, 26);
    private final Font font = new Font(KicksApp.FONT_NAME, Font.PLAIN, 16);
    private final Font fontBold = new Font(KicksApp.FONT_NAME, Font.BOLD, 16);
    private final Font sfont = new Font(KicksApp.FONT_NAME, Font.PLAIN, 12);
    private final Font sfontBold = new Font(KicksApp.FONT_NAME, Font.BOLD, 12);
    private final Font lyricFont = new Font(KicksApp.FONT_NAME, Font.PLAIN, 11);
    private final Font flatFont = new Font(KicksApp.FONT_NAME, Font.PLAIN, 8);

    /*
     * Strokes
     */
    private final Stroke stroke = new BasicStroke(1.0f);
    private final Stroke decorateStroke = new BasicStroke(1.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private final Stroke cursorStroke = new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    /*
     * Text input
     */
    private final JTextComponent text;

    /*
     * Configurables
     */
    private final Dimension dimension = new Dimension();
    private final int borderWidth = DEFAULT_BORDER_WIDTH;
    private double scale;

    /*
     * Cursor
     */
    private int cursorIndex = 0;               // cell index from top right corner
    private int cursorOffset = Locatable.CELL_TICKS / 2; // offset within a cell (0 - CELL_TICKS)
    private boolean cursorOnNote = true;       // flag indicating whether cursor on the notes or the lyrics
    private boolean cursorHighlight = false;   // flag indicating temporarily in mode for highlighting
    private Canvas.AutoCursor autoCursor = Canvas.AutoCursor.HALF; // where to move the cursor after input

    /*
     * Selection
     */
    private final LocatableRange selection = new LocatableRange();

    /*
     * The model.
     */
    private final CanvasModel model;
    private final EventListenerList listeners = new EventListenerList();

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
        text.setFont(lyricFont);
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
        dimension.width = (int) (scale * (CANVAS_WIDTH + 2 * borderWidth));
        dimension.height = (int) (scale * (CANVAS_HEIGHT + 2 * borderWidth));
        setPreferredSize(dimension);
        setMinimumSize(dimension);
        setMaximumSize(dimension);
    }

    void documentSet() {
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

    private void doPaint(Graphics2D g2, boolean print) {

        if (!print) {
            drawSelection(g2);
        }

        // draw the background
        int x = CANVAS_WIDTH - TITLE_WIDTH;
        int y = 0;

        g2.setStroke(stroke);
        g2.setColor(BORDER_BOX_COLOUR);
        while (x > 0) {
            x -= COLUMN_SPACE + COLUMN_WIDTH;
            g2.drawRect(x, y, COLUMN_WIDTH, CANVAS_HEIGHT);
            while (y < CANVAS_HEIGHT) {

                g2.drawRect(x, y, COLUMN_WIDTH / 2, CELL_HEIGHT);
                y += CELL_HEIGHT;
            }
            y = 0;
        }

        KicksDocument doc = model.getDocument();
        if (doc == null) {
            // empty document
            return;
        }

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        FontMetrics fm;
        g2.setColor(FOREGROUND_COLOUR);

        String title = doc.getTitle();
        if (title != null) {
            char[] tchars = title.toCharArray();
            if (tchars.length > 0) {
                // draw the title
                x = CANVAS_WIDTH - TITLE_WIDTH;
                y = 0;

                g2.setFont(titleFont);
                fm = g2.getFontMetrics();

                x += ((TITLE_WIDTH - fm.charWidth(tchars[0])) / 2);
                y += TITLE_MARGIN + titleFont.getSize();
                for (int i = 0; i < tchars.length; i++) {

                    g2.drawChars(tchars, i, 1, x, y);
                    y += titleFont.getSize();
                }
            }
        }

        Tuning tuning = doc.getTuning();
        if (tuning != null) {
            char[] tchars = tuning.getDisplayName().toCharArray();
            if (tchars.length > 0) {
                // draw the tuning
                g2.setFont(sfont);
                fm = g2.getFontMetrics();
                x = CANVAS_WIDTH - TITLE_WIDTH;
                x += ((TITLE_WIDTH - fm.charWidth(tchars[0])) / 2);
                y = CANVAS_HEIGHT - TITLE_MARGIN - (sfont.getSize() * tchars.length);
                for (int i = 0; i < tchars.length; i++) {
                    g2.drawChars(tchars, i, 1, x, y);
                    y += sfont.getSize();
                }
            }
        }

        // draw the notes
        FontMetrics lfm;
        FontMetrics sfm;
        g2.setFont(sfont);
        sfm = g2.getFontMetrics();
        g2.setFont(font);
        fm = lfm = g2.getFontMetrics();

        Note chordStart = null;
        Note slurStart = null;

        for (Note n : doc.getNotes()) {
            if (n.isSmall()) {
                if (fm != sfm) {
                    g2.setFont(sfont);
                    fm = sfm;
                }
            } else {

                if (fm != lfm) {
                    g2.setFont(font);
                    fm = lfm;
                }
            }

            cursorStartHighlight(g2, print, n, true, n.isSmall() ? sfontBold : fontBold);
            drawNote(g2, n, fm);
            cursorEndHighlight(g2, n.isSmall() ? sfont : font);

            if ((chordStart == null) == n.isChord()) //state has changed
            {
                if (n.isChord()) {
                    chordStart = n;
                } else {
                    drawNoteJoiningLine(g2, print, chordStart, n, X_OFFSET_CHORD, lfm, sfm);
                    chordStart = null;
                }
            }
            if ((slurStart == null) == n.isSlur()) //state has changed
            {
                if (n.isSlur()) {
                    slurStart = n;
                } else {
                    drawNoteJoiningLine(g2, print, slurStart, n, X_OFFSET_SLUR, lfm, sfm);
                    slurStart = null;
                }
            }
        }
        if (chordStart != null) {
            //draw the unfinished chord to the last note
            Note end = doc.getNotes().getLast();
            drawNoteJoiningLine(g2, print, chordStart, end, X_OFFSET_CHORD, lfm, sfm);
        }
        if (slurStart != null) {
            //draw the unfinished slur to the last note
            Note end = doc.getNotes().getLast();
            drawNoteJoiningLine(g2, print, slurStart, end, X_OFFSET_SLUR, lfm, sfm);
        }

        // draw the repeats
        for (Repeat r : doc.getRepeats()) {
            cursorStartHighlight(g2, print, r, true, null);
            drawRepeat(g2, r.isBack(), r.getIndex(), r.getOffset());
            cursorEndHighlight(g2, null);
        }

        // draw the lyrics
        g2.setFont(lyricFont);
        fm = g2.getFontMetrics();
        for (Lyric l : doc.getLyrics()) {
            char[] ch = l.getValue().toCharArray();
            for (int i = 0; i < ch.length; i++) {
                cursorStartHighlight(g2, print, l, false, null);
                drawLyric(g2, ch, i, l.getIndex(), l.getOffset(), fm);
                cursorEndHighlight(g2, null);

            }
        }
    }

    private void drawSelection(Graphics2D g2) {
        if (selection.isEmpty()) {
            return;
        }
        g2.setColor(SELECTION_COLOUR);
        Locatable low = selection.getLow();
        Locatable high = selection.getHigh();
        SimpleLocatable start = new SimpleLocatable(low);
        SimpleLocatable end = new SimpleLocatable();
        boolean finished = false;
        while (!finished) {
            int col = start.getIndex() / CELLS_PER_COL;
            end.setIndex(col * CELLS_PER_COL + CELLS_PER_COL - 1);
            end.setOffset(Locatable.CELL_TICKS);
            int x = x(start.getIndex());
            int y = y(start.getIndex(), start.getOffset());
            int height;
            if (!end.isEqualTo(high) && selection.contains(end)) {
                height = CANVAS_HEIGHT - y;
                start.setIndex(end.getIndex() + 1);
                start.setOffset(0);
            } else {
                height = y(high.getIndex(), high.getOffset()) - y;
                finished = true;
            }
            g2.fillRect(x, y, COLUMN_WIDTH / 2, height);
        }
    }

    private void cursorStartHighlight(Graphics2D g2, boolean print, Locatable locatable, boolean noteSide, Font f) {
        if (print || (!isUnderCursor(locatable, noteSide) && !selection.contains(locatable))) {
            return;
        }
        if (f != null) {
            g2.setFont(f);
        }
        g2.setColor(CURSOR_COLOUR);
        cursorHighlight = true;
    }

    private void cursorEndHighlight(Graphics2D g2, Font f) {
        if (!cursorHighlight) {
            return;
        }
        if (f != null) {
            g2.setFont(f);
        }
        g2.setColor(FOREGROUND_COLOUR);
        cursorHighlight = false;
    }

    private boolean isUnderCursor(Locatable l, boolean noteSide) {
        return cursorOnNote == noteSide && l.getIndex() == cursorIndex && l.getOffset() == cursorOffset;
    }

    private void drawNoteJoiningLine(Graphics2D g2, boolean print, Note start, Note end, int xOffset,
                                     FontMetrics lfm, FontMetrics sfm) {
        cursorStartHighlight(g2, print, start, true, null);
        g2.setStroke(decorateStroke);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = x(start.getIndex()) + xOffset;
        int y = y(start.getIndex(), start.getOffset()) - 2;
        int y1 = y(end.getIndex(), end.getOffset(), end.isSmall() ? sfm : lfm) - 2;
        g2.drawLine(x, y, x, y1);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setStroke(stroke);
        cursorEndHighlight(g2, null);
    }

    private void drawRepeat(Graphics2D g2, boolean back, int index, int offset) {
        int x = x(index) + (COLUMN_WIDTH / 2);
        int y = y(index, offset);
        int x1 = x + (COLUMN_WIDTH / 8) * 3;
        int y1 = y;
        g2.drawLine(x, y, x1, y1);
        x = x1;
        y = back ? y1 - CELL_HEIGHT : y1 + CELL_HEIGHT;
        g2.drawLine(x, y, x1, y1);

        int[] xs = new int[3];
        int[] ys = new int[3];

        xs[0] = x - REPEAT_HEAD_WIDTH / 2;
        ys[0] = y;
        xs[1] = x + REPEAT_HEAD_WIDTH / 2;
        ys[1] = y;
        xs[2] = x;
        ys[2] = back ? y - REPEAT_HEAD_HEIGHT : y + REPEAT_HEAD_HEIGHT;
        Polygon tri = new Polygon(xs, ys, 3);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fill(tri);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    private void drawLyric(Graphics2D g2, char[] ch, int choff, int index, int offset, FontMetrics fm) {
        int x = x(index) + (COLUMN_WIDTH / 2);
        int cw = fm.charWidth(ch[choff]);
        x += (COLUMN_WIDTH / 4 - cw) / 2;
        x += choff * cw / 3;
        int y = y(index, offset, fm);
        y += ((int) Math.ceil(fm.getFont().getSize() * 0.6f)) * choff;
        g2.drawChars(ch, choff, 1, x, y);
    }

    private void drawNote(Graphics2D g2, Note n, FontMetrics fm) {
        char[] ch = CanvasResources.getNoteText(n.getString(), n.getPlacement()).toCharArray();
        int x = x(n.getIndex());
        int y = y(n.getIndex(), n.getOffset(), fm) - 1;
        int chw;
        if (ch.length == 1) {
            chw = fm.charWidth(ch[0]);
            x += (COLUMN_WIDTH / 2 - chw) / 2;
            g2.drawChars(ch, 0, 1, x, y);
        } else if ('下' == ch[0]) {
            //TODO: This might need optimizing sometime...

            Font currentFont = g2.getFont();
            Font font = currentFont.deriveFont(AffineTransform.getScaleInstance(1.0, 0.6));
            g2.setFont(font);
            FontMetrics fontMetrics = g2.getFontMetrics();
            chw = fontMetrics.charWidth(ch[0]) + 2;

            x += ((COLUMN_WIDTH / 2) - chw) / 2;
            y = y(n.getIndex(), n.getOffset()) + 1;      //+1 here to squash them together vertically
            g2.drawChars(ch, 0, 1, x + 1, y);
            y += (font.getSize() / 2);                   //-1 here to squash them together vertically (if necessary)
            g2.drawChars(ch, 1, 1, x + 1, y);

            g2.setFont(currentFont);

            y -= 2; //to add padding for the 'utou' for this type of double char
        } else {
            int chw0 = fm.charWidth(ch[0]) - 3;
            int chw1 = fm.charWidth(ch[1]) - 3;
            chw = chw0 + chw1;
            x += ((COLUMN_WIDTH / 2) - chw) / 2;
            g2.drawChars(ch, 0, 1, x - 2, y);
            g2.drawChars(ch, 1, 1, x + chw0 - 1, y);

            //to add a little more padding to the 'utou' for double characters
            chw += 2;
        }

        if (n.getAccidental() == Accidental.FLAT) {
            Font tfont = g2.getFont();
            g2.setFont(flatFont);
            g2.drawString("♭", x - flatFont.getSize() / 2, y);
            g2.setFont(tfont);
        }

        // move to top right of note
        x += chw + 1;
        y -= fm.getFont().getSize() + 1;

        switch (n.getUtou()) {
            case KAKI -> {
                g2.setStroke(decorateStroke);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawLine(x, y, x + 1 - fm.getFont().getSize() / 2, y);
                g2.drawLine(x, y, x, y - 1 + fm.getFont().getSize() / 2);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g2.setStroke(stroke);
            }
            case UCHI -> {
                g2.setStroke(decorateStroke);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawLine(x - fm.getFont().getSize() / 4, y, x, y + fm.getFont().getSize() / 4);
                g2.drawLine(x - fm.getFont().getSize() / 4, y, x + 1, y - 1 + fm.getFont().getSize() / 4);
                g2.drawLine(x, y + fm.getFont().getSize() / 4, x + 1, y - 1 + fm.getFont().getSize() / 4);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g2.setStroke(stroke);
            }
            case NONE -> {
            }
        }
    }

    private int x(int index) {
        int col = index / CELLS_PER_COL;
        return CANVAS_WIDTH - TITLE_WIDTH - (COLUMN_SPACE + COLUMN_WIDTH) - (COLUMN_SPACE + COLUMN_WIDTH) * col;
    }

    private int y(int index, int offset, FontMetrics fm) {
        int y = y(index, offset);
        y += fm.getFont().getSize() / 2;
        return y;
    }

    private int y(int index, int offset) {
        int cell = index % CELLS_PER_COL;
        int y = CELL_HEIGHT * cell;
        y += (offset * CELL_HEIGHT) / Locatable.CELL_TICKS;
        return y;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.scale(scale, scale);
        if (borderWidth > 0) {
            g2.translate(borderWidth, borderWidth);
        }

        doPaint(g2, false);

        // draw the cursor
        g2.setColor(CURSOR_COLOUR);
        g2.setStroke(cursorStroke);
        int x = x(cursorIndex);
        int y = y(cursorIndex, cursorOffset);
        if (!cursorOnNote) {
            x += COLUMN_WIDTH / 2;
        }
        g2.drawLine(x, y, x + COLUMN_WIDTH / 2, y);
        g2.dispose();
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return Printable.NO_SUCH_PAGE;
        }

        Graphics2D g2 = (Graphics2D) graphics.create();

        int x = (int) Math.ceil(pageFormat.getImageableX());
        int y = (int) Math.ceil(pageFormat.getImageableY());
        g2.translate(x, y);

        double pscale = Math.min(pageFormat.getImageableWidth() / CANVAS_WIDTH,
                pageFormat.getImageableHeight() / CANVAS_HEIGHT);
        g2.scale(pscale, pscale);

        doPaint(g2, true);
        g2.dispose();
        return Printable.PAGE_EXISTS;
    }

    private boolean moveCursor(int ticks, boolean selecting) {
        int cursorTicks = cursorIndex * Locatable.CELL_TICKS + cursorOffset;
        cursorTicks += ticks;
        if (cursorTicks < 0 || cursorTicks > CELLS_PER_COL * COLUMNS_PER_PAGE * Locatable.CELL_TICKS) {
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
         * TODO: Give user choice of start or end of cell at new column boundaries.
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
        setCursor(index, offset);
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
         * TODO: Give user choice of start or end of cell at new column boundaries.
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
        setCursor(0, Locatable.CELL_TICKS / 2);
    }

    boolean isCursorOnNote() {
        return cursorOnNote;
    }

    private void handleText() {
        if (cursorOnNote || !selection.isEmpty()) {
            text.setVisible(false);
        } else {
            int size = COLUMN_WIDTH / 2;
            int x = x(cursorIndex) + 7 * COLUMN_WIDTH / 8;
            int y = y(cursorIndex, cursorOffset) - size / 3;
            // convert to screen coordinates
            x = (int) ((x + borderWidth) * scale);
            y = (int) ((y + borderWidth) * scale);
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
        } else if (moveCursor(CELLS_PER_COL * Locatable.CELL_TICKS, selecting)) {
            setCursorOnNote(!cursorOnNote);
        }
    }

    public void moveCursorRight(boolean selecting) {
        if (cursorOnNote) {
            setCursorOnNote(false);
        } else if (moveCursor(-CELLS_PER_COL * Locatable.CELL_TICKS, selecting)) {
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

    class ML extends MouseAdapter implements MouseListener {
        @Override
        public void mouseReleased(MouseEvent e) {
            /*
             * Allow for the transformations - scaling and translation for the border
             */
            int x = (int) Math.ceil(e.getX() / scale - borderWidth);
            int y = (int) Math.ceil(e.getY() / scale - borderWidth);

            if (y < 0 || y > CANVAS_HEIGHT) {
                e.consume();
                return;
            }

            x = CANVAS_WIDTH - x;
            //Which column?
            int col = x / (COLUMN_WIDTH + COLUMN_SPACE);
            if (col < 0 || col > COLUMNS_PER_PAGE) {
                e.consume();
                return;
            }

            /*
             * Handle titles.
             * TODO: This should be generic and check the document to see whether there is a title column
             * at this place.
             */
            if (col == 0) {
                //invoke title edit
                ActionManager.getAction(Title.class).actionPerformed(null);
                e.consume();
                return;
            }
            // -1 to allow for the title column.
            col--;

            // How far into the column?
            int colx = x % (COLUMN_WIDTH + COLUMN_SPACE);
            if (colx > COLUMN_WIDTH) //click in column space!
            {
                e.consume();
                return;
            }
            boolean onNote = colx > COLUMN_WIDTH / 2;

            int cells = y / CELL_HEIGHT;
            int index = col * CELLS_PER_COL + cells;
            int offset = (y % CELL_HEIGHT) / (CELL_HEIGHT / Locatable.CELL_TICKS);
            setCursorWithOnNote(index, offset, onNote);
        }
    }
}
