package at.vintagestory.modelcreator.model;

import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.newdawn.slick.opengl.Texture;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Project;
import at.vintagestory.modelcreator.util.watchservice.*;

public class TextureEntry
{
	public String code;
	public ImageIcon icon;
	public Texture texture;
	public String filePath;
	public String projectType;
	
	public int Width;
	public int Height;

	DirectoryWatchService watchService;

	public TextureEntry(String code, Texture texture, ImageIcon image, String textureLocation, String projectType)
	{
		this.code = code;
		this.texture = texture;
		this.icon = image;
		this.filePath = textureLocation;
		this.projectType = projectType;
		
		if (image == null) return;
		
		Width = texture.getImageWidth();
		Height = texture.getImageHeight();
		
		TextureEntry self = this;
		
		File f = new File(textureLocation);

		SwingUtilities.invokeLater(new Runnable() 
	    {
	      public void run() {
		        try {
		            watchService = new SimpleDirectoryWatchService(); // May throw
		            watchService.register( // May throw
		                    new DirectoryWatchService.OnFileChangeListener() {
		                        @Override
		                        public void onFileModify(String filePath) {
		                            if (!ModelCreator.autoreloadTexture) return;
		                            Project p = ModelCreator.GetProject(projectType);
	                        		if (p != null) {
	                        			PendingTexture ptex = new PendingTexture(self, 3);
	                        			ptex.SetProjectType(projectType);
	                        			ModelCreator.Instance.AddPendingTexture(ptex);
	                            	}
		                        }
		                    },
		                    f.getParent(),
		                    f.getName()
		            );
		            
		            watchService.start();
		        } catch (IOException e) {
		            System.out.println("Unable to register file change listener for " + textureLocation);
		        }
	      }
	    });
	}
	
	public void Dispose() {
		SwingUtilities.invokeLater(new Runnable() 
	    {
	      public void run() {
	    	  watchService.stop();
	      }
	    });
	}

	public String getCode()
	{
		return code;
	}

	public Texture getTexture()
	{
		return texture;
	}


	public ImageIcon getIcon()
	{
		return icon;
	}

	public String getFilePath()
	{
		return filePath;
	}
	
	// LwJgl upsizes all textures to 2^n
	public float LwJglFuckeryScaleW() {
		return (float)Width / texture.getTextureWidth();
	}
	
	public float LwJglFuckeryScaleH() {
		return (float)Height / texture.getTextureHeight();
	}
	
	public float VoxelWidthWithLwJglFuckery(Project project) {
		float scale = LwJglFuckeryScaleW(); 
		
		int[] size = project.TextureSizes.get(code);
		if (size != null) {
			return size[0] / scale;
		}
		
		return project.TextureWidth / scale;
	}
	
	public float VoxelHeighthWithLwJglFuckery(Project project) {
		float scale = LwJglFuckeryScaleH();
		
		int[] size = project.TextureSizes.get(code);
		if (size != null) {
			return size[1] / scale;
		}
		
		return project.TextureHeight / scale;
	}

}