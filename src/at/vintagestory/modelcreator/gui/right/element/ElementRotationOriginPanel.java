package at.vintagestory.modelcreator.gui.right.element;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

public class ElementRotationOriginPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private IElementManager manager;

	private JButton btnPlusX;
	private JButton btnPlusY;
	private JButton btnPlusZ;
	private JTextField xOriginField;
	private JTextField yOriginField;
	private JTextField zOriginField;
	private JButton btnNegX;
	private JButton btnNegY;
	private JButton btnNegZ;

	private DecimalFormat df = new DecimalFormat("#.##");
	
	boolean enabled = true;

	public ElementRotationOriginPanel(IElementManager manager)
	{
		this.manager = manager;
		setLayout(new GridLayout(3, 3, 4, 0));
		setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>Origin</b></html>"));
		setMaximumSize(new Dimension(186, 104));
		initComponents();
		initProperties();
		addComponents();
	}

	public void initComponents()
	{
		btnPlusX = new JButton(Icons.arrow_up_x);
		btnPlusY = new JButton(Icons.arrow_up_y);
		btnPlusZ = new JButton(Icons.arrow_up_z);
		xOriginField = new JTextField();
		yOriginField = new JTextField();
		zOriginField = new JTextField();
		btnNegX = new JButton(Icons.arrow_down_x);
		btnNegY = new JButton(Icons.arrow_down_y);
		btnNegZ = new JButton(Icons.arrow_down_z);
	}

	public void initProperties()
	{
		Font defaultFont = new Font("SansSerif", Font.BOLD, 20);
		xOriginField.setSize(new Dimension(62, 30));
		xOriginField.setFont(defaultFont);
		xOriginField.setHorizontalAlignment(JTextField.CENTER);
		
		AwtUtil.addChangeListener(xOriginField, e -> {
			Element element = manager.getCurrentElement();
			if (element != null)
			{
				element.setOriginX((Parser.parseDouble(xOriginField.getText(), element.getOriginX())));
				ModelCreator.updateValues(xOriginField);
			}
		});
		
		xOriginField.addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				modifyPosition(EnumAxis.X, e.getWheelRotation() > 0 ? 1 : -1, e.getModifiers());
			}
		});

		yOriginField.setSize(new Dimension(62, 30));
		yOriginField.setFont(defaultFont);
		yOriginField.setHorizontalAlignment(JTextField.CENTER);
		
		
		
		AwtUtil.addChangeListener(yOriginField, e -> {
			Element element = manager.getCurrentElement();
			if (element != null)
			{
				element.setOriginY((Parser.parseDouble(yOriginField.getText(), element.getOriginY())));
				ModelCreator.updateValues(yOriginField);
			}
		});
		
		yOriginField.addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				modifyPosition(EnumAxis.Y, e.getWheelRotation() > 0 ? 1 : -1, e.getModifiers());
			}
		});

		zOriginField.setSize(new Dimension(62, 30));
		zOriginField.setFont(defaultFont);
		zOriginField.setHorizontalAlignment(JTextField.CENTER);

		AwtUtil.addChangeListener(zOriginField, e -> {
			Element element = manager.getCurrentElement();
			if (element != null)
			{
				element.setOriginZ((Parser.parseDouble(zOriginField.getText(), element.getOriginZ())));
				ModelCreator.updateValues(zOriginField);
			}
		});
		
		
		zOriginField.addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				modifyPosition(EnumAxis.Z, e.getWheelRotation() > 0 ? 1 : -1, e.getModifiers());				
			}
		});
		

		btnPlusX.addActionListener(e ->
		{
			modifyPosition(EnumAxis.X, 1, e.getModifiers());
		});
		btnPlusX.setPreferredSize(new Dimension(62, 20));
		btnPlusX.setFont(defaultFont);
		btnPlusX.setToolTipText("<html>Increases the X origin.<br><b>Hold shift for decimals</b></html>");

		
		btnPlusY.addActionListener(e ->
		{
			modifyPosition(EnumAxis.Y, 1, e.getModifiers());
		});
		btnPlusY.setPreferredSize(new Dimension(62, 20));
		btnPlusY.setFont(defaultFont);
		btnPlusY.setToolTipText("<html>Increases the Y origin.<br><b>Hold shift for decimals</b></html>");

		btnPlusZ.addActionListener(e ->
		{
			modifyPosition(EnumAxis.Z, 1, e.getModifiers());
		});
		btnPlusZ.setPreferredSize(new Dimension(62, 20));
		btnPlusZ.setFont(defaultFont);
		btnPlusZ.setToolTipText("<html>Increases the Z origin.<br><b>Hold shift for decimals</b></html>");

		btnNegX.addActionListener(e ->
		{
			modifyPosition(EnumAxis.X, -1, e.getModifiers());
		});
		btnNegX.setPreferredSize(new Dimension(62, 20));
		btnNegX.setFont(defaultFont);
		btnNegX.setToolTipText("<html>Decreases the X origin.<br><b>Hold shift for decimals</b></html>");

		btnNegY.addActionListener(e ->
		{
			modifyPosition(EnumAxis.Y, -1, e.getModifiers());
		});
		btnNegY.setPreferredSize(new Dimension(62, 20));
		btnNegY.setFont(defaultFont);
		btnNegY.setToolTipText("<html>Decreases the Y origin.<br><b>Hold shift for decimals</b></html>");

		btnNegZ.addActionListener(e ->
		{
			modifyPosition(EnumAxis.Z, -1, e.getModifiers());
		});
		btnNegZ.setPreferredSize(new Dimension(62, 20));
		btnNegZ.setFont(defaultFont);
		btnNegZ.setToolTipText("<html>Decreases the Z origin.<br><b>Hold shift for decimals</b></html>");
	}

	public void addComponents()
	{
		add(btnPlusX);
		add(btnPlusY);
		add(btnPlusZ);
		add(xOriginField);
		add(yOriginField);
		add(zOriginField);
		add(btnNegX);
		add(btnNegY);
		add(btnNegZ);
	}

	@Override
	public void updateValues(JComponent byGuiElem)
	{
		Element cube = manager.getCurrentElement();
		boolean enabled = cube != null && this.enabled;
		
		btnPlusX.setEnabled(enabled);
		btnPlusY.setEnabled(enabled);
		btnPlusZ.setEnabled(enabled);
		btnNegX.setEnabled(enabled);
		btnNegY.setEnabled(enabled);
		btnNegZ.setEnabled(enabled);
		
		xOriginField.setEnabled(enabled);
		yOriginField.setEnabled(enabled);
		zOriginField.setEnabled(enabled);
		if (byGuiElem != xOriginField) xOriginField.setText(enabled ? df.format(cube.getOriginX()) : "");
		if (byGuiElem != yOriginField) yOriginField.setText(enabled ? df.format(cube.getOriginY()) : "");
		if (byGuiElem != zOriginField) zOriginField.setText(enabled ? df.format(cube.getOriginZ()) : "");
	}
	

	public void modifyPosition(EnumAxis axis, int direction, int modifiers) {
		Element cube = manager.getCurrentElement();
		if (cube == null) return;		
		float size = direction * ((modifiers & ActionEvent.SHIFT_MASK) == 1 ? 0.1f : 1f);
		
		switch (axis) {
		case X:
			cube.addOriginX(size);
			xOriginField.setText(df.format(cube.getOriginX()));
			break;
		case Y:
			cube.addOriginY(size);
			yOriginField.setText(df.format(cube.getOriginY()));
			break;
		default:
			cube.addOriginZ(size);
			zOriginField.setText(df.format(cube.getOriginZ()));			
			break;
		}
	}
}
