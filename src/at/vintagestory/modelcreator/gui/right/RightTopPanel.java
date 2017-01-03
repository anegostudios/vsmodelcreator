package at.vintagestory.modelcreator.gui.right;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.gui.CuboidTabbedPane;
import at.vintagestory.modelcreator.gui.Icons;
import at.vintagestory.modelcreator.gui.right.element.ElementPanel;
import at.vintagestory.modelcreator.gui.right.face.FacePanel;
import at.vintagestory.modelcreator.gui.right.rotation.RotationPanel;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.PendingTexture;

public class RightTopPanel extends JPanel implements IElementManager
{
	private static final long serialVersionUID = 1L;

	private ModelCreator creator;

	// Swing Variables
	private SpringLayout layout;
	private DefaultListModel<Element> model = new DefaultListModel<Element>();
	private JList<Element> list = new JList<Element>();
	private JScrollPane scrollPane;
	private JPanel btnContainer;
	private JButton btnAdd = new JButton();
	private JButton btnRemove = new JButton();
	private JButton btnDuplicate = new JButton();
	private JTextField name = new JTextField();
	private CuboidTabbedPane tabbedPane = new CuboidTabbedPane(this);

	private String particle = null;
	//private String particleLocation = null;
	private boolean ambientOcc = true;

	public RightTopPanel(ModelCreator creator)
	{
		this.creator = creator;
		setLayout(layout = new SpringLayout());
		setPreferredSize(new Dimension(200, 800));
		initComponents();
		setLayoutConstaints();
	}

	public void initComponents()
	{
		Font defaultFont = new Font("SansSerif", Font.BOLD, 14);

		btnContainer = new JPanel(new GridLayout(1, 3, 4, 0));
		btnContainer.setPreferredSize(new Dimension(190, 30));

		btnAdd.setIcon(Icons.cube);
		btnAdd.setToolTipText("New Element");
		btnAdd.addActionListener(e ->
		{
			model.addElement(new Element(1, 1, 1));
			list.setSelectedIndex(model.size() - 1);
		});
		btnAdd.setPreferredSize(new Dimension(30, 30));
		btnContainer.add(btnAdd);

		btnRemove.setIcon(Icons.bin);
		btnRemove.setToolTipText("Remove Element");
		btnRemove.addActionListener(e ->
		{
			int selected = list.getSelectedIndex();
			if (selected != -1)
			{
				model.remove(selected);
				name.setText("");
				name.setEnabled(false);
				tabbedPane.updateValues();
				list.setSelectedIndex(selected);
			}
		});
		btnRemove.setPreferredSize(new Dimension(30, 30));
		btnContainer.add(btnRemove);

		btnDuplicate.setIcon(Icons.copy);
		btnDuplicate.setToolTipText("Duplicate Element");
		btnDuplicate.addActionListener(e ->
		{
			int selected = list.getSelectedIndex();
			if (selected != -1)
			{
				model.addElement(new Element(model.getElementAt(selected)));
				list.setSelectedIndex(model.getSize() - 1);
			}
		});
		btnDuplicate.setFont(defaultFont);
		btnDuplicate.setPreferredSize(new Dimension(30, 30));
		btnContainer.add(btnDuplicate);
		add(btnContainer);

		name.setPreferredSize(new Dimension(190, 30));
		name.setToolTipText("Element Name");
		name.setEnabled(false);
		name.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					updateName();
				}
			}
		});
		name.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				updateName();
			}
		});
		add(name);

		list.setModel(model);
		list.addListSelectionListener(e ->
		{
			Element cube = getSelectedElement();
			if (cube != null)
			{
				tabbedPane.updateValues();
				name.setEnabled(true);
				name.setText(cube.toString());
			}
		});

		scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(190, 170));
		add(scrollPane);

		tabbedPane.setBackground(new Color(127, 132, 145));
		tabbedPane.setForeground(Color.WHITE);
		tabbedPane.add("Element", new ElementPanel(this));
		tabbedPane.add("Rotation", new RotationPanel(this));
		tabbedPane.add("Faces", new FacePanel(this));
		tabbedPane.setPreferredSize(new Dimension(190, 540));
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
		layout.putConstraint(SpringLayout.NORTH, name, 212, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, btnContainer, 176, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, tabbedPane, 250, SpringLayout.NORTH, this);
	}

	@Override
	public Element getSelectedElement()
	{
		int i = list.getSelectedIndex();
		if (i != -1)
			return (Element) model.getElementAt(i);
		return null;
	}

	@Override
	public void setSelectedElement(int pos)
	{
		if (pos < model.size())
		{
			list.setSelectedIndex(pos);
			updateValues();
		}
	}

	@Override
	public List<Element> getAllElements()
	{
		List<Element> list = new ArrayList<Element>();
		for (int i = 0; i < model.size(); i++)
		{
			list.add(model.getElementAt(i));
		}
		return list;
	}

	@Override
	public Element getElement(int index)
	{
		return model.getElementAt(index);
	}

	@Override
	public int getElementCount()
	{
		return model.size();
	}

	@Override
	public void updateName()
	{
		String newName = name.getText();
		if (newName.isEmpty())
			newName = "Cuboid";
		Element cube = getSelectedElement();
		if (cube != null)
		{
			cube.setName(newName);
			name.setText(newName);
			list.updateUI();
		}
	}

	@Override
	public void updateValues()
	{
		tabbedPane.updateValues();
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
		model.clear();
	}

	@Override
	public void addElement(Element e)
	{
		model.addElement(e);
	}

	@Override
	public void setParticle(String texture)
	{
		this.particle = texture;
	}

	@Override
	public String getParticle()
	{
		return particle;
	}

	@Override
	public void reset()
	{
		clearElements();
		ambientOcc = true;
		particle = null;
	}
}
