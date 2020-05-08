package org.colston.gui.task;

import org.colston.i18n.Message;

public interface TaskProgressProvider
{
	public enum MessageAction
	{
		UPDATE, // Change the message to the one provided
		CLEAR,  // Clear the message
		NONE    // Leave the message as it currently is
	}

	public int MAX_VALUE = 100;
	public int MIN_VALUE = 0;

	public boolean isTaskProgressIndeterminate();

	public int getTaskProgress();

	public MessageAction getTaskMessageAction();

	public Message getTaskMessage();
}
