package org.colston.kicks.render;

import org.colston.kicks.KicksApp;
import org.colston.kicks.Settings;
import org.colston.kicks.document.Accidental;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.Locatable;
import org.colston.kicks.document.LocatableRange;
import org.colston.kicks.document.LocatableUtils;
import org.colston.kicks.document.Lyric;
import org.colston.kicks.document.Note;
import org.colston.kicks.document.Repeat;
import org.colston.kicks.document.SimpleLocatable;
import org.colston.kicks.document.SimpleLocatableRange;
import org.colston.kicks.document.Song;
import org.colston.kicks.document.Tuning;
import org.colston.lib.i18n.Messages;
import org.colston.utils.KanaConverter;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * Class that uses a {@link java.awt.Graphics2D} object to render a single page of a {@link org.colston.kicks.document.KicksDocument}.
 * Used for both drawing to the screen and when printing.
 */
public class PageRenderer implements Printable {

    /*
     * Dimensions
     */
    // deprecated: private static final int TITLE_WIDTH = 56;
    private static final int TITLE_MARGIN = 9;
    public static final int COLUMN_WIDTH = 56;
    public static final int COLUMN_SPACE = 9;
    public static final int CELL_HEIGHT = 36;
    public static final int BORDER_WIDTH = 20;

    public static final int COLUMNS_PER_PAGE = 11;
    public static final int CELLS_PER_COL = 12;
    public static final int CELLS_PER_PAGE = COLUMNS_PER_PAGE * CELLS_PER_COL;

    //NOTE: This is now the same as 11 columns, so we could replace any column with a title
    //CANVAS_WIDTH = 706;
    public static final int CANVAS_WIDTH = COLUMN_WIDTH * COLUMNS_PER_PAGE + COLUMN_SPACE * COLUMNS_PER_PAGE;
    //CANVAS_HEIGHT = 432;
    public static final int CANVAS_HEIGHT = CELL_HEIGHT * CELLS_PER_COL;

    private static final int REPEAT_HEAD_WIDTH = 6;
    private static final int REPEAT_HEAD_HEIGHT = 8;

    private static final int X_OFFSET_CHORD = COLUMN_WIDTH / 2 - 4;
    private static final int X_OFFSET_SLUR = 4;

