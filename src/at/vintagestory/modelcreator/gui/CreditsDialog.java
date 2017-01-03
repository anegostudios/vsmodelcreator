package at.vintagestory.modelcreator.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class CreditsDialog
{
	public static void show(JFrame parent)
	{
		JPanel container = new JPanel(new BorderLayout(20, 10));
		container.setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel leftPanel = new JPanel(new BorderLayout());

		JLabel title = new JLabel("<html><span style=\"font-size:16px;\"><b>Vintage Story Model Creator</b></span><br>based on MrCrayfishs Model Creator for Minecraft</html>");
		leftPanel.add(title, BorderLayout.NORTH);

		JLabel message = new JLabel();
		message.setText("<html>All praise for this program should go to MrCrayFish for making this awesome and simply to use tool "
				+ "originally for Minecraft. He open sourced it and so Tyron took the freedom to adjust it to his needs for Vintage Story. "
				+ "If you like this program, show some love to MrCrayFish! &lt;3</html>");
		leftPanel.add(message, BorderLayout.CENTER);

		container.add(leftPanel, BorderLayout.CENTER);

		JPanel btnGrid = new JPanel(new GridLayout(1, 4, 5, 0));
		JButton btnDonate = new JButton("Donate");
		btnDonate.setIcon(Icons.coin);
		btnDonate.addActionListener(a ->
		{
			openUrl("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=HVXLDWFN4MNA2");
		});
		btnGrid.add(btnDonate);

		JButton btnTwitter = new JButton("Twitter");
		btnTwitter.setIcon(Icons.twitter);
		btnTwitter.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				openUrl("https://www.twitter.com/MrCraayfish");
			}

		});
		btnGrid.add(btnTwitter);

		JButton btnFacebook = new JButton("Facebook");
		btnFacebook.setIcon(Icons.facebook);
		btnFacebook.addActionListener(a ->
		{
			openUrl("https://www.facebook.com/MrCrayfish");
		});
		btnGrid.add(btnFacebook);

		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(a ->
		{
			SwingUtilities.getWindowAncestor(btnClose).dispose();
		});
		btnGrid.add(btnClose);

		// container.add(header, BorderLayout.NORTH);
		container.add(btnGrid, BorderLayout.SOUTH);

		JDialog dialog = new JDialog(parent, "Credits", false);
		// dialog.setLayout(new BorderLayout());
		dialog.setResizable(false);
		dialog.setPreferredSize(new Dimension(500, 290));
		dialog.add(container);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		dialog.requestFocusInWindow();
	}

	private static void openUrl(String url)
	{
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
		{
			try
			{
				desktop.browse(new URL(url).toURI());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
