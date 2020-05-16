package org.colston.kicks.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;

import org.colston.gui.actions.ActionManager;
import org.colston.gui.task.TaskWorker;
import org.colston.kicks.KicksMain;
import org.colston.kicks.document.KicksDocument;
import org.colston.sclib.i18n.Message;
import org.colston.sclib.i18n.Messages;
import org.colston.utils.Utils;

public class Open extends AbstractAction
{
	public static final String ACTION_COMMAND = "action.open";

	private static final String MESSAGE_RESOURCE_PREFIX = "open";
	private static final String SMALL_ICON_NAME = "Open16.gif";
	private static final String LARGE_ICON_NAME = "Open24.gif";

	public Open()
	{
		putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
		putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
		putValue(ActionManager.SMALL_ICON_NAME_KEY, SMALL_ICON_NAME);
		putValue(ActionManager.LARGE_ICON_NAME_KEY, LARGE_ICON_NAME);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		File f = Utils.chooseFile(KicksMain.getFrame(), Messages.get(Open.class, "open.file.choose.title"),
				Messages.get(Open.class, "open.file.choose.submit.button"),
				null, Utils.FILE_FILTER, false, null); 
		if (f == null || !f.exists())
		{
			return;
		}
		TaskWorker<KicksDocument> tw = new TaskWorker<KicksDocument>(KicksMain.getFrame(), 
				KicksMain.getCanvas().getComponent())
		{

			@Override
			protected KicksDocument doInBackground() throws Exception
			{
				try (InputStream is = new BufferedInputStream(new FileInputStream(f)))
				{
					return KicksMain.getDocumentStore().load(is);
				}
			}

			@Override
			protected void doneTask()
			{
				try
				{
					KicksMain.getCanvas().setDocument(get());
					KicksMain.setCurrentFile(f);
				} 
				catch (InterruptedException | ExecutionException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		tw.executeTask(new Message(Open.class, "open.progress.message"));
	}
}
