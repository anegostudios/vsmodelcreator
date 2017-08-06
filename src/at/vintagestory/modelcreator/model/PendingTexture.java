package at.vintagestory.modelcreator.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.interfaces.ITextureCallback;

public class PendingTexture
{
	public File texture;
	public ITextureCallback callback;
	
	public TextureEntry entry;
	String textureName;

	public PendingTexture(String textureName, File texture)
	{
		this(textureName, texture, (ITextureCallback) null);
	}

	
	public PendingTexture(String textureName, File texture, ITextureCallback callback)
	{
		this.texture = texture;
		this.callback = callback;
		this.textureName = textureName;
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
				ModelCreator.currentProject.reloadExternalTexture(entry);
				return;
			}
			
			String errormessge = null;
			boolean isnew = false;
			
			String fileName = this.texture.getName().replace(".png", "");
			Texture texture = ModelCreator.currentProject.getTexture(fileName);
			
			if (texture == null)
			{
				FileInputStream is = new FileInputStream(this.texture);
				texture = TextureLoader.getTexture("PNG", is);
				errormessge = ModelCreator.currentProject.loadTexture(textureName, this.texture);
				is.close();
				isnew = true;
			}
			
			if (callback != null) {
				callback.onTextureLoaded(isnew, errormessge, fileName);
			}
		
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
