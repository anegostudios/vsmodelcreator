package at.vintagestory.modelcreator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.BufferedImageUtil;

import at.vintagestory.modelcreator.gui.left.TextureCellRenderer;
import at.vintagestory.modelcreator.gui.right.RightTopPanel;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.ITextureCallback;
import at.vintagestory.modelcreator.model.PendingTexture;
import at.vintagestory.modelcreator.model.TextureEntry;
import at.vintagestory.modelcreator.model.TextureMeta;

public class TextureManager
{
	private static List<TextureEntry> textureCache = new ArrayList<TextureEntry>();

	public static Texture cobblestone;
	public static Texture dirt;

	public static File lastLocation = null;

	public static String loadExternalTexture(File image, File meta) throws IOException
	{
		TextureMeta textureMeta = TextureMeta.parse(meta);

		if (textureMeta != null)
		{
			if (textureMeta.getAnimation() != null)
			{
				System.out.println("Loading Animated Texture!");
				BufferedImage bimage = ImageIO.read(image);

				int fWidth = textureMeta.getAnimation().getWidth();
				int fHeight = textureMeta.getAnimation().getHeight();

				ImageIcon icon = null;

				List<Texture> textures = new ArrayList<Texture>();

				int xpos = 0;
				while (xpos + fWidth <= bimage.getWidth())
				{
					int ypos = 0;
					while (ypos + fHeight <= bimage.getHeight())
					{
						BufferedImage subImage = bimage.getSubimage(xpos, ypos, fWidth, fHeight);
						if (icon == null)
						{
							icon = TextureManager.upscale(new ImageIcon(subImage), 256);
						}
						Texture texture = BufferedImageUtil.getTexture("", subImage);
						textures.add(texture);
						ypos += fHeight;
					}
					xpos += fWidth;
				}
				String imageName = image.getName();
				textureCache.add(new TextureEntry(image.getName().substring(0, imageName.indexOf(".png")), textures, icon, image.getAbsolutePath(), textureMeta, meta.getAbsolutePath()));
				return null;
			}
			
			return loadTexture(image, textureMeta, meta.getAbsolutePath());
		}
		return loadTexture(image, null, null);
	}
	
	
	public static void reloadTextures(ModelCreator creator) {
		for (TextureEntry entry : textureCache) {
			try {
				creator.pendingTextures.add(new PendingTexture(entry));
			} catch (Exception e) {}
		}
	}
	
	
	public static void reloadExternalTexture(TextureEntry entry) throws IOException {
		FileInputStream is = new FileInputStream(entry.getFilePath());
		Texture texture = TextureLoader.getTexture("PNG", is);
		is.close();
		
		if (texture.getImageHeight() % 16 != 0 | texture.getImageWidth() % 16 != 0)
		{
			texture.release();
			return;
		}
		
		entry.icon = upscale(new ImageIcon(entry.getFilePath()), 256);
		entry.textures = Arrays.asList(texture);
	}
	

	private static String loadTexture(File image, TextureMeta meta, String location) throws IOException
	{
		FileInputStream is = new FileInputStream(image);
		Texture texture = TextureLoader.getTexture("PNG", is);
		is.close();

		if (texture.getImageHeight() % 16 != 0 || texture.getImageWidth() % 16 != 0)
		{
			texture.release();
			return "Not a multiple of 16 ("+texture.getImageHeight()+","+texture.getImageWidth()+")";
		}
		
		ImageIcon icon = upscale(new ImageIcon(image.getAbsolutePath()), 256);
		textureCache.add(new TextureEntry(image.getName().replace(".png", ""), texture, icon, image.getAbsolutePath(), meta, location));
		return null;
	}

	public static ImageIcon upscale(ImageIcon source, int length)
	{
		Image img = source.getImage();
		Image newimg = img.getScaledInstance(length, length, java.awt.Image.SCALE_FAST);
		return new ImageIcon(newimg);
	}

	public static TextureEntry getTextureEntry(String name)
	{
		for (TextureEntry entry : textureCache)
		{
			if (entry.getName().equalsIgnoreCase(name))
			{
				return entry;
			}
		}
		return null;
	}

	public static Texture getTexture(String name)
	{
		for (TextureEntry entry : textureCache)
		{
			if (entry.getName().equalsIgnoreCase(name))
			{
				return entry.getTexture();
			}
		}
		return null;
	}

	public static String getTextureLocation(String name)
	{
		for (TextureEntry entry : textureCache)
		{
			if (entry.getName().equalsIgnoreCase(name))
			{
				return entry.getFilePath();
			}
		}
		return null;
	}
	
	public static String getMetaLocation(String name)
	{
		for (TextureEntry entry : textureCache)
		{
			if (entry.getName().equalsIgnoreCase(name))
			{
				return entry.getMetaLocation();
			}
		}
		return null;
	}

	public static ImageIcon getIcon(String name)
	{
		for (TextureEntry entry : textureCache)
		{
			if (entry.getName().equalsIgnoreCase(name))
			{
				return entry.getIcon();
			}
		}
		return null;
	}

	private static String texture = null;

	public static String display(IElementManager manager)
	{
		Font defaultFont = new Font("SansSerif", Font.BOLD, 18);

		DefaultListModel<String> model = generate();
		JList<String> list = new JList<String>();
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setCellRenderer(new TextureCellRenderer());
		list.setVisibleRowCount(-1);
		list.setModel(model);
		list.setFixedCellHeight(256);
		list.setFixedCellWidth(256);
		list.setBackground(new Color(221, 221, 228));
		JScrollPane scroll = new JScrollPane(list);
		scroll.getVerticalScrollBar().setVisible(false);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JPanel panel = new JPanel(new GridLayout(1, 3));
		panel.setPreferredSize(new Dimension(1000, 40));
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
		panel.add(btnSelect);

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
					File meta = new File(chooser.getSelectedFile().getAbsolutePath() + ".mcmeta");
					System.out.println(meta.getName());
					manager.addPendingTexture(new PendingTexture(chooser.getSelectedFile(), meta, new ITextureCallback()
					{
						@Override
						public void callback(boolean isnew, String errormessage, String texture)
						{
							if (isnew)
							{
								model.insertElementAt(texture.replace(".png", ""), 0);
							}
							
							if (errormessage != null)
							{
								JOptionPane error = new JOptionPane();
								error.setMessage(errormessage);
								JDialog dialog = error.createDialog(btnImport, "Texture Error");
								dialog.setLocationRelativeTo(null);
								dialog.setModal(false);
								dialog.setVisible(true);
							}
						}
					}));
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});
		btnImport.setFont(defaultFont);
		panel.add(btnImport);

		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(a ->
		{
			texture = null;
			SwingUtilities.getWindowAncestor(btnClose).dispose();
		});
		btnClose.setFont(defaultFont);
		panel.add(btnClose);

		JDialog dialog = new JDialog(((RightTopPanel) manager).getCreator(), "Texture Manager", false);
		dialog.setLayout(new BorderLayout());
		dialog.setResizable(false);
		dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		dialog.setPreferredSize(new Dimension(540, 480));
		dialog.add(scroll, BorderLayout.CENTER);
		dialog.add(panel, BorderLayout.SOUTH);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);

		return texture;
	}

	private static DefaultListModel<String> generate()
	{
		DefaultListModel<String> model = new DefaultListModel<String>();
		for (TextureEntry entry : textureCache)
		{
			model.addElement(entry.getName());
		}
		return model;
	}
}
