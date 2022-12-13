package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksApp;
import org.colston.kicks.document.KicksDocument;
import org.colston.lib.gui.task.Task;
import org.colston.lib.i18n.Message;
import org.colston.lib.i18n.Messages;
import org.colston.utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Save extends AbstractAction {
    public static final String ACTION_COMMAND = "action.save";

    private static final String MESSAGE_RESOURCE_PREFIX = "save";
    private static final String SMALL_ICON_NAME = "Save24.png";
    private static final String LARGE_ICON_NAME = "Save24.png";

    private File file;

    public Save() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
        putValue(ActionManager.SMALL_ICON_NAME_KEY, SMALL_ICON_NAME);
        putValue(ActionManager.LARGE_ICON_NAME_KEY, LARGE_ICON_NAME);
    }

    /**
     * Save the current document but do not update the UI when finished.
     * @return <code>true</code> if not aborted by user
     */
    public boolean save() {
        return doSave(false, null);
    }

    public void saveAs() {doSave(true, () -> KicksApp.documentSaved(file));}
    /**
     * Save the current document and update run the runnable on the event thread.
     * This is to allow the use of {@link #save()} elsewhere without the usual post-processing for a normal save action.
     * @param alwaysAskForFile if <code>true</code> always ask for file name (for Save As...)
     * @param runnable event thread action
     * @return <code>true</code> if not aborted by user
     */
    private boolean doSave(boolean alwaysAskForFile, Runnable runnable) {
        file = KicksApp.getCurrentFile();
        if (alwaysAskForFile || file == null) {
            file = Utils.chooseFile(KicksApp.frame(),
                    Messages.get(Save.class, "save.file.choose.title"),
                    Messages.get(Save.class, "save.file.choose.submit.button"),
                    null, Utils.FILE_FILTER, true, Utils.FILE_EXT);
            if (file == null) {
                return false;
            }
        }
        // take the reference to the document on the event thread!
        KicksDocument doc = KicksApp.canvas().getDocument();
        Task<Object> tw = new Task<>() {
            @Override
            protected Object doInBackground() throws Exception {
                try (OutputStream is = new BufferedOutputStream(new FileOutputStream(file))) {
                    KicksApp.documentStore().save(doc, is);
                }
                return null;
            }

            @Override
            protected void updateUI() {
                if (runnable != null) {
                    runnable.run();
                }
            }
        };
        tw.execute(new Message(getClass(), "save.progress.message"));
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        doSave(false, () -> KicksApp.documentSaved(file));
    }
}
