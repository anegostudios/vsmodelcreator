package at.vintagestory.modelcreator.gui.right.keyframes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Start;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Face;
import at.vintagestory.modelcreator.model.KeyframeElement;
import at.vintagestory.modelcreator.util.Parser;

public class ElementKeyFrameRotationPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private RightKeyFramesPanel keyFramesPanel;

	
	
	
	private JTextField[] rotationFields;
	private JSlider[] rotationSliders;
	

	static int ROTATION_MIN = -4;
	static int ROTATION_MAX = 4;
	static int ROTATION_INIT = 0;
	
	boolean ignoreSliderChanges;
	
	public boolean enabled = true;
	
	static double multiplier = 22.5;

	public ElementKeyFrameRotationPanel(RightKeyFramesPanel keyFramesPanel)
	{
		rotationFields = new JTextField[3];
		rotationSliders = new JSlider[3];
		
		this.keyFramesPanel = keyFramesPanel;
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
		
		
		rotationFields[num].addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					KeyframeElement element = keyFramesPanel.getCurrentElement();
					if (element != null)
					{
						if (num == 0) element.setRotationX(Parser.parseDouble(rotationFields[num].getText(), element.getRotationX()));
						if (num == 1) element.setRotationY(Parser.parseDouble(rotationFields[num].getText(), element.getRotationY()));
						if (num == 2) element.setRotationZ(Parser.parseDouble(rotationFields[num].getText(), element.getRotationZ()));
						ModelCreator.updateValues();
					}
				}
			}
		});
		
		rotationFields[num].addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				KeyframeElement element = keyFramesPanel.getCurrentElement();
				if (element != null)
				{
					if (num == 0) element.setRotationX(Parser.parseDouble(rotationFields[num].getText(), element.getRotationX()));
					if (num == 1) element.setRotationY(Parser.parseDouble(rotationFields[num].getText(), element.getRotationY()));
					if (num == 2) element.setRotationZ(Parser.parseDouble(rotationFields[num].getText(), element.getRotationZ()));
					ModelCreator.updateValues();
				}
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
			
			KeyframeElement elem = keyFramesPanel.getCurrentElement();
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
			
			ModelCreator.updateValues();
		});
		

		sliderPanel.add(rotationSliders[num]);
		
		layout.putConstraint(SpringLayout.WEST, rotationFields[num], 10, SpringLayout.WEST, sliderPanel);
		layout.putConstraint(SpringLayout.NORTH, rotationFields[num], 5 + num * 45, SpringLayout.NORTH, sliderPanel);
		
		layout.putConstraint(SpringLayout.WEST, rotationSliders[num], 50, SpringLayout.WEST, sliderPanel);
		layout.putConstraint(SpringLayout.NORTH, rotationSliders[num], 5 + num * 45, SpringLayout.NORTH, sliderPanel);
		
		layout.putConstraint(SpringLayout.EAST, sliderPanel, 5, SpringLayout.EAST, rotationSliders[num]);
		layout.putConstraint(SpringLayout.SOUTH, sliderPanel, 5, SpringLayout.SOUTH, rotationSliders[num]);
	}


	@Override
	public void updateValues()
	{
		KeyframeElement element = keyFramesPanel.getCurrentElement();
		toggleFields(element);
	}
	
	public void toggleFields(KeyframeElement element) {
		ignoreSliderChanges = true;
		
		if (ModelCreator.unlockAngles) {
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
		
	
		
		boolean enabled = element != null && this.enabled;
		
		for (int i = 0; i < 3; i++) {
			rotationFields[i].setEnabled(enabled);
			rotationSliders[i].setEnabled(enabled);
		}
		
		rotationSliders[0].setValue(enabled ? (int) Math.round(element.getRotationX() / multiplier) : 0);
		rotationSliders[1].setValue(enabled ? (int) Math.round(element.getRotationY() / multiplier) : 0);
		rotationSliders[2].setValue(enabled ? (int) Math.round(element.getRotationZ() / multiplier) : 0);
		
		rotationFields[0].setText(enabled ? "" + element.getRotationX() : "");
		rotationFields[1].setText(enabled ? "" + element.getRotationY() : "");
		rotationFields[2].setText(enabled ? "" + element.getRotationZ() : "");
		
		ignoreSliderChanges = false;
	}
	
	

	Hashtable<Integer, JLabel> getLabelTable() {
		if (ModelCreator.unlockAngles) {
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
