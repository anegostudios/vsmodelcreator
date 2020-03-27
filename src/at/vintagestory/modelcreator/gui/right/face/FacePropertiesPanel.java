package at.vintagestory.modelcreator.gui.right.face;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

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
import at.vintagestory.modelcreator.model.Face;
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
		setMaximumSize(new Dimension(250, 160));
		initComponents();
		addComponents();
	}

	public void initComponents()
	{
		horizontalBox = new JPanel(new GridLayout(0, 1));
		
		boxEnabled = ComponentUtil.createRadioButton("Enabled","<html>Determines if face should be rendered<br>Default: On</html>");
		boxEnabled.addActionListener(e ->
		{
			ModelCreator.changeHistory.beginMultichangeHistoryState();
			
			if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1) {
				Element elem = manager.getCurrentElement();
				for (int i = 0; i < elem.getAllFaces().length; i++) {
					Face face = elem.getAllFaces()[i];
					face.setEnabled(boxEnabled.isSelected());
				}
				
			} else {
				manager.getCurrentElement().getSelectedFace().setEnabled(boxEnabled.isSelected());
			}
			
			ModelCreator.changeHistory.endMultichangeHistoryState(ModelCreator.currentProject);
		});
		
		boxAutoUV = ComponentUtil.createRadioButton("Auto Resolution", "<html>Automatically sets the UV end coordinates to fit the desired texture resolution<br>Default: On</html>");
		boxAutoUV.addActionListener(e ->
		{
			boolean on = boxAutoUV.isSelected();
			ModelCreator.changeHistory.beginMultichangeHistoryState();
			
			if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1) {
				Element elem = manager.getCurrentElement();
				for (int i = 0; i < elem.getAllFaces().length; i++) {
					Face face = elem.getAllFaces()[i];
					face.setAutoUVEnabled(on);
					face.updateUV();
				}
				
			} else {
				manager.getCurrentElement().getSelectedFace().setAutoUVEnabled(on);
				manager.getCurrentElement().getSelectedFace().updateUV();

			}

			
			ModelCreator.updateValues(boxAutoUV);
			ModelCreator.changeHistory.endMultichangeHistoryState(ModelCreator.currentProject);
		});
		
		
		boxSnapUv = ComponentUtil.createRadioButton("Snap UV", "<html>Determines if auto-uv should snap the coordinates to pixels on the texture. Disable if your element is very small or want full control over the UV Coordinates<br>Default: On</html>");
		boxSnapUv.addActionListener(e ->
		{
			ModelCreator.changeHistory.beginMultichangeHistoryState();
			
			if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1) {
				Element elem = manager.getCurrentElement();
				for (int i = 0; i < elem.getAllFaces().length; i++) {
					Face face = elem.getAllFaces()[i];
					face.setSnapUVEnabled(boxSnapUv.isSelected());
				}
				
			} else {
				manager.getCurrentElement().getSelectedFace().setSnapUVEnabled(boxSnapUv.isSelected());
			}
			
			
			manager.getCurrentElement().updateUV();
			ModelCreator.updateValues(boxSnapUv);
			ModelCreator.changeHistory.endMultichangeHistoryState(ModelCreator.currentProject);
		});
		
		glowValue = new JTextField();
		
		
		AwtUtil.addChangeListener(glowValue, e -> {
			try {
				manager.getCurrentElement().getSelectedFace().setGlow(Integer.parseInt(glowValue.getText()));	
			} catch(Exception ex) {
				
			}
		});
				
		horizontalBox.add(boxEnabled);
		//horizontalBox.add(new JLabel(""));
		horizontalBox.add(boxAutoUV);
		//horizontalBox.add(new JLabel(""));
		horizontalBox.add(boxSnapUv);
		horizontalBox.add(new JLabel("Glow Level (0..255)"));
		horizontalBox.add(glowValue);
		
		//horizontalBox.add(new JLabel(""));
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
