package org.colston.kicks.render;

import org.colston.kicks.document.Accidental;
import org.colston.kicks.document.Note;
import org.colston.printpdf.PDFBoxResourceImage;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class PDFNoteKanjiRenderer implements NoteKanjiRenderer {

    private final double SCALE = (double) 14 / 18;
    private final double XWIDTH = (double) PageRenderer.COLUMN_WIDTH / 2;
    private final int X_OFFSET = (int) ((XWIDTH - SCALE * XWIDTH) / 2);
    private final int Y_OFFSET = PageRenderer.CELL_HEIGHT / 2;

    @Override
    public void render(Graphics2D g2, Note n, int x, int y) {
        renderResource(g2, n, x, y, RendererResources.getNoteResourceName(n.getString(), n.getPlacement()));
        if (n.getAccidental() == Accidental.FLAT) {
            renderResource(g2, n, x, y, "flat.pdf");
        }
        switch (n.getUtou()) {
            case KAKI ->  renderResource(g2, n, x, y, "kaki.pdf");
            case UCHI ->   renderResource(g2, n, x, y, "uchi.pdf");
        }
    }

    private void renderResource(Graphics2D g2, Note n, int x, int y, String resourceName) {
        PDFBoxResourceImage image = new PDFBoxResourceImage(PageRenderer.class, resourceName);
        if (n.isSmall()) {
            image.setTransform(AffineTransform.getScaleInstance(SCALE, SCALE));
            g2.drawImage(image, x + X_OFFSET, y + (int)((SCALE * Y_OFFSET)), null);
        } else  {
            g2.drawImage(image, x, y + Y_OFFSET, null);
        }
    }
}
