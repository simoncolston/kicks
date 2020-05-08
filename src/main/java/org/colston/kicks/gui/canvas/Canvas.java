package org.colston.kicks.gui.canvas;

import java.awt.Component;
import java.awt.print.Printable;

import javax.swing.JComponent;

import org.colston.gui.actions.ActionProvider;
import org.colston.gui.task.TaskListener;
import org.colston.kicks.document.KicksDocument;

public interface Canvas
{
	void requestFocusInWindow();

	JComponent getContainer();
	
	JComponent getComponent();
	
	Component getInputComponent();
	
	ActionProvider getActionProvider();
	
	TaskListener getTaskListener();

	Printable getPrintable();
	
	KicksDocument getDocument();
	
	void setDocument(KicksDocument doc);

	boolean isDocumentChanged();

	void documentSaved();
	
	void undo();
	
	void redo();
}
