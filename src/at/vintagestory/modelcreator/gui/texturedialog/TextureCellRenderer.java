package at.vintagestory.modelcreator.gui.texturedialog;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import at.vintagestory.modelcreator.ModelCreator;

public class TextureCellRenderer extends DefaultListCellRenderer
{
	private static final long serialVersionUID = 1L;

	private JLabel lbl = new JLabel();

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		lbl.setIcon(ModelCreator.currentProject.getIcon((String) list.getModel().getElementAt(index)));
		lbl.revalidate();
		if (isSelected)
		{
			lbl.setOpaque(true);
			lbl.setEnabled(false);
		}
		else
		{
			lbl.setOpaque(false);
			lbl.setEnabled(true);
		}
		return lbl;
	}
}
