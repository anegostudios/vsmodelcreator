package at.vintagestory.modelcreator.gui.right.face;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import at.vintagestory.modelcreator.Start;
import at.vintagestory.modelcreator.gui.Icons;
import at.vintagestory.modelcreator.gui.texturedialog.TextureDialog;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.ITextureCallback;
import at.vintagestory.modelcreator.model.ClipboardTexture;
import at.vintagestory.modelcreator.model.Face;
import at.vintagestory.modelcreator.util.Clipboard;

public class FaceTexturePanel extends JPanel implements ITextureCallback
{
	private static final long serialVersionUID = 1L;

	private IElementManager manager;

	private JButton btnSelect;
	private JButton btnClear;
	private JButton btnCopy;
	private JButton btnPaste;

	public FaceTexturePanel(IElementManager manager)
	{
		this.manager = manager;
		setLayout(new GridLayout(2, 2, 4, 4));
		setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>Texture</b></html>"));
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
			if (manager.getCurrentElement() != null)
			{
				TextureDialog dlg = new TextureDialog();
				String texture = dlg.display(manager);
				if (texture != null)
				{
					manager.getCurrentElement().getSelectedFace().setTextureCode(texture);
				}
				
			}
		});
		btnSelect.setFont(defaultFont);
		btnSelect.setToolTipText("Opens the Texture Manager");

		btnClear = new JButton("Clear");
		btnClear.setIcon(Icons.clear);
		btnClear.addActionListener(e ->
		{
			if (manager.getCurrentElement() != null)
			{
				if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
				{
					manager.getCurrentElement().setAllTextureNames(null);
				}
				else
				{
					manager.getCurrentElement().getSelectedFace().setTextureCode(null);
				}
			}
		});
		btnClear.setFont(defaultFont);
		btnClear.setToolTipText("<html>Clears the texture from this face.<br><b>Hold shift to clear all faces</b></html>");

		btnCopy = new JButton("Copy");
		btnCopy.setIcon(Icons.copy);
		btnCopy.addActionListener(e ->
		{
			if (manager.getCurrentElement() != null)
			{
				Face face = manager.getCurrentElement().getSelectedFace();
				Clipboard.copyTexture(face.getTextureCode());
			}
		});
		btnCopy.setFont(defaultFont);
		btnCopy.setToolTipText("Copies the texture on this face to clipboard");

		btnPaste = new JButton("Paste");
		btnPaste.setIcon(Icons.clipboard);
		btnPaste.addActionListener(e ->
		{
			if (manager.getCurrentElement() != null)
			{
				ClipboardTexture texture = Clipboard.getTexture();
				if (texture != null)
				{
					if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1)
					{
						manager.getCurrentElement().setAllTextures(texture);
					}
					else
					{
						Face face = manager.getCurrentElement().getSelectedFace();
						face.setTextureCode(texture.getTexture());
						//face.setTextureLocation(texture.getLocation());
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
	public void onTextureLoaded(boolean isNew, String errormessage, String texture)
	{
		if (isNew)
			if (manager.getCurrentElement() != null)
			{
				manager.getCurrentElement().getSelectedFace().setTextureCode(texture);
			}
	}
}
