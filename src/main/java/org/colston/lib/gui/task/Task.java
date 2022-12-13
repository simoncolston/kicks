package org.colston.lib.gui.task;

import org.colston.lib.i18n.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.util.concurrent.ExecutionException;

/**
 * Swing worker that blocks user input while working.
 *
 * @param <T> type of object returned from the background thread process
 * @author simon
 */
public abstract class Task<T> {
    private static final Config config = new Config();
    private static final AWTEventListener EVENT_CONSUMER = e -> ((InputEvent) e).consume();

    private final SwingWorker<T, TaskProgressProvider> worker = new SwingWorker<>() {
        @Override
        protected T doInBackground() throws Exception {
            return Task.this.doInBackground();
        }

        @Override
        protected void done() {
            taskComplete();
        }
    };

    private TaskGlassPane glassPane;

    /**
     * Initialises the glass pane using the setting in {@link Config}.
     */
    public Task() {
        init(config.getRootPane());
    }

    /**
     * Find the {@link RootPaneContainer} that is an ancestor of the given component and then initialise the glass pane.
     *
     * @param component component contained in the {@link RootPaneContainer}
     */
    public Task(JComponent component) {
        Component c = component;
        while (!(c instanceof RootPaneContainer)) {
            c = c.getParent();
        }
        init((RootPaneContainer) c);
    }

    /**
     * Initialise the glass pane using the given {@link RootPaneContainer}.
     *
     * @param rpc the {@link RootPaneContainer}
     */
    public Task(RootPaneContainer rpc) {
        init(rpc);
    }

    private void init(RootPaneContainer rootPane) {
        if (rootPane.getGlassPane() instanceof TaskGlassPane) {
            glassPane = (TaskGlassPane) rootPane.getGlassPane();
        } else {
            glassPane = new TaskGlassPane();
            rootPane.setGlassPane(glassPane);
        }
    }

    /**
     * Execute the task.
     */
    public void execute() {
        showGlass();
        worker.execute();
    }

    public void execute(Message message) {
        //TODO: Display message
        execute();
    }

    /**
     * Get the object supplied by the background part of the task.
     *
     * @return the object
     */
    public T get() {
        try {
            return worker.get();
        } catch (InterruptedException | ExecutionException ignored) {
			/*
			This has probably already been dealt with in taskComplete().
			Returns null anyway.
			 */
        }
        return null;
    }

    /**
     * Overridden with task to be performed in the background.
     *
     * @return the result of the processing
     * @throws Exception error during task
     */
    protected abstract T doInBackground() throws Exception;

    /**
     * Called <em>on the event thread</em> once the background task has been performed.
     */
    protected abstract void updateUI();

    /**
     * Error occurred in the background thread.  Can be overridden for custom behaviour.
     *
     * @param e the exception thrown by the background task
     */
    protected void taskException(Exception e) {
        //TODO: Log this
        e.printStackTrace();
        removeGlass();
    }

    private void showGlass() {
        glassPane.setVisible(true);
        // consume all keyboard activity
        Toolkit.getDefaultToolkit().addAWTEventListener(EVENT_CONSUMER, AWTEvent.KEY_EVENT_MASK);
    }

    private void removeGlass() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(EVENT_CONSUMER);
        glassPane.setVisible(false);
    }

    private void taskComplete() {
        try {
            worker.get(); //doing this to find out if there was an exception
            updateUI();
            removeGlass();
        } catch (InterruptedException | ExecutionException e) {
            taskException(e);
        }
    }

    public static Config getConfig() {
        return config;
    }

    public static class Config {
        private RootPaneContainer rootPane;

        public RootPaneContainer getRootPane() {
            return rootPane;
        }

        public Config setRootPane(RootPaneContainer rootPane) {
            this.rootPane = rootPane;
            return this;
        }
    }
}
