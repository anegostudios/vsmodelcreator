package at.vintagestory.modelcreator.gui.right.attachmentpoints;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SpringLayout;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Start;
import at.vintagestory.modelcreator.gui.Icons;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.AttachmentPoint;
import at.vintagestory.modelcreator.model.Element;

public class AttachmentPointsPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	SpringLayout layout;
	
	JList<AttachmentPoint> pointList;
	JPanel btnContainer;
	JButton btnAdd = new JButton();
	JButton btnRemove = new JButton();
	
	JTextField codeField;
	
	AttachmentPointPosPanel posPanel;
	AttachmentPointRotPanel rotPanel;	

	ListModel<AttachmentPoint> pointListModel;
	
	public AttachmentPointsPanel()
	{
		setLayout(layout = new SpringLayout());
		setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>Attachment Points</b></html>"));
		setMaximumSize(new Dimension(186, 124));
		initComponents();
		addComponents();
		setLayoutConstaints();
	}

	public void initComponents()
	{
		pointListModel = new AbstractListModel<AttachmentPoint>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public int getSize()
			{
				if (ModelCreator.currentProject.SelectedElement == null) return 0;
				return ModelCreator.currentProject.SelectedElement.AttachmentPoints.size();
			}

			@Override
			public AttachmentPoint getElementAt(int index)
			{
				if (ModelCreator.currentProject.SelectedElement == null) return null;
				return ModelCreator.currentProject.SelectedElement.AttachmentPoints.get(index);
			}
		};
		
		pointList = new JList<AttachmentPoint>();
		pointList.setModel(pointListModel);
		pointList.setVisible(true);
		pointList.setCellRenderer(new AttachmentPointCellRenderer());
		
		pointList.setPreferredSize(new Dimension(195, 100));
		pointList.addListSelectionListener(e -> {
			ModelCreator.currentProject.SelectedAttachmentPoint = pointList.getSelectedValue();
			updateValues();
		});

		btnContainer = new JPanel(new GridLayout(1, 3, 4, 0));
		btnContainer.setPreferredSize(new Dimension(195, 30));

		btnAdd.setIcon(Icons.add);
		btnAdd.setToolTipText("New Element");
		btnAdd.addActionListener(e -> {
			if (ModelCreator.currentProject.SelectedElement == null) return;
			ModelCreator.currentProject.SelectedAttachmentPoint = ModelCreator.currentProject.SelectedElement.addNewAttachmentPoint();
			updateValues();
			
		});
		btnAdd.setPreferredSize(new Dimension(30, 30));
		btnContainer.add(btnAdd);

		btnContainer.add(new JLabel());
		
		btnRemove.setIcon(Icons.bin);
		btnRemove.setToolTipText("Remove Element");
		btnRemove.addActionListener(e -> { 
			if (ModelCreator.currentProject.SelectedElement == null) return;
			ModelCreator.currentProject.SelectedElement.removeCurrentAttachmentPoint();
			ModelCreator.currentProject.SelectedAttachmentPoint = null;
			updateValues();
		});
		
		btnRemove.setPreferredSize(new Dimension(30, 30));
		btnContainer.add(btnRemove);

		
		codeField = new JTextField();
		codeField.setPreferredSize(new Dimension(195, 25));
		codeField.setToolTipText("Unique text identifier for this attachment point");
		


		codeField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				if (ModelCreator.currentProject.SelectedElement == null || ModelCreator.currentProject.SelectedAttachmentPoint == null) return;
				AttachmentPoint point = ModelCreator.currentProject.SelectedAttachmentPoint;
				
				if (point.getCode().equals(codeField.getText())) return;
				
				if (ModelCreator.currentProject.IsAttachmentPointCodeUsed(codeField.getText(), point)) {
					codeField.setBackground(new Color(50, 0, 0));
				} else {
					point.setCode(codeField.getText());
					codeField.setBackground(getBackground());
				}
				
				updateValues();
			}
		});
		
		posPanel = new AttachmentPointPosPanel();
		
		rotPanel = new AttachmentPointRotPanel();
		rotPanel.setPreferredSize(new Dimension(195, 190));
	}
	

	public void setLayoutConstaints()
	{
		layout.putConstraint(SpringLayout.NORTH, pointList, 0, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, btnContainer, 105, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, codeField, 140, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, posPanel, 190, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, rotPanel, 310, SpringLayout.NORTH, this);
	}
	
	public void addComponents()
	{
		add(pointList);
		add(btnContainer);
		add(codeField);
		add(posPanel);
		add(rotPanel);
	}

	@Override
	public void updateValues()
	{
		pointList.updateUI();
		
		
		posPanel.updateValues();
		rotPanel.updateValues();
		
		Element curElem = ModelCreator.currentProject.SelectedElement;
		pointList.setEnabled(curElem != null);
		codeField.setEnabled(curElem != null);
		
		if (curElem == null) {
			ModelCreator.currentProject.SelectedAttachmentPoint = null;
			return;
		}
				
		AttachmentPoint point = ModelCreator.currentProject.SelectedAttachmentPoint;
		
		codeField.setEnabled(point != null);
		
		if (point != null)
		{
			if (!curElem.AttachmentPoints.contains(point)) {
				ModelCreator.currentProject.SelectedAttachmentPoint = null;
				pointList.setEnabled(false);
				codeField.setEnabled(false);
			} else {
				pointList.setSelectedIndex(curElem.AttachmentPoints.indexOf(point));
			}
			
			codeField.setText(point.getCode());
			if (ModelCreator.currentProject.IsAttachmentPointCodeUsed(codeField.getText(), point)) {
				codeField.setBackground(new Color(50, 0, 0));
			} else {
				codeField.setBackground(getBackground());
			}
		}
	}
}
