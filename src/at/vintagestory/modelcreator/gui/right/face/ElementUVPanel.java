package at.vintagestory.modelcreator.gui.right.face;

import java.awt.Checkbox;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Start;
import at.vintagestory.modelcreator.enums.EnumAxis;
import at.vintagestory.modelcreator.gui.Icons;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.Face;
import at.vintagestory.modelcreator.model.Sized;
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
	JPanel uvCoordPanel;
	JPanel titlePanel;

	protected Checkbox alternateUnwrap;
	protected Checkbox autoUnwrap;

	private DecimalFormat df = new DecimalFormat("#.##");
	
	private DefaultComboBoxModel<String> model;
	
	JTextField[] fields;
	JButton[] buttons;
	

	public ElementUVPanel(IElementManager manager)
	{
		this.manager = manager;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setMaximumSize(new Dimension(186, 224));
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
		
		
		fields = new JTextField[] { xStartField, yStartField };
		buttons = new JButton[] { btnPlusX, btnPlusY, btnNegX, btnNegY };
		
		model = new DefaultComboBoxModel<String>();
		model.addElement("Compact");
		model.addElement("North is front");
		model.addElement("East is front");
		model.addElement("South is front");
		model.addElement("West is front");
		model.addElement("Up is front");
		model.addElement("Down is front");
		model.addElement("Up is front (Saratymode)");
		

		alternateUnwrap = new Checkbox("Alternate unwrap direction");
		alternateUnwrap.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				Element elem = manager.getCurrentElement();
				if (elem == null) return;
				
				elem.setAlternateUnrwapDir(e.getStateChange() == 1);
				elem.updateUV();
				elem.updateUV();
				ModelCreator.updateValues(null);
				ModelCreator.DidModify();
			}
		});

		
		autoUnwrap = new Checkbox("Auto-Unwrap");
		autoUnwrap.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				Element elem = manager.getCurrentElement();
				if (elem == null) return;
				
				elem.setAutoUnwrap(e.getStateChange() == 1);
				elem.updateUV();
				ModelCreator.updateValues(null);
				ModelCreator.DidModify();
			}
		});
		
		
		Font defaultFont = new Font("SansSerif", Font.BOLD, 20);
		for (JButton btn : buttons) {
			btn.setSize(new Dimension(62, 30));
			btn.setFont(defaultFont);
		}
	}

	public void initProperties()
	{
		Font defaultFont = new Font("SansSerif", Font.BOLD, 20);
		xStartField.setSize(new Dimension(62, 30));
		xStartField.setFont(defaultFont);
		xStartField.setHorizontalAlignment(JTextField.CENTER);
		
		AwtUtil.addChangeListener(xStartField, e -> {
			Element element = manager.getCurrentElement();
			if (!ModelCreator.currentProject.EntityTextureMode || ModelCreator.ignoreValueUpdates) return;
			
			if (element != null)
			{
				double newValue = (Parser.parseDouble(xStartField.getText(), element.getTexUStart()));
				if (newValue == element.getTexUStart()) return;
				
				element.setTexUStart(newValue);
				ModelCreator.updateValues(xStartField);
			}
		});

		xStartField.addMouseWheelListener(new MouseWheelListener()
		{
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				int notches = e.getWheelRotation();
				modifyPosition(EnumAxis.X, (notches > 0 ? 1 : -1), e.getModifiers(), xStartField);
			}
		});

		
		yStartField.setSize(new Dimension(62, 30));
		yStartField.setFont(defaultFont);
		yStartField.setHorizontalAlignment(JTextField.CENTER);
		
		AwtUtil.addChangeListener(yStartField, e -> {
			Element element = manager.getCurrentElement();
			if (!ModelCreator.currentProject.EntityTextureMode || ModelCreator.ignoreValueUpdates) return;
			
			if (element != null)
			{
				double newValue = (Parser.parseDouble(yStartField.getText(), element.getTexVStart()));
				if (newValue == element.getTexVStart()) return;
				element.setTexVStart(newValue);
				ModelCreator.updateValues(yStartField);
			}
		});

		
		yStartField.addMouseWheelListener(new MouseWheelListener()
		{
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				int notches = e.getWheelRotation();
				modifyPosition(EnumAxis.Y, (notches > 0 ? 1 : -1), e.getModifiers(), yStartField);
			}
		});
		
		
		btnPlusX.addActionListener(e ->
		{
			modifyPosition(EnumAxis.X, 1, e.getModifiers(), btnPlusX);
		});

		btnPlusX.setToolTipText("<html>Increases the start U.<br><b>Hold shift for decimals</b></html>");

		btnPlusY.addActionListener(e ->
		{
			modifyPosition(EnumAxis.Y, 1, e.getModifiers(), btnPlusY);
		});
		btnPlusY.setToolTipText("<html>Increases the start V.<br><b>Hold shift for decimals</b></html>");

		btnNegX.addActionListener(e ->
		{
			modifyPosition(EnumAxis.X, -1, e.getModifiers(), btnNegX);
		});
		btnNegX.setToolTipText("<html>Decreases the start U.<br><b>Hold shift for decimals</b></html>");

		btnNegY.addActionListener(e ->
		{
			modifyPosition(EnumAxis.Y, -1, e.getModifiers(), btnNegY);

		});
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
				manager.getCurrentElement().updateUV();
				updateValues(menuList);
				ModelCreator.DidModify();
			}
		});
		
		unwrapPanel.setPreferredSize(new Dimension(186, 50));
		unwrapPanel.add(menuList);
	}

	public void addComponents()
	{
		uvCoordPanel = new JPanel(new GridLayout(3, 4, 4, 4));
		

		uvCoordPanel.add(btnPlusX);
		uvCoordPanel.add(btnPlusY);
		uvCoordPanel.add(xStartField);
		uvCoordPanel.add(yStartField);
		uvCoordPanel.add(btnNegX);
		uvCoordPanel.add(btnNegY);
		

		titlePanel = new JPanel(new GridLayout(1, 1,1,1));
		titlePanel.add(new JLabel("<html><b>UV Position (all Faces)</b>"));
		
		add(autoUnwrap);
		add(titlePanel);
		add(uvCoordPanel);
		add(unwrapPanel);
		add(alternateUnwrap);
	}

	@Override
	public void updateValues(JComponent byGuiElem)
	{
		Element cube = manager.getCurrentElement();
		final java.util.function.IntUnaryOperator clampToMenu = (idx) -> {
			int max = menuList.getItemCount() - 1;
			if (max < 0) return -1;
			if (idx < 0) return 0;
			if (idx > max) return max;
			return idx;
		};
		
		boolean autoUnwrapEnabled = cube != null && cube.isAutoUnwrapEnabled();
		boolean enabled = cube != null;
		
		xStartField.setEnabled(enabled && autoUnwrapEnabled);
		yStartField.setEnabled(enabled && autoUnwrapEnabled);
		menuList.setEnabled(enabled && autoUnwrapEnabled);
		alternateUnwrap.setEnabled(enabled && autoUnwrapEnabled);
		btnPlusX.setEnabled(enabled && autoUnwrapEnabled);
		btnPlusY.setEnabled(enabled && autoUnwrapEnabled);
		btnNegX.setEnabled(enabled && autoUnwrapEnabled);
		btnNegY.setEnabled(enabled && autoUnwrapEnabled);
		
		unwrapPanel.setVisible(autoUnwrapEnabled);
		uvCoordPanel.setVisible(autoUnwrapEnabled);
		menuList.setVisible(autoUnwrapEnabled);
		alternateUnwrap.setVisible(autoUnwrapEnabled);
		unwrapPanel.setVisible(autoUnwrapEnabled);
		titlePanel.setVisible(autoUnwrapEnabled);
		
		if (autoUnwrapEnabled) {
			setMaximumSize(new Dimension(186, 224));
		} else {
			setMaximumSize(new Dimension(186, 25));
		}
		
		alternateUnwrap.setVisible(ModelCreator.currentRightTab == 1);
		
		
		autoUnwrap.setEnabled(enabled);
		
		if (cube != null)
		{			
			alternateUnwrap.setState(cube.getAlternateUnrwapDir());
			autoUnwrap.setState(cube.isAutoUnwrapEnabled());
			
			if (byGuiElem != xStartField) xStartField.setText(df.format(cube.getTexUStart()));
			if (byGuiElem != yStartField) yStartField.setText(df.format(cube.getTexVStart()));
			if (byGuiElem != menuList) {
				int desired = clampToMenu.applyAsInt(cube.getUnwrapMode());
				if (desired >= 0) {
					if (desired != cube.getUnwrapMode()) cube.setUnwrapMode(desired);
					menuList.setSelectedIndex(desired);
				}
			}
		}
		else
		{
			xStartField.setText("");
			yStartField.setText("");
		}
		
		
		
		if (cube != null) {
			int index = menuList.getSelectedIndex();
			model.removeAllElements();
			model.addElement("Compact");
			
			
			if (ModelCreator.saratyMode) {
				
				for (int i = 0; i < 6; i++) {
					model.addElement(cube.rotatedfaces[i].Name + " is front");
				}
				model.addElement(cube.rotatedfaces[4].Name + " is front (Saratymode)");
			} else {
				model.addElement("North is front");
				model.addElement("East is front");
				model.addElement("South is front");
				model.addElement("West is front");
				model.addElement("Up is front");
				model.addElement("Down is front");
			}
				
			
			int safeIndex = clampToMenu.applyAsInt(index);
			if (safeIndex >= 0) {
				menuList.setSelectedIndex(safeIndex);
				if (safeIndex != index && cube != null && cube.getUnwrapMode() != safeIndex) {
					cube.setUnwrapMode(safeIndex);
				}
			}
		}
		
	}
	
	

	public void modifyPosition(EnumAxis axis, int direction, int modifiers, JComponent sourceField) {
		Element cube = manager.getCurrentElement();
		if (cube == null) return;
		
		double size = direction * 1f;
		
		boolean ctrl = (modifiers & ActionEvent.CTRL_MASK) > 0;
		boolean shift = (modifiers & ActionEvent.SHIFT_MASK) == 1;
		boolean alt  = (modifiers & ActionEvent.ALT_MASK) > 0;
		
		Face face = cube.getSelectedFace();
		Sized scale = face.getVoxel2PixelScale();
		
		if (shift) {
			size = direction * 0.1;
		}
		else if (ctrl)
		{
			double step = 1 / scale.H;
			if (axis == EnumAxis.X) step = 1 / scale.W;
			
			size = direction * (face.isSnapUvEnabled() ? -step : -0.1);
		}
		else
		{
			size = direction * 1;
		}
		
		if (alt) {
			
			switch (axis) {
				case X:
					cube.moveTexUV(size, 0);
					break;
				case Y:
					cube.moveTexUV(0, size);
					break;
				default:
					break;
		}

			
		} else {		
		
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
		}
		
		ModelCreator.updateValues(sourceField);
		
	}

}
