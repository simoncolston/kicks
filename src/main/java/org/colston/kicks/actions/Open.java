package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksMain;
import org.colston.kicks.document.KicksDocument;
import org.colston.sclib.gui.task.Task;
import org.colston.sclib.i18n.Message;
import org.colston.sclib.i18n.Messages;
import org.colston.utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Open extends AbstractAction {
    public static final String ACTION_COMMAND = "action.open";

    private static final String MESSAGE_RESOURCE_PREFIX = "open";
    private static final String SMALL_ICON_NAME = "Open16.gif";
    private static final String LARGE_ICON_NAME = "Open24.gif";

    public Open() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
        putValue(ActionManager.SMALL_ICON_NAME_KEY, SMALL_ICON_NAME);
        putValue(ActionManager.LARGE_ICON_NAME_KEY, LARGE_ICON_NAME);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File f = Utils.chooseFile(KicksMain.getFrame(), Messages.get(Open.class, "open.file.choose.title"),
                Messages.get(Open.class, "open.file.choose.submit.button"),
                null, Utils.FILE_FILTER, false, null);
        if (f == null || !f.exists()) {
            return;
        }
        Task<KicksDocument> tw = new Task<>() {

            @Override
            protected KicksDocument doInBackground() throws Exception {
                return loadDocument(f);
            }

            @Override
            protected void updateUI() {
                setDocument(f, get());
            }
        };
        tw.execute(new Message(Open.class, "open.progress.message"));
    }

    /**
     * Loads a document from the file and sets it on the canvas.
     * <p>
     * <em>NOTE: This is not event thread safe!</em>  This should only be called <em>off</em> the event thread
     * and only if you know what the consequences are.<br><br>
     * It is only intended to load a document from a file at program initialisation.
     * @param file file to load the document from
     * @throws Exception error during load
     */
    public void openDocumentFromFile(File file) throws Exception {
        setDocument(file, loadDocument(file));
    }

    private KicksDocument loadDocument(File file) throws Exception {
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            return KicksMain.getDocumentStore().load(is);
        }
    }

    private void setDocument(File file, KicksDocument document) {
        KicksMain.getCanvas().setDocument(document);
        KicksMain.setCurrentFile(file);
    }
}
