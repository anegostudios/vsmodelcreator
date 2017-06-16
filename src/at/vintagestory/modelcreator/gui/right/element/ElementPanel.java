package at.vintagestory.modelcreator.gui.right.element;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;

public class ElementPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private IElementManager manager;

	private ElementSizePanel panelSize;
	private ElementPositionPanel panelPosition;
	private ElementRotationOriginPanel panelOrigin;
	private ElementRotationPanel panelRotation;
	private ElementPropertiesPanel panelElementProperties;
	

	public ElementPanel(IElementManager manager)
	{
		this.manager = manager;
		initComponents();
		addComponents();
	}

	public void initComponents()
	{
		panelSize = new ElementSizePanel(manager);
		panelPosition = new ElementPositionPanel(manager);
		panelOrigin = new ElementRotationOriginPanel(manager);
		panelRotation = new ElementRotationPanel(manager);
		panelElementProperties = new ElementPropertiesPanel(manager);
	}

	public void addComponents()
	{
		add(Box.createRigidArea(new Dimension(188, 5)));
		add(panelSize);
		add(panelPosition);
		add(panelOrigin);
		add(panelRotation);
		add(panelElementProperties);
	}

	@Override
	public void updateValues(JComponent byGuiElem)
	{
		panelSize.updateValues(byGuiElem);
		panelPosition.updateValues(byGuiElem);
		panelElementProperties.updateValues(byGuiElem);
		panelOrigin.updateValues(byGuiElem);
		panelRotation.updateValues(byGuiElem);
	}
}
