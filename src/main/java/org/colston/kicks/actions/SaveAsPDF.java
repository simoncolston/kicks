package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksMain;
import org.colston.printpdf.PDFBoxPrintFontMap;
import org.colston.printpdf.PDFBoxPrintService;
import org.colston.sclib.gui.task.Task;
import org.colston.sclib.i18n.Messages;
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

public class SaveAsPDF extends AbstractAction {
    public static final String ACTION_COMMAND = "action.saveaspdf";

    private static final String MESSAGE_RESOURCE_PREFIX = "saveaspdf";
    private static final String SMALL_ICON_NAME = "SaveAs16.gif";
    private static final String LARGE_ICON_NAME = "SaveAs24.gif";

    private static final int MARGIN = 20;

    private File destination = null;

    public SaveAsPDF() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
        putValue(ActionManager.SMALL_ICON_NAME_KEY, SMALL_ICON_NAME);
        putValue(ActionManager.LARGE_ICON_NAME_KEY, LARGE_ICON_NAME);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        destination = Utils.chooseFile(KicksMain.getFrame(),
                Messages.get(this.getClass(), "saveaspdf.file.choose.title"),
                Messages.get(this.getClass(), "saveaspdf.file.choose.submit.button"),
                createPDFDestination(), Utils.PDF_FILE_FILTER, true, Utils.PDF_FILE_EXT);
        if (destination == null) {
            return;
        }

        Task<Object> task = new Task<>() {

            @Override
            protected Object doInBackground() throws Exception {
                Printable printable = KicksMain.getCanvas().getPrintable();
                DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
                PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
                aset.add(MediaSizeName.ISO_A4);
                aset.add(OrientationRequested.LANDSCAPE);

                aset.add(new Destination(destination.toURI()));

                String jobName = destination.getName();
                jobName = jobName.substring(0, jobName.lastIndexOf(".pdf"));
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

                Font font = new Font(KicksMain.FONT_NAME, Font.PLAIN, 1);

                PDFBoxPrintFontMap fontMap = new PDFBoxPrintFontMap();
                fontMap.add(font, KicksMain.class, KicksMain.FONT_RESOURCE_NAME);
                aset.add(fontMap);

                printJob.print(doc, aset);

                return null;
            }

            @Override
            protected void updateUI() {
            }
        };
        task.execute();
    }

    protected static File createPDFDestination() {
        File destination = KicksMain.getCurrentFile();
        if (destination == null) {
            File pwd = Utils.getWorkingDirectory();
            destination = new File(pwd, KicksMain.APPLICATION_NAME + Utils.PDF_FILE_EXT);
        }
        destination = Utils.fixFileExtension(destination, Utils.PDF_FILE_EXT);
        return destination;
    }
}
