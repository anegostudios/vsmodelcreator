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

	private DecimalFormat df = new DecimalFormat("#.#");

	public ElementSizePanel(IElementManager manager)
	{
		this.manager = manager;
		setLayout(new GridLayout(3, 3, 4, 4));
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(221, 221, 228), 5), "<html><b>Size</b></html>"));
		setMaximumSize(new Dimension(186, 124));
		initComponents();
		initProperties();
		addComponents();
	}

	public void initComponents()
	{
		btnPlusX = new JButton(Icons.arrow_up);
		btnPlusY = new JButton(Icons.arrow_up);
		btnPlusZ = new JButton(Icons.arrow_up);
		xSizeField = new JTextField();
		ySizeField = new JTextField();
		zSizeField = new JTextField();
		btnNegX = new JButton(Icons.arrow_down);
		btnNegY = new JButton(Icons.arrow_down);
		btnNegZ = new JButton(Icons.arrow_down);
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
					Element element = manager.getSelectedElement();
					if (element != null)
					{
						element.setWidth(Parser.parseDouble(xSizeField.getText(), element.getWidth()));
						element.updateUV();
						manager.updateValues();
					}

				}
			}
		});
		xSizeField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				Element element = manager.getSelectedElement();
				if (element != null)
				{
					element.setWidth(Parser.parseDouble(xSizeField.getText(), element.getWidth()));
					element.updateUV();
					manager.updateValues();
				}
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
					Element element = manager.getSelectedElement();
					if (element != null)
					{
						element.setHeight(Parser.parseDouble(ySizeField.getText(), element.getHeight()));
						element.updateUV();
						manager.updateValues();
					}

				}
			}
		});
		ySizeField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				Element element = manager.getSelectedElement();
				if (element != null)
				{
					element.setHeight(Parser.parseDouble(ySizeField.getText(), element.getHeight()));
					element.updateUV();
					manager.updateValues();
				}
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
					Element element = manager.getSelectedElement();
					if (element != null)
					{
						element.setDepth(Parser.parseDouble(zSizeField.getText(), element.getDepth()));
						element.updateUV();
						manager.updateValues();
					}

				}
			}
		});
		zSizeField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				Element element = manager.getSelectedElement();
				if (element != null)
				{
					element.setDepth(Parser.parseDouble(zSizeField.getText(), element.getDepth()));
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
					cube.addWidth(0.1F);
				}
				else
				{
					cube.addWidth(1.0F);
				}
				cube.updateUV();
				manager.updateValues();
			}
		});
		btnPlusX.setPreferredSize(new Dimension(62, 30));
		btnPlusX.setFont(defaultFont);
		btnPlusX.setToolTipText("<html>Increases the width.<br><b>Hold shift for decimals</b></html>");

		btnPlusY.addActionListener(e ->
		{
			if (manager.getSelectedElement() != null)
			{
				Element cube = manager.getSelectedElement();
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					cube.addHeight(0.1F);
				}
				else
				{
					cube.addHeight(1.0F);
				}
				cube.updateUV();
				manager.updateValues();
			}
		});
		btnPlusY.setPreferredSize(new Dimension(62, 30));
		btnPlusY.setFont(defaultFont);
		btnPlusY.setToolTipText("<html>Increases the height.<br><b>Hold shift for decimals</b></html>");

		btnPlusZ.addActionListener(e ->
		{
			if (manager.getSelectedElement() != null)
			{
				Element cube = manager.getSelectedElement();
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					cube.addDepth(0.1F);
				}
				else
				{
					cube.addDepth(1.0F);
				}
				cube.updateUV();
				manager.updateValues();
			}
		});
		btnPlusZ.setPreferredSize(new Dimension(62, 30));
		btnPlusZ.setFont(defaultFont);
		btnPlusZ.setToolTipText("<html>Increases the depth.<br><b>Hold shift for decimals</b></html>");

		btnNegX.addActionListener(e ->
		{
			if (manager.getSelectedElement() != null)
			{
				Element cube = manager.getSelectedElement();
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					cube.addWidth(-0.1F);
				}
				else
				{
					cube.addWidth(-1.0F);
				}
				cube.updateUV();
				manager.updateValues();
			}
		});
		btnNegX.setPreferredSize(new Dimension(62, 30));
		btnNegX.setFont(defaultFont);
		btnNegX.setToolTipText("<html>Decreases the width.<br><b>Hold shift for decimals</b></html>");

		btnNegY.addActionListener(e ->
		{
			if (manager.getSelectedElement() != null)
			{
				Element cube = manager.getSelectedElement();
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					cube.addHeight(-0.1F);
				}
				else
				{
					cube.addHeight(-1.0F);
				}
				cube.updateUV();
				manager.updateValues();
			}
		});
		btnNegY.setPreferredSize(new Dimension(62, 30));
		btnNegY.setFont(defaultFont);
		btnNegY.setToolTipText("<html>Decreases the height.<br><b>Hold shift for decimals</b></html>");

		btnNegZ.addActionListener(e ->
		{
			if (manager.getSelectedElement() != null)
			{
				Element cube = manager.getSelectedElement();
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					cube.addDepth(-0.1F);
				}
				else
				{
					cube.addDepth(-1.0F);
				}
				cube.updateUV();
				manager.updateValues();
			}
		});
		btnNegZ.setPreferredSize(new Dimension(62, 30));
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

	@Override
	public void updateValues(Element cube)
	{
		if (cube != null)
		{
			xSizeField.setEnabled(true);
			ySizeField.setEnabled(true);
			zSizeField.setEnabled(true);
			xSizeField.setText(df.format(cube.getWidth()));
			ySizeField.setText(df.format(cube.getHeight()));
			zSizeField.setText(df.format(cube.getDepth()));
		}
		else
		{
			xSizeField.setEnabled(false);
			ySizeField.setEnabled(false);
			zSizeField.setEnabled(false);
			xSizeField.setText("");
			ySizeField.setText("");
			zSizeField.setText("");
		}
	}
}
