package org.colston.lib.gui.icons;

import java.awt.*;

public class MailIcon extends IconBase {
    private static final int P = 2;
    private static final int[] xPoints = new int[]{PAD, DIM - PAD, DIM - PAD, PAD};
    private static final int[] yPoints = new int[]{PAD + P, PAD + P, DIM - PAD - P, DIM - PAD - P};

    private static final MailIcon icon = new MailIcon();

    private MailIcon() {
    }

    public static MailIcon get() {
        return icon;
    }

    @Override
    protected void paintIcon(Graphics2D g2) {
        g2.drawPolygon(xPoints, yPoints, xPoints.length);
        g2.drawLine(xPoints[0], yPoints[0], DIM / 2, DIM / 2);
        g2.drawLine(xPoints[1], yPoints[1], DIM / 2, DIM / 2);
    }
}
