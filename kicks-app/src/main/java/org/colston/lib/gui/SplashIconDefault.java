package org.colston.lib.gui;

import javax.swing.*;
import java.awt.*;

public class SplashIconDefault implements Icon {

    protected static final Color fg = new Color(0xC4, 0xC4, 0xC4);

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(fg);
        g2.drawString("Splash", 150, 150);

        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return 620;
    }

    @Override
    public int getIconHeight() {
        return 300;
    }
}
