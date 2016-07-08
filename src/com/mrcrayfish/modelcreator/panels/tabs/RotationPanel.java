package com.mrcrayfish.modelcreator.panels.tabs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Hashtable;

import javax.swing.*;

import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.panels.IValueUpdater;
import com.mrcrayfish.modelcreator.panels.OriginPanel;
import com.mrcrayfish.modelcreator.util.Parser;

public class RotationPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private ElementManager manager;

	private OriginPanel panelOrigin;
	
	
	private JPanel[] rotationPanels;
	private JTextField[] rotationFields;
	private JSlider[] rotationSliders;
	
	private JPanel extraPanel;
	//private JRadioButton btnRescale;
	

	private final int ROTATION_MIN = -90;
	private final int ROTATION_MAX = 90;
	private final int ROTATION_INIT = 0;

	public RotationPanel(ElementManager manager)
	{
		rotationFields = new JTextField[3];
		rotationSliders = new JSlider[3];
		
		this.manager = manager;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		initMenu();
		initComponents();
		addComponents();
	}

	public void initMenu()
	{
	}

	public void initComponents()
	{
		panelOrigin = new OriginPanel(manager);

		
		//extraPanel = new JPanel(new GridLayout(1, 2));
		//extraPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(221, 221, 228), 5), "<html><b>Extras</b></html>"));
		
		/*btnRescale = ComponentUtil.createRadioButton("Rescale", "<html>Should scale faces across whole block<br>Default: Off<html>");
		btnRescale.addActionListener(e ->
		{
			manager.getSelectedElement().setRescale(btnRescale.isSelected());
		});
		extraPanel.setMaximumSize(new Dimension(186, 50));
		extraPanel.add(btnRescale);*/
	}
	
	
	JPanel GetRotationPanel(String axis, int num) {
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(-90), new JLabel("-90\u00b0"));
		labelTable.put(new Integer(0), new JLabel("0\u00b0"));
		labelTable.put(new Integer(90), new JLabel("90\u00b0"));
		

		JPanel sliderPanel = new JPanel(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		
		cons.gridy = 0;
		cons.fill = GridBagConstraints.HORIZONTAL;

		
		sliderPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(221, 221, 228), 3), "<html><b>Rotation " + axis + "</b></html>"));
		
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
		rotationSliders[num].setLabelTable(labelTable);
		
		
		rotationSliders[num].addChangeListener(e ->
		{
			rotationFields[num].setText(""+rotationSliders[num].getValue());
			if (num == 0) {
				manager.getSelectedElement().setRotationX(rotationSliders[num].getValue());
			}
			if (num == 1) {
				manager.getSelectedElement().setRotationY(rotationSliders[num].getValue());
			}
			if (num == 2) {
				manager.getSelectedElement().setRotationZ(rotationSliders[num].getValue());
			}
		});
		

		cons.gridx = 1;
		cons.weightx = 1.3f;
		
		sliderPanel.add(rotationSliders[num], cons);

		
		return sliderPanel;
	}

	public void addComponents()
	{
		add(Box.createRigidArea(new Dimension(188, 5)));
		add(panelOrigin);
		
		rotationPanels = new JPanel[] { GetRotationPanel("x", 0), GetRotationPanel("y", 1), GetRotationPanel("z", 2)};
		
		add(rotationPanels[0]);
		add(rotationPanels[1]);
		add(rotationPanels[2]);
		
		//add(extraPanel);
	}

	@Override
	public void updateValues(Element cube)
	{
		panelOrigin.updateValues(cube);
		
		if (cube != null)
		{
			for (int i = 0; i < 3; i++) {
				rotationFields[i].setEnabled(true);
				rotationSliders[i].setEnabled(true);
			}
			
			rotationSliders[0].setValue((int) (cube.getRotationX()));
			rotationSliders[1].setValue((int) (cube.getRotationY()));
			rotationSliders[2].setValue((int) (cube.getRotationZ()));

			
			//btnRescale.setEnabled(true);
			//btnRescale.setSelected(cube.shouldRescale());
		}
		else
		{
			for (int i = 0; i < 3; i++) {
				rotationFields[i].setEnabled(false);
				rotationSliders[i].setValue(0);
				rotationSliders[i].setEnabled(false);
			}
			
			
			//btnRescale.setSelected(false);
			//btnRescale.setEnabled(false);
		}
	}
}
