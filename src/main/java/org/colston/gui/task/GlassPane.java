package org.colston.gui.task;

import org.colston.sclib.i18n.Message;
import org.colston.sclib.i18n.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;

class GlassPane extends JPanel {

    private static final Color COLOUR = new Color(238, 238, 238, 192);

    private final JLabel label = new JLabel();
    private final JProgressBar progressBar = new JProgressBar();

    protected GlassPane() {
        setOpaque(false);
        setLayout(new GridBagLayout());

        //capture all mouse activity
        MouseAdapter l = new MouseAdapter() {
        };
        addMouseListener(l);
        addMouseMotionListener(l);
        addMouseWheelListener(l);

        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.add(label);
        label.setAlignmentX(LEFT_ALIGNMENT);

        progressPanel.add(Box.createVerticalStrut(8));

        progressPanel.add(progressBar);
        progressBar.setAlignmentX(LEFT_ALIGNMENT);

        progressPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        add(progressPanel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(COLOUR);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.dispose();
    }

    protected void initialise(Message message) {
        setLabelText(message);
        progressBar.setIndeterminate(true);
    }

    protected void updateProgress(TaskProgressProvider progress) {
        switch (progress.getTaskMessageAction()) {
            case CLEAR:
                label.setText(null);
                break;
            case UPDATE:
                setLabelText(progress.getTaskMessage());
                break;
            case NONE:
            default:
                break;

        }
        if (progressBar.isIndeterminate() ^ progress.isTaskProgressIndeterminate()) {
            progressBar.setIndeterminate(progress.isTaskProgressIndeterminate());
        }
        if (!progressBar.isIndeterminate()) {
            progressBar.setValue(progress.getTaskProgress());
        }
    }

    private void setLabelText(Message m) {
        label.setText(Messages.get(m));
    }
}
