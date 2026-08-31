package org.colston.kicks.gui.canvas;

import org.colston.kicks.KicksApp;
import org.colston.kicks.document.Lyric;
import org.colston.kicks.render.PageCursor;
import org.colston.kicks.render.PageRenderer;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

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
     * Cursor
     */
    private final Stroke cursorStroke = new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private final CanvasCursorModel cursorModel;

    /*
     * The model.
     */
    private final CanvasModel model;

    private final int pageIndex;
    private PageRenderer pageRenderer;

    private final CanvasZoomModel zoomModel;

    public CanvasPanel(CanvasModel model, CanvasCursorModel cursorModel, CanvasZoomModel zoomModel,
                       JTextComponent text, int pageIndex) {
        // remove default layout manager - use absolute positioning for text field
        super(null);
        this.model = model;
        this.cursorModel = cursorModel;
        this.zoomModel = zoomModel;

        setBackground(BACKGROUND_COLOUR);
        setFocusable(true);

        this.text = text;
        text.setFont(PageRenderer.lyricFont);
        add(text);

        this.pageIndex = pageIndex;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.scale(zoomModel.getScale(), zoomModel.getScale());

        pageRenderer = PageRenderer.create(model.getDocument())
                .fillPageWithColumns(true)
                .romaji(KicksApp.settings().isRomaji())
                .selection(cursorModel.getSelection(), SELECTION_COLOUR)
                .pageIndex(pageIndex)
                .includeVersion(KicksApp.settings().isIncludeVersion())
                .cursor(new PageCursor(cursorModel.getCursorIndex(), cursorModel.getCursorOffset(), cursorModel.isCursorOnNote()));
        pageRenderer.doPaint(g2);

        if (pageIndex == PageRenderer.calculatePageIndex(cursorModel.getCursorIndex())) {
            // draw the cursor
            g2.setColor(PageRenderer.CURSOR_COLOUR);
            g2.setStroke(cursorStroke);
            int x = pageRenderer.x(cursorModel.getCursorIndex());
            int y = PageRenderer.y(cursorModel.getCursorIndex(), cursorModel.getCursorOffset());
            if (!cursorModel.isCursorOnNote()) {
                x += PageRenderer.COLUMN_WIDTH / 2;
            }
            g2.drawLine(x, y, x + PageRenderer.COLUMN_WIDTH / 2, y);
        }

        g2.dispose();
    }

    void handleText() {
        if (cursorModel.isCursorOnNote()
                || pageIndex != PageRenderer.calculatePageIndex(cursorModel.getCursorIndex())
                || !cursorModel.getSelection().isEmpty()) {
            text.setVisible(false);
        } else {
            int size = PageRenderer.COLUMN_WIDTH / 2;
            int x = pageRenderer.x(cursorModel.getCursorIndex()) + 7 * PageRenderer.COLUMN_WIDTH / 8;
            int y = PageRenderer.y(cursorModel.getCursorIndex(), cursorModel.getCursorOffset()) - size / 3;
            // convert to screen coordinates
            x = (int) ((x + PageRenderer.BORDER_WIDTH) * zoomModel.getScale());
            y = (int) ((y + PageRenderer.BORDER_WIDTH) * zoomModel.getScale());
            text.setBounds(x, y, size, size);

            Lyric l = model.getDocument().getLyric(cursorModel.getCursorIndex(), cursorModel.getCursorOffset());
            text.setText(l != null ? l.getValue() : null);
            text.selectAll();

            text.setVisible(true);
            text.requestFocusInWindow();
        }
    }

    String getText() {
        return text.getText();
    }
}
