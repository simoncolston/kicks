package org.colston.lib.gui;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    protected JLabel message = new JLabel();

    public StatusPanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        add(message);
    }

    /**
     * Set the message on the status bar. Does not need to be synchronized because
     * this will either be invoked on the event dispatch thread or the text will be
     * set on the event dispatch thread using <code>EventQueue.invokeLater()</code>.
     *
     * @param mess the message to display on the status panel
     */
    public void setMessage(final String mess) {
        if (EventQueue.isDispatchThread()) {
            message.setText(mess);
        } else {
            EventQueue.invokeLater(() -> message.setText(mess));
        }
    }
}
