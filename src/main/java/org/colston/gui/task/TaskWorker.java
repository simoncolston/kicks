package org.colston.gui.task;

import java.awt.Container;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingWorker;

import org.colston.sclib.i18n.Message;

public abstract class TaskWorker<T> extends SwingWorker<T, TaskProgressProvider>
{
	// TODO: This is to show a dialog box if there is an exception thrown
	@SuppressWarnings("unused")
	private JFrame frame;
	private TaskPanel taskPanel;

	public TaskWorker(JFrame frame, JComponent component)
	{
		this.frame = frame;

		Container c = component;
		while (c != null)
		{

			if (c instanceof TaskPanel)
			{

				this.taskPanel = (TaskPanel) c;
				break;
			}
			c = c.getParent();
		}
	}

	public void executeTask() 
	{
		executeTask(new Message(TaskWorker.class, "task.default.progress.message"));
	}
	
	public void executeTask(Message initialMessage)
	{
		if (taskPanel != null)
		{
			taskPanel.showGlass(initialMessage);
		}
		execute();
	}

	@Override
	protected void process(List<TaskProgressProvider> chunks)
	{
		processTaskProgress(chunks);
	}

	private void processTaskProgress(List<TaskProgressProvider> chunks)
	{
		if (taskPanel == null) return;

		for (TaskProgressProvider chunk : chunks)
		{

			taskPanel.updateProgress(chunk);
		}
	}

	protected void doneTask()
	{
	}

	@Override
	protected void done()
	{
		try
		{
			get();
			doneTask();
			if (taskPanel != null)
			{
				taskPanel.removeGlass();
			}
		} catch (InterruptedException | ExecutionException e)
		{
			doneException(e);
		}
	}

	protected void doneException(Exception e)
	{
		// TODO: show error in dialog by default
		e.printStackTrace();
		if (taskPanel != null)
		{
			taskPanel.removeGlass();
		}
	}
}
