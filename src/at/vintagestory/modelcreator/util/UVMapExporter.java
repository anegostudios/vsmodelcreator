package at.vintagestory.modelcreator.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import org.newdawn.slick.Color;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.Face;

public class UVMapExporter
{
	public void Export(String path) {
		BufferedImage img = new BufferedImage(ModelCreator.noTexWidth, ModelCreator.noTexHeight, BufferedImage.TYPE_INT_ARGB);
		
		drawElementList(img, ModelCreator.currentProject.rootElements);
		
		try {
            ImageIO.write(img, "PNG", new File(path));
        } catch ( IOException e) {
            e.printStackTrace();
        }
	}

	private void drawElementList(BufferedImage img, ArrayList<Element> elements)
	{
		for (Element elem : elements) {
			Face[] faces = elem.getAllFaces();
			for (int i = 0; i < faces.length; i++) {
				if (!faces[i].isEnabled()) continue;
				
				drawElementFace(img, faces[i], elem);	
			}
			
			drawElementList(img, elem.ChildElements);
		}
	}

	private void drawElementFace(BufferedImage img, Face face, Element elem)
	{
		int startX = clamped((int)(face.getStartU() * ModelCreator.texScale), 0, ModelCreator.noTexWidth);
		int startY = clamped((int)(face.getStartV() * ModelCreator.texScale), 0, ModelCreator.noTexHeight);
		int endX = clamped((int)(face.getEndU() * ModelCreator.texScale), 0, ModelCreator.noTexWidth);
		int endY = clamped((int)(face.getEndV() * ModelCreator.texScale), 0, ModelCreator.noTexHeight);
		
		Color color = face.getFaceColor();
		int r = (int)(255 * color.r * elem.brightnessByFace[face.getSide()]);
		int g = (int)(255 * color.g * elem.brightnessByFace[face.getSide()]);
		int b = (int)(255 * color.b * elem.brightnessByFace[face.getSide()]);
		
		int rgba = 76 << 24 | r << 16 | g << 8 | b;
		
		for (int x = Math.min(startX, endX); x < Math.max(startX, endX); x++) {
			for (int y = Math.min(startY, endY); y < Math.max(startY, endY); y++) {
				img.setRGB(x, y, rgba);
			}
		}
		
		//GL11.glColor4f(color.r * elem.brightnessByFace[i], color.g * elem.brightnessByFace[i], color.b * elem.brightnessByFace[i], 0.3f);
	}
	
	
	int clamped(int val, int min, int max) {
		return Math.max(min, Math.min(val, max));
	}

}
