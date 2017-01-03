package at.vintagestory.modelcreator.gui.right.rotation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Hashtable;

import javax.swing.*;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.util.Parser;

public class RotationPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private IElementManager manager;

	private RotationOriginPanel panelOrigin;
	
	
	private JTextField[] rotationFields;
	private JSlider[] rotationSliders;
	

	static int ROTATION_MIN = -4;
	static int ROTATION_MAX = 4;
	static int ROTATION_INIT = 0;
	
	boolean ignoreSliderChanges;
	
	static double multiplier = 22.5;

	public RotationPanel(IElementManager manager)
	{
		rotationFields = new JTextField[3];
		rotationSliders = new JSlider[3];
		
		this.manager = manager;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//setAlignmentY(TOP_ALIGNMENT);
		initComponents();
	}

	

	public void initComponents()
	{
		panelOrigin = new RotationOriginPanel(manager);

		add(Box.createRigidArea(new Dimension(188, 5)));
		add(panelOrigin);
		
		JPanel sliderPanel = new JPanel(new GridBagLayout());
		sliderPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(221, 221, 228), 5), "<html><b>XYZ Rotation</b></html>"));
		
		GridBagConstraints cons = new GridBagConstraints();
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.gridheight = 1;
		cons.anchor = GridBagConstraints.NORTH;
		
		cons.gridy = 0;
		AddRotationPanel("X", 0, sliderPanel, cons);
		cons.gridy = 1;
		AddRotationPanel("Y", 1, sliderPanel, cons);
		cons.gridy = 2;
		AddRotationPanel("Z", 2, sliderPanel, cons);
		
		cons.gridy = 3;
		cons.fill = GridBagConstraints.BOTH;
		cons.weightx=1;
		cons.weighty=1;
		cons.gridwidth = 2;
		sliderPanel.add(new JLabel(" "), cons);

		add(sliderPanel);
	}
	
	
	
	void AddRotationPanel(String axis, int num, JPanel sliderPanel, GridBagConstraints cons) {
		rotationFields[num] = new JTextField();
		Font defaultFont = new Font("SansSerif", Font.PLAIN, 10);
		rotationFields[num].setFont(defaultFont);
		rotationFields[num].setHorizontalAlignment(JTextField.CENTER);
		
		
		rotationFields[num].addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					Element element = manager.getSelectedElement();
					if (element != null)
					{
						if (num == 0) element.setRotationX(Parser.parseDouble(rotationFields[num].getText(), element.getRotationX()));
						if (num == 1) element.setRotationY(Parser.parseDouble(rotationFields[num].getText(), element.getRotationY()));
						if (num == 2) element.setRotationZ(Parser.parseDouble(rotationFields[num].getText(), element.getRotationZ()));
						manager.updateValues();
					}
				}
			}
		});
		
		rotationFields[num].addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				Element element = manager.getSelectedElement();
				if (element != null)
				{
					if (num == 0) element.setRotationX(Parser.parseDouble(rotationFields[num].getText(), element.getRotationX()));
					if (num == 1) element.setRotationY(Parser.parseDouble(rotationFields[num].getText(), element.getRotationY()));
					if (num == 2) element.setRotationZ(Parser.parseDouble(rotationFields[num].getText(), element.getRotationZ()));
					manager.updateValues();
				}
			}
		});

		
		
		cons.gridx = 0;
		cons.weightx = 0.5f;
		sliderPanel.add(rotationFields[num], cons);
		
		
		rotationSliders[num] = new JSlider(JSlider.HORIZONTAL, ROTATION_MIN, ROTATION_MAX, ROTATION_INIT);
		rotationSliders[num].setMajorTickSpacing(1);
		rotationSliders[num].setPaintTicks(true);
		rotationSliders[num].setPaintLabels(true);
		rotationSliders[num].setLabelTable(getLabelTable());
		
		
		rotationSliders[num].addChangeListener(e ->
		{
			if (ignoreSliderChanges) return;
			
			double newValue = multiplier * rotationSliders[num].getValue();
			
			rotationFields[num].setText(""+newValue);
			
			Element elem = manager.getSelectedElement();
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
		});
		

		cons.gridx = 1;
		cons.weightx = 1.3f;
				
		sliderPanel.add(rotationSliders[num], cons);
	}


	@Override
	public void updateValues(Element cube)
	{
		panelOrigin.updateValues(cube);
		
		ignoreSliderChanges = true;
		
		if (ModelCreator.unlockAngles) {
			if (ROTATION_MIN != -90) {
				ROTATION_MIN = -90;
				ROTATION_MAX = 90;
				
				for (int i = 0; i < 3; i++) {
					rotationSliders[i].setMinimum(-90);
					rotationSliders[i].setMaximum(90);
					rotationSliders[i].setMajorTickSpacing(45);
					rotationSliders[i].setLabelTable(getLabelTable());
				}
				
				multiplier = 1;				
			}
			
		} else {
			if (ROTATION_MIN != -4) {
				ROTATION_MIN = -4;
				ROTATION_MAX = 4;
				
				for (int i = 0; i < 3; i++) {
					rotationSliders[i].setMinimum(-4);
					rotationSliders[i].setMaximum(4);		
					rotationSliders[i].setMajorTickSpacing(1);
					rotationSliders[i].setLabelTable(getLabelTable());
				}
				
				multiplier = 22.5;							
			}
		}
		
		
		ignoreSliderChanges = false;
		
		
		if (cube != null)
		{
			for (int i = 0; i < 3; i++) {
				rotationFields[i].setEnabled(true);
				rotationSliders[i].setEnabled(true);
			}
			
			rotationSliders[0].setValue((int) Math.round(cube.getRotationX() / multiplier));
			rotationSliders[1].setValue((int) Math.round(cube.getRotationY() / multiplier));
			rotationSliders[2].setValue((int) Math.round(cube.getRotationZ() / multiplier));
		}
		else
		{
			for (int i = 0; i < 3; i++) {
				rotationFields[i].setEnabled(false);
				rotationSliders[i].setValue(0);
				rotationSliders[i].setEnabled(false);
			}
		}
		

	}
	
	

	Hashtable<Integer, JLabel> getLabelTable() {
		if (ModelCreator.unlockAngles) {
			Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
			labelTable.put(new Integer(-90), new JLabel("-90\u00b0"));
			labelTable.put(new Integer(-45), new JLabel("-45\u00b0"));
			labelTable.put(new Integer(0), new JLabel("0\u00b0"));
			labelTable.put(new Integer(45), new JLabel("45\u00b0"));
			labelTable.put(new Integer(90), new JLabel("90\u00b0"));
			
			return labelTable;
			
		} else {
			Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
			labelTable.put(new Integer(-4), new JLabel("-90\u00b0"));
			labelTable.put(new Integer(-2), new JLabel("-45\u00b0"));
			labelTable.put(new Integer(0), new JLabel("0\u00b0"));
			labelTable.put(new Integer(2), new JLabel("45\u00b0"));
			labelTable.put(new Integer(4), new JLabel("90\u00b0"));
			
			return labelTable;
			
		}
	}
	
}
