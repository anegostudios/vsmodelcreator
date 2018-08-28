package at.vintagestory.modelcreator.model;

import java.io.File;
import java.io.IOException;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.interfaces.ITextureCallback;

public class PendingTexture
{
	public File textureFile;
	public ITextureCallback callback;
	
	public TextureEntry entry;
	String textureName;

	public PendingTexture(String textureName, File texture)
	{
		this(textureName, texture, (ITextureCallback) null);
	}

	
	public PendingTexture(String textureName, File texture, ITextureCallback callback)
	{
		this.textureFile = texture;
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
			
			String fileName = this.textureFile.getName().replace(".png", "");
			//Texture texture = ModelCreator.currentProject.getTextureByCode(fileName); - why is this here?
			//if (texture == null) - why is this here?
			//{
				//FileInputStream fileinputstream = new FileInputStream(this.textureFile);
				//TextureLoader.getTexture("PNG", fileinputstream); - why the efff is this here?
				
				//fileinputstream.close();
			//}
			
			BooleanParam isNew = new BooleanParam();
			errormessge = ModelCreator.currentProject.loadTexture(textureName, this.textureFile, isNew);
			
			if (callback != null) {
				callback.onTextureLoaded(isNew.Value, errormessge, fileName);
			}
		
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
