package at.vintagestory.modelcreator.gui.right;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

import org.lwjgl.input.Mouse;

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

public class RightPanel extends JPanel implements IElementManager, IValueUpdater {
	private static final long serialVersionUID = 1L;

	private ModelCreator creator;
	
	// Swing Variables
	private SpringLayout layout;
	public JScrollPane scrollPane;
	private JPanel btnContainer;
	private JButton btnAdd;
	private JButton btnRemove;
	private JButton btnDuplicate;
	private JTextField nameField;
	private CuboidTabbedPane tabbedPane;
	
	RightKeyFramesPanel rightKeyFramesPanel;

	public ElementTree tree = new ElementTree();
	
	public int dy = 60;
	
	public RightPanel(ModelCreator creator)
	{
		this.creator = creator;
		setPreferredSize(new Dimension(215, 1450));
		initComponents();
	}

	public void initComponents()
	{
		removeAll();
		
		btnAdd = new JButton();
		btnRemove = new JButton();
		btnDuplicate = new JButton();
		nameField = new JTextField();
		tabbedPane = new CuboidTabbedPane(this);

		ModelCreator.currentProject.tree = tree;
		
		add(tree.jtree);
		scrollPane = new JScrollPane(tree.jtree);
		scrollPane.setPreferredSize(new Dimension(205, ModelCreator.elementTreeHeight + dy));
		add(scrollPane);

		
		Font defaultFont = new Font("SansSerif", Font.BOLD, 14);
		btnContainer = new JPanel(new GridLayout(1, 3, 4, 0));
		btnContainer.setPreferredSize(new Dimension(205, 30));

		btnAdd.setIcon(Icons.cube);
		btnAdd.setToolTipText("New Element");
		btnAdd.addActionListener(e -> { 
			Element elem = new Element(1,1,1);
			
			if (ModelCreator.currentProject.TexturesByCode.size() > 0) {
				elem.setTextureCode(ModelCreator.currentProject.TexturesByCode.values().iterator().next().code, false);
			}
			
			ModelCreator.currentProject.addElementAsChild(elem); 
		});
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
						elem.setName(nameField.getText().replaceAll(",", ""));
						nameField.setBackground(getBackground());
					}
				}
				
				tree.updateUI();
			}
		});
		add(nameField);
		



		tabbedPane.add("Cube", new ElementPanel(this));
		tabbedPane.add("Face", new FacePanel(this));
		tabbedPane.add("Keyframe", rightKeyFramesPanel = new RightKeyFramesPanel());
		tabbedPane.add("P", new AttachmentPointsPanel());
		
		tabbedPane.setPreferredSize(new Dimension(205, 1150));
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
			ModelCreator.guiMain.itemSaveGifAnimation.setEnabled(tabbedPane.getSelectedIndex() == 2 && ModelCreator.currentProject != null && ModelCreator.currentProject.SelectedAnimation != null);
			ModelCreator.guiMain.itemSavePngAnimation.setEnabled(tabbedPane.getSelectedIndex() == 2 && ModelCreator.currentProject != null && ModelCreator.currentProject.SelectedAnimation != null);
			
			ModelCreator.ignoreValueUpdates = true;
			updateValues(tabbedPane);
			ModelCreator.ignoreValueUpdates = false;
		});
		
		add(tabbedPane);
		
		setLayout(dy);
		setSidebarWidth(ModelCreator.prefs.getInt("rightBarWidth", 215));

		revalidate();
	}

	public void setLayout(int dy)
	{
		layout = new SpringLayout();
		
		int my = ModelCreator.elementTreeHeight - 240;
		
		layout.putConstraint(SpringLayout.NORTH, nameField, 212 + 70 + dy + my, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, btnContainer, 176 + 70 + dy + my, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, tabbedPane, 250 + 70 + dy + my, SpringLayout.NORTH, this);
		setLayout(layout);
	}

	@Override
	public Element getCurrentElement()
	{
		return ModelCreator.currentProject.SelectedElement;
	}



	public ModelCreator getCreator()
	{
		return creator;
	}

	@Override
	public void updateValues(JComponent byGuiElem)
	{
		tabbedPane.updateValues(byGuiElem);
		
		Element cube = getCurrentElement();
		if (cube != null)
		{
			nameField.setText(cube.getName());
			if (ModelCreator.currentProject.IsElementNameUsed(nameField.getText(), cube)) {
				nameField.setBackground(new Color(50, 0, 0));
			} else {
				nameField.setBackground(getBackground());
			}
		}
		
		nameField.setEnabled(cube != null);
		btnRemove.setEnabled(cube != null);
		btnDuplicate.setEnabled(cube != null);
		
		ModelCreator.guiMain.itemSaveGifAnimation.setEnabled(tabbedPane.getSelectedIndex() == 2 && ModelCreator.currentProject != null && ModelCreator.currentProject.SelectedAnimation != null);
		ModelCreator.guiMain.itemSavePngAnimation.setEnabled(tabbedPane.getSelectedIndex() == 2 && ModelCreator.currentProject != null && ModelCreator.currentProject.SelectedAnimation != null);
		
	}
	
	
	public void updateFrame(JComponent byGuiElem) {
		rightKeyFramesPanel.updateFrame(byGuiElem);
	}
	
	
	

	boolean nowResizingSidebar;
	int lastGrabMouseX;
	boolean overSidebar;

	public void onMouseDownOnRightPanel()
	{
		int width = getSize().width;
		int nowMouseX = MouseInfo.getPointerInfo().getLocation().x - ModelCreator.Instance.getX();
		int edgeX = -42 + ModelCreator.Instance.getRootPane().getWidth() - scrollPane.getWidth(); // ModelCreator.Instance.leftSidebarWidth() + 2 + ModelCreator.canvas.getWidth();
		
		if (Math.abs(edgeX - nowMouseX) < 8) {
			if (Mouse.isButtonDown(0)) {
				if (!nowResizingSidebar) {
					lastGrabMouseX = nowMouseX; 
				}
				
				nowResizingSidebar = true;
			}
			
			overSidebar = true;
		}
		
		if (nowResizingSidebar) {
			final int newwidth = Math.min(600, Math.max(215, width + (lastGrabMouseX - nowMouseX)));
			setSidebarWidth(newwidth);
			
			lastGrabMouseX = nowMouseX;
		}	
	}

	public void setSidebarWidth(int newwidth) {
		final int prevheight = getSize().height;
	
		ModelCreator.prefs.putInt("rightBarWidth", newwidth);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setPreferredSize(new Dimension(newwidth, prevheight));
				int my = ModelCreator.elementTreeHeight;
				scrollPane.setPreferredSize(new Dimension(newwidth - 10, my + dy));
				
				invalidate();
				ModelCreator.Instance.revalidate();
			}
		});		
	}
	
	public void Draw()
	{
		PointerInfo pinfo = MouseInfo.getPointerInfo(); 

		if (!Mouse.isButtonDown(0)) {
			nowResizingSidebar=false;
		}
		
		if (pinfo != null) {
		
			int nowMouseX = pinfo.getLocation().x - ModelCreator.Instance.getX();
			int edgeX = -42 + ModelCreator.Instance.getRootPane().getWidth() - scrollPane.getWidth(); // ModelCreator.Instance.leftSidebarWidth() + 2 + ModelCreator.canvas.getWidth();
			
			//System.out.println(edgeX + " vs. " + nowMouseX);
			
			if (Math.abs(edgeX - nowMouseX) < 8) {
				ModelCreator.Instance.isOnRightPanel=true;
				ModelCreator.canvas.setCursor(new java.awt.Cursor(Cursor.E_RESIZE_CURSOR));
				overSidebar = true;
			} else {
				ModelCreator.Instance.isOnRightPanel=false;
				if (overSidebar) {
					ModelCreator.canvas.setCursor(java.awt.Cursor.getDefaultCursor());				
					overSidebar = false;
				}
			}
		}
	}
}
