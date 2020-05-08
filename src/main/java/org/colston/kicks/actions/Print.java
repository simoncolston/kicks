package org.colston.kicks.actions;

import java.awt.event.ActionEvent;
import java.awt.print.PrinterJob;
import java.io.File;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.AbstractAction;

import org.colston.gui.actions.ActionManager;
import org.colston.gui.task.TaskWorker;
import org.colston.i18n.Message;
import org.colston.kicks.KicksMain;
import org.colston.utils.Utils;

public class Print extends AbstractAction
{
	public static final String ACTION_COMMAND = "action.print";

	private static final String MESSAGE_RESOURCE_PREFIX = "print";
	private static final String SMALL_ICON_NAME = "Print16.gif";
	private static final String LARGE_ICON_NAME = "Print24.gif";

	public Print()
	{
		putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
		putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
		putValue(ActionManager.SMALL_ICON_NAME_KEY, SMALL_ICON_NAME);
		putValue(ActionManager.LARGE_ICON_NAME_KEY, LARGE_ICON_NAME);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setPrintable(KicksMain.getCanvas().getPrintable());
		
		// create a meaningful job name
		File pdfDestination = SaveAsPDF.createPDFDestination();
		String jobName = pdfDestination.getName();
		jobName = jobName.substring(0, jobName.lastIndexOf(Utils.PDF_FILE_EXT));
		printJob.setJobName(jobName);
		
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		pras.add(MediaSizeName.ISO_A4);
		pras.add(OrientationRequested.LANDSCAPE);
		pras.add(new JobName(jobName, null));
		
		if (printJob.printDialog(pras))
		{
			TaskWorker<Object> tw = new TaskWorker<Object>(KicksMain.getFrame(), KicksMain.getCanvas().getComponent())
			{
				@Override
				protected Object doInBackground() throws Exception
				{
					//TODO: add a print job listener to handle printer problems
					
					printJob.print(pras);
					return null;
				}
			};
			tw.executeTask(new Message(getClass(), "print.progress.message"));
		}
	}
}
