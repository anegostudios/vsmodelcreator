package at.vintagestory.modelcreator.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
		
		
		
		
		int count = ModelCreator.currentProject.TexturesByCode.size() + ModelCreator.currentProject.MissingTexturesByCode.size();

		
		
		JPanel panelRow2 = new JPanel(new GridLayout(4 + 2*count, 2, 5, 5));
		
		label = new JLabel("Texture Width");
		label.setPreferredSize(new Dimension(30, 20));
		panelRow2.add(label);
		
		label = new JLabel("Texture Height");
		label.setPreferredSize(new Dimension(30, 20));
		panelRow2.add(label);
		
		
		
		HashMap<String, JTextField[]> textureSizes = new HashMap<String, JTextField[]>();
		
		Set<String> entries = new HashSet<String>();
		entries.addAll(ModelCreator.currentProject.TexturesByCode.keySet());
		entries.addAll(ModelCreator.currentProject.MissingTexturesByCode.keySet());
		
		if (count > 1) {
			for (String keycode : entries) {
				
				label = new JLabel(keycode);
				label.setPreferredSize(new Dimension(30, 20));
				panelRow2.add(label);
				panelRow2.add(new JLabel(""));
				
				JTextField widthTextField = new JTextField();
				widthTextField.setPreferredSize(new Dimension(50, 20));
				
				int width = ModelCreator.currentProject.TextureWidth;
				int height = ModelCreator.currentProject.TextureHeight;
				if (ModelCreator.currentProject.TextureSizes.containsKey(keycode)) {
					width = ModelCreator.currentProject.TextureSizes.get(keycode)[0];
					height = ModelCreator.currentProject.TextureSizes.get(keycode)[1];
				}
				
				widthTextField.setText(""+width);
				panelRow2.add(widthTextField);
				
				JTextField heightTextField = new JTextField();
				heightTextField.setPreferredSize(new Dimension(50, 20));
				heightTextField.setText(""+height);
				panelRow2.add(heightTextField);				
				
				textureSizes.put(keycode, new JTextField[] { widthTextField, heightTextField });
			}
			
		} else {

			JTextField widthTextField = new JTextField();
			widthTextField.setPreferredSize(new Dimension(50, 20));
			widthTextField.setText(""+ModelCreator.currentProject.TextureWidth);
			panelRow2.add(widthTextField);
			
			JTextField heightTextField = new JTextField();
			heightTextField.setPreferredSize(new Dimension(50, 20));
			heightTextField.setText(""+ModelCreator.currentProject.TextureHeight);
			panelRow2.add(heightTextField);
			
			
			textureSizes.put("__generic", new JTextField[] { widthTextField, heightTextField });
		}
		
		JButton btnSubmit = new JButton("Save");
		btnSubmit.setIcon(Icons.disk);
		btnSubmit.addActionListener(a ->
		{
			try {
				float scale = Float.parseFloat(scaleTextField.getText());
				
				for (String keyCode : textureSizes.keySet()) {
					JTextField[] fields = textureSizes.get(keyCode);
					
					int width = Integer.parseInt(fields[0].getText());
					int height = Integer.parseInt(fields[1].getText());
					
					if (((scale * width) % 8) != 0 || ((scale * height) % 8) != 0) {
						JOptionPane.showMessageDialog(null, "Width and Height, multiplied with the scale, must be a multiple of 8!", "Invalid values", JOptionPane.ERROR_MESSAGE, null);
						return;
					}					
				}
				
				boolean didChange = false;
				
				
				for (String keyCode : textureSizes.keySet()) {
					JTextField[] fields = textureSizes.get(keyCode);
					
					int width = Integer.parseInt(fields[0].getText());
					int height = Integer.parseInt(fields[1].getText());
					
					if (keyCode == "__generic") {
						ModelCreator.currentProject.TextureWidth = width;
						ModelCreator.currentProject.TextureHeight = height;
					} else {
						ModelCreator.currentProject.TextureSizes.put(keyCode, new int[] { width, height });
					}

					didChange |= width != ModelCreator.currentProject.TextureWidth || height != ModelCreator.currentProject.TextureHeight;
				}				
				

				
				if (didChange) {
					ModelCreator.DidModify();
				}

				ModelCreator.noTexScale = scale;
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
