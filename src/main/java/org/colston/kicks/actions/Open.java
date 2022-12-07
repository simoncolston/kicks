package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksApp;
import org.colston.kicks.document.KicksDocument;
import org.colston.sclib.gui.task.Task;
import org.colston.sclib.i18n.Message;
import org.colston.sclib.i18n.Messages;
import org.colston.utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class Open extends AbstractAction {
    public static final String ACTION_COMMAND = "action.open";

    private static final String MESSAGE_RESOURCE_PREFIX = "open";
    private static final String SMALL_ICON_NAME = "Open24.png";
    private static final String LARGE_ICON_NAME = "Open24.png";

    public Open() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
        putValue(ActionManager.SMALL_ICON_NAME_KEY, SMALL_ICON_NAME);
        putValue(ActionManager.LARGE_ICON_NAME_KEY, LARGE_ICON_NAME);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!KicksApp.checkSaveChangesToCurrentDocument()) {
            return;
        }
        File f = Utils.chooseFile(KicksApp.frame(), Messages.get(Open.class, "open.file.choose.title"),
                Messages.get(Open.class, "open.file.choose.submit.button"),
                null, Utils.FILE_FILTER, false, null);
        if (f == null || !f.exists()) {
            return;
        }
        Task<KicksDocument> tw = new Task<>() {

            @Override
            protected KicksDocument doInBackground() throws Exception {
                return KicksApp.documentStore().load(f);
            }

            @Override
            protected void updateUI() {
                KicksApp.setDocument(f, get());
            }
        };
        tw.execute(new Message(Open.class, "open.progress.message"));
    }
}
