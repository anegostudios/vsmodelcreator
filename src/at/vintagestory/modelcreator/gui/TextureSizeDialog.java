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

		JPanel panel = new JPanel(new GridLayout(2, 1, 5, 0));
		
		
		JPanel panelRow1 = new JPanel(new GridLayout(4, 1, 5, 0));
		JLabel label = new JLabel("Scale, when a texture is applied");
		label.setPreferredSize(new Dimension(30, 20));
		panelRow1.add(label);
		
		JTextField scaleTextField = new JTextField();
		scaleTextField.setPreferredSize(new Dimension(100, 20));
		scaleTextField.setText(""+ModelCreator.texScale);
		panelRow1.add(scaleTextField);
		
		
		panelRow1.add(new JLabel());
		
		label = new JLabel("Size, when no texture is applied, used when exporting a UV map");
		label.setPreferredSize(new Dimension(370, 20));
		panelRow1.add(label);
		
		panel.add(panelRow1);
		
		
		
		
		
		
		JPanel panelRow2 = new JPanel(new GridLayout(4, 2, 5, 0));
		
		label = new JLabel("Width");
		label.setPreferredSize(new Dimension(30, 20));
		panelRow2.add(label);
		
		label = new JLabel("Height");
		label.setPreferredSize(new Dimension(30, 20));
		panelRow2.add(label);
		

		JTextField widthTextField = new JTextField();
		widthTextField.setPreferredSize(new Dimension(150, 20));
		widthTextField.setText(""+ModelCreator.noTexWidth);
		panelRow2.add(widthTextField);
		
		
		
		JTextField heightTextField = new JTextField();
		heightTextField.setPreferredSize(new Dimension(150, 20));
		heightTextField.setText(""+ModelCreator.noTexHeight);
		panelRow2.add(heightTextField);
		
		
		JButton btnSubmit = new JButton("Save");
		btnSubmit.setIcon(Icons.disk);
		btnSubmit.addActionListener(a ->
		{
			try {
				ModelCreator.noTexWidth = Integer.parseInt(widthTextField.getText());
				ModelCreator.noTexHeight = Integer.parseInt(heightTextField.getText());
				ModelCreator.texScale = Float.parseFloat(scaleTextField.getText());
				
				ModelCreator.prefs.putInt("noTexWidth", ModelCreator.noTexWidth);
				ModelCreator.prefs.putInt("noTexHeight", ModelCreator.noTexHeight);
				ModelCreator.prefs.putFloat("texScale", ModelCreator.texScale);
				
				dialog.dispose();
			} catch (Exception e) {
				
			}
			
		});
		
		
		panelRow2.add(new JLabel(" "));
		panelRow2.add(new JLabel());
		
		panelRow2.add(btnSubmit);
		panelRow2.add(new JLabel());

		
		
		panel.add(panelRow2);
			
		
		container.add(panel);
		
		
		dialog.setResizable(false);
		//dialog.setPreferredSize(new Dimension(350, 250));
		dialog.add(container);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		dialog.requestFocusInWindow();
	}
}
