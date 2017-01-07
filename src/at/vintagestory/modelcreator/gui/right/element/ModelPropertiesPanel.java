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

public class ModelPropertiesPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private IElementManager manager;

	private JRadioButton ambientOcc;
	//private JButton btnParticle;

	public ModelPropertiesPanel(IElementManager manager)
	{
		this.manager = manager;
		setLayout(new GridLayout(2, 1, 0, 5));
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(221, 221, 228), 5), "<html><b>Model Properties</b></html>"));
		setMaximumSize(new Dimension(186, 80));
		initComponents();
		addComponents();
	}

	public void initComponents()
	{
		ambientOcc = ComponentUtil.createRadioButton("Ambient Occlusion", "Determine the light for each element");
		ambientOcc.setSelected(true);
		ambientOcc.addActionListener(a -> manager.setAmbientOcc(ambientOcc.isSelected()));

		/*btnParticle = new JButton("Particle");
		btnParticle.setIcon(Icons.texture);
		btnParticle.addActionListener(a ->
		{
			String texture = TextureManager.display(manager);
			if (texture != null)
			{
				manager.setParticle(texture);
				btnParticle.setText(texture);
			}
		});*/
	}

	public void addComponents()
	{
		add(ambientOcc);
		//add(btnParticle);
	}

	@Override
	public void updateValues(Element cube)
	{
		ambientOcc.setSelected(manager.getAmbientOcc());
		/*if (manager.getParticle() == null)
		{
			btnParticle.setText("Particle");
		}
		else
		{
			btnParticle.setText(manager.getParticle());
		}*/
	}
}
