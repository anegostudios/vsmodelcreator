package at.vintagestory.modelcreator.gui.right.element;

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

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Start;
import at.vintagestory.modelcreator.enums.EnumAxis;
import at.vintagestory.modelcreator.gui.Icons;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.util.Parser;

public class ElementSizePanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private IElementManager manager;

	private JButton btnPlusX;
	private JButton btnPlusY;
	private JButton btnPlusZ;
	private JTextField xSizeField;
	private JTextField ySizeField;
	private JTextField zSizeField;
	private JButton btnNegX;
	private JButton btnNegY;
	private JButton btnNegZ;
	
	boolean enabled = true;

	private DecimalFormat df = new DecimalFormat("#.#");

	public ElementSizePanel(IElementManager manager)
	{
		this.manager = manager;
		setLayout(new GridLayout(3, 3, 4, 0));
		setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>Size</b></html>"));
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
		xSizeField = new JTextField();
		ySizeField = new JTextField();
		zSizeField = new JTextField();
		btnNegX = new JButton(Icons.arrow_down_x);
		btnNegY = new JButton(Icons.arrow_down_y);
		btnNegZ = new JButton(Icons.arrow_down_z);
	}

	public void initProperties()
	{
		Font defaultFont = new Font("SansSerif", Font.BOLD, 20);
		xSizeField.setSize(new Dimension(62, 30));
		xSizeField.setFont(defaultFont);
		xSizeField.setHorizontalAlignment(JTextField.CENTER);
		xSizeField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					Element element = manager.getCurrentElement();
					if (element != null)
					{
						element.setWidth(Parser.parseDouble(xSizeField.getText(), element.getWidth()));
						element.updateUV();
						ModelCreator.updateValues();
					}

				}
			}
		});
		xSizeField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				Element element = manager.getCurrentElement();
				if (element != null)
				{
					element.setWidth(Parser.parseDouble(xSizeField.getText(), element.getWidth()));
					element.updateUV();
					ModelCreator.updateValues();
				}
			}
		});
		xSizeField.addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				modifySize(EnumAxis.X, e.getWheelRotation() > 0 ? 1 : -1, e.getModifiers());				
			}
		});

		ySizeField.setSize(new Dimension(62, 30));
		ySizeField.setFont(defaultFont);
		ySizeField.setHorizontalAlignment(JTextField.CENTER);
		ySizeField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					Element element = manager.getCurrentElement();
					if (element != null)
					{
						element.setHeight(Parser.parseDouble(ySizeField.getText(), element.getHeight()));
						element.updateUV();
						ModelCreator.updateValues();
					}

				}
			}
		});
		ySizeField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				Element element = manager.getCurrentElement();
				if (element != null)
				{
					element.setHeight(Parser.parseDouble(ySizeField.getText(), element.getHeight()));
					element.updateUV();
					ModelCreator.updateValues();
				}
			}
		});
		ySizeField.addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				modifySize(EnumAxis.Y, e.getWheelRotation() > 0 ? 1 : -1, e.getModifiers());				
			}
		});


		zSizeField.setSize(new Dimension(62, 30));
		zSizeField.setFont(defaultFont);
		zSizeField.setHorizontalAlignment(JTextField.CENTER);
		zSizeField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					Element element = manager.getCurrentElement();
					if (element != null)
					{
						element.setDepth(Parser.parseDouble(zSizeField.getText(), element.getDepth()));
						element.updateUV();
						ModelCreator.updateValues();
					}

				}
			}
		});
		zSizeField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				Element element = manager.getCurrentElement();
				if (element != null)
				{
					element.setDepth(Parser.parseDouble(zSizeField.getText(), element.getDepth()));
					element.updateUV();
					ModelCreator.updateValues();
				}
			}
		});
		zSizeField.addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				modifySize(EnumAxis.Z, e.getWheelRotation() > 0 ? 1 : -1, e.getModifiers());				
			}
		});


		btnPlusX.addActionListener(e ->
		{
			modifySize(EnumAxis.X, 1, e.getModifiers());
		});
		btnPlusX.setPreferredSize(new Dimension(62, 20));
		btnPlusX.setFont(defaultFont);
		btnPlusX.setToolTipText("<html>Increases the width.<br><b>Hold shift for decimals</b></html>");

		btnPlusY.addActionListener(e ->
		{
			modifySize(EnumAxis.Y, 1, e.getModifiers());
		});
		btnPlusY.setPreferredSize(new Dimension(62, 20));
		btnPlusY.setFont(defaultFont);
		btnPlusY.setToolTipText("<html>Increases the height.<br><b>Hold shift for decimals</b></html>");

		btnPlusZ.addActionListener(e ->
		{
			modifySize(EnumAxis.Z, 1, e.getModifiers());
		});
		btnPlusZ.setPreferredSize(new Dimension(62, 20));
		btnPlusZ.setFont(defaultFont);
		btnPlusZ.setToolTipText("<html>Increases the depth.<br><b>Hold shift for decimals</b></html>");

		btnNegX.addActionListener(e ->
		{
			modifySize(EnumAxis.X, -1, e.getModifiers());
		});
		btnNegX.setPreferredSize(new Dimension(62, 20));
		btnNegX.setFont(defaultFont);
		btnNegX.setToolTipText("<html>Decreases the width.<br><b>Hold shift for decimals</b></html>");

		btnNegY.addActionListener(e ->
		{
			modifySize(EnumAxis.Y, -1, e.getModifiers());
		});
		btnNegY.setPreferredSize(new Dimension(62, 20));
		btnNegY.setFont(defaultFont);
		btnNegY.setToolTipText("<html>Decreases the height.<br><b>Hold shift for decimals</b></html>");

		btnNegZ.addActionListener(e ->
		{
			modifySize(EnumAxis.Z, -1, e.getModifiers());
		});
		btnNegZ.setPreferredSize(new Dimension(62, 20));
		btnNegZ.setFont(defaultFont);
		btnNegZ.setToolTipText("<html>Decreases the depth.<br><b>Hold shift for decimals</b></html>");
	}

	public void addComponents()
	{
		add(btnPlusX);
		add(btnPlusY);
		add(btnPlusZ);
		add(xSizeField);
		add(ySizeField);
		add(zSizeField);
		add(btnNegX);
		add(btnNegY);
		add(btnNegZ);
	}
	
	
	public void modifySize(EnumAxis axis, int direction, int modifiers) {
		Element cube = manager.getCurrentElement();
		if (cube == null) return;
		
		float size = direction * ((modifiers & ActionEvent.SHIFT_MASK) == 1 ? 0.1f : 1f);
		
		switch (axis) {
		case X:
			cube.addWidth(size);
			break;
		case Y:
			cube.addHeight(size);
			break;
		default:
			cube.addDepth(size);			
			break;
		}
		
		cube.updateUV();
		ModelCreator.updateValues();
	}


	@Override
	public void updateValues()
	{
		Element cube = manager.getCurrentElement();
		boolean enabled = cube != null && this.enabled;
		btnPlusX.setEnabled(enabled);
		btnPlusY.setEnabled(enabled);
		btnPlusZ.setEnabled(enabled);
		btnNegX.setEnabled(enabled);
		btnNegY.setEnabled(enabled);
		btnNegZ.setEnabled(enabled);
		
		xSizeField.setEnabled(enabled);
		ySizeField.setEnabled(enabled);
		zSizeField.setEnabled(enabled);
		xSizeField.setText(enabled ? df.format(cube.getWidth()) : "");
		ySizeField.setText(enabled ? df.format(cube.getHeight()) : "");
		zSizeField.setText(enabled ? df.format(cube.getDepth()) : "");
	}
}
