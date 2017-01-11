package at.vintagestory.modelcreator.gui.right.face;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Start;
import at.vintagestory.modelcreator.gui.ComponentUtil;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;

public class FacePropertiesPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private IElementManager manager;

	private JPanel horizontalBox;
	private JRadioButton boxEnabled;
	private JRadioButton boxAutoUV;
	private JTextField glowValue;

	public FacePropertiesPanel(IElementManager manager)
	{
		this.manager = manager;
		setLayout(new BorderLayout(0, 5));
		setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>Properties</b></html>"));
		setMaximumSize(new Dimension(186, 100));
		initComponents();
		addComponents();
	}

	public void initComponents()
	{
		horizontalBox = new JPanel(new GridLayout(0, 2));
		
		boxEnabled = ComponentUtil.createRadioButton("Enable","<html>Determines if face should be rendered<br>Default: On</html>");
		boxEnabled.addActionListener(e ->
		{
			manager.getCurrentElement().getSelectedFace().setEnabled(boxEnabled.isSelected());
		});
		boxAutoUV = ComponentUtil.createRadioButton("Auto UV", "<html>Determines if UV end coordinates should be set based on element size<br>Default: On</html>");
		boxAutoUV.addActionListener(e ->
		{
			manager.getCurrentElement().getSelectedFace().setAutoUVEnabled(boxAutoUV.isSelected());
			manager.getCurrentElement().getSelectedFace().updateUV();
			ModelCreator.updateValues();
		});
		glowValue = new JTextField();
		
		glowValue.addActionListener(e -> {
			try {
				manager.getCurrentElement().getSelectedFace().setGlow(Integer.parseInt(glowValue.getText()));	
			} catch(Exception ex) {
				
			}
			
		});
				
		horizontalBox.add(boxEnabled);
		horizontalBox.add(boxAutoUV);
		horizontalBox.add(new JLabel("Glow Level"));
		horizontalBox.add(glowValue);
		
		horizontalBox.add(new JLabel(""));
	}

	public void addComponents()
	{
		add(horizontalBox, BorderLayout.NORTH);
	}

	@Override
	public void updateValues()
	{
		Element cube = manager.getCurrentElement();
		if (cube != null)
		{
			boxEnabled.setEnabled(true);
			boxEnabled.setSelected(cube.getSelectedFace().isEnabled());
			boxAutoUV.setEnabled(true);
			boxAutoUV.setSelected(cube.getSelectedFace().isAutoUVEnabled());
			glowValue.setEnabled(true);
			glowValue.setText(""+cube.getSelectedFace().getGlow());
		}
		else
		{
			boxEnabled.setEnabled(false);
			boxEnabled.setSelected(false);
			boxAutoUV.setEnabled(false);
			boxAutoUV.setSelected(false);
			glowValue.setEnabled(false);
		}
	}
}
