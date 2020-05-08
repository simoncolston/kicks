package org.colston.kicks.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.AbstractAction;

import org.colston.gui.actions.ActionManager;
import org.colston.gui.task.TaskWorker;
import org.colston.i18n.Message;
import org.colston.i18n.Messages;
import org.colston.kicks.KicksMain;
import org.colston.utils.Utils;

public class Save extends AbstractAction
{
	public static final String ACTION_COMMAND = "action.save";

	private static final String MESSAGE_RESOURCE_PREFIX = "save";
	private static final String SMALL_ICON_NAME = "Save16.gif";
	private static final String LARGE_ICON_NAME = "Save24.gif";
	
	private File file;
	
	public Save()
	{
		putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
		putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
		putValue(ActionManager.SMALL_ICON_NAME_KEY, SMALL_ICON_NAME);
		putValue(ActionManager.LARGE_ICON_NAME_KEY, LARGE_ICON_NAME);
	}

	public boolean save()
	{
		file = KicksMain.getCurrentFile();
		if (file == null)
		{
			file = Utils.chooseFile(KicksMain.getFrame(), 
					Messages.get(Save.class, "save.file.choose.title"),
					Messages.get(Save.class, "save.file.choose.submit.button"),
					null, Utils.FILE_FILTER, true, Utils.FILE_EXT); 
			if (file == null)
			{
				return false;
			}
		}
		TaskWorker<Object> tw = new TaskWorker<Object>(KicksMain.getFrame(), KicksMain.getCanvas().getComponent())
		{
			@Override
			protected Object doInBackground() throws Exception
			{
				try (OutputStream is = new BufferedOutputStream(new FileOutputStream(file)))
				{
					KicksMain.getDocumentStore().save(KicksMain.getCanvas().getDocument(), is);
				}
				return null;
			}
			
			@Override
			protected void doneTask()
			{
				KicksMain.getCanvas().documentSaved();
				KicksMain.setCurrentFile(file);
			}
			
		};
		tw.executeTask(new Message(getClass(), "save.progress.message"));
		return true;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		save();
	}
}
