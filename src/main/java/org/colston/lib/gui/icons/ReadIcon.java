package org.colston.lib.gui.icons;

import java.awt.*;

public class ReadIcon extends IconBase {
    private static final ReadIcon icon = new ReadIcon();

    private ReadIcon() {
    }

    public static ReadIcon get() {
        return icon;
    }

    @Override
    protected void paintIcon(Graphics2D g2) {
        g2.drawRect(DIM / 2 - 1, DIM / 2 - 1, 2, 2);
    }
}
