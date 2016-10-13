package com.mrcrayfish.modelcreator.texture;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class PendingTexture
{
	public File texture;
	public File meta;
	public TextureCallback callback;
	
	public TextureEntry entry;

	public PendingTexture(File texture)
	{
		this(texture, (TextureCallback) null);
	}

	public PendingTexture(File texture, File meta)
	{
		this(texture, meta, null);
	}

	public PendingTexture(File texture, TextureCallback callback)
	{
		this.texture = texture;
		this.callback = callback;
	}

	public PendingTexture(File texture, File meta, TextureCallback callback)
	{
		this.texture = texture;
		this.meta = meta;
		this.callback = callback;
	}

	public PendingTexture(TextureEntry entry)
	{
		this.entry = entry;
	}

	public void load()
	{
		try
		{
			if (entry != null) {
				TextureManager.reloadExternalTexture(entry);
				return;
			}
			
			String errormessge = null;
			boolean isnew = false;
			
			String fileName = this.texture.getName().replace(".png", "").replaceAll("\\d*$", "");
			Texture texture = TextureManager.getTexture(fileName);
			
			if (texture == null)
			{
				FileInputStream is = new FileInputStream(this.texture);
				texture = TextureLoader.getTexture("PNG", is);
				errormessge = TextureManager.loadExternalTexture(this.texture, this.meta);
				is.close();
				isnew = true;
			}
			
			if (callback != null) {
				callback.callback(isnew, errormessge, fileName);
			}
				
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
