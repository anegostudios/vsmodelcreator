package at.vintagestory.modelcreator.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class ControlsDialog
{
	public static void show(JFrame parent)
	{
		JPanel container = new JPanel(new BorderLayout(20, 10));
		container.setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel leftPanel = new JPanel(new BorderLayout());

		JLabel message = new JLabel();
		message.setText("<html>"
				+ "<p><b>Drag&Drop of files</b></p>"
				+ "<ul>"
				+ "<li>Drop in model json files to open them</li>"
				+ "<li>Drop in a png file to the center pane to import a texture. Drop it in one of the three appearing colored rectangles to determine import behavior.</li>"
				+ "</ul>"
				+ "<p><b>UV Panel (Left)</b></p>"
				+ "<ul>"
				+ "<li>Hold left mouse button to move UV Rectangles</li>"
				+ "<li>Hold right mouse button to resize UV Rectangles (disables auto-uv)</li>"
				+ "</ul>"
				+ "<p><b>Model Pane (Middle)</b></p>"
				+ "<ul>"
				+ "<li>Hold left mouse button to move the camera</li>"
				+ "<li>Hold right mouse button to rotate the camera</li>"
				+ "<li>Use the mouse wheel or +/- keys to zoom in/out</li>"
				+ "<li>Use the arrow keys or Pg Up/Pg Down to move a selected element</li>"
				+ "<li>Hold CTRL and click the left mouse button to select a cube</li>"
				+ "<li>Hold CTRL and hold the left mouse button to move a cube</li>"
				+ "<li>Hold CTRL and hold the right mouse button to resize a cube</li>"
				+ "</ul>"
				+ "<p><b>Element Tree Pane (Right)<b></p>"
				+ "<ul>"
				+ "<li>Drag and Drop Elements to reorder them</li>"
				+ "<li>Click on the cube icon to hide them. Note, this will not make them invisible in-game</li>"
				+ "</ul>"
				+ "<p><b>Cube Properties Pane (Right)<b></p>"
				+ "<ul>"
				+ "<li>Hold SHIFT and click on any of the up/down arrows to modify in increments of 0.1</li>"
				+ "<li>Hold CTRL and click on any up/down arrows of the cubes position to not move the origin</li>"
				+ "<li>Focus any of the position/origin/size text fields and use mouse wheel to modify increase/decrease the size in -1/+1 (also works in conjunction with the CTRL and Shift modifiers)</li>"				
				+ "</ul>"		
				+ "<p><b>Face Pane, UVs (Right)<b></p>"
				+ "<ul>"
				+ "<li>Hold SHIFT and mousewheel or click on any of the up/down arrows to modify in increments of 0.1</li>"
				+ "<li>Hold CTRL and mousewheel or click on any of the up/down arrows to modify in increments of single texture pixels</li>"
				+ "</ul>"
				+ "<p><b>Face Pane, Textures (Right)<b></p>"
				+ "<ul>"
				+ "<li>Press Copy Button and then hold SHIFT + Left Mouse Click the Paste Button to quickly assign a texture to all the faces of a cube</li>"
				+ "<li>Press Copy Button and then hold CTRL + SHIFT + Left Mouse Click the Paste Button to quickly assign a texture to all the faces of a cube and all its child elements</li>"
				+ "<li>Hold SHIFT and toggle 'Enabled', 'Auto Resolution' or 'Snap UV' to toggle it for all faces</li>"
				+ "</ul>"
				+ "<p><b>Face Pane, Properties<b></p>"
				+ "<ul>"
				+ "<li>Hold CTRL+SHIFT and select a windmode to apply that windmode to the entire element</li>"
				+ "</ul>"
				+ "<p><b>Keyframe Pane (Right)<b></p>"
				+ "<ul>"
				+ "<li>Hold SHIFT and toggle 'Position', 'Rotation' or 'Stretch' to NOT create a new tweening keyframe, i.e. take on the current position/rotation/stretch values of the current frame</li>"
				+ "</ul>"		
				+ "</html>"
		);
		leftPanel.add(message, BorderLayout.NORTH);

		container.add(leftPanel, BorderLayout.NORTH);

		JPanel btnGrid = new JPanel(new GridLayout(1, 4, 5, 0));

		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(a ->
		{
			SwingUtilities.getWindowAncestor(btnClose).dispose();
		});
		btnGrid.add(btnClose);

		container.add(btnGrid, BorderLayout.SOUTH);

		JDialog dialog = new JDialog(parent, "Quick Controls", false);
		dialog.setResizable(false);
		dialog.setPreferredSize(new Dimension(800, 850));
		dialog.add(container);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		dialog.requestFocusInWindow();
	}
}
