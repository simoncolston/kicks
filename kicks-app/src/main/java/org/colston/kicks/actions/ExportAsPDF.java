package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksApp;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.render.KicksDocumentRenderer;
import org.colston.lib.gui.Utils;
import org.colston.lib.gui.task.Task;
import org.colston.lib.i18n.Messages;
import org.colston.printpdf.PDFBoxDocumentCreator;

import javax.print.attribute.standard.OrientationRequested;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class ExportAsPDF extends AbstractAction {
    public static final String ACTION_COMMAND = "action.export.pdf";

    private static final String MESSAGE_RESOURCE_PREFIX = "export.pdf";
    private static final String SMALL_ICON_NAME = "Export24.png";
    private static final String LARGE_ICON_NAME = "Export24.png";

    private File destination = null;

    public ExportAsPDF() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
        putValue(ActionManager.SMALL_ICON_NAME_KEY, SMALL_ICON_NAME);
        putValue(ActionManager.LARGE_ICON_NAME_KEY, LARGE_ICON_NAME);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        destination = Utils.chooseFile(KicksApp.frame(),
                Messages.get(this.getClass(), "export.pdf.file.choose.title"),
                Messages.get(this.getClass(), "export.pdf.file.choose.submit.button"),
                createPDFDestination(), Utils.PDF_FILE_FILTER, true, Utils.PDF_FILE_EXT);
        if (destination == null) {
            return;
        }

        Task<Object> task = getObjectTask();
        task.execute();
    }

    private Task<Object> getObjectTask() {
        KicksDocument document = KicksApp.canvas().getDocument();
        return new Task<>() {
            @Override
            protected Object doInBackground() throws Exception {
                doExport(document, destination);
                // open the pdf
                if (Desktop.isDesktopSupported() && KicksApp.settings().isOpenPdfAfterExport()) {
                    Desktop.getDesktop().open(destination);
                }
                return null;
            }

            @Override
            protected void updateUI() {
            }
        };
    }

    private void doExport(KicksDocument doc, File destination) throws IOException {
        PDFBoxDocumentCreator creator = PDFBoxDocumentCreator.getInstance()
                .orientation(OrientationRequested.LANDSCAPE);
        KicksDocumentRenderer renderer = KicksDocumentRenderer.create(doc)
                .romaji(KicksApp.settings().isRomaji())
                .includeVersion(KicksApp.settings().isIncludeVersion());
        creator.save(renderer, destination);
    }

    protected static File createPDFDestination() {
        File destination = KicksApp.getCurrentFile();
        if (destination == null) {
            File pwd = Utils.getWorkingDirectory();
            destination = new File(pwd, KicksApp.APPLICATION_NAME + Utils.PDF_FILE_EXT);
        }
        destination = Utils.fixFileExtension(destination, Utils.PDF_FILE_EXT);
        return destination;
    }
}
