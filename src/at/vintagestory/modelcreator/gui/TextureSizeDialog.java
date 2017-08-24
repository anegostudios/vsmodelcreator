package at.vintagestory.modelcreator.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import at.vintagestory.modelcreator.ModelCreator;

public class TextureSizeDialog
{
	public static void show(JFrame parent)
	{
		JDialog dialog = new JDialog(parent, "Texture Size", false);
		
		JPanel container = new JPanel(new BorderLayout(20, 10));
		container.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		
		JPanel panelRow1 = new JPanel(new GridLayout(5, 1, 5, 0));
		JLabel label = new JLabel("Texture Pack Resolution");
		label.setPreferredSize(new Dimension(200, 20));
		panelRow1.add(label);
		
		label = new JLabel("(= amount of pixels per voxel)");
		label.setPreferredSize(new Dimension(200, 20));
		panelRow1.add(label);

		label = new JLabel("(only used during UV map export)");
		label.setPreferredSize(new Dimension(200, 20));
		panelRow1.add(label);

		
		JTextField scaleTextField = new JTextField();
		scaleTextField.setPreferredSize(new Dimension(50, 20));
		scaleTextField.setText(""+ModelCreator.noTexScale);
		panelRow1.add(scaleTextField);
		
		
		container.add(panelRow1, BorderLayout.CENTER);
		
		
		
		
		
		
		JPanel panelRow2 = new JPanel(new GridLayout(4, 2, 5, 0));
		
		label = new JLabel("Texture Width");
		label.setPreferredSize(new Dimension(30, 20));
		panelRow2.add(label);
		
		label = new JLabel("Texture Height");
		label.setPreferredSize(new Dimension(30, 20));
		panelRow2.add(label);
		

		JTextField widthTextField = new JTextField();
		widthTextField.setPreferredSize(new Dimension(50, 20));
		widthTextField.setText(""+ModelCreator.currentProject.TextureWidth);
		panelRow2.add(widthTextField);
		
		
		
		JTextField heightTextField = new JTextField();
		heightTextField.setPreferredSize(new Dimension(50, 20));
		heightTextField.setText(""+ModelCreator.currentProject.TextureHeight);
		panelRow2.add(heightTextField);
		
		
		JButton btnSubmit = new JButton("Save");
		btnSubmit.setIcon(Icons.disk);
		btnSubmit.addActionListener(a ->
		{
			try {
				int width = Integer.parseInt(widthTextField.getText());
				int height = Integer.parseInt(heightTextField.getText());
				
				if (width != ModelCreator.currentProject.TextureWidth || height != ModelCreator.currentProject.TextureHeight) {
					ModelCreator.DidModify();
				}
				
				ModelCreator.currentProject.TextureWidth = width;
				ModelCreator.currentProject.TextureHeight = height;
				ModelCreator.noTexScale = Float.parseFloat(scaleTextField.getText());
				ModelCreator.prefs.putFloat("texScale", ModelCreator.noTexScale);
				
				
				
				dialog.dispose();
			} catch (Exception e) {
				
			}
		});
		
		
		panelRow2.add(new JLabel(" "));
		panelRow2.add(new JLabel());
		
		panelRow2.add(btnSubmit);
		panelRow2.add(new JLabel());

		
		
		container.add(panelRow2, BorderLayout.SOUTH);
		
		dialog.setResizable(false);
		
		dialog.add(container);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		dialog.requestFocusInWindow();
	}
}
