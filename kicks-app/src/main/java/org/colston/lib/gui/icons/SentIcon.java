package org.colston.lib.gui.icons;

import java.awt.*;

public class SentIcon extends IconBase {
    private static final SentIcon icon = new SentIcon();

    private SentIcon() {
    }

    public static SentIcon get() {
        return icon;
    }

    @Override
    protected void paintIcon(Graphics2D g2) {
        g2.drawLine(PAD, PAD, DIM - PAD, DIM / 2);
        g2.drawLine(PAD, DIM - PAD, DIM - PAD, DIM / 2);
        g2.drawLine(PAD + HEAD, DIM / 2, DIM - PAD, DIM / 2);
        g2.drawLine(PAD, PAD, PAD + HEAD, DIM / 2);
        g2.drawLine(PAD, DIM - PAD, PAD + HEAD, DIM / 2);
    }
}
