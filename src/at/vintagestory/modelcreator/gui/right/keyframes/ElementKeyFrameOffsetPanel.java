package at.vintagestory.modelcreator.gui.right.keyframes;

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
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.KeyframeElement;
import at.vintagestory.modelcreator.util.Parser;

public class ElementKeyFrameOffsetPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private RightKeyFramesPanel keyFramesPanel;

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
	
	public boolean enabled = true;

	public ElementKeyFrameOffsetPanel(RightKeyFramesPanel keyFramesPanel)
	{
		this.keyFramesPanel = keyFramesPanel;
		setLayout(new GridLayout(3, 3, 4, 0));
		setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>Position</b></html>"));
		setMaximumSize(new Dimension(186, 104));
		setAlignmentX(JPanel.CENTER_ALIGNMENT);
		initComponents();
		initProperties();
		addComponents();
	}

	public void initComponents()
	{
		btnPlusX = new JButton(Icons.arrow_up_x);
		btnPlusY = new JButton(Icons.arrow_up_y);
		btnPlusZ = new JButton(Icons.arrow_up_z);
		xPositionField = new JTextField();
		yPositionField = new JTextField();
		zPositionField = new JTextField();
		btnNegX = new JButton(Icons.arrow_down_x);
		btnNegY = new JButton(Icons.arrow_down_y);
		btnNegZ = new JButton(Icons.arrow_down_z);
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
					KeyframeElement element = keyFramesPanel.getCurrentElement();
					if (element != null)
					{
						element.setOffsetX(Parser.parseDouble(xPositionField.getText(), element.getOffsetX()));
						ModelCreator.updateValues();
					}

				}
			}
		});
		xPositionField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				KeyframeElement element = keyFramesPanel.getCurrentElement();
				if (element != null)
				{
					element.setOffsetX(Parser.parseDouble(xPositionField.getText(), element.getOffsetX()));
					ModelCreator.updateValues();
				}
			}
		});
		xPositionField.addMouseWheelListener(new MouseWheelListener()
		{
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				int notches = e.getWheelRotation();
				modifyPosition(EnumAxis.X, (notches > 0 ? 1 : -1), e.getModifiers());
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
					KeyframeElement element = keyFramesPanel.getCurrentElement();
					if (element != null)
					{
						element.setOffsetY(Parser.parseDouble(yPositionField.getText(), element.getOffsetY()));
						ModelCreator.updateValues();
					}

				}
			}
		});
		yPositionField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				KeyframeElement element = keyFramesPanel.getCurrentElement();
				if (element != null)
				{
					element.setOffsetY(Parser.parseDouble(yPositionField.getText(), element.getOffsetY()));
					ModelCreator.updateValues();
				}
			}
		});
		yPositionField.addMouseWheelListener(new MouseWheelListener()
		{
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				int notches = e.getWheelRotation();
				modifyPosition(EnumAxis.Y, (notches > 0 ? 1 : -1), e.getModifiers());
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
					KeyframeElement element = keyFramesPanel.getCurrentElement();
					if (element != null)
					{
						element.setOffsetZ(Parser.parseDouble(zPositionField.getText(), element.getOffsetZ()));
						ModelCreator.updateValues();
					}

				}
			}
		});
		
		zPositionField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				KeyframeElement element = keyFramesPanel.getCurrentElement();
				if (element != null)
				{
					element.setOffsetZ(Parser.parseDouble(zPositionField.getText(), element.getOffsetZ()));
					ModelCreator.updateValues();
				}
			}
		});
		
		zPositionField.addMouseWheelListener(new MouseWheelListener()
		{
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				int notches = e.getWheelRotation();
				modifyPosition(EnumAxis.Z, (notches > 0 ? 1 : -1), e.getModifiers());
			}
		});


		btnPlusX.addActionListener(e ->
		{
			modifyPosition(EnumAxis.X, 1, e.getModifiers());
		});
		btnPlusX.setPreferredSize(new Dimension(62, 20));
		btnPlusX.setFont(defaultFont);
		btnPlusX.setToolTipText("<html>Increases the X position.<br><b>Hold shift for decimals</b><br><b>Hold ctrl to also move the rotation origin</b></html>");

		btnPlusY.addActionListener(e ->
		{
			modifyPosition(EnumAxis.Y, 1, e.getModifiers());
		});
		btnPlusY.setPreferredSize(new Dimension(62, 20));
		btnPlusY.setFont(defaultFont);
		btnPlusY.setToolTipText("<html>Increases the Y position.<br><b>Hold shift for decimals</b><br><b>Hold ctrl to also move the rotation origin</b></html>");

		btnPlusZ.addActionListener(e ->
		{
			modifyPosition(EnumAxis.Z, 1, e.getModifiers());
		});
		btnPlusZ.setPreferredSize(new Dimension(62, 20));
		btnPlusZ.setFont(defaultFont);
		btnPlusZ.setToolTipText("<html>Increases the Z position.<br><b>Hold shift for decimals</b><br><b>Hold ctrl to also move the rotation origin</b></html>");

		btnNegX.addActionListener(e ->
		{
			modifyPosition(EnumAxis.X, -1, e.getModifiers());
		});
		btnNegX.setPreferredSize(new Dimension(62, 20));
		btnNegX.setFont(defaultFont);
		btnNegX.setToolTipText("<html>Decreases the X position.<br><b>Hold shift for decimals</b><br><b>Hold ctrl to also move the rotation origin</b></html>");

		btnNegY.addActionListener(e ->
		{
			modifyPosition(EnumAxis.Y, -1, e.getModifiers());
		});
		btnNegY.setPreferredSize(new Dimension(62, 20));
		btnNegY.setFont(defaultFont);
		btnNegY.setToolTipText("<html>Decreases the Y position.<br><b>Hold shift for decimals</b><br><b>Hold ctrl to also move the rotation origin</b></html>");

		btnNegZ.addActionListener(e ->
		{
			modifyPosition(EnumAxis.Z, -1, e.getModifiers());
		});
		btnNegZ.setPreferredSize(new Dimension(62, 20));
		btnNegZ.setFont(defaultFont);
		btnNegZ.setToolTipText("<html>Decreases the Z position.<br><b>Hold shift for decimals</b><br><b>Hold ctrl to also move the rotation origin</b></html>");
	}
	
	
	public void modifyPosition(EnumAxis axis, int direction, int modifiers) {
		KeyframeElement cube = keyFramesPanel.getCurrentElement();
		if (cube == null) return;
		
		//float size = direction * ((modifiers & ActionEvent.SHIFT_MASK) == 1 ? 0.1f : 1f);
		//boolean ctrl = (modifiers & ActionEvent.CTRL_MASK) > 0;
		
		switch (axis) {
		case X:
			cube.setOffsetX(cube.getOffsetX() + direction);
			//if (ctrl) cube.addOriginX(size);
			
			xPositionField.setText(df.format(cube.getOffsetX()));
			
			break;
		case Y:
			cube.setOffsetY(cube.getOffsetY() + direction);
			//if (ctrl) cube.addOriginY(size);
			
			yPositionField.setText(df.format(cube.getOffsetY()));
			break;
		default:
			cube.setOffsetZ(cube.getOffsetZ() + direction);
			//if (ctrl) cube.addOriginZ(size);
			
			zPositionField.setText(df.format(cube.getOffsetZ()));
			break;
		}
		
		ModelCreator.updateValues();
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
	public void updateValues()
	{
		KeyframeElement cube = keyFramesPanel.getCurrentElement();
		toggleFields(cube);
	}
	
	
	public void toggleFields(KeyframeElement cube) {
		boolean enabled = cube != null && this.enabled;
		btnPlusX.setEnabled(enabled);
		btnPlusY.setEnabled(enabled);
		btnPlusZ.setEnabled(enabled);
		btnNegX.setEnabled(enabled);
		btnNegY.setEnabled(enabled);
		btnNegZ.setEnabled(enabled);
		
		xPositionField.setEnabled(enabled);
		yPositionField.setEnabled(enabled);
		zPositionField.setEnabled(enabled);
		xPositionField.setText(enabled ? df.format(cube.getOffsetX()) : "");
		yPositionField.setText(enabled ? df.format(cube.getOffsetY()) : "");
		zPositionField.setText(enabled ? df.format(cube.getOffsetZ()) : "");
	}
}
