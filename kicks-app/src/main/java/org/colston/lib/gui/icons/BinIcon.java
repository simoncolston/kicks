package org.colston.lib.gui.icons;

import java.awt.*;

public class BinIcon extends IconBase {
    private static final int P = 2;
    private static final int H = 4;
    private static final int[] xhandle = new int[]{DIM / 2 - 3, DIM / 2 + 3, DIM / 2 + 3, DIM / 2 - 3};
    private static final int[] yhandle = new int[]{PAD, PAD, PAD + P, PAD + P};
    private static final int[] xlid = new int[]{PAD, DIM - PAD, DIM - PAD, PAD};
    private static final int[] ylid = new int[]{PAD + P, PAD + P, PAD + P + H, PAD + P + H};
    private static final int[] xbody = new int[]{PAD + P + 1, DIM - PAD - P - 1, DIM - PAD - P - 1, PAD + P + 1};
    private static final int[] ybody = new int[]{PAD + P + H, PAD + P + H, DIM - PAD, DIM - PAD};

    private static final BinIcon icon = new BinIcon();

    private BinIcon() {
    }

    public static BinIcon get() {
        return icon;
    }

    @Override
    protected void paintIcon(Graphics2D g2) {
        g2.drawPolygon(xhandle, yhandle, yhandle.length);
        g2.drawPolygon(xlid, ylid, xlid.length);
        g2.drawPolygon(xbody, ybody, xbody.length);
    }
}
