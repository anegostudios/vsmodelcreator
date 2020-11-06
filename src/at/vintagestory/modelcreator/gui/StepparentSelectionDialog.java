package at.vintagestory.modelcreator.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.ParentElemEntry;

public class StepparentSelectionDialog
{
	public static void show(IElementManager manager, JFrame parent, Element forElement)
	{
		StepparentSelectionDialog.rebuildElementList(forElement.getStepParent());
		
		JDialog dialog = new JDialog(parent, "Select step parent element", false);
		
		JPanel container = new JPanel(new BorderLayout(20, 10));
		container.setBorder(new EmptyBorder(10, 10, 10, 10));

		
		JPanel panelRow1 = new JPanel(new GridLayout(5, 1, 5, 0));
		JLabel label = new JLabel("Element");
		label.setPreferredSize(new Dimension(200, 20));
		panelRow1.add(label);
				
		JComboBox<ParentElemEntry> stepParentList = new JComboBox<ParentElemEntry>();
		stepParentList.setToolTipText("To define a parent element, without actually having it be part of the model. Useful for defining child elements of backdrop models.");
		DefaultComboBoxModel<ParentElemEntry> model = elementList;		
		stepParentList.setModel(model);
		stepParentList.setPreferredSize(new Dimension(450, 25));
		stepParentList.setMaximumRowCount(40);
		if (forElement.getStepParent() != null) {
			stepParentList.setSelectedItem(forElement.getStepParent());
		}
		panelRow1.add(stepParentList);
		
		
		container.add(panelRow1, BorderLayout.CENTER);
		
		JButton btnDelete = new JButton("Remove step parent");
		btnDelete.setIcon(Icons.bin);
		btnDelete.addActionListener(a ->
		{
			forElement.setStepParent(null);			
			dialog.dispose();
			ModelCreator.updateValues(btnDelete);
		});
		
		
		
		JPanel panelRow2 = new JPanel(new GridLayout(2, 2, 15, 15));
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setIcon(Icons.clear);
		btnCancel.addActionListener(a ->
		{
			dialog.dispose();
		});
		
		
		panelRow2.add(btnCancel);
		
		JButton btnDuplicate = new JButton("Apply");
		btnDuplicate.setIcon(Icons.disk);
		btnDuplicate.addActionListener(a ->
		{
			ParentElemEntry entry = (ParentElemEntry)stepParentList.getSelectedItem();
			forElement.setStepParent(entry.ElemName);			
			dialog.dispose();
			
			ModelCreator.updateValues(btnDuplicate);
		});
		
		
		panelRow2.add(btnDuplicate);
		panelRow2.add(btnDelete);
			
		container.add(panelRow2, BorderLayout.SOUTH);
		
		
		dialog.setResizable(false);
		dialog.add(container);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		dialog.requestFocusInWindow();
	}
	
	
	static DefaultComboBoxModel<ParentElemEntry> elementList;

	static ArrayList<ParentElemEntry> elements = new ArrayList<ParentElemEntry>();
	
	
	public static void rebuildElementList(String selectedElement)
	{
		if (elementList == null) {
			elementList = new DefaultComboBoxModel<ParentElemEntry>();
		} else {
			elementList.removeAllElements();
			elements.clear();
		}
		
		addElementsToModel(elementList, ModelCreator.currentProject.rootElements, "");
		
		if (ModelCreator.currentBackdropProject != null) {
			addElementsToModel(elementList, ModelCreator.currentBackdropProject.rootElements, "");
		}
		
		for (ParentElemEntry elem : elements) {
			if (elem.ElemName.equals(selectedElement)) {
				elementList.setSelectedItem(elem);
				break;
			}
		}
	}

	private static void addElementsToModel(DefaultComboBoxModel<ParentElemEntry> model, ArrayList<Element> elems, String prefix)
	{
		for (Element elem : elems) {
			ParentElemEntry entry = new ParentElemEntry(prefix + elem.getName(), elem.getName());
			elements.add(entry);
			model.addElement(entry);
			
			prefix += "  ";
			addElementsToModel(model, elem.ChildElements, prefix);
		}
	}
	
}
