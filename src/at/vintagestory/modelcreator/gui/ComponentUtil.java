package at.vintagestory.modelcreator.gui;

import javax.swing.JRadioButton;

public class ComponentUtil
{
	public static JRadioButton createRadioButton(String name, String toolTip)
	{
		JRadioButton button = new JRadioButton(name);
		button.setToolTipText(toolTip);
		button.setIcon(Icons.light_on);
		button.setRolloverIcon(Icons.light_on);
		button.setSelectedIcon(Icons.light_off);
		button.setRolloverSelectedIcon(Icons.light_off);
		return button;
	}
}
