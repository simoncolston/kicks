package org.colston.kicks.gui.canvas;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

class NoteAction extends CanvasAction
{
	private int string;
	private int placement;

	NoteAction(KeyStroke[] keyStrokes, String actionCommand, int string, int placement)
	{
		super(keyStrokes, actionCommand);
		this.string = string;
		this.placement = placement;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		control.addNote(string, placement, (e.getModifiers() & ActionEvent.SHIFT_MASK) > 0);
	}
	
	public int getString()
	{
		return string;
	}
	
	public int getPlacement()
	{
		return placement;
	}
}
