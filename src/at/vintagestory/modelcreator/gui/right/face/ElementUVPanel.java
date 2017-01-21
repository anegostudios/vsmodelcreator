package at.vintagestory.modelcreator.gui.right.face;

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

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Start;
import at.vintagestory.modelcreator.gui.Icons;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;
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

	private DecimalFormat df = new DecimalFormat("#.#");

	public ElementUVPanel(IElementManager manager)
	{
		this.manager = manager;
		setLayout(new GridLayout(3, 4, 4, 4));
		setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>UV</b></html>"));
		setMaximumSize(new Dimension(186, 124));
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
	}

	public void initProperties()
	{
		Font defaultFont = new Font("SansSerif", Font.BOLD, 20);
		xStartField.setSize(new Dimension(62, 30));
		xStartField.setFont(defaultFont);
		xStartField.setHorizontalAlignment(JTextField.CENTER);
		xStartField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					Element elem = manager.getCurrentElement();
					if (elem == null) return;
					elem.setTexUStart(Parser.parseDouble(xStartField.getText(), elem.getTexUStart()));
					ModelCreator.updateValues();

				}
			}
		});
		xStartField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				Element elem = manager.getCurrentElement();
				if (elem == null) return;
				elem.setTexUStart(Parser.parseDouble(xStartField.getText(), elem.getTexUStart()));
				ModelCreator.updateValues();

			}
		});

		yStartField.setSize(new Dimension(62, 30));
		yStartField.setFont(defaultFont);
		yStartField.setHorizontalAlignment(JTextField.CENTER);
		yStartField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					Element elem = manager.getCurrentElement();
					if (elem == null) return;
					elem.setTexVStart(Parser.parseDouble(yStartField.getText(), elem.getTexVStart()));
					ModelCreator.updateValues();

				}
			}
		});
		yStartField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				Element elem = manager.getCurrentElement();
				if (elem == null) return;
				elem.setTexVStart(Parser.parseDouble(yStartField.getText(), elem.getTexVStart()));
				ModelCreator.updateValues();
			}
		});


		btnPlusX.addActionListener(e ->
		{
			Element elem = manager.getCurrentElement();
			if (elem == null) return;
			double diff = ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1) ? 0.1 : 1;
			elem.setTexUStart(elem.getTexUStart() + diff);
			ModelCreator.updateValues();
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
			ModelCreator.updateValues();
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
			ModelCreator.updateValues();
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
			ModelCreator.updateValues();

		});
		btnNegY.setSize(new Dimension(62, 30));
		btnNegY.setFont(defaultFont);
		btnNegY.setToolTipText("<html>Decreases the start V.<br><b>Hold shift for decimals</b></html>");
	}

	public void addComponents()
	{
		add(btnPlusX);
		add(btnPlusY);
		add(xStartField);
		add(yStartField);
		add(btnNegX);
		add(btnNegY);
	}

	@Override
	public void updateValues()
	{
		Element cube = manager.getCurrentElement();
		if (cube != null)
		{
			xStartField.setEnabled(true);
			yStartField.setEnabled(true);
			xStartField.setText(df.format(cube.getTexUStart()));
			yStartField.setText(df.format(cube.getTexVStart()));
		}
		else
		{
			xStartField.setEnabled(false);
			yStartField.setEnabled(false);
			xStartField.setText("");
			yStartField.setText("");
		}
	}
}
