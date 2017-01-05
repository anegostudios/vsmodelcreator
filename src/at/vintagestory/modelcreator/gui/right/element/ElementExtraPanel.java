package at.vintagestory.modelcreator.gui.right.element;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import at.vintagestory.modelcreator.gui.ComponentUtil;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;

public class ElementExtraPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private IElementManager manager;

	private JRadioButton btnShade;

	public ElementExtraPanel(IElementManager manager)
	{
		this.manager = manager;
		setLayout(new GridLayout(1, 2));
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(221, 221, 228), 5), "<html><b>Extras</b></html>"));
		setMaximumSize(new Dimension(186, 50));
		initComponents();
		addComponents();
	}

	public void initComponents()
	{
		btnShade = ComponentUtil.createRadioButton("Shade", "<html>Determines if shadows should be rendered<br>Default: On</html>");
		btnShade.addActionListener(e ->
		{
			Element elem = manager.getSelectedElement();
			if (elem != null) elem.setShade(btnShade.isSelected());
		});
	}

	public void addComponents()
	{
		add(btnShade);
	}

	@Override
	public void updateValues(Element cube)
	{
		if (cube != null)
		{
			btnShade.setEnabled(true);
			btnShade.setSelected(cube.isShaded());
		}
		else
		{
			btnShade.setEnabled(false);
			btnShade.setSelected(false);
		}
	}
}
