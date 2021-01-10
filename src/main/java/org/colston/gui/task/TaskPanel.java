package org.colston.gui.task;

import org.colston.sclib.i18n.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TaskPanel extends JLayeredPane {
    private static final Integer BASE_LAYER = 0;
    private static final Integer GLASS_LAYER = 1;

    private final GlassPane glass;
    private final JComponent component;

    public TaskPanel(JComponent component) {
        this.glass = new GlassPane();

        this.component = component;
        add(component, BASE_LAYER);
        component.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setSizes();
            }
        });
        setSizes();
    }

    private void setSizes() {
        Dimension size = component.getPreferredSize();
        component.setBounds(0, 0, size.width, size.height);

        glass.setPreferredSize(size);
        glass.setBounds(0, 0, size.width, size.height);
        glass.setMinimumSize(component.getMinimumSize());

        setPreferredSize(size);
        setMinimumSize(component.getMinimumSize());
        setMaximumSize(component.getMaximumSize());

        revalidate();
    }

    public void showGlass(Message message) {
        glass.initialise(message);
        add(glass, GLASS_LAYER);

        fireTaskStarted();
        repaint();
    }

    public void removeGlass() {
        fireTaskEnded();

        remove(glass);
        repaint();
    }

    private void fireTaskStarted() {
        for (TaskListener listener : TaskListeners.getAll()) {
            listener.taskStarted();
        }
    }

    private void fireTaskEnded() {
        for (TaskListener listener : TaskListeners.getAll()) {
            listener.taskEnded();
        }
    }

    public void updateProgress(TaskProgressProvider progress) {
        glass.updateProgress(progress);
    }
}
