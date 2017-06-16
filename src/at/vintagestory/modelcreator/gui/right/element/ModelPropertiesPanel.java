package at.vintagestory.modelcreator.gui.right.element;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Start;
import at.vintagestory.modelcreator.gui.ComponentUtil;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;

public class ModelPropertiesPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;


	private JRadioButton ambientOcc;

	public ModelPropertiesPanel(IElementManager manager)
	{
		setLayout(new GridLayout(2, 1, 0, 5));
		setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>Model Properties</b></html>"));
		setMaximumSize(new Dimension(186, 80));
		initComponents();
		addComponents();
	}

	public void initComponents()
	{
		ambientOcc = ComponentUtil.createRadioButton("Ambient Occlusion", "Determine the light for each element");
		ambientOcc.setSelected(true);
		ambientOcc.addActionListener(a -> ModelCreator.currentProject.AmbientOcclusion = ambientOcc.isSelected());
	}

	public void addComponents()
	{
		add(ambientOcc);
	}

	@Override
	public void updateValues(JComponent byGuiElem)
	{
		ambientOcc.setSelected(ModelCreator.currentProject.AmbientOcclusion);
	}
}
