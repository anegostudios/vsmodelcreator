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

		JPanel panel = new JPanel(new GridLayout(2, 3, 5, 0));

		JLabel label = new JLabel("Width");
		label.setPreferredSize(new Dimension(30, 20));
		panel.add(label);
		
		label = new JLabel("Height");
		label.setPreferredSize(new Dimension(30, 20));
		panel.add(label);
		
		label = new JLabel("");
		label.setPreferredSize(new Dimension(30, 20));
		panel.add(label);

		JTextField widthTextField = new JTextField();
		widthTextField.setPreferredSize(new Dimension(150, 30));
		widthTextField.setText(""+ModelCreator.noTexWidth);
		panel.add(widthTextField);
		
		
		
		JTextField heightTextField = new JTextField();
		heightTextField.setPreferredSize(new Dimension(150, 30));
		heightTextField.setText(""+ModelCreator.noTexHeight);
		panel.add(heightTextField);
		
		
		JButton btnSubmit = new JButton("Save");
		btnSubmit.setIcon(Icons.disk);
		btnSubmit.addActionListener(a ->
		{
			try {
				ModelCreator.noTexWidth = Integer.parseInt(widthTextField.getText());
				ModelCreator.noTexHeight = Integer.parseInt(heightTextField.getText());
				
				ModelCreator.prefs.putInt("noTexWidth", ModelCreator.noTexWidth);
				ModelCreator.prefs.putInt("noTexHeight", ModelCreator.noTexHeight);
				
				dialog.dispose();
			} catch (Exception e) {
				
			}
			
		});
		
		panel.add(btnSubmit);

		
		
		container.add(panel);
		
		
		dialog.setResizable(false);
		dialog.setPreferredSize(new Dimension(350, 100));
		dialog.add(container);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		dialog.requestFocusInWindow();
	}
}
