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


	public TextureEntry(String name, Texture texture, ImageIcon image, String textureLocation)
	{
		this.name = name;
		this.textures = Arrays.asList(texture);
		this.icon = image;
		this.filePath = textureLocation;
	}

	public TextureEntry(String name, List<Texture> textures, ImageIcon image, String textureLocation)
	{
		this.name = name;
		this.textures = textures;
		this.icon = image;
		this.filePath = textureLocation;
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

	public TextureEntry clone()
	{
		TextureEntry cloned = new TextureEntry(name, textures, icon, filePath);
		return cloned;
	}
}