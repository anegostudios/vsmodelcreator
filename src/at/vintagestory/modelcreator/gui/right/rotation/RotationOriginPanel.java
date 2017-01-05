package at.vintagestory.modelcreator.gui.right.rotation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import at.vintagestory.modelcreator.enums.EnumAxis;
import at.vintagestory.modelcreator.gui.Icons;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.util.Parser;

public class RotationOriginPanel extends JPanel implements IValueUpdater
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

	private DecimalFormat df = new DecimalFormat("#.#");

	public RotationOriginPanel(IElementManager manager)
	{
		this.manager = manager;
		setLayout(new GridLayout(3, 3, 4, 4));
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(221, 221, 228), 5), "<html><b>Origin</b></html>"));
		setMaximumSize(new Dimension(186, 124));
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
		xOriginField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					Element element = manager.getSelectedElement();
					if (element != null)
					{
						element.setOriginX((Parser.parseDouble(xOriginField.getText(), element.getOriginX())));
						manager.updateValues();
					}
				}
			}
		});
		xOriginField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				Element element = manager.getSelectedElement();
				if (element != null)
				{
					element.setOriginX((Parser.parseDouble(xOriginField.getText(), element.getOriginX())));
					manager.updateValues();
				}
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
		yOriginField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					Element element = manager.getSelectedElement();
					if (element != null)
					{
						element.setOriginY((Parser.parseDouble(yOriginField.getText(), element.getOriginY())));
						manager.updateValues();
					}
				}
			}
		});
		yOriginField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				Element element = manager.getSelectedElement();
				if (element != null)
				{
					element.setOriginY((Parser.parseDouble(yOriginField.getText(), element.getOriginY())));
					manager.updateValues();
				}
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
		zOriginField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					Element element = manager.getSelectedElement();
					if (element != null)
					{
						element.setOriginZ((Parser.parseDouble(zOriginField.getText(), element.getOriginZ())));
						manager.updateValues();
					}
				}
			}
		});
		
		zOriginField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				Element element = manager.getSelectedElement();
				if (element != null)
				{
					element.setOriginZ((Parser.parseDouble(zOriginField.getText(), element.getOriginZ())));
					manager.updateValues();
				}
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
		btnPlusX.setPreferredSize(new Dimension(62, 30));
		btnPlusX.setFont(defaultFont);
		btnPlusX.setToolTipText("<html>Increases the X origin.<br><b>Hold shift for decimals</b></html>");

		
		btnPlusY.addActionListener(e ->
		{
			modifyPosition(EnumAxis.Y, 1, e.getModifiers());
		});
		btnPlusY.setPreferredSize(new Dimension(62, 30));
		btnPlusY.setFont(defaultFont);
		btnPlusY.setToolTipText("<html>Increases the Y origin.<br><b>Hold shift for decimals</b></html>");

		btnPlusZ.addActionListener(e ->
		{
			modifyPosition(EnumAxis.Z, 1, e.getModifiers());
		});
		btnPlusZ.setPreferredSize(new Dimension(62, 30));
		btnPlusZ.setFont(defaultFont);
		btnPlusZ.setToolTipText("<html>Increases the Z origin.<br><b>Hold shift for decimals</b></html>");

		btnNegX.addActionListener(e ->
		{
			modifyPosition(EnumAxis.X, -1, e.getModifiers());
		});
		btnNegX.setPreferredSize(new Dimension(62, 30));
		btnNegX.setFont(defaultFont);
		btnNegX.setToolTipText("<html>Decreases the X origin.<br><b>Hold shift for decimals</b></html>");

		btnNegY.addActionListener(e ->
		{
			modifyPosition(EnumAxis.Y, -1, e.getModifiers());
		});
		btnNegY.setPreferredSize(new Dimension(62, 30));
		btnNegY.setFont(defaultFont);
		btnNegY.setToolTipText("<html>Decreases the Y origin.<br><b>Hold shift for decimals</b></html>");

		btnNegZ.addActionListener(e ->
		{
			modifyPosition(EnumAxis.Z, -1, e.getModifiers());
		});
		btnNegZ.setPreferredSize(new Dimension(62, 30));
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
	public void updateValues(Element cube)
	{
		if (cube != null)
		{
			xOriginField.setEnabled(true);
			yOriginField.setEnabled(true);
			zOriginField.setEnabled(true);
			xOriginField.setText(df.format(cube.getOriginX()));
			yOriginField.setText(df.format(cube.getOriginY()));
			zOriginField.setText(df.format(cube.getOriginZ()));
		}
		else
		{
			xOriginField.setEnabled(false);
			yOriginField.setEnabled(false);
			zOriginField.setEnabled(false);
			xOriginField.setText("");
			yOriginField.setText("");
			zOriginField.setText("");
		}
	}
	

	public void modifyPosition(EnumAxis axis, int direction, int modifiers) {
		Element cube = manager.getSelectedElement();
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
