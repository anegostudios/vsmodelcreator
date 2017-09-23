package at.vintagestory.modelcreator.gui.right.element;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Start;
import at.vintagestory.modelcreator.gui.ComponentUtil;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.util.Parser;

public class ElementPropertiesPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private IElementManager manager;

	private JRadioButton btnShade;
	
	JTextField tintIndexField;
	private JComboBox<String> renderPassList;


	public ElementPropertiesPanel(IElementManager manager)
	{
		this.manager = manager;
		setLayout(new GridLayout(3, 2));
		setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>Element Properties</b></html>"));
		setPreferredSize(new Dimension(200, 100));
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
		
		tintIndexField = new JTextField();
		tintIndexField.setToolTipText("0 for no tint, 1 = for climate foliage tint, 2 = for climate water tint");
		tintIndexField.setPreferredSize(new Dimension(200, 25));
		
		tintIndexField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				Element elem = manager.getCurrentElement();
				if (elem != null) {
					int index = Parser.parseInt(tintIndexField.getText(), 0);
					int previndex = elem.getTintIndex();
					elem.setTintIndex(index);
					
					if (index != previndex) ModelCreator.DidModify();
					ModelCreator.updateValues(tintIndexField);
				}
				
			}
		});
		
		renderPassList = new JComboBox<String>();
		renderPassList.setToolTipText("Leave at default to use the blocks render pass. Set to another value to override the block render pass. For foliage you might want to use OpaqueNoCull.");
		DefaultComboBoxModel<String> model = renderPassList();		
		renderPassList.setModel(model);
		renderPassList.setPreferredSize(new Dimension(200, 25));
		
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
		
		add(new JLabel("Tint index"));
		add(tintIndexField);
		
		add(new JLabel("Render pass"));
		add(renderPassList);
	}

	@Override
	public void updateValues(JComponent byGuiElem)
	{
		Element cube = manager.getCurrentElement();
		if (cube != null)
		{
			btnShade.setEnabled(true);
			btnShade.setSelected(cube.isShaded());
			tintIndexField.setEnabled(true);
			renderPassList.setEnabled(true);
			
			tintIndexField.setText(cube.getTintIndex() + "");
			renderPassList.setSelectedIndex(cube.getRenderPass() + 1);
		}
		else
		{
			btnShade.setEnabled(false);
			btnShade.setSelected(false);
			renderPassList.setEnabled(false);
			
			tintIndexField.setEnabled(false);
			tintIndexField.setText("");
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
