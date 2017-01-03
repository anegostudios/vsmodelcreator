package at.vintagestory.modelcreator.gui.right.element;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import at.vintagestory.modelcreator.gui.Icons;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.util.Parser;

public class ElementPositionPanel extends JPanel implements IValueUpdater
{

	private static final long serialVersionUID = 1L;

	private IElementManager manager;

	private JButton btnPlusX;
	private JButton btnPlusY;
	private JButton btnPlusZ;
	private JTextField xPositionField;
	private JTextField yPositionField;
	private JTextField zPositionField;
	private JButton btnNegX;
	private JButton btnNegY;
	private JButton btnNegZ;

	private DecimalFormat df = new DecimalFormat("#.#");

	public ElementPositionPanel(IElementManager manager)
	{
		this.manager = manager;
		setLayout(new GridLayout(3, 3, 4, 4));
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(221, 221, 228), 5), "<html><b>Position</b></html>"));
		setMaximumSize(new Dimension(186, 124));
		setAlignmentX(JPanel.CENTER_ALIGNMENT);
		initComponents();
		initProperties();
		addComponents();
	}

	public void initComponents()
	{
		btnPlusX = new JButton(Icons.arrow_up);
		btnPlusY = new JButton(Icons.arrow_up);
		btnPlusZ = new JButton(Icons.arrow_up);
		xPositionField = new JTextField();
		yPositionField = new JTextField();
		zPositionField = new JTextField();
		btnNegX = new JButton(Icons.arrow_down);
		btnNegY = new JButton(Icons.arrow_down);
		btnNegZ = new JButton(Icons.arrow_down);
	}

	public void initProperties()
	{
		Font defaultFont = new Font("SansSerif", Font.BOLD, 20);
		xPositionField.setSize(new Dimension(62, 30));
		xPositionField.setFont(defaultFont);
		xPositionField.setHorizontalAlignment(JTextField.CENTER);
		xPositionField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					Element element = manager.getSelectedElement();
					if (element != null)
					{
						element.setStartX(Parser.parseDouble(xPositionField.getText(), element.getStartX()));
						element.updateUV();
						manager.updateValues();
					}

				}
			}
		});
		xPositionField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				Element element = manager.getSelectedElement();
				if (element != null)
				{
					element.setStartX(Parser.parseDouble(xPositionField.getText(), element.getStartX()));
					element.updateUV();
					manager.updateValues();
				}
			}
		});

		yPositionField.setSize(new Dimension(62, 30));
		yPositionField.setFont(defaultFont);
		yPositionField.setHorizontalAlignment(JTextField.CENTER);
		yPositionField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					Element element = manager.getSelectedElement();
					if (element != null)
					{
						element.setStartY(Parser.parseDouble(yPositionField.getText(), element.getStartY()));
						element.updateUV();
						manager.updateValues();
					}

				}
			}
		});
		yPositionField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				Element element = manager.getSelectedElement();
				if (element != null)
				{
					element.setStartY(Parser.parseDouble(yPositionField.getText(), element.getStartY()));
					element.updateUV();
					manager.updateValues();
				}
			}
		});

		zPositionField.setSize(new Dimension(62, 30));
		zPositionField.setFont(defaultFont);
		zPositionField.setHorizontalAlignment(JTextField.CENTER);
		zPositionField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					Element element = manager.getSelectedElement();
					if (element != null)
					{
						element.setStartZ(Parser.parseDouble(zPositionField.getText(), element.getStartZ()));
						element.updateUV();
						manager.updateValues();
					}

				}
			}
		});
		zPositionField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				Element element = manager.getSelectedElement();
				if (element != null)
				{
					element.setStartZ(Parser.parseDouble(zPositionField.getText(), element.getStartZ()));
					element.updateUV();
					manager.updateValues();
				}
			}
		});

		btnPlusX.addActionListener(e ->
		{
			if (manager.getSelectedElement() != null)
			{
				Element cube = manager.getSelectedElement();
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					cube.addStartX(0.1F);
				}
				else
				{
					cube.addStartX(1.0F);
				}
				xPositionField.setText(df.format(cube.getStartX()));
			}
		});
		btnPlusX.setPreferredSize(new Dimension(62, 30));
		btnPlusX.setFont(defaultFont);
		btnPlusX.setToolTipText("<html>Increases the X position.<br><b>Hold shift for decimals</b></html>");

		btnPlusY.addActionListener(e ->
		{
			if (manager.getSelectedElement() != null)
			{
				Element cube = manager.getSelectedElement();
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					cube.addStartY(0.1F);
				}
				else
				{
					cube.addStartY(1.0F);
				}
				yPositionField.setText(df.format(cube.getStartY()));
			}
		});
		btnPlusY.setPreferredSize(new Dimension(62, 30));
		btnPlusY.setFont(defaultFont);
		btnPlusY.setToolTipText("<html>Increases the Y position.<br><b>Hold shift for decimals</b></html>");

		btnPlusZ.addActionListener(e ->
		{
			if (manager.getSelectedElement() != null)
			{
				Element cube = manager.getSelectedElement();
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					cube.addStartZ(0.1F);
				}
				else
				{
					cube.addStartZ(1.0F);
				}
				zPositionField.setText(df.format(cube.getStartZ()));
			}
		});
		btnPlusZ.setPreferredSize(new Dimension(62, 30));
		btnPlusZ.setFont(defaultFont);
		btnPlusZ.setToolTipText("<html>Increases the Z position.<br><b>Hold shift for decimals</b></html>");

		btnNegX.addActionListener(e ->
		{
			if (manager.getSelectedElement() != null)
			{
				Element cube = manager.getSelectedElement();
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					cube.addStartX(-0.1F);
				}
				else
				{
					cube.addStartX(-1.0F);
				}
				xPositionField.setText(df.format(cube.getStartX()));
			}
		});
		btnNegX.setPreferredSize(new Dimension(62, 30));
		btnNegX.setFont(defaultFont);
		btnNegX.setToolTipText("<html>Decreases the X position.<br><b>Hold shift for decimals</b></html>");

		btnNegY.addActionListener(e ->
		{
			if (manager.getSelectedElement() != null)
			{
				Element cube = manager.getSelectedElement();
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					cube.addStartY(-0.1F);
				}
				else
				{
					cube.addStartY(-1.0F);
				}
				yPositionField.setText(df.format(cube.getStartY()));
			}
		});
		btnNegY.setPreferredSize(new Dimension(62, 30));
		btnNegY.setFont(defaultFont);
		btnNegY.setToolTipText("<html>Decreases the Y position.<br><b>Hold shift for decimals</b></html>");

		btnNegZ.addActionListener(e ->
		{
			if (manager.getSelectedElement() != null)
			{
				Element cube = manager.getSelectedElement();
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					cube.addStartZ(-0.1F);
				}
				else
				{
					cube.addStartZ(-1.0F);
				}
				zPositionField.setText(df.format(cube.getStartZ()));
			}
		});
		btnNegZ.setPreferredSize(new Dimension(62, 30));
		btnNegZ.setFont(defaultFont);
		btnNegZ.setToolTipText("<html>Decreases the Z position.<br><b>Hold shift for decimals</b></html>");
	}

	public void addComponents()
	{
		add(btnPlusX);
		add(btnPlusY);
		add(btnPlusZ);
		add(xPositionField);
		add(yPositionField);
		add(zPositionField);
		add(btnNegX);
		add(btnNegY);
		add(btnNegZ);
	}

	@Override
	public void updateValues(Element cube)
	{
		if (cube != null)
		{
			xPositionField.setEnabled(true);
			xPositionField.setEnabled(true);
			xPositionField.setEnabled(true);
			xPositionField.setText(df.format(cube.getStartX()));
			yPositionField.setText(df.format(cube.getStartY()));
			zPositionField.setText(df.format(cube.getStartZ()));
		}
		else
		{
			xPositionField.setEnabled(false);
			xPositionField.setEnabled(false);
			xPositionField.setEnabled(false);
			xPositionField.setText("");
			yPositionField.setText("");
			zPositionField.setText("");
		}
	}
}
