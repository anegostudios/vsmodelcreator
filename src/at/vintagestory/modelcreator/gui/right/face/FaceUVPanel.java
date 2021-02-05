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
import at.vintagestory.modelcreator.model.Face;
import at.vintagestory.modelcreator.model.Sized;
import at.vintagestory.modelcreator.util.AwtUtil;
import at.vintagestory.modelcreator.util.Parser;

public class FaceUVPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private IElementManager manager;
	private JButton btnPlusUStart;
	private JButton btnPlusVStart;
	private JTextField uStartField;
	private JTextField vStartField;
	private JButton btnNegUStart;
	private JButton btnNegVStart;

	private JButton btnPlusUEnd;
	private JButton btnPlusVEnd;
	private JTextField uEndField;
	private JTextField vEndField;
	private JButton btnNegUEnd;
	private JButton btnNegVEnd;
	
	JTextField[] fields;
	JButton[] buttons;

	private DecimalFormat df = new DecimalFormat("#.##");

	public FaceUVPanel(IElementManager manager)
	{
		this.manager = manager;
		setLayout(new GridLayout(3, 4, 4, 4));
		setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>Face UV</b></html>"));
		setMaximumSize(new Dimension(186, 124));
		initComponents();
		initProperties();
		addComponents();
	}

	public void initComponents()
	{
		btnPlusUStart = new JButton(Icons.arrow_up);
		btnPlusVStart = new JButton(Icons.arrow_up);
		uStartField = new JTextField();
		vStartField = new JTextField();
		btnNegUStart = new JButton(Icons.arrow_down);
		btnNegVStart = new JButton(Icons.arrow_down);
		
		btnPlusUEnd = new JButton(Icons.arrow_up);
		btnPlusVEnd = new JButton(Icons.arrow_up);
		uEndField = new JTextField();
		vEndField = new JTextField();
		btnNegUEnd = new JButton(Icons.arrow_down);
		btnNegVEnd = new JButton(Icons.arrow_down);

		fields = new JTextField[] { uStartField, vStartField, uEndField, vEndField };
		buttons = new JButton[] { btnPlusUStart, btnPlusVStart, btnNegUStart, btnNegVStart, btnPlusUEnd, btnPlusVEnd, btnNegUEnd, btnNegVEnd };
		
		Font defaultFont = new Font("SansSerif", Font.BOLD, 20);
		for (JButton btn : buttons) {
			btn.setSize(new Dimension(62, 30));
			btn.setFont(defaultFont);
		}
	}

	public void initProperties()
	{
		Font defaultFont = new Font("SansSerif", Font.BOLD, 20);
		uStartField.setSize(new Dimension(62, 30));
		uStartField.setFont(defaultFont);
		uStartField.setHorizontalAlignment(JTextField.CENTER);
		
		
		AwtUtil.addChangeListener(uStartField, e -> {
			Element element = manager.getCurrentElement();
			if (element == null) return;			
			Face face = element.getSelectedFace();
			face.setStartU(Parser.parseDouble(uStartField.getText(), face.getStartU()));
			face.updateUV();
			ModelCreator.updateValues(uStartField);			
		});
		

		vStartField.setSize(new Dimension(62, 30));
		vStartField.setFont(defaultFont);
		vStartField.setHorizontalAlignment(JTextField.CENTER);
		
		
		AwtUtil.addChangeListener(vStartField, e -> {
			Element element = manager.getCurrentElement();
			if (element == null) return;			
			Face face = element.getSelectedFace();
			
			face.setStartV(Parser.parseDouble(vStartField.getText(), face.getStartV()));
			face.updateUV();
			ModelCreator.updateValues(vStartField);			
		});


		uEndField.setSize(new Dimension(62, 30));
		uEndField.setFont(defaultFont);
		uEndField.setHorizontalAlignment(JTextField.CENTER);
		
		AwtUtil.addChangeListener(uEndField, e -> {
			Element element = manager.getCurrentElement();
			if (element == null) return;			
			Face face = element.getSelectedFace();
			
			double nowEndU = Parser.parseDouble(uEndField.getText(), face.getEndU());
			// Disable auto-uv if user changed End U
			if (nowEndU != face.getEndU()) {
				face.setAutoUVEnabled(false);
			}
			 
			
			face.setEndU(nowEndU);
			face.updateUV();
			ModelCreator.updateValues(uEndField);			
		});

		vEndField.setSize(new Dimension(62, 30));
		vEndField.setFont(defaultFont);
		vEndField.setHorizontalAlignment(JTextField.CENTER);
				
		AwtUtil.addChangeListener(vEndField, e -> {
			Element element = manager.getCurrentElement();
			if (element == null) return;			
			Face face = element.getSelectedFace();
			
			double nowEndV = Parser.parseDouble(vEndField.getText(), face.getEndV());
			// Disable auto-uv if user changed End V
			if (nowEndV != face.getEndV()) {
				face.setAutoUVEnabled(false);
			}
			
			face.setEndV(nowEndV);
			face.updateUV();
			ModelCreator.updateValues(vEndField);			
		});

		
		for (int i = 0; i < fields.length; i++)
		{
			JTextField field = fields[i];
			
			final int index = i;
			
			field.addMouseWheelListener(new MouseWheelListener()
			{
				
				@Override
				public void mouseWheelMoved(MouseWheelEvent e)
				{
					int notches = e.getWheelRotation();
					modifyPosition(index, (notches > 0 ? 1 : -1), e.getModifiers(), field);
				}
			});	
		}
		

		btnPlusUStart.addActionListener(e ->
		{
			modifyPosition(0, 1, e.getModifiers(), btnPlusUStart);
		});

		btnPlusUStart.setToolTipText("<html>Increases the start U.<br><b>Hold shift for decimals</b></html>");

		btnPlusVStart.addActionListener(e ->
		{
			modifyPosition(1, 1, e.getModifiers(), btnPlusUStart);
		});
		
		btnPlusVStart.setToolTipText("<html>Increases the start V.<br><b>Hold shift for decimals</b></html>");

		btnNegUStart.addActionListener(e ->
		{
			modifyPosition(0, -1, e.getModifiers(), btnPlusVStart);
		});
		
		btnNegUStart.setToolTipText("<html>Decreases the start U.<br><b>Hold shift for decimals</b></html>");

		btnNegVStart.addActionListener(e ->
		{
			modifyPosition(1, -1, e.getModifiers(), btnNegUStart);
		});
		
		btnNegVStart.setToolTipText("<html>Decreases the start V.<br><b>Hold shift for decimals</b></html>");

		btnPlusUEnd.addActionListener(e ->
		{
			modifyPosition(2, 1, e.getModifiers(), btnPlusUEnd);
		});
		btnPlusUEnd.setToolTipText("<html>Increases the end U.<br><b>Hold shift for decimals</b></html>");

		btnPlusVEnd.addActionListener(e ->
		{
			modifyPosition(3, 1, e.getModifiers(), btnPlusVEnd);
		});
		
		btnPlusVEnd.setToolTipText("<html>Increases the end V.<br><b>Hold shift for decimals</b></html>");

		btnNegUEnd.addActionListener(e ->
		{
			modifyPosition(2, -1, e.getModifiers(), btnNegUEnd);
		});
		
		btnNegUEnd.setToolTipText("<html>Decreases the end U.<br><b>Hold shift for decimals</b></html>");

		btnNegVEnd.addActionListener(e ->
		{
			modifyPosition(3, -1, e.getModifiers(), btnNegVEnd);
		});
		
		
		btnNegVEnd.setToolTipText("<html>Decreases the end V.<br><b>Hold shift for decimals</b></html>");
	}

	public void addComponents()
	{
		add(btnPlusUStart);
		add(btnPlusVStart);
		add(btnPlusUEnd);
		add(btnPlusVEnd);
		add(uStartField);
		add(vStartField);
		add(uEndField);
		add(vEndField);
		add(btnNegUStart);
		add(btnNegVStart);
		add(btnNegUEnd);
		add(btnNegVEnd);
	}

	@Override
	public void updateValues(JComponent byGuiElem)
	{
		Element cube = manager.getCurrentElement();
		
		if (cube != null)
		{
			uStartField.setEnabled(true);
			vStartField.setEnabled(true);
			uEndField.setEnabled(true);
			vEndField.setEnabled(true);
			if (byGuiElem != uStartField) uStartField.setText(df.format(cube.getSelectedFace().getStartU()));
			if (byGuiElem != vStartField) vStartField.setText(df.format(cube.getSelectedFace().getStartV()));
			if (byGuiElem != uEndField) uEndField.setText(df.format(cube.getSelectedFace().getEndU()));
			if (byGuiElem != vEndField) vEndField.setText(df.format(cube.getSelectedFace().getEndV()));
			
			

		}
		else
		{
			uStartField.setEnabled(false);
			vStartField.setEnabled(false);
			uEndField.setEnabled(false);
			vEndField.setEnabled(false);
			uStartField.setText("");
			vStartField.setText("");
			uEndField.setText("");
			vEndField.setText("");
		}
	}
	
	
	
	public void modifyPosition(int type, int direction, int modifiers, JComponent sourceField) {
		Element cube = manager.getCurrentElement();
		if (cube == null) return;
		Face face = cube.getSelectedFace();
		
		double size = direction * ((modifiers & ActionEvent.SHIFT_MASK) == 1 ? 0.1f : 1f);
		
		Sized scale = face.getVoxel2PixelScale();
		
		boolean ctrl = (modifiers & ActionEvent.CTRL_MASK) > 0;
		boolean shift = (modifiers & ActionEvent.SHIFT_MASK) == 1;
		
		if (shift) {
			size = direction * 0.1;
		}
		else if (ctrl)
		{
			double step = 1 / scale.H;
			if (type == 0 || type == 2) step = 1 / scale.W;
			
			size = direction * (face.isSnapUvEnabled() ? -step : -0.1);
		}
		else
		{
			size = direction * 1;
		}
		
		
		ModelCreator.changeHistory.beginMultichangeHistoryState();
		
		JTextField targetField;
		double targetValue;
		
		switch (type) {
		case 0:
			targetField = uStartField;
			targetValue = Parser.parseDouble(targetField.getText(), face.getStartU()) + size;
			face.setStartU(targetValue);			
			break;
		case 1:
			targetField = vStartField;
			targetValue = Parser.parseDouble(targetField.getText(), face.getStartV()) + size;
			face.setStartV(targetValue);
			break;
		case 2:
			targetField = uEndField;
			targetValue = Parser.parseDouble(targetField.getText(), face.getEndU()) + size;
			face.setEndU(targetValue);
			break;
		case 3:
			targetField = vEndField;
			targetValue = Parser.parseDouble(targetField.getText(), face.getEndV()) + size;
			face.setEndV(targetValue);
			break;
		default:
			return;
		}
		
		
		ModelCreator.changeHistory.endMultichangeHistoryState(ModelCreator.currentProject);
		targetField.setText(df.format(targetValue));
		face.updateUV();
		ModelCreator.updateValues(sourceField);
	}
}

