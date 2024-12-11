package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksApp;
import org.colston.printpdf.PDFBoxPrintFontMap;
import org.colston.printpdf.PDFBoxPrintService;
import org.colston.lib.gui.task.Task;
import org.colston.lib.i18n.Messages;
import org.colston.utils.Utils;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.print.Printable;
import java.io.File;

public class ExportAsPDF extends AbstractAction {
    public static final String ACTION_COMMAND = "action.export.pdf";

    private static final String MESSAGE_RESOURCE_PREFIX = "export.pdf";
    private static final String SMALL_ICON_NAME = "Export24.png";
    private static final String LARGE_ICON_NAME = "Export24.png";

    private static final int MARGIN = 20;

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

        Printable printable = KicksApp.canvas().getPrintable();
        Task<Object> task = new Task<>() {
            @Override
            protected Object doInBackground() throws Exception {
                DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
                PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
                aset.add(MediaSizeName.ISO_A4);
                aset.add(OrientationRequested.LANDSCAPE);

                aset.add(new Destination(destination.toURI()));

                String jobName = destination.getName();
                jobName = jobName.substring(0, jobName.lastIndexOf(Utils.PDF_FILE_EXT));
                aset.add(new JobName(jobName, null));

                //TODO:  The whole 'lookup print service' thing - looks fun!
                PrintService pservice = new PDFBoxPrintService();

                DocPrintJob printJob = pservice.createPrintJob();
                Doc doc = new SimpleDoc(printable, flavor, /* daset */ null);

                MediaSize mediaSize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A4);
                float width = mediaSize.getX(Size2DSyntax.MM) - (MARGIN * 2);
                float height = mediaSize.getY(Size2DSyntax.MM) - (MARGIN * 2);
                MediaPrintableArea mpa = new MediaPrintableArea(MARGIN, MARGIN, width, height, MediaPrintableArea.MM);
                aset.add(mpa);

                Font font = new Font(KicksApp.FONT_NAME, Font.PLAIN, 1);

                PDFBoxPrintFontMap fontMap = new PDFBoxPrintFontMap();
                fontMap.add(font, KicksApp.class, KicksApp.FONT_RESOURCE_NAME);
                aset.add(fontMap);

                printJob.print(doc, aset);

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
        task.execute();
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
