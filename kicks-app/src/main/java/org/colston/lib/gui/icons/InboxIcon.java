package org.colston.lib.gui.icons;

import java.awt.*;

public class InboxIcon extends IconBase {
    private static final int p = DIM / 2;
    private static final int q = 3 * DIM / 5;

    private static final InboxIcon icon = new InboxIcon();

    private InboxIcon() {
    }

    public static InboxIcon get() {
        return icon;
    }

    @Override
    protected void paintIcon(Graphics2D g2) {
        g2.drawLine(DIM / 2, PAD, p, q);
        g2.drawLine(p, q, p - HEAD, q - HEAD);
        g2.drawLine(p, q, p + HEAD, q - HEAD);
        g2.drawLine(PAD, DIM / 2, PAD, DIM - PAD);
        g2.drawLine(PAD, DIM - PAD, DIM - PAD, DIM - PAD);
        g2.drawLine(DIM - PAD, DIM / 2, DIM - PAD, DIM - PAD);
    }
}
