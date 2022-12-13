package org.colston.lib.gui.icons;

import java.awt.*;

public class DraftsIcon extends IconBase {
    private static final int H = 5;
    private static final int P = 2;
    private static final int[] xPoints = new int[]{DIM - PAD - H - P, DIM - PAD - H - P, DIM - PAD - P};
    private static final int[] yPoints = new int[]{PAD, PAD + H, PAD + H};

    private static final DraftsIcon icon = new DraftsIcon();

    private DraftsIcon() {
    }

    public static DraftsIcon get() {
        return icon;
    }

    @Override
    protected void paintIcon(Graphics2D g2) {
        g2.drawPolygon(xPoints, yPoints, xPoints.length);
        g2.drawLine(PAD + P, PAD, PAD + P, DIM - PAD);
        g2.drawLine(PAD + P, PAD, xPoints[0], yPoints[0]);
        g2.drawLine(xPoints[2], yPoints[2], xPoints[2], DIM - PAD);
        g2.drawLine(PAD + P, DIM - PAD, xPoints[2], DIM - PAD);
    }
}
