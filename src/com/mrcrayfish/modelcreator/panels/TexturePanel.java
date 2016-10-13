package com.mrcrayfish.modelcreator.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.mrcrayfish.modelcreator.Icons;
import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.element.Face;
import com.mrcrayfish.modelcreator.texture.ClipboardTexture;
import com.mrcrayfish.modelcreator.texture.TextureCallback;
import com.mrcrayfish.modelcreator.texture.TextureManager;
import com.mrcrayfish.modelcreator.util.Clipboard;

public class TexturePanel extends JPanel implements TextureCallback
{
	private static final long serialVersionUID = 1L;

	private ElementManager manager;

	private JButton btnSelect;
	private JButton btnClear;
	private JButton btnCopy;
	private JButton btnPaste;

	public TexturePanel(ElementManager manager)
	{
		this.manager = manager;
		setLayout(new GridLayout(2, 2, 4, 4));
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(221, 221, 228), 5), "<html><b>Texture</b></html>"));
		setMaximumSize(new Dimension(186, 90));
		initComponents();
		addComponents();
	}

	public void initComponents()
	{
		Font defaultFont = new Font("SansSerif", Font.BOLD, 14);

		btnSelect = new JButton("Image");
		btnSelect.setIcon(Icons.texture);
		btnSelect.addActionListener(e ->
		{
			if (manager.getSelectedElement() != null)
			{
				String texture = TextureManager.display(manager);
				if (texture != null)
				{
					manager.getSelectedElement().getSelectedFace().setTexture(texture);
				}
			}
		});
		btnSelect.setFont(defaultFont);
		btnSelect.setToolTipText("Opens the Texture Manager");

		btnClear = new JButton("Clear");
		btnClear.setIcon(Icons.clear);
		btnClear.addActionListener(e ->
		{
			if (manager.getSelectedElement() != null)
			{
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					manager.getSelectedElement().setAllTextures(null, null);
				}
				else
				{
					manager.getSelectedElement().getSelectedFace().setTexture(null);
				}
			}
		});
		btnClear.setFont(defaultFont);
		btnClear.setToolTipText("<html>Clears the texture from this face.<br><b>Hold shift to clear all faces</b></html>");

		btnCopy = new JButton("Copy");
		btnCopy.setIcon(Icons.copy);
		btnCopy.addActionListener(e ->
		{
			if (manager.getSelectedElement() != null)
			{
				Face face = manager.getSelectedElement().getSelectedFace();
				Clipboard.copyTexture(face.getTextureLocation(), face.getTextureName());
			}
		});
		btnCopy.setFont(defaultFont);
		btnCopy.setToolTipText("Copies the texture on this face to clipboard");

		btnPaste = new JButton("Paste");
		btnPaste.setIcon(Icons.clipboard);
		btnPaste.addActionListener(e ->
		{
			if (manager.getSelectedElement() != null)
			{
				ClipboardTexture texture = Clipboard.getTexture();
				if (texture != null)
				{
					if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
					{
						manager.getSelectedElement().setAllTextures(texture);
					}
					else
					{
						Face face = manager.getSelectedElement().getSelectedFace();
						face.setTexture(texture.getTexture());
						face.setTextureLocation(texture.getLocation());
					}
				}
			}
		});
		btnPaste.setFont(defaultFont);
		btnPaste.setToolTipText("<html>Pastes the clipboard texture to this face.<br><b>Hold shift to paste to all faces</b></html>");
	}

	public void addComponents()
	{
		add(btnSelect);
		add(btnClear);
		add(btnCopy);
		add(btnPaste);
	}

	@Override
	public void callback(boolean success, String errormessage, String texture)
	{
		if (success)
			if (manager.getSelectedElement() != null)
			{
				manager.getSelectedElement().getSelectedFace().setTexture(texture);
			}
	}
}
