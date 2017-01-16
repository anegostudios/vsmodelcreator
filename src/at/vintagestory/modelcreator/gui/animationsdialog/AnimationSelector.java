package at.vintagestory.modelcreator.gui.animationsdialog;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Project;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.model.Animation;

public class AnimationSelector
{
	
	public static void display(IElementManager manager)
	{
		Font defaultFont = new Font("SansSerif", Font.BOLD, 18);
		
		JList<String> list = new JList<String>();
		JTextField nameField = new JTextField();
		JScrollPane scroll = new JScrollPane(list);
		

		// 1. List and scroll pane
		DefaultListModel<String> model = generate();		
		list.setModel(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		scroll.getVerticalScrollBar().setVisible(false);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setPreferredSize(new Dimension(300, 300));
		
		 
		if (ModelCreator.currentProject.SelectedAnimation != null) {
			list.setSelectedIndex(ModelCreator.currentProject.getSelectedAnimationIndex());
			nameField.setText(ModelCreator.currentProject.SelectedAnimation.getName());
		}
		
		list.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				int selectedIndex = list.getSelectedIndex();
				ModelCreator.currentProject.SelectedAnimation = ModelCreator.currentProject.Animations.get(selectedIndex);
				ModelCreator.updateValues();
				
				nameField.setText(ModelCreator.currentProject.SelectedAnimation.getName());
			}
		});
		
		// 2. Textfield
		nameField.setPreferredSize(new Dimension(300, 25));
		nameField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				if (ModelCreator.currentProject.SelectedAnimation == null) return;
				ModelCreator.currentProject.SelectedAnimation.setName(nameField.getText());
				
				model.set(list.getSelectedIndex(), nameField.getText());
				ModelCreator.updateValues();
			}
		});
		

		// 3. Buttons
		JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
		buttonPanel.setPreferredSize(new Dimension(236, 30));
		
		
		
		
		JButton btnSelect = new JButton("New");
		btnSelect.addActionListener(a ->
		{
			Project project = ModelCreator.currentProject;
			Animation anim = new Animation();
			anim.setName("Animation " + (project.Animations.size() + 1));
			project.Animations.add(anim);
			project.SelectedAnimation = anim;
			
			model.addElement(anim.getName());
			list.setSelectedIndex(project.getSelectedAnimationIndex());
			
			ModelCreator.updateValues();
			
			nameField.setText(ModelCreator.currentProject.SelectedAnimation.getName());
		});
		
		btnSelect.setFont(defaultFont);
		buttonPanel.add(btnSelect);

		
		
		JButton btnImport = new JButton("Delete");
		btnImport.addActionListener(a ->
		{
			Project project = ModelCreator.currentProject;
			if (project.SelectedAnimation == null) return;
			
			int dialogResult = JOptionPane.showConfirmDialog (null, "Really delete this Animation?","Warning", JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION){
				int nextSelected = Math.max(0, project.getSelectedAnimationIndex() - 1);
				project.Animations.remove(project.SelectedAnimation);
				model.removeElementAt(list.getSelectedIndex());
				
				if (project.Animations.size() > 0) {
					list.setSelectedIndex(nextSelected);
					project.SelectedAnimation = project.Animations.get(nextSelected);
				}
				
				ModelCreator.updateValues();
				
				if (ModelCreator.currentProject.SelectedAnimation == null) {
					nameField.setText("");
				} else {
					nameField.setText(ModelCreator.currentProject.SelectedAnimation.getName());	
				}
				
			}		
		});
		
		btnImport.setFont(defaultFont);
		buttonPanel.add(btnImport);

		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(a ->
		{
			SwingUtilities.getWindowAncestor(btnClose).dispose();
		});
		btnClose.setFont(defaultFont);
		buttonPanel.add(btnClose);

		
		JDialog dialog = new JDialog(manager.getCreator(), "List of Animations", false);
		SpringLayout layout = new SpringLayout();
		dialog.setLayout(layout);
		
		layout.putConstraint(SpringLayout.WEST, scroll, 0, SpringLayout.WEST, dialog);
		layout.putConstraint(SpringLayout.NORTH, scroll, 0, SpringLayout.NORTH, dialog);
		
		layout.putConstraint(SpringLayout.WEST, nameField, 0, SpringLayout.WEST, dialog);
		layout.putConstraint(SpringLayout.NORTH, nameField, 0, SpringLayout.SOUTH, scroll);
		
		layout.putConstraint(SpringLayout.WEST, buttonPanel, 0, SpringLayout.WEST, dialog);
		layout.putConstraint(SpringLayout.NORTH, buttonPanel, 0, SpringLayout.SOUTH, nameField);

		//layout.putConstraint(SpringLayout.EAST, dialog, 0, SpringLayout.EAST, scroll);
		//layout.putConstraint(SpringLayout.SOUTH, dialog, 480, SpringLayout.SOUTH, scroll);

		
		dialog.setResizable(false);
		dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		dialog.setPreferredSize(new Dimension(240, 384));
		dialog.add(scroll);
		dialog.add(nameField);
		dialog.add(buttonPanel);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		
		
	}

	
	private static DefaultListModel<String> generate()
	{
		DefaultListModel<String> model = new DefaultListModel<String>();
		Project project = ModelCreator.currentProject;
		if (project != null) {
			for (Animation anim : project.Animations) {
				model.addElement("<html><b>"+ anim.getName() +"</b></html>");	
			}
		}
		
		return model;
	}


}
