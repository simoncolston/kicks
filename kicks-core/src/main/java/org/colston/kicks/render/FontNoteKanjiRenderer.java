package org.colston.kicks.render;

import org.colston.kicks.document.Accidental;
import org.colston.kicks.document.Note;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class FontNoteKanjiRenderer implements  NoteKanjiRenderer {

    private static final Font flatFont = new Font(PageRenderer.FONT_NAME, Font.PLAIN, 9);
    private final Stroke decorateStroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private int fontSize;
    private int x;
    private int y;
    private int chw;

    @Override
    public void render(Graphics2D g2, Note n, int xx, int yy) {

        this.fontSize = g2.getFont().getSize();
        this.x = xx;
        this.y = yy + fontSize / 2 - 1;

        char[] ch = RendererResources.getNoteText(n.getString(), n.getPlacement()).toCharArray();
        FontMetrics fm = g2.getFontMetrics();
        if (ch.length == 1) {
            chw = fm.charWidth(ch[0]);
            x += (PageRenderer.COLUMN_WIDTH / 2 - chw) / 2;
            g2.drawChars(ch, 0, 1, x, y);
        } else if ('下' == ch[0]) {
            Font currentFont = g2.getFont();
            Font font = currentFont.deriveFont(AffineTransform.getScaleInstance(1.0, 0.6));
            g2.setFont(font);
            FontMetrics fontMetrics = g2.getFontMetrics();
            chw = fontMetrics.charWidth(ch[0]) + 2;

            x += ((PageRenderer.COLUMN_WIDTH / 2) - chw) / 2;
            y = PageRenderer.y(n.getIndex(), n.getOffset()) + 1;      //+1 here to squash them together vertically
            g2.drawChars(ch, 0, 1, x + 1, y);
            y += (font.getSize() / 2) - 1;                   //-1 here to squash them together vertically (if necessary)
            g2.drawChars(ch, 1, 1, x + 1, y);

            g2.setFont(currentFont);

            y -= 2; //to add padding for the 'utou' for this type of double char
        } else {
            int chw0 = fm.charWidth(ch[0]) - 3;
            int chw1 = fm.charWidth(ch[1]) - 3;
            chw = chw0 + chw1;
            x += ((PageRenderer.COLUMN_WIDTH / 2) - chw) / 2;
            g2.drawChars(ch, 0, 1, x - 1, y);
            g2.drawChars(ch, 1, 1, x + chw0 - 1, y);

            //to add a little more padding to the 'utou' for double characters
            chw += 2;
        }

        if (n.getAccidental() == Accidental.FLAT) {
            Font tfont = g2.getFont();
            g2.setFont(flatFont);
            g2.drawString("♭", x - 2 - flatFont.getSize() / 2, y);
            g2.setFont(tfont);
        }

        x = getRightX();
        y = getTopY();
        switch (n.getUtou()) {
            case KAKI -> {
                g2.setStroke(decorateStroke);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawLine(x, y, x + 1 - fm.getFont().getSize() / 2, y);
                g2.drawLine(x, y, x, y - 1 + fm.getFont().getSize() / 2);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            }
            case UCHI -> {
                g2.setStroke(decorateStroke);
                int[] xs = new int[3];
                int[] ys = new int[3];

                xs[0] = x - fm.getFont().getSize() / 4;
                ys[0] = y;
                xs[1] = x;
                ys[1] = y + fm.getFont().getSize() / 4;
                xs[2] = x + 1;
                ys[2] = y - 1 + fm.getFont().getSize() / 4;
                Polygon tri = new Polygon(xs, ys, 3);

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawLine(xs[0], ys[0], xs[1], ys[1]);
                g2.drawLine(xs[0], ys[0], xs[2], ys[2]);
                g2.drawLine(xs[1], ys[1], xs[2], ys[2]);
                g2.fill(tri);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            }
            case NONE -> {
                // do nothing
            }
        }
    }

    private int getRightX() {
        return x + chw + 1;
    }

    private int getTopY() {
        return y - fontSize + 2; // +2 to cwtch up to the note a bit
    }
}
