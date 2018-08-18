package at.vintagestory.modelcreator.gui.right.face;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
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
import at.vintagestory.modelcreator.util.AwtUtil;

public class FacePropertiesPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private IElementManager manager;

	private JPanel horizontalBox;
	private JRadioButton boxEnabled;
	private JRadioButton boxAutoUV;
	private JRadioButton boxSnapUv;
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
		
		boxEnabled = ComponentUtil.createRadioButton("Enabled","<html>Determines if face should be rendered<br>Default: On</html>");
		boxEnabled.addActionListener(e ->
		{
			manager.getCurrentElement().getSelectedFace().setEnabled(boxEnabled.isSelected());
		});
		boxAutoUV = ComponentUtil.createRadioButton("Auto UV", "<html>Determines if UV end coordinates should be set based on element size<br>Default: On</html>");
		boxAutoUV.addActionListener(e ->
		{
			manager.getCurrentElement().getSelectedFace().setAutoUVEnabled(boxAutoUV.isSelected());
			manager.getCurrentElement().getSelectedFace().updateUV();
			ModelCreator.updateValues(boxAutoUV);
		});
		
		
		boxSnapUv = ComponentUtil.createRadioButton("Snap UV", "<html>Determines if auto-uv should snap the coordinates to pixels on the texture. Disable if your element is very small or want full control over the UV Coordinates<br>Default: On</html>");
		boxSnapUv.addActionListener(e ->
		{
			manager.getCurrentElement().getSelectedFace().setSnapUVEnabled(boxSnapUv.isSelected());
			manager.getCurrentElement().updateUV();
			ModelCreator.updateValues(boxSnapUv);
		});
		
		glowValue = new JTextField();
		
		
		AwtUtil.addChangeListener(glowValue, e -> {
			try {
				manager.getCurrentElement().getSelectedFace().setGlow(Integer.parseInt(glowValue.getText()));	
			} catch(Exception ex) {
				
			}
		});
				
		horizontalBox.add(boxEnabled);
		horizontalBox.add(new JLabel(""));
		horizontalBox.add(boxAutoUV);
		horizontalBox.add(boxSnapUv);
		horizontalBox.add(new JLabel("Glow Level"));
		horizontalBox.add(glowValue);
		
		horizontalBox.add(new JLabel(""));
	}

	public void addComponents()
	{
		add(horizontalBox, BorderLayout.NORTH);
	}

	@Override
	public void updateValues(JComponent byGuiElem)
	{
		Element cube = manager.getCurrentElement();
		
		boxEnabled.setEnabled(cube != null);
		boxEnabled.setSelected(cube != null);
		boxAutoUV.setEnabled(cube != null);
		boxAutoUV.setSelected(cube != null);
		boxSnapUv.setEnabled(cube != null);
		glowValue.setEnabled(cube != null);
		
		if (cube != null)
		{
			boxEnabled.setSelected(cube.getSelectedFace().isEnabled());
			boxAutoUV.setSelected(cube.getSelectedFace().isAutoUVEnabled());
			boxSnapUv.setSelected(cube.getSelectedFace().isSnapUvEnabled());
			if (byGuiElem != glowValue) glowValue.setText(""+cube.getSelectedFace().getGlow());
		}
	}
}
