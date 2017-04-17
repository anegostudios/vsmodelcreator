package at.vintagestory.modelcreator.util.screenshot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.stream.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;


import at.vintagestory.modelcreator.ModelCreator;

public class AnimatedGifCapture
{
	int currentFrame = 0;
	
	GifSequenceWriter gifwriter;
	
	public AnimatedGifCapture(String filename) {
		ImageOutputStream outstream;
		try
		{
			outstream = new FileImageOutputStream(new File(filename));
			gifwriter = new GifSequenceWriter(outstream, BufferedImage.TYPE_INT_RGB, 33, true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
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

			gifwriter.writeToSequence(image);
			
			currentFrame++;
			
			if (isComplete()) {
				gifwriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
