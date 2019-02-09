package at.vintagestory.modelcreator.util.screenshot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;


import at.vintagestory.modelcreator.ModelCreator;

public class AnimationPngCapture extends AnimationCapture
{
	int currentFrame = 0;
	String filename;
	
	public AnimationPngCapture(String filename) {
		this.filename = filename;
		
		if (!filename.endsWith(".png")) filename += ".png";
	}
	
	public boolean isComplete()
	{
		return ModelCreator.currentProject.SelectedAnimation == null || currentFrame >= ModelCreator.currentProject.SelectedAnimation.GetQuantityFrames() - 1;
	}

	public void PrepareFrame()
	{
		ModelCreator.currentProject.SelectedAnimation.currentFrame = currentFrame;
	}

	public void CaptureFrame(int width, int height)
	{
		GL11.glReadBuffer(GL11.GL_FRONT);
		int bpp = 4;
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

		try
		{
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			
			for (int x = 0; x < width; x++)
			{
				for (int y = 0; y < height; y++)
				{
					int i = (x + (width * y)) * bpp;
					int r = buffer.get(i) & 0xFF;
					int g = buffer.get(i + 1) & 0xFF;
					int b = buffer.get(i + 2) & 0xFF;
					image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
				}
			}
			
			
			String fname = filename.replace(".png", "-" + currentFrame + ".png");
			ImageIO.write(image, "PNG", new File(fname));
			
			currentFrame++;

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
