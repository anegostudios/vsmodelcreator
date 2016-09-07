package com.mrcrayfish.modelcreator.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.util.ComponentUtil;

public class FaceExtrasPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private ElementManager manager;

	private JPanel horizontalBox;
	//private JRadioButton boxCullFace;
	private JRadioButton boxFill;
	private JRadioButton boxEnabled;
	private JRadioButton boxAutoUV;
	private JTextField glowValue;

	public FaceExtrasPanel(ElementManager manager)
	{
		this.manager = manager;
		setLayout(new BorderLayout(0, 5));
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(221, 221, 228), 5), "<html><b>Extras</b></html>"));
		setMaximumSize(new Dimension(186, 100));
		initComponents();
		addComponents();
	}

	public void initComponents()
	{
		horizontalBox = new JPanel(new GridLayout(0, 2));
		/*boxCullFace = ComponentUtil.createRadioButton("Cullface", "<html>Should render face is another block is adjacent<br>Default: Off</html>");
		boxCullFace.addActionListener(e ->
		{
			manager.getSelectedElement().getSelectedFace().setCullface(boxCullFace.isSelected());
		});*/
		boxFill = ComponentUtil.createRadioButton("Fill", "<html>Makes the texture fill the face<br>Default: Off</html>");
		boxFill.addActionListener(e ->
		{
			manager.getSelectedElement().getSelectedFace().fitTexture(boxFill.isSelected());
		});
		boxEnabled = ComponentUtil.createRadioButton("Enable","<html>Determines if face should be rendered<br>Default: On</html>");
		boxEnabled.addActionListener(e ->
		{
			manager.getSelectedElement().getSelectedFace().setEnabled(boxEnabled.isSelected());
		});
		boxAutoUV = ComponentUtil.createRadioButton("Auto UV", "<html>Determines if UV end coordinates should be set based on element size<br>Default: On</html>");
		boxAutoUV.addActionListener(e ->
		{
			manager.getSelectedElement().getSelectedFace().setAutoUVEnabled(boxAutoUV.isSelected());
			manager.getSelectedElement().getSelectedFace().updateUV();
			manager.updateValues();
		});
		glowValue = new JTextField();
		
		glowValue.addActionListener(e -> {
			try {
				manager.getSelectedElement().getSelectedFace().setGlow(Integer.parseInt(glowValue.getText()));	
			} catch(Exception ex) {
				
			}
			
		});
				
		//horizontalBox.add(boxCullFace);
		horizontalBox.add(boxFill);
		horizontalBox.add(boxEnabled);
		horizontalBox.add(boxAutoUV);
		horizontalBox.add(new JLabel(""));
		horizontalBox.add(new JLabel("Glow"));
		
		horizontalBox.add(glowValue);
		horizontalBox.add(new JLabel(""));
	}

	public void addComponents()
	{
		add(horizontalBox, BorderLayout.NORTH);
	}

	@Override
	public void updateValues(Element cube)
	{
		if (cube != null)
		{
			//boxCullFace.setEnabled(true);
			//boxCullFace.setSelected(cube.getSelectedFace().isCullfaced());
			boxFill.setEnabled(true);
			boxFill.setSelected(cube.getSelectedFace().shouldFitTexture());
			boxEnabled.setEnabled(true);
			boxEnabled.setSelected(cube.getSelectedFace().isEnabled());
			boxAutoUV.setEnabled(true);
			boxAutoUV.setSelected(cube.getSelectedFace().isAutoUVEnabled());
			glowValue.setEnabled(true);
			glowValue.setText(""+cube.getSelectedFace().getGlow());
		}
		else
		{
			//boxCullFace.setEnabled(false);
			//boxCullFace.setSelected(false);
			boxFill.setEnabled(false);
			boxFill.setSelected(false);
			boxEnabled.setEnabled(false);
			boxEnabled.setSelected(false);
			boxAutoUV.setEnabled(false);
			boxAutoUV.setSelected(false);
			glowValue.setEnabled(false);
		}
	}
}
