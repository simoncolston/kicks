package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksApp;
import org.colston.lib.gui.task.Task;
import org.colston.lib.i18n.Message;
import org.colston.utils.Utils;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterJob;
import java.io.File;

public class Print extends AbstractAction {
    public static final String ACTION_COMMAND = "action.print";

    private static final String MESSAGE_RESOURCE_PREFIX = "print";
    private static final String SMALL_ICON_NAME = "Print24.png";
    private static final String LARGE_ICON_NAME = "Print24.png";

    public Print() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
        putValue(ActionManager.SMALL_ICON_NAME_KEY, SMALL_ICON_NAME);
        putValue(ActionManager.LARGE_ICON_NAME_KEY, LARGE_ICON_NAME);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(KicksApp.canvas().getPrintable());

        // create a meaningful job name
        File pdfDestination = ExportAsPDF.createPDFDestination();
        String jobName = pdfDestination.getName();
        jobName = jobName.substring(0, jobName.lastIndexOf(Utils.PDF_FILE_EXT));
        printJob.setJobName(jobName);

        PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
        attributeSet.add(MediaSizeName.ISO_A4);
        attributeSet.add(OrientationRequested.LANDSCAPE);
        attributeSet.add(new JobName(jobName, null));

        if (printJob.printDialog(attributeSet)) {
            Task<Object> tw = new Task<>() {
                @Override
                protected Object doInBackground() throws Exception {
                    //TODO: add a print job listener to handle printer problems

                    printJob.print(attributeSet);
                    return null;
                }

                @Override
                protected void updateUI() {
                }
            };
            tw.execute(new Message(getClass(), "print.progress.message"));
        }
    }
}
