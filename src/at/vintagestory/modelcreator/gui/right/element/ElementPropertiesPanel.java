package at.vintagestory.modelcreator.gui.right.element;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import at.vintagestory.modelcreator.Start;
import at.vintagestory.modelcreator.gui.ComponentUtil;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;

public class ElementPropertiesPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private IElementManager manager;

	private JRadioButton btnShade;

	public ElementPropertiesPanel(IElementManager manager)
	{
		this.manager = manager;
		setLayout(new GridLayout(1, 2));
		setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>Element Properties</b></html>"));
		setPreferredSize(new Dimension(200, 50));
		setAlignmentX(JPanel.LEFT_ALIGNMENT);
		initComponents();
		addComponents();
	}

	public void initComponents()
	{
		btnShade = ComponentUtil.createRadioButton("Shade", "<html>Determines if shadows should be rendered<br>Default: On</html>");
		btnShade.addActionListener(e ->
		{
			Element elem = manager.getCurrentElement();
			if (elem != null) elem.setShade(btnShade.isSelected());
		});
	}

	public void addComponents()
	{
		add(btnShade);
	}

	@Override
	public void updateValues()
	{
		Element cube = manager.getCurrentElement();
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
