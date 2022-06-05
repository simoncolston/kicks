package org.colston.kicks.gui.canvas;

import javax.swing.*;
import java.awt.*;

public class NoteButtonPanel extends JPanel {
    private static final Stroke TOP_STRING_STROKE = new BasicStroke(1.0f);
    private static final Stroke MIDDLE_STRING_STROKE = new BasicStroke(2.5f);
    private static final Stroke BOTTOM_STRING_STROKE = new BasicStroke(4.0f);
    private static final Stroke BRIDGE_STROKE = new BasicStroke(3);

    private static final Dimension BUTTON_SIZE = new Dimension(38, 32);
    private static final int SPACING = 24;
    private static final int BRIDGE_OFFSET = 12 + BUTTON_SIZE.width / 2;

    private static final int WIDTH = 9 * BUTTON_SIZE.width + 9 * SPACING;
    private static final int HEIGHT = 3 * BUTTON_SIZE.height + 4 * SPACING;

    public NoteButtonPanel() {
        super(null);
        setOpaque(true);
        setBackground(Color.BLACK);

        Dimension dim = new Dimension(WIDTH, HEIGHT);
        setPreferredSize(dim);
        setMaximumSize(dim);
        setMinimumSize(dim);

        for (CanvasAction ca : CanvasActions.getActionsWithPrefix("canvas.note")) {
            if (!(ca instanceof NoteAction na)) {
                continue;
            }
            JButton b = new JButton(na);
            b.setIcon(NoteValues.getImage(na.getString(), na.getPlacement()));
            b.setMargin(new Insets(2, 2, 2, 2));
            b.setFocusable(false);

            int x = BRIDGE_OFFSET - BUTTON_SIZE.width / 2;
            x += na.getPlacement() * (BUTTON_SIZE.width + SPACING);
            int y = HEIGHT - na.getString() * (SPACING + BUTTON_SIZE.height);
            b.setBounds(x, y, BUTTON_SIZE.width, BUTTON_SIZE.height);
            add(b);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        int height = getHeight();
        int width = getWidth();
        g2.setColor(Color.WHITE);
        g2.setStroke(BRIDGE_STROKE);
        g2.drawLine(BRIDGE_OFFSET, 0, BRIDGE_OFFSET, height);

        g2.setStroke(TOP_STRING_STROKE);
        int y = height / 2 - SPACING - BUTTON_SIZE.height;
        g2.drawLine(0, y, width, y);
        g2.setStroke(MIDDLE_STRING_STROKE);
        y = height / 2;
        g2.drawLine(0, y, width, y);
        g2.setStroke(BOTTOM_STRING_STROKE);
        y = height / 2 + SPACING + BUTTON_SIZE.height;
        g2.drawLine(0, y, width, y);

        g2.dispose();
    }
}
