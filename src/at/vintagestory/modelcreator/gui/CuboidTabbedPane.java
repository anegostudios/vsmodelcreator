package at.vintagestory.modelcreator.gui;

import java.awt.Component;

import javax.swing.JTabbedPane;

import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;

public class CuboidTabbedPane extends JTabbedPane
{
	private static final long serialVersionUID = 1L;

	protected IElementManager manager;

	public CuboidTabbedPane(IElementManager manager)
	{
		this.manager = manager;
	}

	public void updateValues()
	{
		for (int i = 0; i < getTabCount(); i++)
		{
			Component component = getComponentAt(i);
			if (component != null)
			{
				if (component instanceof IValueUpdater)
				{
					IValueUpdater updater = (IValueUpdater) component;
					updater.updateValues();
				}
			}
		}
	}
}
