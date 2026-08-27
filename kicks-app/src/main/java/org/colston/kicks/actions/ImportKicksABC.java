package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksApp;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.persistence.DocumentStore;
import org.colston.kicks.document.persistence.DocumentStoreFactory;
import org.colston.lib.gui.SingleExtensionFileFilter;
import org.colston.lib.gui.Utils;
import org.colston.lib.gui.task.Task;
import org.colston.lib.i18n.Message;
import org.colston.lib.i18n.Messages;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Optional;

public class ImportKicksABC extends AbstractAction {
    public static final String ACTION_COMMAND = "action.import.kicksabc";
    public static final String KICKSABC_FILE_EXT = ".kicksabc";
    public static final FileFilter KICKSABC_FILE_FILTER = new SingleExtensionFileFilter(KICKSABC_FILE_EXT, "kicksabc");

    private static final String MESSAGE_RESOURCE_PREFIX = "import.kicksabc";

    public ImportKicksABC() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!KicksApp.checkSaveChangesToCurrentDocument()) {
            return;
        }
        File f = Utils.chooseFile(KicksApp.frame(), Messages.get(ImportKicksABC.class, "import.kicksabc.file.choose.title"),
                Messages.get(ImportKicksABC.class, "import.kicksabc.file.choose.submit.button"),
                null, KICKSABC_FILE_FILTER, false, null);
        if (f == null || !f.exists()) {
            return;
        }
        Task<KicksDocument> tw = new Task<>() {

            @Override
            protected KicksDocument doInBackground() throws Exception {
                Optional<DocumentStore> store = DocumentStoreFactory.create(f);
                if (store.isEmpty()) {
                    throw new Exception("Importer not found");
                }
                return store.get().load(f);
            }

            @Override
            protected void updateUI() {
                KicksApp.setDocument(f, get());
            }
        };
        tw.execute(new Message(ImportKicksABC.class, "import.kicksabc.progress.message"));
    }
}
