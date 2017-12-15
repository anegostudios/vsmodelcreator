package at.vintagestory.modelcreator.gui.animationsdialog;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
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
import at.vintagestory.modelcreator.enums.EnumEntityActivityStoppedHandling;
import at.vintagestory.modelcreator.enums.EnumEntityAnimationEndHandling;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.model.Animation;

public class AnimationSelector
{
	JDialog dialog;

	JList<String> list = new JList<String>();
	JTextField nameField = new JTextField();
	JTextField codeField = new JTextField();
	JScrollPane scroll = new JScrollPane(list);

	
	private JComboBox<String> activityStoppedList;
	private JComboBox<String> animEndedList;
	
	SpringLayout layout;
	
	JPanel leftPanel;
	JPanel rightPanel;
	
	boolean suggestCode = false;
	boolean ignoreSelectionChange = false;

	
	public AnimationSelector() {
		Font defaultFont = new Font("SansSerif", Font.BOLD, 18);
		
		// 1. List and scroll pane
		DefaultListModel<String> model = animList();		
		list.setModel(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
		scroll.getVerticalScrollBar().setVisible(false);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setPreferredSize(new Dimension(232, 300));
		
		 
		if (ModelCreator.currentProject.SelectedAnimation != null) {
			list.setSelectedIndex(ModelCreator.currentProject.getSelectedAnimationIndex());
			nameField.setText(ModelCreator.currentProject.SelectedAnimation.getName());
		}
		
		list.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if (ignoreSelectionChange) return;
				
				int selectedIndex = list.getSelectedIndex();
				
				ModelCreator.currentProject.SelectedAnimation = ModelCreator.currentProject.Animations.get(selectedIndex);
				//System.out.println("selected " + ModelCreator.currentProject.SelectedAnimation);
				nameField.setText(ModelCreator.currentProject.SelectedAnimation.getName());
				
				ModelCreator.updateValues(list);
				updateValues();
			}
		});
		
		
		// 2. Text field
		nameField.setPreferredSize(new Dimension(231, 25));
		nameField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				Animation anim = ModelCreator.currentProject.SelectedAnimation;
				if (anim == null) return;
				if (anim.getName() != null && anim.getName().equals(nameField.getText())) return;

				ModelCreator.ignoreDidModify = true;
				if (suggestCode) {
					anim.setCode(nameField.getText().toLowerCase().replaceAll(" ", ""));
					codeField.setText(anim.getCode());
				}
				anim.setName(nameField.getText());
				
				model.set(list.getSelectedIndex(), nameField.getText());
				ModelCreator.ignoreDidModify = false;
				
				ModelCreator.updateValues(nameField);
				ModelCreator.DidModify();
				
			}
		});
		

		// 3. Buttons
		JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
		buttonPanel.setPreferredSize(new Dimension(233, 30));
		
		
		
		
		JButton btnSelect = new JButton("New");
		btnSelect.addActionListener(a ->
		{
			Project project = ModelCreator.currentProject;
			Animation anim = new Animation();
			anim.setName("Animation " + (project.Animations.size() + 1));
			anim.setCode(anim.getName().toLowerCase().replaceAll(" ", ""));
			project.Animations.add(anim);
			project.SelectedAnimation = anim;
			
			model.addElement(anim.getName());
			list.setSelectedIndex(project.getSelectedAnimationIndex());
			
			ModelCreator.updateValues(btnSelect);
			
			nameField.setText(ModelCreator.currentProject.SelectedAnimation.getName());
			
			updateValues();
			suggestCode = true;
		});
		
		btnSelect.setFont(defaultFont);
		buttonPanel.add(btnSelect);

		
		
		JButton btnImport = new JButton("Delete");
		btnImport.addActionListener(a ->
		{
			Project project = ModelCreator.currentProject;
			if (project.SelectedAnimation == null) return;
			
			int dialogResult = JOptionPane.showConfirmDialog (null, "Really delete this Animation?","Warning", JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION) {
				
				ignoreSelectionChange = true;
				int nextSelected = Math.max(0, project.getSelectedAnimationIndex() - 1);
				project.Animations.remove(project.SelectedAnimation);
				model.removeElementAt(list.getSelectedIndex());
				
				if (project.Animations.size() > 0) {
					list.setSelectedIndex(nextSelected);
					project.SelectedAnimation = project.Animations.get(nextSelected);
				}
				
				ModelCreator.updateValues(btnImport);
				
				if (ModelCreator.currentProject.SelectedAnimation == null) {
					nameField.setText("");
				} else {
					nameField.setText(ModelCreator.currentProject.SelectedAnimation.getName());	
				}
				
				updateValues();
				ignoreSelectionChange = false;
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

		
		dialog = new JDialog(ModelCreator.Instance, "List of Animations", false);
		dialog.setLayout(new GridLayout(1, 2, 10, 10));
		

		leftPanel = new JPanel(layout);
		leftPanel.add(scroll);
		leftPanel.add(nameField);
		leftPanel.add(buttonPanel);
		
		layout = new SpringLayout();
		leftPanel.setLayout(layout);
		layout.putConstraint(SpringLayout.WEST, scroll, 0, SpringLayout.WEST, leftPanel);
		layout.putConstraint(SpringLayout.NORTH, scroll, 10, SpringLayout.NORTH, leftPanel);
		
		layout.putConstraint(SpringLayout.WEST, nameField, 0, SpringLayout.WEST, leftPanel);
		layout.putConstraint(SpringLayout.NORTH, nameField, 0, SpringLayout.SOUTH, scroll);
		
		layout.putConstraint(SpringLayout.WEST, buttonPanel, 0, SpringLayout.WEST, leftPanel);
		layout.putConstraint(SpringLayout.NORTH, buttonPanel, 2, SpringLayout.SOUTH, nameField);

		dialog.add(leftPanel);
		
		addAnimationSettings();
				
		dialog.setResizable(false);
		dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		dialog.setPreferredSize(new Dimension(480, 394));
		
		dialog.pack();
		dialog.setLocationRelativeTo(null);
	}
	

	private void addAnimationSettings()
	{
		layout = new SpringLayout();
		rightPanel = new JPanel(layout);
		
		codeField = new JTextField();
		codeField.setPreferredSize(new Dimension(200, 25));
		codeField.setToolTipText("The internal name the animation is referenced by. Must be a unique name per model");
		codeField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				Animation anim = ModelCreator.currentProject.SelectedAnimation;
				if (anim == null) return;
				
				if (anim.getCode() != null && anim.getCode().equals(codeField.getText())) return;
				
				
				anim.setCode(codeField.getText());
				ModelCreator.updateValues(codeField);
				ModelCreator.DidModify();
				suggestCode = false;
			}
		});
		
		
		activityStoppedList = new JComboBox<String>();
		activityStoppedList.setToolTipText("What should happen once the activity has ended");
		activityStoppedList.addActionListener(e ->
		{
			if (ignoreSelectionChange) return;
			
			int selectedIndex = activityStoppedList.getSelectedIndex();
			ModelCreator.currentProject.SelectedAnimation.OnActivityStopped = EnumEntityActivityStoppedHandling.values()[selectedIndex];
			ModelCreator.DidModify();
		});
		
		activityStoppedList.setPreferredSize(new Dimension(200, 29));	
		activityStoppedList.setModel(activityStoppedList());
		
		
		animEndedList = new JComboBox<String>();
		animEndedList.setToolTipText("What should happen once the animation has ended");
		animEndedList.addActionListener(e ->
		{
			if (ignoreSelectionChange) return;
			
			int selectedIndex = animEndedList.getSelectedIndex();
			ModelCreator.currentProject.SelectedAnimation.OnAnimationEnd = EnumEntityAnimationEndHandling.values()[selectedIndex];
			ModelCreator.DidModify();
		});
		
		animEndedList.setPreferredSize(new Dimension(200, 29));	
		animEndedList.setModel(animationEndList());
		
		JLabel label = new JLabel("Code");
		label.setPreferredSize(new Dimension(170, 29));
		layout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, rightPanel);
		layout.putConstraint(SpringLayout.NORTH, label, 10, SpringLayout.NORTH, rightPanel);
		rightPanel.add(label);
		rightPanel.add(codeField);
		layout.putConstraint(SpringLayout.WEST, codeField, 0, SpringLayout.WEST, label);
		layout.putConstraint(SpringLayout.NORTH, codeField, 0, SpringLayout.SOUTH, label);
		
		
		
		label = new JLabel("On Activity stopped");
		label.setPreferredSize(new Dimension(170, 29));
		layout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, codeField);
		layout.putConstraint(SpringLayout.NORTH, label, 10, SpringLayout.SOUTH, codeField);
		
		rightPanel.add(label);
		rightPanel.add(activityStoppedList);
		layout.putConstraint(SpringLayout.WEST, activityStoppedList, 0, SpringLayout.WEST, label);
		layout.putConstraint(SpringLayout.NORTH, activityStoppedList, 0, SpringLayout.SOUTH, label);

		
		
		label = new JLabel("On Animation ended");
		label.setPreferredSize(new Dimension(170, 29));
		layout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, activityStoppedList);
		layout.putConstraint(SpringLayout.NORTH, label, 10, SpringLayout.SOUTH, activityStoppedList);		
		rightPanel.add(label);
		rightPanel.add(animEndedList);
		
		layout.putConstraint(SpringLayout.WEST, animEndedList, 0, SpringLayout.WEST, label);
		layout.putConstraint(SpringLayout.NORTH, animEndedList, 0, SpringLayout.SOUTH, label);

		
		rightPanel.add(new JLabel(""));
		
		
		dialog.add(rightPanel);
		
		updateValues();
	}


	private DefaultListModel<String> animList()
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
	



	private DefaultComboBoxModel<String> activityStoppedList()
	{
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		
		for (EnumEntityActivityStoppedHandling activity : EnumEntityActivityStoppedHandling.values()) {
			model.addElement("<html><b>"+ activity +"</b></html>");	
		}
		
		return model;
	}


	private DefaultComboBoxModel<String> animationEndList()
	{
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		
		for (EnumEntityAnimationEndHandling activity : EnumEntityAnimationEndHandling.values()) {
			model.addElement("<html><b>"+ activity +"</b></html>");	
		}
		
		return model;
	}
	
	
	public void updateValues() {
		ignoreSelectionChange = true;
		
		Animation anim = ModelCreator.currentProject.SelectedAnimation;
		
		codeField.setEnabled(anim != null);
		activityStoppedList.setEnabled(anim != null);
		animEndedList.setEnabled(anim != null);
		
		if (anim == null) {
			ignoreSelectionChange = false;
			return;
		}
		
		
		codeField.setText(anim.getCode());
		activityStoppedList.setSelectedIndex(anim.OnActivityStopped == null ? 0 : anim.OnActivityStopped.index());
		animEndedList.setSelectedIndex(anim.OnAnimationEnd == null ? 0 : anim.OnAnimationEnd.index());
		
		ignoreSelectionChange = false;
	}

	
	public static void display(IElementManager manager)
	{
		AnimationSelector selector = new AnimationSelector();
		selector.show();
	}

	
	
	private void show()
	{
		dialog.setVisible(true);
		
	}


		
}
