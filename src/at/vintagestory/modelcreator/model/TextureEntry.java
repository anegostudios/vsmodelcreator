package at.vintagestory.modelcreator.model;

import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;

import org.newdawn.slick.opengl.Texture;

import at.vintagestory.modelcreator.old.TextureAnimation;

public class TextureEntry
{
	public String name;
	public ImageIcon icon;
	public List<Texture> textures;
	public String filePath;
	public String metaLocation;

	private TextureAnimation anim;
	private TextureProperties props;

	public TextureEntry(String name, Texture texture, ImageIcon image, String textureLocation)
	{
		this.name = name;
		this.textures = Arrays.asList(texture);
		this.icon = image;
		this.filePath = textureLocation;
	}

	public TextureEntry(String name, Texture texture, ImageIcon image, String textureLocation, TextureMeta meta, String metaLocation)
	{
		this.name = name;
		this.textures = Arrays.asList(texture);
		this.icon = image;
		this.filePath = textureLocation;
		if (meta != null)
			this.anim = meta.getAnimation();
		if (meta != null)
			this.props = meta.getProperties();
		this.metaLocation = metaLocation;
	}

	public TextureEntry(String name, List<Texture> textures, ImageIcon image, String textureLocation, TextureMeta meta, String metaLocation)
	{
		this.name = name;
		this.textures = textures;
		this.icon = image;
		this.filePath = textureLocation;
		if (meta != null)
			this.anim = meta.getAnimation();
		if (meta != null)
			this.props = meta.getProperties();
		this.metaLocation = metaLocation;
	}

	public String getName()
	{
		return name;
	}

	public Texture getTexture()
	{
		if (isAnimated())
			return textures.get(anim.getCurrentAnimationFrame());
		return textures.get(0);
	}

	public Texture getNextTexture()
	{
		if (isAnimated())
			return textures.get(anim.getNextAnimationFrame());
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

	public TextureAnimation getAnimation()
	{
		return anim;
	}

	public boolean isAnimated()
	{
		return anim != null;
	}

	public TextureProperties getProperties()
	{
		return props;
	}

	public boolean hasProperties()
	{
		return props != null;
	}

	public String getMetaLocation()
	{
		return metaLocation;
	}

	public int getPasses()
	{
		if (anim != null)
			return anim.getPasses();
		return 1;
	}
}
