package at.vintagestory.modelcreator.gui.right.element;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Start;
import at.vintagestory.modelcreator.gui.ComponentUtil;
import at.vintagestory.modelcreator.gui.StepparentSelectionDialog;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.util.Parser;

public class ElementPropertiesPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private IElementManager manager;

	private JRadioButton btnShade;
	
	JTextField climateColorMapField;
	JTextField seasonColorMapField;
	private JComboBox<String> renderPassList;
	//private JComboBox<String> windModeList;
	JButton stepparentButton;


	public ElementPropertiesPanel(IElementManager manager)
	{
		this.manager = manager;
		setLayout(new GridLayout(10, 2, 0, 5));
		
		setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>Element Properties</b></html>"));
		setPreferredSize(new Dimension(200, 240));
		setAlignmentX(JPanel.LEFT_ALIGNMENT);
		initComponents();
		addComponents();
	}

	public void initComponents()
	{
		btnShade = ComponentUtil.createRadioButton("Shade", "<html>Determines if shadows should be rendered<br>Default: On</html>");
		btnShade.addActionListener(e ->
		{
			Element elem2 = manager.getCurrentElement();
			if (elem2 != null) elem2.setShade(btnShade.isSelected());
		});
		
		climateColorMapField = new JTextField();
		climateColorMapField.setToolTipText("Leave empty for no color mapping, 'climatePlantColor' for climate foliage tint, 'climateWaterColor' = for climate water tint");
		climateColorMapField.setPreferredSize(new Dimension(190, 25));
		
		climateColorMapField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				Element elem = manager.getCurrentElement();
				if (elem != null) {
					String nowColorMap = climateColorMapField.getText();
					String prevColorMap = elem.getClimateColorMap();
					elem.setClimateColorMap(nowColorMap);
					
					if (prevColorMap != nowColorMap) ModelCreator.DidModify();
					ModelCreator.updateValues(climateColorMapField);
				}
			}
		});
		
		
		
		seasonColorMapField = new JTextField();
		seasonColorMapField.setToolTipText("Leave empty for no season color mapping");
		seasonColorMapField.setPreferredSize(new Dimension(190, 25));
		
		seasonColorMapField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				Element elem = manager.getCurrentElement();
				if (elem != null) {
					String nowColorMap = seasonColorMapField.getText();
					String prevColorMap = elem.getSeasonColorMap();
					elem.setSeasonColorMap(nowColorMap);
					
					if (prevColorMap != nowColorMap) ModelCreator.DidModify();
					ModelCreator.updateValues(seasonColorMapField);
				}
			}
		});
		
		
		
		
		renderPassList = new JComboBox<String>();
		renderPassList.setToolTipText("Leave at default to use the blocks render pass. Set to another value to override the block render pass. For foliage you might want to use OpaqueNoCull.");
		DefaultComboBoxModel<String> model = renderPassList();		
		renderPassList.setModel(model);
		renderPassList.setPreferredSize(new Dimension(190, 25));
		
		renderPassList.addActionListener(e -> {
			Element elem = manager.getCurrentElement();
			if (elem != null) {
				int prevPass = elem.getRenderPass();
				int newpass = renderPassList.getSelectedIndex() - 1;
				elem.setRenderPass(newpass);
				
				if (prevPass != newpass) ModelCreator.DidModify();
				
				ModelCreator.updateValues(renderPassList);
			}
		});
		
		

	}

	public void addComponents()
	{
		add(btnShade);
		add(new JLabel());
		
		add(new JLabel("Climate color map"));
		add(climateColorMapField);
		
		add(new JLabel("Season color map"));
		add(seasonColorMapField);
		
		add(new JLabel("Render pass"));
		add(renderPassList);

		
		JLabel label = new JLabel("Stepparent element");
		stepparentButton = new JButton();
		stepparentButton.setText("Not set");
		stepparentButton.addActionListener(e ->
		{
			StepparentSelectionDialog.show(manager, ModelCreator.Instance, ModelCreator.currentProject.SelectedElement);
		});
		
		label.setToolTipText("To define a parent element, without actually having it be part of the model. Useful for defining child elements of backdrop models.");
		add(label);
		add(stepparentButton);
	}

	@Override
	public void updateValues(JComponent byGuiElem)
	{
		Element cube = manager.getCurrentElement();
		if (cube != null)
		{
			btnShade.setEnabled(true);
			btnShade.setSelected(cube.isShaded());
			climateColorMapField.setEnabled(true);
			renderPassList.setEnabled(true);
			
			climateColorMapField.setText(cube.getClimateColorMap() == null ? "" : cube.getClimateColorMap());
			seasonColorMapField.setText(cube.getSeasonColorMap() == null ? "" : cube.getSeasonColorMap());
			renderPassList.setSelectedIndex(cube.getRenderPass() + 1);
			
			stepparentButton.setEnabled(true);
			String stp = cube.getStepParent();
			stepparentButton.setText(stp == null ? "Not set" : stp);
		}
		else
		{
			btnShade.setEnabled(false);
			btnShade.setSelected(false);
			renderPassList.setEnabled(false);
			
			climateColorMapField.setEnabled(false);
			climateColorMapField.setText("");
			stepparentButton.setText("Not set");
			stepparentButton.setEnabled(false);
		}
	}
	
	
	private DefaultComboBoxModel<String> renderPassList()
	{
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		
		model.addElement("<html><b>Default</b></html>");
		model.addElement("<html><b>Opaque</b></html>");
		model.addElement("<html><b>OpaqueNoCull</b></html>");
		model.addElement("<html><b>BlendNoCull</b></html>");
		model.addElement("<html><b>Transparent</b></html>");
		model.addElement("<html><b>Liquid</b></html>");
		model.addElement("<html><b>TopSoil</b></html>");
		model.addElement("<html><b>Meta</b></html>");
		
		return model;
	}
	

}
