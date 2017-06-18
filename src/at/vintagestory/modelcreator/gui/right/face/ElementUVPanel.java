package at.vintagestory.modelcreator.gui.right.face;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Start;
import at.vintagestory.modelcreator.enums.EnumAxis;
import at.vintagestory.modelcreator.gui.Icons;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.util.AwtUtil;
import at.vintagestory.modelcreator.util.Parser;

public class ElementUVPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private IElementManager manager;
	private JButton btnPlusX;
	private JButton btnPlusY;
	private JTextField xStartField;
	private JTextField yStartField;
	private JButton btnNegX;
	private JButton btnNegY;
	private JComboBox<String> menuList;
	private JPanel unwrapPanel;

	private DecimalFormat df = new DecimalFormat("#.#");
	
	private DefaultComboBoxModel<String> model;

	public ElementUVPanel(IElementManager manager)
	{
		this.manager = manager;
		//setLayout(new GridLayout(2, 1, 4, 4));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>UV (all Faces)</b></html>"));
		setMaximumSize(new Dimension(186, 174));
		initComponents();
		initProperties();
		addComponents();
	}

	public void initComponents()
	{
		btnPlusX = new JButton(Icons.arrow_up);
		btnPlusY = new JButton(Icons.arrow_up);
		xStartField = new JTextField();
		yStartField = new JTextField();
		btnNegX = new JButton(Icons.arrow_down);
		btnNegY = new JButton(Icons.arrow_down);
		
		model = new DefaultComboBoxModel<String>();
		model.addElement("Compact");
		model.addElement("North is front");
		model.addElement("East is front");
		model.addElement("South is front");
		model.addElement("West is front");
		model.addElement("Up is front");
		model.addElement("Down is front");
	}

	public void initProperties()
	{
		Font defaultFont = new Font("SansSerif", Font.BOLD, 20);
		xStartField.setSize(new Dimension(62, 30));
		xStartField.setFont(defaultFont);
		xStartField.setHorizontalAlignment(JTextField.CENTER);
		
		AwtUtil.addChangeListener(xStartField, e -> {
			Element element = manager.getCurrentElement();
			if (element != null)
			{
				element.setTexUStart((Parser.parseDouble(xStartField.getText(), element.getTexUStart())));
				ModelCreator.updateValues(xStartField);
			}
		});

		xStartField.addMouseWheelListener(new MouseWheelListener()
		{
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				int notches = e.getWheelRotation();
				modifyPosition(EnumAxis.X, (notches > 0 ? 1 : -1));
			}
		});

		
		yStartField.setSize(new Dimension(62, 30));
		yStartField.setFont(defaultFont);
		yStartField.setHorizontalAlignment(JTextField.CENTER);
		
		AwtUtil.addChangeListener(yStartField, e -> {
			Element element = manager.getCurrentElement();
			if (element != null)
			{
				element.setTexVStart((Parser.parseDouble(yStartField.getText(), element.getTexVStart())));
				ModelCreator.updateValues(yStartField);
			}
		});

		
		yStartField.addMouseWheelListener(new MouseWheelListener()
		{
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				int notches = e.getWheelRotation();
				modifyPosition(EnumAxis.Y, (notches > 0 ? 1 : -1));
			}
		});

		


		btnPlusX.addActionListener(e ->
		{
			Element elem = manager.getCurrentElement();
			if (elem == null) return;
			double diff = ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1) ? 0.1 : 1;
			elem.setTexUStart(elem.getTexUStart() + diff);
			ModelCreator.updateValues(btnPlusX);
		});

		btnPlusX.setSize(new Dimension(62, 30));
		btnPlusX.setFont(defaultFont);
		btnPlusX.setToolTipText("<html>Increases the start U.<br><b>Hold shift for decimals</b></html>");

		btnPlusY.addActionListener(e ->
		{
			Element elem = manager.getCurrentElement();
			if (elem == null) return;
			double diff = ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1) ? 0.1 : 1;
			elem.setTexVStart(elem.getTexVStart() + diff);
			ModelCreator.updateValues(btnPlusX);
		});
		btnPlusY.setPreferredSize(new Dimension(62, 30));
		btnPlusY.setFont(defaultFont);
		btnPlusY.setToolTipText("<html>Increases the start V.<br><b>Hold shift for decimals</b></html>");

		btnNegX.addActionListener(e ->
		{
			Element elem = manager.getCurrentElement();
			if (elem == null) return;
			double diff = ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1) ? -0.1 : -1;
			elem.setTexUStart(elem.getTexUStart() + diff);
			ModelCreator.updateValues(btnNegX);
		});
		btnNegX.setSize(new Dimension(62, 30));
		btnNegX.setFont(defaultFont);
		btnNegX.setToolTipText("<html>Decreases the start U.<br><b>Hold shift for decimals</b></html>");

		btnNegY.addActionListener(e ->
		{
			Element elem = manager.getCurrentElement();
			if (elem == null) return;
			double diff = ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1) ? -0.1 : -1;
			elem.setTexVStart(elem.getTexVStart() + diff);
			ModelCreator.updateValues(btnNegY);

		});
		btnNegY.setSize(new Dimension(62, 30));
		btnNegY.setFont(defaultFont);
		btnNegY.setToolTipText("<html>Decreases the start V.<br><b>Hold shift for decimals</b></html>");
		
		
		unwrapPanel = new JPanel(new GridLayout(1, 1));
		unwrapPanel.setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>UV Unwrap Order</b></html>"));
		menuList = new JComboBox<String>();
		menuList.setModel(model);
		menuList.setToolTipText("How to unwrap the box UV map, choosing the right one will help you when texturing the model");
		menuList.addActionListener(e ->
		{
			if (ModelCreator.ignoreValueUpdates) return;
			if (manager.getCurrentElement() != null)
			{
				manager.getCurrentElement().setUnwrapMode(menuList.getSelectedIndex());
				manager.getCurrentElement().updateUV();
				updateValues(menuList);
			}
		});
		
		unwrapPanel.setPreferredSize(new Dimension(186, 50));
		unwrapPanel.add(menuList);
	}

	public void addComponents()
	{
		JPanel uvCoordPanel = new JPanel(new GridLayout(3, 4, 4, 4));
		
		uvCoordPanel.add(btnPlusX);
		uvCoordPanel.add(btnPlusY);
		uvCoordPanel.add(xStartField);
		uvCoordPanel.add(yStartField);
		uvCoordPanel.add(btnNegX);
		uvCoordPanel.add(btnNegY);
		
		add(uvCoordPanel);
		add(unwrapPanel);
	}

	@Override
	public void updateValues(JComponent byGuiElem)
	{
		Element cube = manager.getCurrentElement();
		boolean enabled = cube != null;
		
		xStartField.setEnabled(enabled);
		yStartField.setEnabled(enabled);
		menuList.setEnabled(enabled);

		if (cube != null)
		{			
			if (byGuiElem != xStartField) xStartField.setText(df.format(cube.getTexUStart()));
			if (byGuiElem != yStartField) yStartField.setText(df.format(cube.getTexVStart()));
			if (byGuiElem != menuList) menuList.setSelectedIndex(cube.getUnwrapMode());
		}
		else
		{
			xStartField.setText("");
			yStartField.setText("");
		}
	}
	
	

	public void modifyPosition(EnumAxis axis, int direction) {
		Element cube = manager.getCurrentElement();
		if (cube == null) return;
		
		float size = direction * 1f;
		
		switch (axis) {
			case X:
				cube.setTexUStart(cube.getTexUStart() + size);
				xStartField.setText(df.format(cube.getTexUStart()));
				break;
			case Y:
				cube.setTexVStart(cube.getTexVStart() + size);
				yStartField.setText(df.format(cube.getTexVStart()));
				break;
			default:
				break;
		}
		
		ModelCreator.updateValues(null);
		
	}

}