    /*
     * Strokes
     */
    private final Stroke stroke = new BasicStroke(1.0f);
    private final Stroke decorateStroke = new BasicStroke(1.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    /*
     * Colours
     */
    private static final Color BORDER_BOX_COLOUR = new Color(150, 150, 150);
    private static final Color FOREGROUND_COLOUR = Color.BLACK;
    public static final Color CURSOR_COLOUR = Color.BLUE;

    /*
     * Fonts
     */
    private static final Font titleFont = new Font(KicksApp.V_FONT_NAME, Font.PLAIN, 26);
    private static final Font ftitleFont = new Font(KicksApp.V_FONT_NAME, Font.PLAIN, 12);
    private static final Font rtitleFont = new Font(KicksApp.R_FONT_NAME, Font.PLAIN, 14);
    private static final Font font = new Font(KicksApp.FONT_NAME, Font.PLAIN, 18);
    private static final Font fontBold = new Font(KicksApp.FONT_NAME, Font.BOLD, 18);
    private static final Font sfont = new Font(KicksApp.FONT_NAME, Font.PLAIN, 14);
    private static final Font sfontBold = new Font(KicksApp.FONT_NAME, Font.BOLD, 14);
    private static final Font flatFont = new Font(KicksApp.FONT_NAME, Font.PLAIN, 9);
    private static final Font fingerFont = new Font(KicksApp.FONT_NAME, Font.PLAIN, 7);
    private static final Font tempoFont = new Font(KicksApp.R_FONT_NAME, Font.PLAIN, 9);
    public static final Font lyricFont = new Font(KicksApp.V_FONT_NAME, Font.PLAIN, 12);

    /*
     * Text Constants
     */
    private static final String VERSION = Messages.get(PageRenderer.class, "renderer.version");
    private static final String TRANSCRIPTION_FROM = Messages.get(PageRenderer.class, "renderer.transcription.from");

    /*
     * Stuff to render
     */
    private final KicksDocument doc;
    private final LocatableRange selection;
    private final PageCursor cursor;
    private final Color selectionColour;
    private final Settings settings;

    /*
     * State
     */
    private boolean cursorHighlight =  false;
    private LocatableRange pageRange;

    public PageRenderer(KicksDocument doc, Settings settings) {
        this(doc, settings, null, null, -1, null);
    }

    public PageRenderer(KicksDocument doc, Settings settings, LocatableRange selection, Color selectionColour,
            int pageIndex, PageCursor cursor) {
        this.doc = doc;
        this.selection = selection;
        this.selectionColour = selectionColour;
        this.cursor = cursor;
        this.pageRange = pageIndex >= 0 ? calculatePageRange(pageIndex) : null;
        this.settings = settings;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        pageRange = calculatePageRange(pageIndex);
        Locatable highest = LocatableUtils.findHighest(doc.getAllLocatables());

        if (highest == null || highest.isLessThan(pageRange.getLow())) {
            return Printable.NO_SUCH_PAGE;
        }

        Graphics2D g2 = (Graphics2D) graphics.create();

        int x = (int) Math.ceil(pageFormat.getImageableX());
        int y = (int) Math.ceil(pageFormat.getImageableY());
        g2.translate(x, y);

        double scale = Math.min(pageFormat.getImageableWidth() / CANVAS_WIDTH,
                pageFormat.getImageableHeight() / CANVAS_HEIGHT);
        g2.scale(scale, scale);

        doPaint(g2);

        g2.dispose();
        return Printable.PAGE_EXISTS;
    }

    public void doPaint(Graphics2D g2) {

        // draw properties
        drawProperties(g2);

        // create a border
        g2.translate(BORDER_WIDTH, BORDER_WIDTH);

        drawSelection(g2);

        // draw the background cells
        int x = CANVAS_WIDTH;
        int y = 0;
        int index = pageRange.getLow().getIndex();

        g2.setStroke(stroke);
        g2.setColor(BORDER_BOX_COLOUR);
        while (x > 0) {
            x -= COLUMN_SPACE + COLUMN_WIDTH;
            if (doc.getSongAtIndex(index, CELLS_PER_COL).isEmpty()) {
                // only draw the cells if there is not a title here
                g2.drawRect(x, y, COLUMN_WIDTH, CANVAS_HEIGHT);
                while (y < CANVAS_HEIGHT) {

                    g2.drawRect(x, y, COLUMN_WIDTH / 2, CELL_HEIGHT);
                    y += CELL_HEIGHT;
                }
            }
            y = 0;
            index += CELLS_PER_COL;
        }

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        FontMetrics fm;
        g2.setColor(FOREGROUND_COLOUR);

        for (Song song : doc.getSongs(pageRange)) {
            drawTitle(g2, song);
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

        for (Note n : doc.getNotes(pageRange)) {
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

            cursorStartHighlight(g2, n, true, n.isSmall() ? sfontBold : fontBold);
            drawNote(g2, n, fm);
            cursorEndHighlight(g2, n.isSmall() ? sfont : font);

            if ((chordStart == null) == n.isChord()) //state has changed
            {
                if (n.isChord()) {
                    chordStart = n;
                } else {
                    drawNoteJoiningLine(g2, chordStart, n, X_OFFSET_CHORD, lfm, sfm);
                    chordStart = null;
                }
            }
            if ((slurStart == null) == n.isSlur()) //state has changed
            {
                if (n.isSlur()) {
                    slurStart = n;
                } else {
                    drawNoteJoiningLine(g2, slurStart, n, X_OFFSET_SLUR, lfm, sfm);
                    slurStart = null;
                }
            }
        }
        if (chordStart != null) {
            //draw the unfinished chord to the last note
            Note end = doc.getNotes().getLast();
            drawNoteJoiningLine(g2, chordStart, end, X_OFFSET_CHORD, lfm, sfm);
        }
        if (slurStart != null) {
            //draw the unfinished slur to the last note
            Note end = doc.getNotes().getLast();
            drawNoteJoiningLine(g2, slurStart, end, X_OFFSET_SLUR, lfm, sfm);
        }

        // draw the repeats
        for (Repeat r : doc.getRepeats(pageRange)) {
            cursorStartHighlight(g2, r, true, null);
            drawRepeat(g2, r.isBack(), r.getIndex(), r.getOffset());
            cursorEndHighlight(g2, null);
        }

        // draw the lyrics
        g2.setFont(lyricFont);
        fm = g2.getFontMetrics();
        for (Lyric l : doc.getLyrics(pageRange)) {
            if (settings != null && settings.isRomaji()) {
                drawRomajiLyric(g2, l.getValue(), l.getIndex(), l.getOffset(), fm);
            } else {
                char[] ch = l.getValue().toCharArray();
                for (int i = 0; i < ch.length; i++) {
                    cursorStartHighlight(g2, l, false, null);
                    drawLyric(g2, ch, i, l.getIndex(), l.getOffset(), fm);
                    cursorEndHighlight(g2, null);

                }
            }
        }
    }

    private void drawProperties(Graphics2D g2) {
        String version = doc.getDocumentVersion();
        String transcription = doc.getTranscription();
        if ((transcription == null || transcription.isBlank())
                && (version == null || version.isBlank())) {
            return;
        }
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(flatFont);
        g2.setColor(BORDER_BOX_COLOUR);

        int x = BORDER_WIDTH;
        if (version != null && !version.isBlank()) {
            g2.drawString(VERSION + " " + version, x, flatFont.getSize() + 2);
            x += g2.getFontMetrics().stringWidth(VERSION + " " + version + " ");
        }
        if (transcription != null && !transcription.isBlank()) {
            g2.drawString(TRANSCRIPTION_FROM + " " + transcription, x, flatFont.getSize() + 2);
        }

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }

    private void drawSelection(Graphics2D g2) {
        if (selection == null || selection.isEmpty()) {
            return;
        }
        g2.setColor(selectionColour);
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

    private void drawTitle(Graphics2D g2, Song song) {
        String title = song.getTitle();
        String romaji = song.getTitleRomaji();

        //start with some analysis
        int titleCharWidth = 0;
        int furiganaCharWidth = 0;
        int romajiCharWidth = 0;
        if (title != null && !title.isBlank()) {
            char[] tchars = title.toCharArray();
            g2.setFont(titleFont);
            FontMetrics titleFontMetrics = g2.getFontMetrics();
            for (char ch : tchars) {
                titleCharWidth = Math.max(titleFontMetrics.charWidth(ch), titleCharWidth);
            }
            if (title.indexOf('{') >= 0) {
                g2.setFont(ftitleFont);
                FontMetrics ftitleFontMetrics = g2.getFontMetrics();
                for (char ch : tchars) {
                    furiganaCharWidth = Math.max(ftitleFontMetrics.charWidth(ch), furiganaCharWidth);
                }
            }
        }
        if (romaji != null && !romaji.isBlank()) {
            romajiCharWidth = rtitleFont.getSize();
        }

        if (title != null && !title.isBlank()) {
            char[] tchars = title.toCharArray();
            int x = calcTitleBaseX(song);
            int y = 0;

            g2.setFont(titleFont);
            // centralise if there is no romaji
            x += romajiCharWidth > 0 ? 1 : (COLUMN_WIDTH - titleCharWidth - furiganaCharWidth) / 2;
            y += TITLE_MARGIN + titleFont.getSize();
            for (int i = 0; i < tchars.length; i++) {
                if (tchars[i] == '{') {
                    // start of kanji with furigana
                    int kstart = i + 1;
                    int kend = kstart;
                    int fstart = 0;
                    int fend = 0;
                    for (int j = kstart; j < tchars.length; j++) {
                        if (tchars[j] == '}') {
                            kend = j;
                            break;
                        }
                    }
                    if (tchars[kend + 1] == '{') {
                        fstart = kend + 2;
                        for (int j = fstart; j < tchars.length; j++) {
                            if (tchars[j] == '}') {
                                fend = j;
                                break;
                            }
                        }
                    }
                    int kpad = 0;
                    int fpad = 0;
                    int kheight = (kend - kstart) * titleFont.getSize();
                    int fheight = (fend - fstart) * ftitleFont.getSize();
                    if (kheight > fheight) {
                        fpad = (kheight - fheight) / (fend - fstart + 1);
                    } else if (kheight < fheight) {
                        kpad = (fheight - kheight) / (kend - kstart + 1);
                    }
                    int ystart = y;
                    for (int j = kstart; j < kend; j++) {
                        y += kpad;
                        g2.drawChars(tchars, j, 1, x, y);
                        y += titleFont.getSize();
                    }
                    int yend = y;
                    y = ystart - titleFont.getSize() + ftitleFont.getSize();
                    g2.setFont(ftitleFont);
                    for (int j = fstart; j < fend; j++) {
                        y += fpad;
                        g2.drawChars(tchars, j, 1, x + titleCharWidth - 2, y); // -2 to cwtch up to the kanji a bit
                        y += ftitleFont.getSize();
                    }
                    g2.setFont(titleFont);
                    y = yend + kpad;
                    i = fend;
                } else {
                    g2.drawChars(tchars, i, 1, x, y);
                    y += titleFont.getSize();
                }
            }
        }
        if (romaji != null && !romaji.isBlank()) {
            int x = calcTitleBaseX(song);
            x += COLUMN_WIDTH - romajiCharWidth;
            int y = TITLE_MARGIN + titleFont.getSize() - rtitleFont.getSize();
            g2.setFont(rtitleFont);
            g2.rotate(Math.toRadians(90), x, y);
            g2.drawString(romaji, x, y - 1);  // subtract from y to move slightly to the right
            g2.rotate(Math.toRadians(-90), x, y);
        }

        Tuning tuning = song.getTuning();
        if (tuning != null) {
            char[] tchars = tuning.getDisplayName().toCharArray();
            if (tchars.length > 0) {
                // draw the tuning
                g2.setFont(sfont);
                FontMetrics fm = g2.getFontMetrics();
                int x = calcTitleBaseX(song);
                x += ((COLUMN_WIDTH - fm.charWidth(tchars[0])) / 2);
                int y = CANVAS_HEIGHT - TITLE_MARGIN - (sfont.getSize() * tchars.length);
                for (int i = 0; i < tchars.length; i++) {
                    g2.drawChars(tchars, i, 1, x, y);
                    y += sfont.getSize();
                }
            }
        }

        String tempo = song.getTempo();
        if (tempo != null && !tempo.isEmpty()) {
            tempo += " BPM";
            g2.setFont(tempoFont);
            FontMetrics fm = g2.getFontMetrics();
            int x = calcTitleBaseX(song);
            x += ((COLUMN_WIDTH - fm.stringWidth(tempo)) / 2);
            g2.drawString(tempo, x, CANVAS_HEIGHT);
        }
    }

    private int calcTitleBaseX(Song song) {
        return CANVAS_WIDTH - (COLUMN_WIDTH + COLUMN_SPACE) * (((song.getIndex() - pageRange.getLow().getIndex()) / CELLS_PER_COL) + 1);
    }

    private void drawNote(Graphics2D g2, Note n, FontMetrics fm) {
        char[] ch = RendererResources.getNoteText(n.getString(), n.getPlacement()).toCharArray();
        int x = x(n.getIndex());
        int y = y(n.getIndex(), n.getOffset(), fm) - 1;

        if (n.getFinger() != 0) {
            Font tfont = g2.getFont();
            g2.setFont(fingerFont);
            g2.drawString(RendererResources.getNoteFingerText(n.getFinger()),
                    x - COLUMN_SPACE + 1,
                    y(n.getIndex(), n.getOffset(), g2.getFontMetrics()) + 1);
            g2.setFont(tfont);
        }

        int chw;
        if (ch.length == 1) {
            chw = fm.charWidth(ch[0]);
            x += (COLUMN_WIDTH / 2 - chw) / 2;
            g2.drawChars(ch, 0, 1, x, y);
        } else if ('下' == ch[0]) {
            Font currentFont = g2.getFont();
            Font font = currentFont.deriveFont(AffineTransform.getScaleInstance(1.0, 0.6));
            g2.setFont(font);
            FontMetrics fontMetrics = g2.getFontMetrics();
            chw = fontMetrics.charWidth(ch[0]) + 2;

            x += ((COLUMN_WIDTH / 2) - chw) / 2;
            y = y(n.getIndex(), n.getOffset()) + 1;      //+1 here to squash them together vertically
            g2.drawChars(ch, 0, 1, x + 1, y);
            y += (font.getSize() / 2) - 1;                   //-1 here to squash them together vertically (if necessary)
            g2.drawChars(ch, 1, 1, x + 1, y);

            g2.setFont(currentFont);

            y -= 2; //to add padding for the 'utou' for this type of double char
        } else {
            int chw0 = fm.charWidth(ch[0]) - 3;
            int chw1 = fm.charWidth(ch[1]) - 3;
            chw = chw0 + chw1;
            x += ((COLUMN_WIDTH / 2) - chw) / 2;
            g2.drawChars(ch, 0, 1, x - 1, y);
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
        y -= fm.getFont().getSize() - 2; // -2 to move it down towards the note

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
                // do nothing
            }
        }
    }

    private void drawNoteJoiningLine(Graphics2D g2, Note start, Note end, int xOffset,
                                     FontMetrics lfm, FontMetrics sfm) {
        cursorStartHighlight(g2, start, true, null);
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
        int y = y(index, offset, fm);
        y += ((int) Math.ceil(fm.getFont().getSize() * 0.7f)) * choff;
        g2.drawChars(ch, choff, 1, x, y);
    }

    private void drawRomajiLyric(Graphics2D g2, String value, int index, int offset, FontMetrics fm) {
        int x = x(index) + (COLUMN_WIDTH / 2) + 2;
        String romaji = KanaConverter.toRomaji(value);
        int y = y(index, offset, fm);
        g2.drawString(romaji, x, y);
    }

    private void cursorStartHighlight(Graphics2D g2, Locatable locatable, boolean noteSide, Font f) {
        if (cursor == null || (!isUnderCursor(locatable, noteSide) && !selection.contains(locatable))) {
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
        return cursor.onNote() == noteSide && l.getIndex() == cursor.index() && l.getOffset() == cursor.offset();
    }

    public int x(int index) {
        int col = (index - pageRange.getLow().getIndex()) / CELLS_PER_COL;
        return CANVAS_WIDTH - (COLUMN_SPACE + COLUMN_WIDTH) - (COLUMN_SPACE + COLUMN_WIDTH) * col;
    }

    private int y(int index, int offset, FontMetrics fm) {
        int y = y(index, offset);
        y += fm.getFont().getSize() / 2;
        return y;
    }

    public int y(int index, int offset) {
        int cell = (index - pageRange.getLow().getIndex()) % CELLS_PER_COL;
        int y = CELL_HEIGHT * cell;
        y += (offset * CELL_HEIGHT) / Locatable.CELL_TICKS;
        return y;
    }

    public static LocatableRange calculatePageRange(int pageIndex) {
        int startIndex = pageIndex * COLUMNS_PER_PAGE * CELLS_PER_COL;
        int endIndex = startIndex + COLUMNS_PER_PAGE * CELLS_PER_COL - 1;
        return new SimpleLocatableRange(startIndex, 1, endIndex, 12);
    }

    public static int calculateNumberOfPages(int highestIndex) {
        return (highestIndex / (COLUMNS_PER_PAGE * CELLS_PER_COL)) + 1;
    }

    public static int calculatePageIndex(int cursorIndex) {
        return cursorIndex / (COLUMNS_PER_PAGE * CELLS_PER_COL);
    }
}
