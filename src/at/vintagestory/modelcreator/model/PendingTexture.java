package at.vintagestory.modelcreator.model;

import java.io.File;
import java.io.IOException;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Project;
import at.vintagestory.modelcreator.interfaces.ITextureCallback;

public class PendingTexture
{
	public File textureFile;
	public ITextureCallback callback;
	public String ProjectType = "normal"; // "normal", "backdrop", "mountbackdrop"
	
	boolean doReplaceAll;
	boolean doReplacedForSelectedElement;
	boolean insertTextureSizeEntry;
	
	public TextureEntry entry;
	
	// Wait 3 frames before actually loading this file
	// Added because gimp seems to make VSMC crash otherwise
	public int LoadDelay = 3;
	
	String textureName;

	public PendingTexture(String textureName, File texture, int loadDelay)
	{
		this(textureName, texture, (ITextureCallback) null, loadDelay);
	}

	
	public PendingTexture(String textureName, File texture, ITextureCallback callback, int loadDelay)
	{
		this.textureFile = texture;
		this.callback = callback;
		this.textureName = textureName;
		this.LoadDelay = loadDelay;
	}
	
	public PendingTexture(TextureEntry entry, int loadDelay)
	{
		this.entry = entry;
		this.LoadDelay = loadDelay;
	}

	public void load()
	{
		try
		{
			Project project = ModelCreator.GetProject(ProjectType);
			
			if (entry != null) {
				project.reloadExternalTexture(entry);
				return;
			}
			
			String errormessge = null;			
			String fileName = this.textureFile.getName().replace(".png", "");
			

			BooleanParam isNew = new BooleanParam();			
			errormessge = project.loadTexture(textureName, this.textureFile, isNew, ProjectType, doReplaceAll, doReplacedForSelectedElement, insertTextureSizeEntry);
			
			if (callback != null) {
				callback.onTextureLoaded(isNew.Value, errormessge, fileName);
			}
		
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			// It seems like the Gimp "Overwrite file" feature saves the image in a non-traditional way?
			// Because VSMC randomly crashes with "IllegalArgumentException: Buffer size <=0"
			e.printStackTrace();
		}
	}


	public void SetProjectType(String type)
	{
		ProjectType = type;
	}


	public void SetReplacesAllTextures()
	{
		doReplaceAll = true;		
	}


	public void SetReplacesSelectElementTextures()
	{
		doReplacedForSelectedElement = true;		
	}


	public void SetInsertTextureSizeEntry()
	{
		insertTextureSizeEntry=true;		
	}
}
