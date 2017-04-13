package at.vintagestory.modelcreator.model;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.swing.ImageIcon;
import org.newdawn.slick.opengl.Texture;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.util.watchservice.*;

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
		
		TextureEntry self = this;
		
		File f = new File(textureLocation);

        try {
            DirectoryWatchService watchService = new SimpleDirectoryWatchService(); // May throw
            watchService.register( // May throw
                    new DirectoryWatchService.OnFileChangeListener() {
                        @Override
                        public void onFileModify(String filePath) {
                            
                        	if (ModelCreator.autoreloadTexture && ModelCreator.currentProject != null) {
                    			ModelCreator.Instance.pendingTextures.add(new PendingTexture(self));
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