package at.vintagestory.modelcreator.gui.right.face;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Start;
import at.vintagestory.modelcreator.gui.Icons;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.Face;
import at.vintagestory.modelcreator.util.AwtUtil;
import at.vintagestory.modelcreator.util.Parser;

public class FaceUVPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private IElementManager manager;
	private JButton btnPlusX;
	private JButton btnPlusY;
	private JTextField xStartField;
	private JTextField yStartField;
	private JButton btnNegX;
	private JButton btnNegY;

	private JButton btnPlusXEnd;
	private JButton btnPlusYEnd;
	private JTextField xEndField;
	private JTextField yEndField;
	private JButton btnNegXEnd;
	private JButton btnNegYEnd;

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
		btnPlusX = new JButton(Icons.arrow_up);
		btnPlusY = new JButton(Icons.arrow_up);
		xStartField = new JTextField();
		yStartField = new JTextField();
		btnNegX = new JButton(Icons.arrow_down);
		btnNegY = new JButton(Icons.arrow_down);

		btnPlusXEnd = new JButton(Icons.arrow_up);
		btnPlusYEnd = new JButton(Icons.arrow_up);
		xEndField = new JTextField();
		yEndField = new JTextField();
		btnNegXEnd = new JButton(Icons.arrow_down);
		btnNegYEnd = new JButton(Icons.arrow_down);
	}

	public void initProperties()
	{
		Font defaultFont = new Font("SansSerif", Font.BOLD, 20);
		xStartField.setSize(new Dimension(62, 30));
		xStartField.setFont(defaultFont);
		xStartField.setHorizontalAlignment(JTextField.CENTER);
		
		
		AwtUtil.addChangeListener(xStartField, e -> {
			Element element = manager.getCurrentElement();
			if (element == null) return;			
			Face face = element.getSelectedFace();
			face.setStartU(Parser.parseDouble(xStartField.getText(), face.getStartU()));
			face.updateUV();
			ModelCreator.updateValues(xStartField);			
		});

		yStartField.setSize(new Dimension(62, 30));
		yStartField.setFont(defaultFont);
		yStartField.setHorizontalAlignment(JTextField.CENTER);
		
		
		AwtUtil.addChangeListener(yStartField, e -> {
			Element element = manager.getCurrentElement();
			if (element == null) return;			
			Face face = element.getSelectedFace();
			
			face.setStartV(Parser.parseDouble(yStartField.getText(), face.getStartV()));
			face.updateUV();
			ModelCreator.updateValues(yStartField);			
		});


		xEndField.setSize(new Dimension(62, 30));
		xEndField.setFont(defaultFont);
		xEndField.setHorizontalAlignment(JTextField.CENTER);
		
		AwtUtil.addChangeListener(xEndField, e -> {
			Element element = manager.getCurrentElement();
			if (element == null) return;			
			Face face = element.getSelectedFace();
			face.setAutoUVEnabled(false);
			face.setEndU(Parser.parseDouble(xEndField.getText(), face.getEndU()));
			face.updateUV();
			ModelCreator.updateValues(xEndField);			
		});

		yEndField.setSize(new Dimension(62, 30));
		yEndField.setFont(defaultFont);
		yEndField.setHorizontalAlignment(JTextField.CENTER);
				
		AwtUtil.addChangeListener(yEndField, e -> {
			Element element = manager.getCurrentElement();
			if (element == null) return;			
			Face face = element.getSelectedFace();
			face.setAutoUVEnabled(false);
			face.setEndV(Parser.parseDouble(yEndField.getText(), face.getEndV()));
			face.updateUV();
			ModelCreator.updateValues(yEndField);			
		});


		btnPlusX.addActionListener(e ->
		{
			if (manager.getCurrentElement() != null)
			{
				Element cube = manager.getCurrentElement();
				Face face = cube.getSelectedFace();
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					face.addTextureX(0.1);
				}
				else
				{
					face.addTextureX(1.0);
				}
				cube.updateUV();
				ModelCreator.updateValues(btnPlusX);
			}
		});

		btnPlusX.setSize(new Dimension(62, 30));
		btnPlusX.setFont(defaultFont);
		btnPlusX.setToolTipText("<html>Increases the start U.<br><b>Hold shift for decimals</b></html>");

		btnPlusY.addActionListener(e ->
		{
			if (manager.getCurrentElement() != null)
			{
				Element cube = manager.getCurrentElement();
				Face face = cube.getSelectedFace();
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					face.addTextureY(0.1);
				}
				else
				{
					face.addTextureY(1.0);
				}
				cube.updateUV();
				ModelCreator.updateValues(btnPlusY);
			}
		});
		btnPlusY.setPreferredSize(new Dimension(62, 30));
		btnPlusY.setFont(defaultFont);
		btnPlusY.setToolTipText("<html>Increases the start V.<br><b>Hold shift for decimals</b></html>");

		btnNegX.addActionListener(e ->
		{
			if (manager.getCurrentElement() != null)
			{
				Element cube = manager.getCurrentElement();
				Face face = cube.getSelectedFace();
				
				
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					face.addTextureX(-0.1);
				}
				else
				{
					face.addTextureX(-1.0);
				}
				cube.updateUV();
				ModelCreator.updateValues(btnNegX);
			}
		});
		btnNegX.setSize(new Dimension(62, 30));
		btnNegX.setFont(defaultFont);
		btnNegX.setToolTipText("<html>Decreases the start U.<br><b>Hold shift for decimals</b></html>");

		btnNegY.addActionListener(e ->
		{
			if (manager.getCurrentElement() != null)
			{
				Element cube = manager.getCurrentElement();
				Face face = cube.getSelectedFace();
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					face.addTextureY(-0.1);
				}
				else
				{
					face.addTextureY(-1.0);
				}
				cube.updateUV();
				ModelCreator.updateValues(btnNegY);
			}
		});
		btnNegY.setSize(new Dimension(62, 30));
		btnNegY.setFont(defaultFont);
		btnNegY.setToolTipText("<html>Decreases the start V.<br><b>Hold shift for decimals</b></html>");

		btnPlusXEnd.addActionListener(e ->
		{
			if (manager.getCurrentElement() != null)
			{
				Element cube = manager.getCurrentElement();
				Face face = cube.getSelectedFace();
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					face.addTextureXEnd(0.1);
				}
				else
				{
					face.addTextureXEnd(1.0);
				}
				
				ModelCreator.updateValues(btnPlusXEnd);
			}
		});
		btnPlusXEnd.setSize(new Dimension(62, 30));
		btnPlusXEnd.setFont(defaultFont);
		btnPlusXEnd.setToolTipText("<html>Increases the end U.<br><b>Hold shift for decimals</b></html>");

		btnPlusYEnd.addActionListener(e ->
		{
			if (manager.getCurrentElement() != null)
			{
				Element cube = manager.getCurrentElement();
				Face face = cube.getSelectedFace();
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					face.addTextureYEnd(0.1);
				}
				else
				{
					face.addTextureYEnd(1.0);
				}
				
				ModelCreator.updateValues(btnPlusYEnd);
			}
		});
		btnPlusYEnd.setPreferredSize(new Dimension(62, 30));
		btnPlusYEnd.setFont(defaultFont);
		btnPlusYEnd.setToolTipText("<html>Increases the end V.<br><b>Hold shift for decimals</b></html>");

		btnNegXEnd.addActionListener(e ->
		{
			if (manager.getCurrentElement() != null)
			{
				Element cube = manager.getCurrentElement();
				Face face = cube.getSelectedFace();
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					face.addTextureXEnd(-0.1);
				}
				else
				{
					face.addTextureXEnd(-1.0);
				}
				
				ModelCreator.updateValues(btnNegXEnd);
			}
		});
		btnNegXEnd.setSize(new Dimension(62, 30));
		btnNegXEnd.setFont(defaultFont);
		btnNegXEnd.setToolTipText("<html>Decreases the end U.<br><b>Hold shift for decimals</b></html>");

		btnNegYEnd.addActionListener(e ->
		{
			if (manager.getCurrentElement() != null)
			{
				Element cube = manager.getCurrentElement();
				Face face = cube.getSelectedFace();
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					face.addTextureYEnd(-0.1);
				}
				else
				{
					face.addTextureYEnd(-1.0);
				}
				
				ModelCreator.updateValues(btnNegYEnd);
			}
		});
		btnNegYEnd.setSize(new Dimension(62, 30));
		btnNegYEnd.setFont(defaultFont);
		btnNegYEnd.setToolTipText("<html>Decreases the end V.<br><b>Hold shift for decimals</b></html>");
	}

	public void addComponents()
	{
		add(btnPlusX);
		add(btnPlusY);
		add(btnPlusXEnd);
		add(btnPlusYEnd);
		add(xStartField);
		add(yStartField);
		add(xEndField);
		add(yEndField);
		add(btnNegX);
		add(btnNegY);
		add(btnNegXEnd);
		add(btnNegYEnd);
	}

	@Override
	public void updateValues(JComponent byGuiElem)
	{
		Element cube = manager.getCurrentElement();
		
		if (cube != null)
		{
			xStartField.setEnabled(true);
			yStartField.setEnabled(true);
			xEndField.setEnabled(true);
			yEndField.setEnabled(true);
			if (byGuiElem != xStartField) xStartField.setText(df.format(cube.getSelectedFace().getStartU()));
			if (byGuiElem != yStartField) yStartField.setText(df.format(cube.getSelectedFace().getStartV()));
			if (byGuiElem != xEndField) xEndField.setText(df.format(cube.getSelectedFace().getEndU()));
			if (byGuiElem != yEndField) yEndField.setText(df.format(cube.getSelectedFace().getEndV()));
			
			

		}
		else
		{
			xStartField.setEnabled(false);
			yStartField.setEnabled(false);
			xEndField.setEnabled(false);
			yEndField.setEnabled(false);
			xStartField.setText("");
			yStartField.setText("");
			xEndField.setText("");
			yEndField.setText("");
		}
	}
}
