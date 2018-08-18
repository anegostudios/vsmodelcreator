package at.vintagestory.modelcreator.gui.right.face;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Start;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.Face;

public class FacePanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private IElementManager manager;

	private JPanel menuPanel;
	private JComboBox<String> menuList;
	private ElementUVPanel panelElemUV;
	private FaceUVPanel panelFaceUV;
	private JPanel sliderPanel;
	private JSlider rotation;
	private FaceTexturePanel panelTexture;
	private FacePropertiesPanel panelFaceExtras;

	private final int ROTATION_MIN = 0;
	private final int ROTATION_MAX = 3;
	private final int ROTATION_INIT = 0;

	private DefaultComboBoxModel<String> model;

	public FacePanel(IElementManager manager)
	{
		this.manager = manager;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		initMenu();
		initComponents();
		addComponents();
	}

	String FaceColor(int face) {
		return 
			(int)(Face.ColorsByFace[face].getRed() * Element.DefaultBlockSideBrightnessByFacing[face]) + "," +
			(int)(Face.ColorsByFace[face].getGreen() * Element.DefaultBlockSideBrightnessByFacing[face]) + "," +
			(int)(Face.ColorsByFace[face].getBlue() * Element.DefaultBlockSideBrightnessByFacing[face])
		;
				
	}
	
	public void initMenu()
	{
		model = new DefaultComboBoxModel<String>();
		model.addElement("<html><div style='padding:5px;color:rgb(" + FaceColor(0) + ");'><b>North</b></html>");
		model.addElement("<html><div style='padding:5px;color:rgb(" + FaceColor(1) + ");'><b>East</b></html>");
		model.addElement("<html><div style='padding:5px;color:rgb(" + FaceColor(2) + ");'><b>South</b></html>");
		model.addElement("<html><div style='padding:5px;color:rgb(" + FaceColor(3) + ");'><b>West</b></html>");
		model.addElement("<html><div style='padding:5px;color:rgb(" + FaceColor(4) + ");'><b>Up</b></html>");
		model.addElement("<html><div style='padding:5px;color:rgb(" + FaceColor(5) + ");'><b>Down</b></html>");
	}

	public void initComponents()
	{
		panelElemUV = new ElementUVPanel(manager);
		panelElemUV.setVisible(ModelCreator.currentProject.EntityTextureMode);
		
		menuPanel = new JPanel(new GridLayout(1, 1));
		menuPanel.setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>Side</b></html>"));
		menuList = new JComboBox<String>();
		menuList.setModel(model);
		menuList.setToolTipText("The face to edit.");
		menuList.addActionListener(e ->
		{
			if (ModelCreator.ignoreValueUpdates) return;
			if (manager.getCurrentElement() != null)
			{
				manager.getCurrentElement().setSelectedFace(menuList.getSelectedIndex());
				updateValues(menuList);
			}
		});
		menuPanel.setMaximumSize(new Dimension(186, 50));
		menuPanel.add(menuList);

		panelTexture = new FaceTexturePanel(manager);
		panelFaceUV = new FaceUVPanel(manager);
		panelFaceExtras = new FacePropertiesPanel(manager);

		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(0), new JLabel("0\u00b0"));
		labelTable.put(new Integer(1), new JLabel("90\u00b0"));
		labelTable.put(new Integer(2), new JLabel("180\u00b0"));
		labelTable.put(new Integer(3), new JLabel("270\u00b0"));

		sliderPanel = new JPanel(new GridLayout(1, 1));
		sliderPanel.setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>Rotation</b></html>"));
		rotation = new JSlider(JSlider.HORIZONTAL, ROTATION_MIN, ROTATION_MAX, ROTATION_INIT);
		rotation.setMajorTickSpacing(4);
		rotation.setPaintTicks(true);
		rotation.setPaintLabels(true);
		rotation.setLabelTable(labelTable);
		
		
		rotation.addChangeListener(e ->
		{
			if (ModelCreator.ignoreValueUpdates) return;
			
			if (manager.getCurrentElement() != null) {
				manager.getCurrentElement().getSelectedFace().setRotation(rotation.getValue());
				ModelCreator.updateValues(rotation);				
			}
		});
		
		rotation.setToolTipText("<html>The rotation of the texture<br>Default: 0\u00b0</html>");
		sliderPanel.setMaximumSize(new Dimension(190, 80));
		sliderPanel.add(rotation);
	}

	public void addComponents()
	{
		add(Box.createRigidArea(new Dimension(192, 5)));
		add(panelElemUV);
		add(menuPanel);
		add(panelTexture);
		add(panelFaceUV);
		add(sliderPanel);
		add(panelFaceExtras);
	}

	@Override
	public void updateValues(JComponent byGuiElem)
	{
		Element cube = manager.getCurrentElement();
		if (cube != null)
		{
			menuList.setSelectedIndex(cube.getSelectedFaceIndex());
			rotation.setEnabled(true);
			rotation.setValue(cube.getSelectedFace().getRotation());
		}
		else
		{
			rotation.setEnabled(false);
			rotation.setValue(0);
		}
		panelFaceUV.updateValues(byGuiElem);
		panelFaceExtras.updateValues(byGuiElem);
		panelElemUV.updateValues(byGuiElem);
		panelElemUV.setVisible(ModelCreator.currentProject.EntityTextureMode);
		
		boolean manualUnwrap = !ModelCreator.currentProject.EntityTextureMode || cube == null || !cube.isAutoUnwrapEnabled() || !cube.getSelectedFace().isAutoUVEnabled();
		panelFaceUV.setVisible(manualUnwrap);
		sliderPanel.setVisible(manualUnwrap);
		
	}
}
