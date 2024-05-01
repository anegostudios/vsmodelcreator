package at.vintagestory.modelcreator.gui.left;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Project;
import at.vintagestory.modelcreator.Start;
import at.vintagestory.modelcreator.gui.FrameSelectionDialog;
import at.vintagestory.modelcreator.gui.Icons;
import at.vintagestory.modelcreator.gui.animationsdialog.AnimationSelector;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Animation;
import at.vintagestory.modelcreator.model.AnimationFrame;
import at.vintagestory.modelcreator.util.AwtUtil;
import at.vintagestory.modelcreator.model.AnimFrameElement;

public class LeftKeyFramesPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;
	private IElementManager manager;
	
	private JComboBox<String> animationsList;
	private DefaultComboBoxModel<String> animationsListModel;	
	
	
	private JComboBox<String> mountanimationsList;
	private DefaultComboBoxModel<String> mountanimationsListModel;
	
	
	JButton animationAddRemoveButton;	
	
	AbstractTableModel tableModel;
	
	JLabel mountAnimLabel;
	
	JTextField durationTextField;
	JSlider frameSlider;
	JLabel currentFrameLabel;
	
	JPanel btnContainerTop;
	JButton playPauseButton;
	JButton nextFrameButton;
	JButton prevFrameButton;
	
	JTable keyFramesTable;
	String[] columnNames = {"#", "", "", ""};
	
	JPanel btnContainerBottom;
	
	JButton deleteFrameButton;
	JButton duplicateFrameButton;
	JButton moveFrameRightButton;
	JButton moveFrameLeftButton;
	
	JCheckBox treadMill;
	JTextField treadMillSpeed;
	
	boolean ignoreSelectionChange = false;
	
	
	public LeftKeyFramesPanel(IElementManager manager)
	{
		this.manager = manager;
	}
	
	public void Load() {
		this.removeAll();
		setPreferredSize(new Dimension(215, 900));
		initComponents();
		addComponents();	
		updateValues(this);
	}


	private void initComponents()
	{
		durationTextField = new JTextField();
		frameSlider = new JSlider();
		playPauseButton = new JButton();
		nextFrameButton = new JButton();
		prevFrameButton = new JButton();
		animationAddRemoveButton = new JButton();
		currentFrameLabel = new JLabel();
		keyFramesTable = new JTable();
		deleteFrameButton = new JButton();
		duplicateFrameButton = new JButton();
		moveFrameLeftButton = new JButton();
		moveFrameRightButton = new JButton();
		
		treadMill = new JCheckBox();
		treadMillSpeed = new JTextField("1");
		
		
		tableModel = new AbstractTableModel()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public String getColumnName(int col) {
		        return columnNames[col];
		    }
			
			@Override
			public Object getValueAt(int rowIndex, int columnIndex)
			{
				Project project = ModelCreator.CurrentAnimProject();
				int[] frameNumbers = project.GetFrameNumbers();
				if (frameNumbers == null || project.SelectedAnimation == null) return "";
				
				if (columnIndex == 0) return frameNumbers[rowIndex];
				
				AnimationFrame keyFrame = project.SelectedAnimation.keyframes[rowIndex];
				
				if (keyFrame == null) return "";
				
				AnimFrameElement keyframElem = keyFrame.GetKeyFrameElementFlat(ModelCreator.currentProject.SelectedElement);
				
				if (keyframElem == null) return "";

				if (columnIndex == 1) return keyframElem.PositionSet ? "P" : "";
				if (columnIndex == 2) return keyframElem.RotationSet ? "R" : "";
				if (columnIndex == 3) return keyframElem.StretchSet ? "S" : "";
				
				return "";
			}
			
			@Override
			public int getRowCount()
			{
				Project project = ModelCreator.CurrentAnimProject();
				
				if (project == null) return 0;
				return project.GetKeyFrameCount();
			}
			
			@Override
			public int getColumnCount()
			{
				return 4;
			}
			
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Class getColumnClass(int c) {
		        return String.class; //getValueAt(0, c).getClass();
		    }
		};
		
		keyFramesTable.setModel(tableModel);
	}
	
	

	private void addComponents()
	{
		
		// 0. Mount animation dropdown
		if (ModelCreator.currentMountBackdropProject != null) {
			add(mountAnimLabel=new JLabel("Mount Animation"));
			
			mountanimationsList = new JComboBox<String>();
			
			mountanimationsList.setToolTipText("The current mount animation to be played");
			mountanimationsList.addActionListener(e ->
			{
				if (ignoreSelectionChange) return;
				int selectedIndex = mountanimationsList.getSelectedIndex();
				selectMountAnimation(selectedIndex);
			});
			
			mountanimationsList.setPreferredSize(new Dimension(170, 29));
			mountanimationsList.setMaximumRowCount(25);
			
			add(mountanimationsList);
		}
		
		// 1. Animation DropDown
		{
			SpringLayout animationListPanelLayout = new SpringLayout();
			JPanel animationListPanel = new JPanel(animationListPanelLayout);

			animationListPanel.setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>Animation</b></html>"));
			
			animationsList = new JComboBox<String>();
			
			animationsList.setToolTipText("The current animation you want to edit");
			animationsList.addActionListener(e ->
			{
				if (ignoreSelectionChange) return;
				int selectedIndex = animationsList.getSelectedIndex();
				selectAnimation(selectedIndex);
			});
			
			animationsList.setPreferredSize(new Dimension(170, 29));
			animationsList.setMaximumRowCount(25);
			
			animationListPanel.add(animationsList);
	
			
			
			animationAddRemoveButton.setIcon(Icons.addremove);
			animationAddRemoveButton.setToolTipText("Add/Remove Animation");
			animationAddRemoveButton.addActionListener(e ->
			{
				AnimationSelector.display(manager);
			});
			
			animationAddRemoveButton.setPreferredSize(new Dimension(30, 29));		
			animationListPanel.add(animationAddRemoveButton);
			
			animationListPanelLayout.putConstraint(SpringLayout.WEST, animationsList, 0, SpringLayout.WEST, animationListPanel);
			animationListPanelLayout.putConstraint(SpringLayout.NORTH, animationsList, 5, SpringLayout.NORTH, animationListPanel);
					
			animationListPanelLayout.putConstraint(SpringLayout.WEST, animationAddRemoveButton, 5, SpringLayout.EAST, animationsList);
			animationListPanelLayout.putConstraint(SpringLayout.NORTH, animationAddRemoveButton, 5, SpringLayout.NORTH, animationListPanel);
	
			animationListPanelLayout.putConstraint(SpringLayout.EAST, animationListPanel, 0, SpringLayout.EAST, animationAddRemoveButton);
			animationListPanelLayout.putConstraint(SpringLayout.SOUTH, animationListPanel, 5, SpringLayout.SOUTH, animationAddRemoveButton);
			
			add(animationListPanel);
		}
		
		// 2. Duration
		JPanel durationPanel = new JPanel(new GridLayout(1, 2));
		durationPanel.setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>Duration in Frames</b></html>"));
		
		Font defaultFont = new Font("SansSerif", Font.BOLD, 14);
		durationTextField.setSize(new Dimension(62, 30));
		durationTextField.setFont(defaultFont);
		
		durationTextField.setPreferredSize(new Dimension(100, 25));
		durationTextField.setToolTipText("Duration in Frames");
		durationTextField.setEnabled(false);

		durationTextField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				setNewQuantityFrames();
			}
		});
		
		durationTextField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				setNewQuantityFrames();
			}

		});
		
		durationPanel.add(durationTextField);
		
		durationPanel.add(new JLabel("")); // Spacer
		
		add(durationPanel);
		
		
		// 3. Current Frame
		
		SpringLayout layout = new SpringLayout();
		JPanel panel = new JPanel(layout);
		
		panel.setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b></b></html>"));
		
		frameSlider = new JSlider(JSlider.HORIZONTAL, 0, 0, 0);
		frameSlider.setMajorTickSpacing(1);
		frameSlider.setPaintTicks(true);
		frameSlider.setPaintLabels(true);
		frameSlider.setPreferredSize(new Dimension(170, 40));
		frameSlider.addChangeListener(new ChangeListener()
		{			
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (ignoreChange) return;
				
				Project project = ModelCreator.CurrentAnimProject();
				
				int frameDelta = frameSlider.getValue() - project.SelectedAnimation.currentFrame;
				if (frameDelta != 0 && ModelCreator.currentMountBackdropProject != null && ModelCreator.currentMountBackdropProject.SelectedAnimation != null) {
					int newframe = ModelCreator.currentMountBackdropProject.SelectedAnimation.currentFrame + frameDelta;
					ModelCreator.currentMountBackdropProject.SelectedAnimation.currentFrame = Animation.mod(newframe, ModelCreator.currentMountBackdropProject.SelectedAnimation.GetQuantityFrames());
				}
				
				project.SelectedAnimation.currentFrame = frameSlider.getValue();
				
				
				if (project.SelectedAnimation.currentFrame == 0 && ModelCreator.currentMountBackdropProject != null && ModelCreator.currentMountBackdropProject.SelectedAnimation != null) {
					ModelCreator.currentMountBackdropProject.SelectedAnimation.currentFrame=0;
				}
				
				ModelCreator.updateFrame();		
			}
		});
		
		
		panel.add(frameSlider);
		
		currentFrameLabel.setText("");
		currentFrameLabel.setPreferredSize(new Dimension(30, 20));
		
		
		panel.add(currentFrameLabel);
		
		layout.putConstraint(SpringLayout.WEST, frameSlider, 0, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, frameSlider, 5, SpringLayout.NORTH, panel);
				
		layout.putConstraint(SpringLayout.WEST, currentFrameLabel, 5, SpringLayout.EAST, frameSlider);
		layout.putConstraint(SpringLayout.NORTH, currentFrameLabel, 2, SpringLayout.NORTH, panel);

		layout.putConstraint(SpringLayout.EAST, panel, 5, SpringLayout.EAST, currentFrameLabel);
		layout.putConstraint(SpringLayout.SOUTH, panel, 5, SpringLayout.SOUTH, currentFrameLabel);

		add(panel);
		
		
		// 4. Play, Prev and Next Button
		btnContainerTop = new JPanel(new GridLayout(1, 3, 4, 0));
		btnContainerTop.setPreferredSize(new Dimension(205, 30));
		
		playPauseButton.setIcon(Icons.play);
		playPauseButton.setToolTipText("Play/Pause");
		playPauseButton.addActionListener(e ->
		{
			Project project = ModelCreator.CurrentAnimProject();
			
			project.PlayAnimation = !project.PlayAnimation;
			playPauseButton.setIcon(project.PlayAnimation ? Icons.pause : Icons.play);
			
			if (!project.PlayAnimation) ModelCreator.updateFrame(); 
		});
		playPauseButton.setPreferredSize(new Dimension(30, 30));
		btnContainerTop.add(playPauseButton);
		
		
		
		prevFrameButton.setIcon(Icons.previous);
		prevFrameButton.setToolTipText("Previous Frame");
		prevFrameButton.addActionListener(e ->
		{
			Project project = ModelCreator.CurrentAnimProject();
			project.SelectedAnimation.PrevFrame();
			
			if (ModelCreator.currentMountBackdropProject != null && ModelCreator.currentMountBackdropProject.SelectedAnimation != null) {
				ModelCreator.currentMountBackdropProject.SelectedAnimation.PrevFrame();
				
				if (project.SelectedAnimation.currentFrame == 0) {
					ModelCreator.currentMountBackdropProject.SelectedAnimation.currentFrame=0;
				}

			}				
			
			
			ModelCreator.updateFrame();
		});
		prevFrameButton.setPreferredSize(new Dimension(30, 30));
		btnContainerTop.add(prevFrameButton);

		
		
		nextFrameButton.setIcon(Icons.next);
		nextFrameButton.setToolTipText("Next Frame");
		nextFrameButton.addActionListener(e ->
		{
			Project project = ModelCreator.CurrentAnimProject();
			project.SelectedAnimation.NextFrame();

			if (ModelCreator.currentMountBackdropProject != null && ModelCreator.currentMountBackdropProject.SelectedAnimation != null) {
				ModelCreator.currentMountBackdropProject.SelectedAnimation.NextFrame();
			}				

			ModelCreator.updateFrame();
		});
		nextFrameButton.setPreferredSize(new Dimension(30, 30));
		btnContainerTop.add(nextFrameButton);
		
		add(btnContainerTop);
		
		
		// 5. List of Keyframes
		JLabel label = new JLabel("Key Frames");
		label.setAlignmentX(LEFT_ALIGNMENT);
		label.setPreferredSize(new Dimension(205, 30));
		add(label);
		
		ListSelectionModel cellSelectionModel = keyFramesTable.getSelectionModel();
	    cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	    cellSelectionModel.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if (ModelCreator.ignoreFrameUpdates) return;
				Project project = ModelCreator.CurrentAnimProject();
				
				int row = keyFramesTable.getSelectedRow();
				if (project.SelectedAnimation != null && row >= 0) {
				 	AnimationFrame keyframe = project.SelectedAnimation.keyframes[row];
				 	
				 	project.SelectedAnimation.SetFrame(keyframe.getFrameNumber());
				 	ModelCreator.updateFrame();
				}
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(keyFramesTable);
		keyFramesTable.setFillsViewportHeight(true);
		
		scrollPane.setPreferredSize(new Dimension(205, 400));
		
		add(scrollPane);
		
		
		// 6. Keyframe editing buttons
		btnContainerBottom = new JPanel(new GridLayout(1, 4, 4, 0));
		btnContainerBottom.setPreferredSize(new Dimension(205, 30));

		deleteFrameButton.setIcon(Icons.bin);
		deleteFrameButton.setToolTipText("Delete Frame");
		deleteFrameButton.addActionListener(e ->
		{
			Project project = ModelCreator.CurrentAnimProject();
			project.SelectedAnimation.DeleteCurrentFrame();
			ModelCreator.updateFrame();
		});
		deleteFrameButton.setPreferredSize(new Dimension(30, 30));
		
		btnContainerBottom.add(deleteFrameButton);
		
		
		duplicateFrameButton.setIcon(Icons.copy);
		duplicateFrameButton.setToolTipText("Duplicate Frame");
		duplicateFrameButton.addActionListener(e -> {
			FrameSelectionDialog.show(ModelCreator.Instance);
		});
		btnContainerBottom.add(duplicateFrameButton);
		
		
		moveFrameRightButton.setIcon(Icons.left);
		moveFrameRightButton.setToolTipText("Move frame to the left");
		moveFrameRightButton.addActionListener(e ->
		{
			Project project = ModelCreator.CurrentAnimProject();
			project.SelectedAnimation.MoveSelectedFrame(-1);
		});
		moveFrameRightButton.setPreferredSize(new Dimension(30, 30));
		btnContainerBottom.add(moveFrameRightButton);
		
		
		
		moveFrameLeftButton.setIcon(Icons.right);
		moveFrameLeftButton.setToolTipText("Move frame to the right");
		moveFrameLeftButton.addActionListener(e ->
		{
			Project project = ModelCreator.CurrentAnimProject();
			project.SelectedAnimation.MoveSelectedFrame(1);
		});
		moveFrameLeftButton.setPreferredSize(new Dimension(30, 30));
		btnContainerBottom.add(moveFrameLeftButton);
		
		add(btnContainerBottom);
		
		
		// 7. Treadmill
		
		treadMill.addChangeListener(e -> {
			ModelCreator.showTreadmill = treadMill.isSelected();
		});
		AwtUtil.addChangeListener(treadMillSpeed, e -> {
			try {
				float value= Float.valueOf(treadMillSpeed.getText());
				
				ModelCreator.TreadMillSpeed = value;
		
			} catch (Exception ex) {}
		});

		JPanel treadmillPanel = new JPanel(new GridLayout(1, 2));
		
		JPanel panel1 = new JPanel(new GridLayout(2, 1));
		panel1.add(new JLabel("Treadmill"));
		panel1.add(treadMill);
		
		JPanel panel2 = new JPanel(new GridLayout(2, 1));
		panel2.add(new JLabel("Treadmill Speed"));
		panel2.add(treadMillSpeed);

		treadmillPanel.add(panel1);
		treadmillPanel.add(panel2);
		
		add(treadmillPanel);
		
		
		updateValues(null);
	}



	private void selectAnimation(int selectedIndex)
	{
		Project project = ModelCreator.CurrentAnimProject();
		
		if (selectedIndex >= 0) {
			if (project.Animations.size() > 0) {
				project.SelectedAnimation = project.Animations.get(selectedIndex);
			}
		} else {
			project.SelectedAnimation = null;	
		}
		
		ModelCreator.updateValues(animationsList);		
	}
	
	private void selectMountAnimation(int selectedIndex)
	{
		Project project = ModelCreator.currentMountBackdropProject;
		
		if (selectedIndex >= 0) {
			if (project.Animations.size() > 0) {
				Animation anim = project.Animations.get(selectedIndex);
				project.SelectedAnimation = anim;
			}
		} else {
			project.SelectedAnimation = null;	
		}
	}


	private void setNewQuantityFrames()
	{
		if (ignoreSelectionChange) return;
		
		if (ModelCreator.backdropAnimationsMode && ModelCreator.currentBackdropProject != null) {
			durationTextField.setText(ModelCreator.currentBackdropProject.SelectedAnimation.GetQuantityFrames()+"");
			return;
		}
		
		Project project = ModelCreator.CurrentAnimProject();
		
		int newQuantityFrames = 0;
		try {
			newQuantityFrames = Integer.parseInt(durationTextField.getText());
		} catch (Exception ex) {}
		
		if (newQuantityFrames == 0) return;
		if (newQuantityFrames == project.SelectedAnimation.GetQuantityFrames()) return;
		
		newQuantityFrames = Math.min(newQuantityFrames, 5000);
		
		ignoreSelectionChange = true;
		
		AnimationFrame[] keyframes = project.SelectedAnimation.keyframes;
		if (keyframes.length > 0) {			
			int maxKeyFrame = 0;
			for (int i = 0; i < keyframes.length; i++) {
				maxKeyFrame = Math.max(maxKeyFrame, keyframes[keyframes.length - 1].getFrameNumber());	
			}
			
			if (newQuantityFrames < maxKeyFrame) {
				int dialogButton = JOptionPane.YES_NO_OPTION;
				int dialogResult = JOptionPane.showConfirmDialog (null, "You have keyframes above frame number " + newQuantityFrames + ", those will be deleted. Proceed?", "Warning", dialogButton);
				if (dialogResult == JOptionPane.YES_OPTION){
					for (int i = 0; i < keyframes.length; i++) {
						if (keyframes[i].getFrameNumber() > newQuantityFrames) {
							project.SelectedAnimation.RemoveKeyFrame(keyframes[i]);
						}
					}
					project.SelectedAnimation.ReloadFrameNumbers();
					project.SelectedAnimation.SetFramesDirty();
					ModelCreator.updateFrame();
					ModelCreator.DidModify();
				} else {
					durationTextField.setText("" + (maxKeyFrame+1));
					ignoreSelectionChange = false;
					return;
				}
			}
		}
		
		
		if (newQuantityFrames > 0) frameSlider.setMaximum(newQuantityFrames - 1);
		frameSlider.setEnabled(newQuantityFrames > 0);
		
		project.SelectedAnimation.SetQuantityFrames(newQuantityFrames);
		project.SelectedAnimation.currentFrame = Math.min(project.SelectedAnimation.currentFrame, newQuantityFrames);
		ModelCreator.updateFrame();
		
		ignoreSelectionChange = false;
	}

	private void loadAnimationList()
	{
		animationsListModel = new DefaultComboBoxModel<String>();
		Project project = ModelCreator.CurrentAnimProject();
		
		for (Animation anim : project.Animations) {
			animationsListModel.addElement("<html><b>"+ anim.getName() +"</b></html>");	
		}
		
		animationsList.setModel(animationsListModel);
		
		if (animationsListModel.getSize() > 0 && project.SelectedAnimation == null) {
			selectAnimation(0);
		}
		
		if (project.SelectedAnimation != null) {
			ignoreSelectionChange = true;
			animationsList.setSelectedIndex(project.getSelectedAnimationIndex());
			ignoreSelectionChange = false;
		}
		
		
		// Mount anims
		if (ModelCreator.currentMountBackdropProject != null) {
			mountanimationsListModel = new DefaultComboBoxModel<String>();
			project = ModelCreator.currentMountBackdropProject;
			
			for (Animation anim : project.Animations) {
				mountanimationsListModel.addElement("<html><b>"+ anim.getName() +"</b></html>");	
			}
			
			mountanimationsList.setModel(mountanimationsListModel);
			
			if (mountanimationsListModel.getSize() > 0 && project.SelectedAnimation == null) {
				selectMountAnimation(0);
			}
			
			if (project.SelectedAnimation != null) {
				ignoreSelectionChange = true;
				mountanimationsList.setSelectedIndex(project.getSelectedAnimationIndex());
				ignoreSelectionChange = false;
			}
		}
	}
	
	
	
	@Override
	public void updateValues(JComponent byGuiElem)
	{
		if (animationsList == null) return;
				
		loadAnimationList();	
		
		Project project = ModelCreator.CurrentAnimProject();
		boolean enabled = project.SelectedAnimation != null; 
		
		durationTextField.setEnabled(enabled);
		durationTextField.setText(enabled ? project.SelectedAnimation.GetQuantityFrames() + "" : "");
		
		playPauseButton.setEnabled(enabled);
		prevFrameButton.setEnabled(enabled);
		nextFrameButton.setEnabled(enabled);
		
		deleteFrameButton.setEnabled(enabled);
		moveFrameRightButton.setEnabled(enabled);
		moveFrameLeftButton.setEnabled(enabled);
		duplicateFrameButton.setEnabled(enabled);
		
		currentFrameLabel.setText(enabled ? ("" + project.SelectedAnimation.currentFrame) : "");
		
		keyFramesTable.updateUI();
		
		frameSlider.setEnabled(enabled);
		if (enabled) frameSlider.setMaximum(project.SelectedAnimation.GetQuantityFrames() - 1);
	}

	
	boolean ignoreChange = false;
	public void updateFrame()
	{
		if (animationsList == null) return;
		
		Project project = ModelCreator.CurrentAnimProject();
		if (project.SelectedAnimation == null) return;
		
		ignoreChange=true;
		frameSlider.setValue(project.SelectedAnimation.currentFrame);
		ignoreChange=false;
		currentFrameLabel.setText("" + project.SelectedAnimation.currentFrame);
		
		if (ModelCreator.currentMountBackdropProject != null && ModelCreator.currentMountBackdropProject.SelectedAnimation != null) {
			mountAnimLabel.setText("Mount Animation ("+ ModelCreator.currentMountBackdropProject.SelectedAnimation.currentFrame +")");
		}
		
		boolean enabled = !project.PlayAnimation && project.SelectedAnimation != null && project.SelectedAnimation.IsCurrentFrameKeyFrame();
		
		deleteFrameButton.setEnabled(enabled);
		duplicateFrameButton.setEnabled(enabled);
		moveFrameRightButton.setEnabled(enabled);
		moveFrameLeftButton.setEnabled(enabled);
		
		keyFramesTable.clearSelection();
		
		if (!project.PlayAnimation && project.SelectedAnimation != null && project.SelectedAnimation.IsCurrentFrameKeyFrame()) {
			int[] frameNumbers = project.GetFrameNumbers();
			int index = 0;
			for (; index < frameNumbers.length; index++) {
				if (frameNumbers[index] == project.SelectedAnimation.currentFrame) {
					keyFramesTable.setRowSelectionInterval(index, index);
					break;
				}
			}
		}
	}
	
}
