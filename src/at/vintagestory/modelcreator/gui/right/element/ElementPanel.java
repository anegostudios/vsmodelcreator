package at.vintagestory.modelcreator.gui.right.element;

import java.awt.Dimension;
import javax.swing.Box;
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
	
//	private ModelPropertiesPanel panelGlobal;
	

	public ElementPanel(IElementManager manager)
	{
		this.manager = manager;
		//setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
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
//		panelGlobal = new ModelPropertiesPanel(manager);
		
	}

	public void addComponents()
	{
		add(Box.createRigidArea(new Dimension(188, 5)));
		add(panelSize);
		add(panelPosition);
		add(panelOrigin);
		add(panelRotation);
		add(panelElementProperties);
		//add(new JSeparator(JSeparator.HORIZONTAL));
		//add(panelGlobal);
	}

	@Override
	public void updateValues()
	{
		panelSize.updateValues();
		panelPosition.updateValues();
		panelElementProperties.updateValues();
		panelOrigin.updateValues();
		panelRotation.updateValues();
		//panelGlobal.updateValues();
	}
}
