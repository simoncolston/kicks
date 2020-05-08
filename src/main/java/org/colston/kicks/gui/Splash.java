package org.colston.kicks.gui;

import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JWindow;

/**
 * @author simon
 * 
 */
public class Splash extends JWindow
{

	public Splash(Frame frame, Icon splashIcon)
	{
		super(frame);
		setName(frame.getTitle());
		JLabel l = new JLabel(splashIcon);
		getContentPane().add(l);
	}

	public void showSplash()
	{

		if (EventQueue.isDispatchThread())
		{
			pack();
			setLocationRelativeTo(getOwner());
			setVisible(true);
		} 
		else
		{

			EventQueue.invokeLater(() ->
			{
				pack();
				setLocationRelativeTo(getOwner());
				setVisible(true);
			});
		}
	}

	public void disposeSplash()
	{
		if (EventQueue.isDispatchThread())
		{
			dispose();
		} 
		else
		{
			try
			{
				EventQueue.invokeAndWait(() -> dispose());
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}
