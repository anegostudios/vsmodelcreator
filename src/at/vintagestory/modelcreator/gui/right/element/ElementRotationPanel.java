package at.vintagestory.modelcreator.gui.right.element;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;
import java.util.Hashtable;
import javax.swing.*;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Start;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.Face;
import at.vintagestory.modelcreator.util.AwtUtil;
import at.vintagestory.modelcreator.util.Parser;

public class ElementRotationPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private IElementManager manager;

	
	private DecimalFormat df = new DecimalFormat("#.##");	
	
	private JTextField[] rotationFields;
	private JSlider[] rotationSliders;
	

	static int ROTATION_MIN = -4;
	static int ROTATION_MAX = 4;
	static int ROTATION_INIT = 0;
	
	boolean ignoreSliderChanges;
	
	public boolean enabled = true;
	
	static double multiplier = 22.5;

	public ElementRotationPanel(IElementManager manager)
	{
		rotationFields = new JTextField[3];
		rotationSliders = new JSlider[3];
		
		this.manager = manager;
		setMaximumSize(new Dimension(186, 270));
		
		initComponents();
	}
	

	public void initComponents()
	{
		
		SpringLayout layout = new SpringLayout();
		
		JPanel slidersPanel = new JPanel(layout);
		
		slidersPanel.setBorder(BorderFactory.createTitledBorder(Start.Border, "<html>&nbsp;&nbsp;&nbsp;<b>XYZ Rotation</b></html>"));		
		
		AddRotationPanel("X", 0, slidersPanel, layout);
		AddRotationPanel("Y", 1, slidersPanel, layout);
		AddRotationPanel("Z", 2, slidersPanel, layout);
		
		add(slidersPanel);		
	}
	
	
	
	void AddRotationPanel(String axis, int num, JPanel sliderPanel, SpringLayout layout) {
		rotationFields[num] = new JTextField();
		Font defaultFont = new Font("SansSerif", Font.PLAIN, 12);
		rotationFields[num].setFont(defaultFont);
		rotationFields[num].setForeground(new Color(0,0,0));
		rotationFields[num].setHorizontalAlignment(JTextField.CENTER);
		rotationFields[num].setPreferredSize(new Dimension(38, 20));
		
		int colIndex = num == 1 ? 4 : (num == 0 ? 1 : 2);
		
		rotationFields[num].setBackground(new Color(Face.ColorsByFace[colIndex].r, Face.ColorsByFace[colIndex].g, Face.ColorsByFace[colIndex].b));
		
		
		AwtUtil.addChangeListener(rotationFields[num], e -> {
			Element element = manager.getCurrentElement();
			if (element == null) return;
			
			if (num == 0) element.setRotationX(Parser.parseDouble(rotationFields[num].getText(), element.getRotationX()));
			if (num == 1) element.setRotationY(Parser.parseDouble(rotationFields[num].getText(), element.getRotationY()));
			if (num == 2) element.setRotationZ(Parser.parseDouble(rotationFields[num].getText(), element.getRotationZ()));
			ModelCreator.updateValues(rotationFields[num]);
		});
		
		
		rotationFields[num].addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				modifyAngle(num, e.getWheelRotation() > 0 ? 1 : -1, e.getModifiers());
			}
		});
		
		sliderPanel.add(rotationFields[num]);
		
		
		rotationSliders[num] = new JSlider(JSlider.HORIZONTAL, ROTATION_MIN, ROTATION_MAX, ROTATION_INIT);
		rotationSliders[num].setMajorTickSpacing(1);
		rotationSliders[num].setPaintTicks(true);
		rotationSliders[num].setPaintLabels(true);
		rotationSliders[num].setLabelTable(getLabelTable());
		rotationSliders[num].setPreferredSize(new Dimension(160, 40));
		
		
		rotationSliders[num].addChangeListener(e ->
		{
			if (ignoreSliderChanges) return;
			
			double newValue = multiplier * rotationSliders[num].getValue();
			
			rotationFields[num].setText(""+newValue);
			
			Element elem = manager.getCurrentElement();
			if (elem == null) return;
			
			if (num == 0) {
				elem.setRotationX(newValue);
			}
			if (num == 1) {
				elem.setRotationY(newValue);
			}
			if (num == 2) {
				elem.setRotationZ(newValue);
			}
			
			ModelCreator.updateValues(rotationSliders[num]);
		});
		

		sliderPanel.add(rotationSliders[num]);
		
		layout.putConstraint(SpringLayout.WEST, rotationFields[num], 10, SpringLayout.WEST, sliderPanel);
		layout.putConstraint(SpringLayout.NORTH, rotationFields[num], 5 + num * 45, SpringLayout.NORTH, sliderPanel);
		
		layout.putConstraint(SpringLayout.WEST, rotationSliders[num], 50, SpringLayout.WEST, sliderPanel);
		layout.putConstraint(SpringLayout.NORTH, rotationSliders[num], 5 + num * 45, SpringLayout.NORTH, sliderPanel);
		
		layout.putConstraint(SpringLayout.EAST, sliderPanel, 5, SpringLayout.EAST, rotationSliders[num]);
		layout.putConstraint(SpringLayout.SOUTH, sliderPanel, 5, SpringLayout.SOUTH, rotationSliders[num]);
	}

	
	public void modifyAngle(int num, int direction, int modifiers) {
		Element cube = manager.getCurrentElement();
		if (cube == null) return;		
		float size = direction * ((modifiers & ActionEvent.SHIFT_MASK) == 1 ? 0.1f : 1f);
		double newValue;
		
		switch (num) {
		case 0:
			newValue = cube.getRotationX() + size;
			cube.setRotationX(newValue);
			rotationFields[num].setText(""+df.format(newValue));
			break;
		case 1:
			newValue = cube.getRotationY() + size;
			cube.setRotationY(newValue);
			rotationFields[num].setText(""+df.format(newValue));
			break;
		default:
			newValue = cube.getRotationZ() + size;
			cube.setRotationZ(newValue);
			rotationFields[num].setText(""+df.format(newValue));			
			break;
		}
		
		ModelCreator.updateValues(null);
	}

	@Override
	public void updateValues(JComponent byGuiElem)
	{
		Element cube = manager.getCurrentElement();
		toggleFields(cube, byGuiElem);
	}
	
	public void toggleFields(Element cube, JComponent byGuiElem) {
		ignoreSliderChanges = true;
		
		if (ModelCreator.currentProject.AllAngles) {
			if (ROTATION_MIN != -180) {
				ROTATION_MIN = -180;
				ROTATION_MAX = 180;
				
				for (int i = 0; i < 3; i++) {
					rotationSliders[i].setMinimum(-180);
					rotationSliders[i].setMaximum(180);
					rotationSliders[i].setMajorTickSpacing(45);
					rotationSliders[i].setLabelTable(getLabelTable());
				}
				
				multiplier = 1;				
			}
			
		} else {
			if (ROTATION_MIN != -8) {
				ROTATION_MIN = -8;
				ROTATION_MAX = 8;
				
				for (int i = 0; i < 3; i++) {
					rotationSliders[i].setMinimum(-8);
					rotationSliders[i].setMaximum(8);		
					rotationSliders[i].setMajorTickSpacing(1);
					rotationSliders[i].setLabelTable(getLabelTable());
				}
				
				multiplier = 22.5;							
			}
		}
		
	
		
		boolean enabled = cube != null && this.enabled;
		
		for (int i = 0; i < 3; i++) {
			rotationFields[i].setEnabled(enabled);
			rotationSliders[i].setEnabled(enabled);
		}
		
		rotationSliders[0].setValue(enabled ? (int) Math.round(cube.getRotationX() / multiplier) : 0);
		rotationSliders[1].setValue(enabled ? (int) Math.round(cube.getRotationY() / multiplier) : 0);
		rotationSliders[2].setValue(enabled ? (int) Math.round(cube.getRotationZ() / multiplier) : 0);
		
		if (enabled) {
			if (byGuiElem != rotationFields[0]) rotationFields[0].setText(""+df.format(cube.getRotationX()));
			if (byGuiElem != rotationFields[1]) rotationFields[1].setText(""+df.format(cube.getRotationY()));
			if (byGuiElem != rotationFields[2]) rotationFields[2].setText(""+df.format(cube.getRotationZ()));			
		}
		
		ignoreSliderChanges = false;
	}
	
	

	Hashtable<Integer, JLabel> getLabelTable() {
		if (ModelCreator.currentProject.AllAngles) {
			Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
			labelTable.put(new Integer(-180), new JLabel("-180\u00b0"));
			labelTable.put(new Integer(-90), new JLabel("-90\u00b0"));
			labelTable.put(new Integer(0), new JLabel("0\u00b0"));
			labelTable.put(new Integer(90), new JLabel("90\u00b0"));
			labelTable.put(new Integer(180), new JLabel("180\u00b0"));
			
			return labelTable;
			
		} else {
			Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
			labelTable.put(new Integer(-8), new JLabel("-180\u00b0"));
			labelTable.put(new Integer(-4), new JLabel("-90\u00b0"));
			labelTable.put(new Integer(0), new JLabel("0\u00b0"));
			labelTable.put(new Integer(4), new JLabel("90\u00b0"));
			labelTable.put(new Integer(8), new JLabel("180\u00b0"));
			
			return labelTable;
		}
	}
	
}
