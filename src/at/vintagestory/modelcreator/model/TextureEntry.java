package at.vintagestory.modelcreator.model;

import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;

import org.newdawn.slick.opengl.Texture;

public class TextureEntry
{
	public String name;
	public ImageIcon icon;
	public List<Texture> textures;
	public String filePath;
	
	public int Width;
	public int Height;


	public TextureEntry(String name, Texture texture, ImageIcon image, String textureLocation)
	{
		this.name = name;
		this.textures = Arrays.asList(texture);
		this.icon = image;
		this.filePath = textureLocation;
		Width = texture.getImageWidth();
		Height = texture.getImageHeight();
	}

	public String getName()
	{
		return name;
	}

	public Texture getTexture()
	{
		return textures.get(0);
	}

	public Texture getNextTexture()
	{
		return textures.get(0);
	}

	public ImageIcon getIcon()
	{
		return icon;
	}

	public String getFilePath()
	{
		return filePath;
	}

}