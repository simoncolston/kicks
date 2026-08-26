package org.colston.lib.gui.icons;

import javax.swing.*;
import java.awt.*;

public abstract class IconBase implements Icon {
    protected static final int DIM = 24;
    protected static final int PAD = 4;
    protected static final int HEAD = 4;

    protected static final BasicStroke thickStroke = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    protected static final BasicStroke thinStroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    //	protected Color bg = Color.WHITE;
    protected static final Color bg = new Color(0xC4, 0xC4, 0xC4);
    protected static final Color fg = Color.BLACK;
//	protected Color fg = new Color(0x04, 0x0B, 0x60);
//	protected Color fg = new Color(0x0A, 0x19, 0xC2);

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //draw thick background colour
        g2.setColor(bg);
        g2.setStroke(thickStroke);
        paintIcon(g2);

        //draw thin foreground colour
        g2.setColor(fg);
        g2.setStroke(thinStroke);
        paintIcon(g2);

        g2.dispose();
    }

    protected abstract void paintIcon(Graphics2D g2);

    @Override
    public int getIconWidth() {
        return DIM;
    }

    @Override
    public int getIconHeight() {
        return DIM;
    }
}
