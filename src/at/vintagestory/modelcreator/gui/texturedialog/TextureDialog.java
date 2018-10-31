package at.vintagestory.modelcreator.gui.texturedialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.ITextureCallback;
import at.vintagestory.modelcreator.model.PendingTexture;
import at.vintagestory.modelcreator.model.TextureEntry;

public class TextureDialog implements ITextureCallback
{
	public File lastLocation = null;

	private String texture = null;
	DefaultListModel<String> model;
	JButton btnImport;
	JTextField textureCodeField;
	
	boolean ignoreSelects = false;
	
	public String display(IElementManager manager)
	{
		Font defaultFont = new Font("SansSerif", Font.BOLD, 18);

		model = generate();
		JList<String> list = new JList<String>();
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setCellRenderer(new TextureCellRenderer());
		list.setVisibleRowCount(-1);
		list.setModel(model);
		list.setFixedCellHeight(256);
		list.setFixedCellWidth(256);
		list.setBackground(new Color(221, 221, 228));
		list.addListSelectionListener(elem -> {
			if (ignoreSelects) return;
			String oldCode = list.getSelectedValue();
			textureCodeField.setEnabled(true);
			textureCodeField.setText(oldCode);
			
		});
		
		JScrollPane scroll = new JScrollPane(list);
		scroll.getVerticalScrollBar().setVisible(false);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		
		JPanel textFieldAndButtons = new JPanel(new GridLayout(2,1));

		JPanel textFieldRow = new JPanel(new GridLayout(1,1));
		textFieldRow.setEnabled(false);
		textFieldRow.setPreferredSize(new Dimension(1000, 30));
		
		textureCodeField = new JTextField();
		textFieldRow.add(textureCodeField);
		
		
		textureCodeField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e)
			{
				
				String oldCode = list.getSelectedValue();
				String newCode = textureCodeField.getText();
				
				if (newCode.length() == 0) return;
				
				ModelCreator.currentProject.UpdateTextureCode(oldCode, newCode);
				ignoreSelects = true;
				model = generate();
				list.setModel(model);
				
				int newIndex = 0;
				int i=0;
				for (TextureEntry entry : ModelCreator.currentProject.TexturesByCode.values())
				{
					if (entry.code.equals(newCode)) {
						newIndex = i;
						break;
					}
					i++;
				}
				list.setSelectedIndex(newIndex);
				
				ignoreSelects = false;
			}
		});
		
		JPanel buttonRow = new JPanel(new GridLayout(1, 3));
		buttonRow.setPreferredSize(new Dimension(1000, 30));
		
		
		
		
		
		JButton btnSelect = new JButton("Apply");
		btnSelect.addActionListener(a ->
		{
			if (list.getSelectedValue() != null)
			{
				texture = list.getSelectedValue();
				SwingUtilities.getWindowAncestor(btnSelect).dispose();
			}
		});
		btnSelect.setFont(defaultFont);
		buttonRow.add(btnSelect);

		JButton btnImport = new JButton("Import");
		btnImport.addActionListener(a ->
		{
			JFileChooser chooser = new JFileChooser(ModelCreator.prefs.get("texturePath",""));
			if (lastLocation != null)
				chooser.setCurrentDirectory(lastLocation);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				lastLocation = chooser.getSelectedFile().getParentFile();
				try
				{
					ModelCreator.Instance.AddPendingTexture(new PendingTexture(null, chooser.getSelectedFile(), this, 0));
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});
		btnImport.setFont(defaultFont);
		buttonRow.add(btnImport);

		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(a ->
		{
			texture = null;
			SwingUtilities.getWindowAncestor(btnClose).dispose();
		});
		btnClose.setFont(defaultFont);
		buttonRow.add(btnClose);

		JDialog dialog = new JDialog(manager.getCreator(), "Texture Manager", false);
		dialog.setLayout(new BorderLayout());
		dialog.setResizable(false);
		dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		dialog.setPreferredSize(new Dimension(540, 480));
		dialog.add(scroll, BorderLayout.CENTER);
		
		textFieldAndButtons.add(textFieldRow);
		textFieldAndButtons.add(buttonRow);
		
		dialog.add(textFieldAndButtons, BorderLayout.SOUTH);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);

		return texture;
	}
	
	
	@Override
	public void onTextureLoaded(boolean isnew, String errormessage, String texture) {
	
		if (isnew)
		{
			model.insertElementAt(texture.replace(".png", ""), 0);
		}
		
		if (errormessage != null)
		{
			JOptionPane error = new JOptionPane();
			error.setMessage(errormessage);
			JDialog dialog = error.createDialog(btnImport, "Textur Error");
			dialog.setLocationRelativeTo(null);
			dialog.setModal(false);
			dialog.setVisible(true);
		}
	}

	private DefaultListModel<String> generate()
	{
		DefaultListModel<String> model = new DefaultListModel<String>();
		for (TextureEntry entry : ModelCreator.currentProject.TexturesByCode.values())
		{
			model.addElement(entry.getCode());
		}
		return model;
	}
}
