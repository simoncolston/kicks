package org.colston.gui.task;

import org.colston.sclib.i18n.Message;

public class TaskProgress implements TaskProgressProvider {
    private final boolean indeterminate;
    private final int taskProgress;
    private MessageAction messageAction;
    private Message message;

    /**
     * Indeterminate task with no message.
     */
    public TaskProgress() {
        this.indeterminate = true;
        this.taskProgress = 0;
        this.messageAction = MessageAction.NONE;
    }

    /**
     * Task that has made some progress.
     *
     * @param taskProgress amount of progress between
     *                     <code>TaskProgressProvider.MAX_VALUE</code> and
     *                     <code>TaskProgressProvider.MIN_VALUE</code>
     */
    public TaskProgress(int taskProgress) {
        this.indeterminate = false;
        this.taskProgress = taskProgress > MAX_VALUE ? MAX_VALUE : (Math.max(taskProgress, MIN_VALUE));
        this.messageAction = MessageAction.NONE;
    }

    @Override
    public int getTaskProgress() {
        return taskProgress;
    }

    @Override
    public boolean isTaskProgressIndeterminate() {
        return indeterminate;
    }

    @Override
    public MessageAction getTaskMessageAction() {
        return messageAction;
    }

    @Override
    public Message getTaskMessage() {
        return message;
    }

    public static TaskProgress createIndeterminateProgress(Message m) {
        TaskProgress tp = new TaskProgress();
        tp.messageAction = MessageAction.UPDATE;
        tp.message = m;
        return tp;
    }

    public static TaskProgress createMessageProgress(int progress, Message m) {
        TaskProgress tp = new TaskProgress(progress);
        tp.messageAction = MessageAction.UPDATE;
        tp.message = m;
        return tp;
    }

    public static TaskProgress createClearMessageProgress(int progress) {
        TaskProgress tp = new TaskProgress(progress);
        tp.messageAction = MessageAction.CLEAR;
        return tp;
    }
}
