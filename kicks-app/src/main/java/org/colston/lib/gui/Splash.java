package org.colston.lib.gui;

import javax.swing.*;
import java.awt.*;

/**
 * @author simon
 */
public class Splash extends JWindow {

    public Splash(Frame frame, Icon splashIcon) {
        super(frame);
        setName(frame.getTitle());
        JLabel l = new JLabel(splashIcon);
        getContentPane().add(l);
    }

    public void showSplash() {

        if (EventQueue.isDispatchThread()) {
            pack();
            setLocationRelativeTo(getOwner());
            setVisible(true);
        } else {

            EventQueue.invokeLater(() ->
            {
                pack();
                setLocationRelativeTo(getOwner());
                setVisible(true);
            });
        }
    }

    public void disposeSplash() {
        if (EventQueue.isDispatchThread()) {
            dispose();
        } else {
            try {
                EventQueue.invokeAndWait(this::dispose);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
