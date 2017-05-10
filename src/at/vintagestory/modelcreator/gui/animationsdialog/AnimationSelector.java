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
import at.vintagestory.modelcreator.enums.EnumEntityActivity;
import at.vintagestory.modelcreator.enums.EnumEntityActivityStoppedHandling;
import at.vintagestory.modelcreator.enums.EnumEntityAnimationEndHandling;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.model.Animation;

public class AnimationSelector
{
	JDialog dialog;

	JList<String> list = new JList<String>();
	JTextField nameField = new JTextField();
	JScrollPane scroll = new JScrollPane(list);

	private JList<String> activitiesList;
	
	
	private JComboBox<String> activityStoppedList;
	private JComboBox<String> animEndedList;
	
	SpringLayout layout;
	
	JPanel leftPanel;
	JPanel rightPanel;
	
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
				
				ModelCreator.updateValues();
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
				if (ModelCreator.currentProject.SelectedAnimation == null) return;
				ModelCreator.currentProject.SelectedAnimation.setName(nameField.getText());
				
				model.set(list.getSelectedIndex(), nameField.getText());
				ModelCreator.updateValues();
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
			project.Animations.add(anim);
			project.SelectedAnimation = anim;
			
			model.addElement(anim.getName());
			list.setSelectedIndex(project.getSelectedAnimationIndex());
			
			ModelCreator.updateValues();
			
			nameField.setText(ModelCreator.currentProject.SelectedAnimation.getName());
			
			updateValues();
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
				
				ModelCreator.updateValues();
				
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
	
	
	public static void display(IElementManager manager)
	{
		AnimationSelector selector = new AnimationSelector();
		selector.show();
	}

	
	
	private void show()
	{
		dialog.setVisible(true);
		
	}


	private void addAnimationSettings()
	{
		layout = new SpringLayout();
		rightPanel = new JPanel(layout);
		
		
		activitiesList = new JList<String>();
		activitiesList.setToolTipText("The actvities for which the animation should play for");
		activitiesList.addListSelectionListener(e ->
		{
			if (ignoreSelectionChange) return;
			
			int[] indices = activitiesList.getSelectedIndices();
			
			ModelCreator.currentProject.SelectedAnimation.ForActivities.clear();
			
			for (int i = 0; i < indices.length; i++) {
				int index = indices[i];
				ModelCreator.currentProject.SelectedAnimation.ForActivities.add(EnumEntityActivity.values()[index]);	
			}
			
			ModelCreator.DidModify();
			ModelCreator.updateValues();
		});
		
		DefaultListModel<String> activityListItems = activityList();
		activitiesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		activitiesList.setPreferredSize(new Dimension(170, 170));	
		activitiesList.setModel(activityListItems);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(activitiesList);
		
		
		activityStoppedList = new JComboBox<String>();
		activityStoppedList.setToolTipText("What should happen once the activity has ended");
		activityStoppedList.addActionListener(e ->
		{
			int selectedIndex = activityStoppedList.getSelectedIndex();
			ModelCreator.currentProject.SelectedAnimation.OnActivityStopped = EnumEntityActivityStoppedHandling.values()[selectedIndex];	
		});
		
		activityStoppedList.setPreferredSize(new Dimension(170, 29));	
		activityStoppedList.setModel(activityStoppedList());
		
		
		animEndedList = new JComboBox<String>();
		animEndedList.setToolTipText("What should happen once the animation has ended");
		animEndedList.addActionListener(e ->
		{
			int selectedIndex = animEndedList.getSelectedIndex();
			ModelCreator.currentProject.SelectedAnimation.OnAnimationEnd = EnumEntityAnimationEndHandling.values()[selectedIndex];	
		});
		
		animEndedList.setPreferredSize(new Dimension(170, 29));	
		animEndedList.setModel(animationEndList());
		
		JLabel label = new JLabel("For Activities");
		label.setPreferredSize(new Dimension(170, 29));
		layout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, rightPanel);
		layout.putConstraint(SpringLayout.NORTH, label, 10, SpringLayout.NORTH, rightPanel);
		rightPanel.add(label);
		rightPanel.add(scrollPane);
		layout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, label);
		layout.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.SOUTH, label);
		
		
		
		label = new JLabel("On Activity stopped");
		label.setPreferredSize(new Dimension(170, 29));
		layout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, scrollPane);
		layout.putConstraint(SpringLayout.NORTH, label, 10, SpringLayout.SOUTH, scrollPane);
		
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
	

	private DefaultListModel<String> activityList()
	{
		DefaultListModel<String> model = new DefaultListModel<String>();
		
		for (EnumEntityActivity activity : EnumEntityActivity.values()) {
			model.addElement("<html><b>"+ activity +"</b></html>");	
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
		
		activitiesList.setEnabled(anim != null);
		activityStoppedList.setEnabled(anim != null);
		animEndedList.setEnabled(anim != null);
		
		if (anim == null) {
			ignoreSelectionChange = false;
			return;
		}
		
		int[] indices = new int[anim.ForActivities.size()];
		for (int i = 0; i < indices.length; i++) indices[i] = anim.ForActivities.get(i).index();
		activitiesList.setSelectedIndices(indices);
		
		activityStoppedList.setSelectedIndex(anim.OnActivityStopped == null ? 0 : anim.OnActivityStopped.index());
		animEndedList.setSelectedIndex(anim.OnAnimationEnd == null ? 0 : anim.OnAnimationEnd.index());
		
		ignoreSelectionChange = false;
	}

		
}
