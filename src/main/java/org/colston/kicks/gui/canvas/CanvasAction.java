package org.colston.kicks.gui.canvas;

import java.awt.event.ActionEvent;
import java.util.function.BiConsumer;

import javax.swing.KeyStroke;

import org.colston.gui.actions.ComponentAction;

class CanvasAction extends ComponentAction
{
	protected CanvasControl control;
	protected BiConsumer<CanvasControl, ActionEvent> handler;
	
	CanvasAction(BiConsumer<CanvasControl, ActionEvent> handler, KeyStroke[] keyStrokes, String actionCommand)
	{
		super(keyStrokes, actionCommand);
		this.handler = handler;
	}
	
	CanvasAction(KeyStroke[] keyStrokes, String actionCommand)
	{
		super(keyStrokes, actionCommand);
	}
	
	void setControl(CanvasControl cc)
	{
		this.control = cc;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (handler != null)
		{
			handler.accept(control, e);
		}
	}
}
