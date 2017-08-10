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
import at.vintagestory.modelcreator.model.Keyframe;
import at.vintagestory.modelcreator.model.KeyframeElement;

public class LeftKeyFramesPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;
	private IElementManager manager;
	
	private JComboBox<String> animationsList;
	private DefaultComboBoxModel<String> animationsListModel;
	JButton animationAddRemoveButton;	
	
	AbstractTableModel tableModel;
	
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
	
	boolean ignoreSelectionChange = false;
	
	
	public LeftKeyFramesPanel(IElementManager manager)
	{
		this.manager = manager;
		setPreferredSize(new Dimension(215, 900));
		initComponents();
		addComponents();
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
				int[] frameNumbers = ModelCreator.currentProject.GetFrameNumbers();
				if (frameNumbers==null || ModelCreator.currentProject.SelectedAnimation == null) return "";
				
				if (columnIndex == 0) return frameNumbers[rowIndex];
				
				Keyframe keyFrame = ModelCreator.currentProject.SelectedAnimation.keyframes[rowIndex];
				
				if (keyFrame == null) return "";
				
				KeyframeElement keyframElem = keyFrame.GetKeyFrameElement(ModelCreator.currentProject.SelectedElement);
				
				if (keyframElem == null) return "";

				if (columnIndex == 1) return keyframElem.PositionSet ? "P" : "";
				if (columnIndex == 2) return keyframElem.RotationSet ? "R" : "";
				if (columnIndex == 3) return keyframElem.StretchSet ? "S" : "";
				
				return "";
			}
			
			@Override
			public int getRowCount()
			{
				if (ModelCreator.currentProject == null) return 0;
				return ModelCreator.currentProject.GetKeyFrameCount();
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
		// 1. Animation DropDown
		SpringLayout animationListPanelLayout = new SpringLayout();
		JPanel animationListPanel = new JPanel(animationListPanelLayout);

		animationListPanel.setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>Animation</b></html>"));
		
		animationsList = new JComboBox<String>();
		
		animationsList.setToolTipText("The current animation you want to edit");
		animationsList.addActionListener(e ->
		{
			if (ignoreSelectionChange) return;
			
			int selectedIndex = animationsList.getSelectedIndex();
			if (selectedIndex > 0) {
				ModelCreator.currentProject.SelectedAnimation = ModelCreator.currentProject.Animations.get(selectedIndex);	
			} else {
				ModelCreator.currentProject.SelectedAnimation = null;	
			}
			
			ModelCreator.updateValues(animationsList);
		});
		
		animationsList.setPreferredSize(new Dimension(170, 29));
		
		//animationListPanel.setPreferredSize(new Dimension(186, 50));
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
				int max = 0;
				try {
					max = Integer.parseInt(durationTextField.getText());
				} catch (Exception ex) {}
				
				if (max == 0) return;
				
				if (max > 0) frameSlider.setMaximum(max - 1);
				frameSlider.setEnabled(max > 0);
				
				ModelCreator.currentProject.SelectedAnimation.SetQuantityFrames(max, ModelCreator.currentProject);
				ModelCreator.currentProject.SelectedAnimation.currentFrame = Math.min(ModelCreator.currentProject.SelectedAnimation.currentFrame, max);
				ModelCreator.updateFrame();	
			}
		});
		
		durationTextField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				Keyframe[] keyframes = ModelCreator.currentProject.SelectedAnimation.keyframes;
				if (keyframes.length == 0) return;
				
				int maxKeyFrame = 0;
				for (int i = 0; i < keyframes.length; i++) {
					maxKeyFrame = Math.max(maxKeyFrame, keyframes[keyframes.length - 1].getFrameNumber());	
				}
				
				int quantityFrames = ModelCreator.currentProject.SelectedAnimation.GetQuantityFrames();
				
				if (quantityFrames < maxKeyFrame) {
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog (null, "You have keyframes above frame number " + quantityFrames + ", those will be deleted. Proceed?", "Warning", dialogButton);
					if (dialogResult == JOptionPane.YES_OPTION){
						for (int i = 0; i < keyframes.length; i++) {
							if (keyframes[i].getFrameNumber() > quantityFrames) {
								ModelCreator.currentProject.SelectedAnimation.RemoveKeyFrame(keyframes[i]);
							}
						}
						ModelCreator.currentProject.SelectedAnimation.ReloadFrameNumbers();
						ModelCreator.currentProject.SelectedAnimation.calculateAllFrames(ModelCreator.currentProject);
						ModelCreator.updateFrame();
						ModelCreator.DidModify();
					} else {
						durationTextField.setText("" + maxKeyFrame);
						return;
					}
				}
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
				ModelCreator.currentProject.SelectedAnimation.currentFrame = frameSlider.getValue();
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
			ModelCreator.currentProject.PlayAnimation = !ModelCreator.currentProject.PlayAnimation;
			playPauseButton.setIcon(ModelCreator.currentProject.PlayAnimation ? Icons.pause : Icons.play);
		});
		playPauseButton.setPreferredSize(new Dimension(30, 30));
		btnContainerTop.add(playPauseButton);
		
		
		
		prevFrameButton.setIcon(Icons.previous);
		prevFrameButton.setToolTipText("Previous Frame");
		prevFrameButton.addActionListener(e ->
		{
			ModelCreator.currentProject.SelectedAnimation.PrevFrame();
			ModelCreator.updateFrame();
		});
		prevFrameButton.setPreferredSize(new Dimension(30, 30));
		btnContainerTop.add(prevFrameButton);

		
		
		nextFrameButton.setIcon(Icons.next);
		nextFrameButton.setToolTipText("Next Frame");
		nextFrameButton.addActionListener(e ->
		{
			ModelCreator.currentProject.SelectedAnimation.NextFrame();
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
				int row = keyFramesTable.getSelectedRow();
				if (ModelCreator.currentProject.SelectedAnimation != null && row >= 0) {
				 	Keyframe keyframe = ModelCreator.currentProject.SelectedAnimation.keyframes[row];
				 	
				 	ModelCreator.currentProject.SelectedAnimation.SetFrame(keyframe.getFrameNumber());
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
			ModelCreator.currentProject.SelectedAnimation.DeleteCurrentFrame();
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
			ModelCreator.currentProject.SelectedAnimation.MoveSelectedFrame(-1);
		});
		moveFrameRightButton.setPreferredSize(new Dimension(30, 30));
		btnContainerBottom.add(moveFrameRightButton);
		
		
		
		moveFrameLeftButton.setIcon(Icons.right);
		moveFrameLeftButton.setToolTipText("Move frame to the right");
		moveFrameLeftButton.addActionListener(e ->
		{
			ModelCreator.currentProject.SelectedAnimation.MoveSelectedFrame(1);
		});
		moveFrameLeftButton.setPreferredSize(new Dimension(30, 30));
		btnContainerBottom.add(moveFrameLeftButton);
		
		add(btnContainerBottom);
		
		
		updateValues(null);
	}

	

	private void loadAnimationList()
	{
		animationsListModel = new DefaultComboBoxModel<String>();
		Project project = ModelCreator.currentProject;
		for (Animation anim : project.Animations) {
			animationsListModel.addElement("<html><b>"+ anim.getName() +"</b></html>");	
		}
		animationsList.setModel(animationsListModel);
		
		if (animationsListModel.getSize() > 0 && ModelCreator.currentProject.SelectedAnimation == null) {
			animationsList.setSelectedIndex(0);
			ModelCreator.currentProject.SelectedAnimation = ModelCreator.currentProject.Animations.get(0);
			ModelCreator.updateValues(null);
		}
		
		if (ModelCreator.currentProject.SelectedAnimation != null) {
			ignoreSelectionChange = true;
			animationsList.setSelectedIndex(ModelCreator.currentProject.getSelectedAnimationIndex());
			ignoreSelectionChange = false;
		}
	}
	
	
	
	@Override
	public void updateValues(JComponent byGuiElem)
	{
		loadAnimationList();	
		
		boolean enabled = ModelCreator.currentProject.SelectedAnimation != null; 
		
		durationTextField.setEnabled(enabled);
		durationTextField.setText(enabled ? ModelCreator.currentProject.SelectedAnimation.GetQuantityFrames() + "" : "");
		
		playPauseButton.setEnabled(enabled);
		prevFrameButton.setEnabled(enabled);
		nextFrameButton.setEnabled(enabled);
		
		deleteFrameButton.setEnabled(enabled);
		moveFrameRightButton.setEnabled(enabled);
		moveFrameLeftButton.setEnabled(enabled);
		duplicateFrameButton.setEnabled(enabled);
		
		currentFrameLabel.setText(enabled ? ("" + ModelCreator.currentProject.SelectedAnimation.currentFrame) : "");
		
		keyFramesTable.updateUI();
		
		frameSlider.setEnabled(enabled);
		if (enabled) frameSlider.setMaximum(ModelCreator.currentProject.SelectedAnimation.GetQuantityFrames() - 1);
	}

	public void updateFrame()
	{
		if (ModelCreator.currentProject.SelectedAnimation == null) return;
		
		frameSlider.setValue(ModelCreator.currentProject.SelectedAnimation.currentFrame);		
		currentFrameLabel.setText("" + ModelCreator.currentProject.SelectedAnimation.currentFrame);
		
		
		boolean enabled = !ModelCreator.currentProject.PlayAnimation && ModelCreator.currentProject.SelectedAnimation != null && ModelCreator.currentProject.SelectedAnimation.IsCurrentFrameKeyFrame();
		
		deleteFrameButton.setEnabled(enabled);
		duplicateFrameButton.setEnabled(enabled);
		moveFrameRightButton.setEnabled(enabled);
		moveFrameLeftButton.setEnabled(enabled);
		
		keyFramesTable.clearSelection();
		
		if (ModelCreator.currentProject.SelectedAnimation != null && ModelCreator.currentProject.SelectedAnimation.IsCurrentFrameKeyFrame()) {
			int[] frameNumbers = ModelCreator.currentProject.GetFrameNumbers();
			int index = 0;
			for (; index < frameNumbers.length; index++) {
				if (frameNumbers[index] == ModelCreator.currentProject.SelectedAnimation.currentFrame) {
					keyFramesTable.setRowSelectionInterval(index, index);
					break;
				}
			}
		}
	}
	
}
