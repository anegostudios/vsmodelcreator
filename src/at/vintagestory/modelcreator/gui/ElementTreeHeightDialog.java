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

public class ElementTreeHeightDialog
{
	public static void show(JFrame parent)
	{
		JDialog dialog = new JDialog(parent, "Element Tree height", false);
		
		JPanel container = new JPanel(new BorderLayout(20, 10));
		container.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		JPanel panelRow1 = new JPanel(new GridLayout(5, 1, 5, 0));

		JLabel label = new JLabel("Element Tree Height");
		label.setPreferredSize(new Dimension(200, 20));
		panelRow1.add(label);
		
		JTextField eleTreeHeight = new JTextField();
		eleTreeHeight.setPreferredSize(new Dimension(50, 20));
		eleTreeHeight.setText(""+ModelCreator.elementTreeHeight);
		panelRow1.add(eleTreeHeight);
		
		
		container.add(panelRow1, BorderLayout.CENTER);
	
		
		JButton btnSubmit = new JButton("Save");
		btnSubmit.setIcon(Icons.disk);
		btnSubmit.addActionListener(a ->
		{
			try {
				ModelCreator.elementTreeHeight = Integer.parseInt(eleTreeHeight.getText());
				ModelCreator.rightTopPanel.initComponents();
				ModelCreator.rightTopPanel.updateValues(null);
				ModelCreator.prefs.putInt("elementTreeHeight", ModelCreator.elementTreeHeight);
				dialog.dispose();
			} catch (Exception e) {
				
			}
		});
		
		JPanel panelRow2 = new JPanel(new GridLayout(1, 2, 5, 5));
		panelRow2.add(btnSubmit);
		container.add(panelRow2, BorderLayout.SOUTH);
		
		dialog.setResizable(false);
		
		dialog.add(container);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		dialog.requestFocusInWindow();
	}
}
