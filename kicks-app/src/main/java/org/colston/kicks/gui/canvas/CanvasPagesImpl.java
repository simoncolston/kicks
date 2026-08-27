package org.colston.kicks.gui.canvas;

import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.KicksDocumentUtils;
import org.colston.kicks.document.Locatable;
import org.colston.kicks.document.Song;
import org.colston.lib.gui.JapaneseTextFocusListener;
import org.colston.kicks.render.PageRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class CanvasPagesImpl implements CanvasPages {

    private final JPanel container;
    private final CanvasModel model;
    private final CanvasCursorModel cursorModel;
    private final CanvasZoomModel zoomModel;

    private final List<CanvasPanel> canvasPanels = new ArrayList<>();
    private final List<Component> paddings = new ArrayList<>();

    private CanvasPagesImpl(JPanel container, CanvasModel model, CanvasCursorModel cursorModel, CanvasZoomModel zoomModel) {
        this.container = container;
        this.model = model;
        this.cursorModel = cursorModel;
        this.zoomModel = zoomModel;

        MouseListener mouseListener = new ML();
        this.container.addMouseListener(mouseListener);
    }

    static CanvasPagesImpl create(JPanel container, CanvasModel model, CanvasCursorModel cursorModel, CanvasZoomModel zoomModel) {
        CanvasPagesImpl canvasPages = new CanvasPagesImpl(container, model, cursorModel, zoomModel);
        canvasPages.addPage(0);
        return canvasPages;
    }

    void addPage(int pageIndex) {
        CanvasPanel canvas = createCanvasPanel(pageIndex);
        canvasPanels.add(pageIndex, canvas);
        container.add(canvas);
        Component padding = Box.createVerticalStrut(PAGE_PADDING);
        paddings.add(pageIndex, padding);
        container.add(padding);
    }

    private CanvasPanel createCanvasPanel(int pageIndex) {
        JTextField text = new JTextField();
        text.setBorder(BorderFactory.createLineBorder(PageRenderer.CURSOR_COLOUR));
        text.enableInputMethods(true);
        text.addFocusListener(new JapaneseTextFocusListener());
        CanvasActions.addPrefixToInputActionMaps(text, "canvas.cursor.");
        CanvasActions.addPrefixToInputActionMaps(text, "canvastext.");
        CanvasPanel canvasPanel = new CanvasPanel(model, cursorModel, zoomModel, text, pageIndex);
        canvasPanel.setName(String.valueOf(pageIndex));  // used in the mouse listener to identify the panel
        return canvasPanel;
    }

    void removePage(int pageIndex) {
        CanvasPanel canvas = canvasPanels.remove(pageIndex);
        container.remove(canvas);
        Component padding = paddings.remove(pageIndex);
        container.remove(padding);
    }

    @Override
    public int getNumberOfPages() {
        return canvasPanels.size();
    }

    @Override
    public void requestFocusInWindow() {

    }

    @Override
    public void scrollPageToVisible(int pageIndex) {
        Rectangle r = canvasPanels.get(pageIndex).getBounds();
        container.scrollRectToVisible(new Rectangle(r.x, r.y - PAGE_PADDING, r.width, r.height + PAGE_PADDING * 2));
    }

    @Override
    public void documentSet() {
        KicksDocument doc = model.getDocument();
        int highestIndex = KicksDocumentUtils.calculateHighestIndex(doc);
        int numberOfPages = PageRenderer.calculateNumberOfPages(highestIndex);
        for (int pageIndex = canvasPanels.size(); canvasPanels.size() < numberOfPages; pageIndex++) {
            addPage(pageIndex);
        }
        for (int pageIndex = canvasPanels.size() - 1; canvasPanels.size() > numberOfPages; pageIndex--) {
            removePage(pageIndex);
        }
        cursorModel.clearSelection();
        cursorModel.initialiseCursor();
        redraw();
    }

    @Override
    public void redraw() {
        container.revalidate();
        container.repaint();
    }

    @Override
    public void handleText() {
        canvasPanels.forEach(CanvasPanel::handleText);
    }

    @Override
    public String getText() {
        int pageIndex = PageRenderer.calculatePageIndex(cursorModel.getCursorIndex());
        CanvasPanel canvasPanel = canvasPanels.get(pageIndex);
        return canvasPanel.getText();
    }

    @Override
    public void setDimensions(Dimension dimension) {
        canvasPanels.forEach(p -> {
            p.setPreferredSize(dimension);
            p.setMinimumSize(dimension);
            p.setMaximumSize(dimension);
        });
    }

    private class ML extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            if (container.getComponentAt(e.getPoint()) instanceof CanvasPanel canvasPanel) {
                int pageIndex = Integer.parseInt(canvasPanel.getName());
                Rectangle bounds = canvasPanel.getBounds();
                int x = e.getX() - bounds.x;
                int y = e.getY() - bounds.y;

                double scale = zoomModel.getScale();

                // Allow for the transformations - scaling and translation for the border
                x = (int) Math.ceil(x / scale - PageRenderer.BORDER_WIDTH);
                y = (int) Math.ceil(y / scale - PageRenderer.BORDER_WIDTH);

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

                if (offset == 0) {
                    index--;
                    offset = Locatable.CELL_TICKS;
                }

                // move index onto actual page
                index += PageRenderer.CELLS_PER_PAGE * pageIndex;

                // Handle titles.
                Optional<Song> song = model.getDocument().getSongAtIndex(index, PageRenderer.CELLS_PER_COL);
                if (song.isPresent()) {
                    //invoke title edit
                    SongHeaderEditor sde = new SongHeaderEditor();
                    sde.edit(song.get(), cursorModel.getCursorIndex(), cursorModel.getCursorOffset());
                    e.consume();
                    return;
                }
                cursorModel.setCursor(index, offset, onNote, false);
            }
            e.consume();
        }
    }
}
