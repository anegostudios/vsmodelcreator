package at.vintagestory.modelcreator.gui.right;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.*;
import javax.swing.tree.*;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.gui.CuboidTabbedPane;
import at.vintagestory.modelcreator.gui.Icons;
import at.vintagestory.modelcreator.gui.right.element.ElementPanel;
import at.vintagestory.modelcreator.gui.right.face.FacePanel;
import at.vintagestory.modelcreator.gui.right.keyframes.KeyFramesPanel;
import at.vintagestory.modelcreator.gui.right.rotation.ElementRotationPanel;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.PendingTexture;

public class RightTopPanel extends JPanel implements IElementManager {
	private static final long serialVersionUID = 1L;

	private ModelCreator creator;

	// Swing Variables
	private SpringLayout layout;
	private JScrollPane scrollPane;
	private JPanel btnContainer;
	private JButton btnAdd = new JButton();
	private JButton btnRemove = new JButton();
	private JButton btnDuplicate = new JButton();
	private JTextField name = new JTextField();
	private CuboidTabbedPane tabbedPane = new CuboidTabbedPane(this);
	private boolean ambientOcc = true;

	public ElementTree tree;
	
	public RightTopPanel(ModelCreator creator)
	{
		this.creator = creator;
		setLayout(layout = new SpringLayout());
		setPreferredSize(new Dimension(215, 900));
		initComponents();
		setLayoutConstaints();
	}

	public void initComponents()
	{
		tree = new ElementTree();
		
		Font defaultFont = new Font("SansSerif", Font.BOLD, 14);

		btnContainer = new JPanel(new GridLayout(1, 3, 4, 0));
		btnContainer.setPreferredSize(new Dimension(205, 30));

		btnAdd.setIcon(Icons.cube);
		btnAdd.setToolTipText("New Element");
		btnAdd.addActionListener(e ->
		{
			tree.addElementAsChild(new Element(1,1,1));
			updateValues();
		});
		btnAdd.setPreferredSize(new Dimension(30, 30));
		btnContainer.add(btnAdd);

		btnRemove.setIcon(Icons.bin);
		btnRemove.setToolTipText("Remove Element");
		btnRemove.addActionListener(e ->
		{
			tree.removeCurrentElement();
			updateValues();
		});
		btnRemove.setPreferredSize(new Dimension(30, 30));
		btnContainer.add(btnRemove);

		btnDuplicate.setIcon(Icons.copy);
		btnDuplicate.setToolTipText("Duplicate Element");
		btnDuplicate.addActionListener(e ->
		{
			Element elem = tree.getSelectedElement();
			if (elem != null) {
				tree.addElementAsSibling(new Element(elem));
			}
		});
		btnDuplicate.setFont(defaultFont);
		btnDuplicate.setPreferredSize(new Dimension(30, 30));
		btnContainer.add(btnDuplicate);
		add(btnContainer);

		name.setPreferredSize(new Dimension(205, 25));
		name.setToolTipText("Element Name");
		name.setEnabled(false);

		name.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				Element elem = tree.getSelectedElement();
				if (elem != null) {
					elem.name = name.getText();
				}
				tree.jtree.updateUI();
			}
		});
		add(name);
		

		tree.jtree.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				updateValues();
			}
		});
		

		add(tree.jtree);

		scrollPane = new JScrollPane(tree.jtree);
		scrollPane.setPreferredSize(new Dimension(205, 240));
		add(scrollPane);

		tabbedPane.setBackground(new Color(127, 132, 145));
		tabbedPane.setForeground(Color.WHITE);
		tabbedPane.add("Element", new ElementPanel(this));
		tabbedPane.add("Faces", new FacePanel(this));
		tabbedPane.add("Keyframes", new KeyFramesPanel(this));
		tabbedPane.setPreferredSize(new Dimension(205, 600));
		tabbedPane.setTabPlacement(JTabbedPane.TOP);
		tabbedPane.addChangeListener(c ->
		{
			if (tabbedPane.getSelectedIndex() == 2)
			{
				creator.setSidebar(ModelCreator.uvSidebar);
			}
			else
			{
				creator.setSidebar(null);
			}
		});
		add(tabbedPane);
	}

	public void setLayoutConstaints()
	{
		layout.putConstraint(SpringLayout.NORTH, name, 212 + 70, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, btnContainer, 176 + 70, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, tabbedPane, 250 + 70, SpringLayout.NORTH, this);
	}

	@Override
	public Element getSelectedElement()
	{
		return tree.getSelectedElement();
	}

	@Override
	public void selectElementByOpenGLName(int pos)
	{
		tree.selectElementByOpenGLName(pos);
		updateValues();
	}

	@Override
	public List<Element> getRootElements()
	{
		return tree.GetRootElements();
	}



	@Override
	public void updateValues()
	{
		tabbedPane.updateValues();
		Element cube = getSelectedElement();
		if (cube != null)
		{
			name.setText(cube.name);
		}
		name.setEnabled(cube != null);
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
	public boolean getAmbientOcc()
	{
		return ambientOcc;
	}

	@Override
	public void setAmbientOcc(boolean occ)
	{
		ambientOcc = occ;
	}

	@Override
	public void clearElements()
	{
		tree.clearElements();
		updateValues();
	}

	@Override
	public void addElementAsChild(Element e)
	{
		tree.addElementAsChild(e);
		updateValues();
		tree.jtree.updateUI();
	}
	
	@Override
	public void addRootElement(Element e)
	{
		tree.addRootElement(e);
		updateValues();
		tree.jtree.updateUI();
	}


	@Override
	public void reset()
	{
		clearElements();
		ambientOcc = true;
	}
}
