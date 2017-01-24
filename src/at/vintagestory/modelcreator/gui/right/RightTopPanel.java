package at.vintagestory.modelcreator.gui.right;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.gui.CuboidTabbedPane;
import at.vintagestory.modelcreator.gui.Icons;
import at.vintagestory.modelcreator.gui.right.attachmentpoints.AttachmentPointsPanel;
import at.vintagestory.modelcreator.gui.right.element.ElementPanel;
import at.vintagestory.modelcreator.gui.right.face.FacePanel;
import at.vintagestory.modelcreator.gui.right.keyframes.RightKeyFramesPanel;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.PendingTexture;

public class RightTopPanel extends JPanel implements IElementManager, IValueUpdater {
	private static final long serialVersionUID = 1L;

	private ModelCreator creator;
	
	// Swing Variables
	private SpringLayout layout;
	private JScrollPane scrollPane;
	private JPanel btnContainer;
	private JButton btnAdd = new JButton();
	private JButton btnRemove = new JButton();
	private JButton btnDuplicate = new JButton();
	private JTextField nameField = new JTextField();
	private CuboidTabbedPane tabbedPane = new CuboidTabbedPane(this);
	
	RightKeyFramesPanel rightKeyFramesPanel;

	public ElementTree tree = new ElementTree();
	
	public RightTopPanel(ModelCreator creator)
	{
		this.creator = creator;
		setLayout(layout = new SpringLayout());
		setPreferredSize(new Dimension(215, 950));
		initComponents();
		setLayoutConstaints();
	}

	public void initComponents()
	{
		ModelCreator.currentProject.tree = tree;
		
		Font defaultFont = new Font("SansSerif", Font.BOLD, 14);
		btnContainer = new JPanel(new GridLayout(1, 3, 4, 0));
		btnContainer.setPreferredSize(new Dimension(205, 30));

		btnAdd.setIcon(Icons.cube);
		btnAdd.setToolTipText("New Element");
		btnAdd.addActionListener(e -> { ModelCreator.currentProject.addElementAsChild(new Element(1,1,1)); });
		btnAdd.setPreferredSize(new Dimension(30, 30));
		btnContainer.add(btnAdd);

		btnRemove.setIcon(Icons.bin);
		btnRemove.setToolTipText("Remove Element");
		btnRemove.addActionListener(e -> { ModelCreator.currentProject.removeCurrentElement(); });
		btnRemove.setPreferredSize(new Dimension(30, 30));
		btnContainer.add(btnRemove);

		btnDuplicate.setIcon(Icons.copy);
		btnDuplicate.setToolTipText("Duplicate Element");
		btnDuplicate.addActionListener(e -> { ModelCreator.currentProject.duplicateCurrentElement(); });
		btnDuplicate.setFont(defaultFont);
		btnDuplicate.setPreferredSize(new Dimension(30, 30));
		btnContainer.add(btnDuplicate);
		add(btnContainer);

		nameField.setPreferredSize(new Dimension(205, 25));
		nameField.setToolTipText("Element Name");
		nameField.setEnabled(false);

		nameField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				Element elem = tree.getSelectedElement();
				if (elem != null) {
					if (ModelCreator.currentProject.IsElementNameUsed(nameField.getText(), elem)) {
						nameField.setBackground(new Color(50, 0, 0));
					} else {
						elem.setName(nameField.getText());
						nameField.setBackground(getBackground());
					}
				}
				
				tree.updateUI();
			}
		});
		add(nameField);
		

		add(tree.jtree);

		scrollPane = new JScrollPane(tree.jtree);
		scrollPane.setPreferredSize(new Dimension(205, 240));
		add(scrollPane);

		tabbedPane.add("Cube", new ElementPanel(this));
		tabbedPane.add("Face", new FacePanel(this));
		tabbedPane.add("Keyframe", rightKeyFramesPanel = new RightKeyFramesPanel());
		tabbedPane.add("P", new AttachmentPointsPanel());
		
		tabbedPane.setPreferredSize(new Dimension(205, 650));
		tabbedPane.setTabPlacement(JTabbedPane.TOP);
		
		tabbedPane.addChangeListener(c ->
		{
			if (tabbedPane.getSelectedIndex() == 1)
			{
				creator.setSidebar(creator.uvSidebar);
				
			} else {
				creator.setSidebar(null);
			}
			
			ModelCreator.leftKeyframesPanel.setVisible(tabbedPane.getSelectedIndex() == 2);
			ModelCreator.renderAttachmentPoints = tabbedPane.getSelectedIndex() == 3;
		});
		
		add(tabbedPane);
	}

	public void setLayoutConstaints()
	{
		layout.putConstraint(SpringLayout.NORTH, nameField, 212 + 70, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, btnContainer, 176 + 70, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, tabbedPane, 250 + 70, SpringLayout.NORTH, this);
	}

	@Override
	public Element getCurrentElement()
	{
		return ModelCreator.currentProject.SelectedElement;
	}

	@Override
	public void addPendingTexture(PendingTexture texture)
	{
		creator.pendingTextures.add(texture);
	}

	public ModelCreator getCreator()
	{
		return creator;
	}

	@Override
	public void updateValues()
	{
		tabbedPane.updateValues();
		
		Element cube = getCurrentElement();
		if (cube != null)
		{
			nameField.setText(cube.name);
			if (ModelCreator.currentProject.IsElementNameUsed(nameField.getText(), cube)) {
				nameField.setBackground(new Color(50, 0, 0));
			} else {
				nameField.setBackground(getBackground());
			}
		}
		
		nameField.setEnabled(cube != null);
		btnRemove.setEnabled(cube != null);
		btnDuplicate.setEnabled(cube != null);
		
	}

	
	public void updateFrame() {
		rightKeyFramesPanel.updateFrame();
	}
}
