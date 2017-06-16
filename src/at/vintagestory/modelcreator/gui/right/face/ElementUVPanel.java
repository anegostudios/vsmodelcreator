package at.vintagestory.modelcreator.gui.right.face;

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
		setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>UV (all Faces)</b></html>"));
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
	public void updateValues(JComponent byGuiElem)
	{
		Element cube = manager.getCurrentElement();
		if (cube != null)
		{
			xStartField.setEnabled(true);
			yStartField.setEnabled(true);
			if (byGuiElem != xStartField) xStartField.setText(df.format(cube.getTexUStart()));
			if (byGuiElem != yStartField) yStartField.setText(df.format(cube.getTexVStart()));
		}
		else
		{
			xStartField.setEnabled(false);
			yStartField.setEnabled(false);
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
