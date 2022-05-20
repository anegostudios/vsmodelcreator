package at.vintagestory.modelcreator.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.model.Keyframe;

public class FrameSelectionDialog
{
	public static void show(JFrame parent)
	{
		JDialog dialog = new JDialog(parent, "Duplicate to Target Frame", false);
		
		JPanel container = new JPanel(new BorderLayout(20, 10));
		container.setBorder(new EmptyBorder(10, 10, 10, 10));

		
		JPanel panelRow1 = new JPanel(new GridLayout(5, 1, 5, 0));
		JLabel label = new JLabel("For Target frame");
		label.setPreferredSize(new Dimension(200, 20));
		panelRow1.add(label);
				
		JTextField frameTextFiel = new JTextField();
		frameTextFiel.setPreferredSize(new Dimension(50, 20));
		
		if (ModelCreator.currentProject.SelectedAnimation.IsCurrentFrameKeyFrame()) {
			frameTextFiel.setText("" + (ModelCreator.currentProject.SelectedAnimation.currentFrame + 1));
		} else {
			frameTextFiel.setText("" + ModelCreator.currentProject.SelectedAnimation.currentFrame);
		}
			
		
		panelRow1.add(frameTextFiel);
		
		container.add(panelRow1, BorderLayout.CENTER);
		
		JPanel panelRow2 = new JPanel(new GridLayout(1, 2, 15, 0));
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setIcon(Icons.clear);
		btnCancel.addActionListener(a ->
		{
			dialog.dispose();
		});
		
		
		panelRow2.add(btnCancel);
		
		JButton btnDuplicate = new JButton("Duplicate");
		btnDuplicate.setIcon(Icons.copy);
		btnDuplicate.addActionListener(a ->
		{
			int forFrame = Integer.parseInt(frameTextFiel.getText());
			
			if (forFrame < 0) {
				JOptionPane.showMessageDialog(null, "Must be a positive frame number");
				return;
			}
			
			for (int i = 0; i < ModelCreator.currentProject.SelectedAnimation.keyframes.length; i++) {
				Keyframe keyframe = ModelCreator.currentProject.SelectedAnimation.keyframes[i];
				if (keyframe.getFrameNumber() == forFrame) {
					JOptionPane.showMessageDialog(null, "Can't duplicate to this frame, there is already a key frame at this position. Delete that one first.");
					return;
				}
			}
			
			Keyframe sourceFrame = ModelCreator.currentProject.SelectedAnimation.allFrames.get(ModelCreator.currentProject.SelectedAnimation.currentFrame).clone(true);
			sourceFrame.setFrameNumber(forFrame);
			
			ModelCreator.currentProject.SelectedAnimation.InsertKeyFrame(sourceFrame);
			
			dialog.dispose();
		});
		
		
		panelRow2.add(btnDuplicate);
		
		
		container.add(panelRow2, BorderLayout.SOUTH);
			
		
		dialog.setResizable(false);
		dialog.add(container);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		dialog.requestFocusInWindow();
	}
}
