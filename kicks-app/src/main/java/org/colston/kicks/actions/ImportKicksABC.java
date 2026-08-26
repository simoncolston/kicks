package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksApp;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.importer.Importer;
import org.colston.kicks.document.importer.ImporterFactory;
import org.colston.lib.gui.task.Task;
import org.colston.lib.i18n.Message;
import org.colston.lib.i18n.Messages;
import org.colston.utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Optional;

public class ImportKicksABC extends AbstractAction {
    public static final String ACTION_COMMAND = "action.import.kicksabc";

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
                null, Utils.KICKSABC_FILE_FILTER, false, null);
        if (f == null || !f.exists()) {
            return;
        }
        Task<KicksDocument> tw = new Task<>() {

            @Override
            protected KicksDocument doInBackground() throws Exception {
                Optional<Importer> importer = ImporterFactory.getImporter(f);
                if (importer.isEmpty()) {
                    throw new Exception("Importer not found");
                }
                return importer.get().importFile(f);
            }

            @Override
            protected void updateUI() {
                KicksApp.setDocument(f, get());
            }
        };
        tw.execute(new Message(ImportKicksABC.class, "import.kicksabc.progress.message"));
    }
}
