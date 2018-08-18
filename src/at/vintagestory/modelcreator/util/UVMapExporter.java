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
		BufferedImage img = new BufferedImage(
				(int)(ModelCreator.currentProject.TextureWidth * ModelCreator.noTexScale),
				(int)(ModelCreator.currentProject.TextureHeight * ModelCreator.noTexScale), 
				BufferedImage.TYPE_INT_ARGB
		);
		
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
		double startX = clamped((face.getStartU() * ModelCreator.noTexScale), 0, (int)(ModelCreator.currentProject.TextureWidth * ModelCreator.noTexScale));
		double startY = clamped((face.getStartV() * ModelCreator.noTexScale), 0, (int)(ModelCreator.currentProject.TextureHeight * ModelCreator.noTexScale));
		double endX = clamped((face.getEndU() * ModelCreator.noTexScale), 0, (int)(ModelCreator.currentProject.TextureWidth * ModelCreator.noTexScale));
		double endY = clamped((face.getEndV() * ModelCreator.noTexScale), 0, (int)(ModelCreator.currentProject.TextureHeight * ModelCreator.noTexScale));
		
		Color color = face.getFaceColor();
		int r = (int)(255 * color.r * elem.brightnessByFace[face.getSide()]);
		int g = (int)(255 * color.g * elem.brightnessByFace[face.getSide()]);
		int b = (int)(255 * color.b * elem.brightnessByFace[face.getSide()]);
		
		int rgba = 76 << 24 | r << 16 | g << 8 | b;
		
		int minX = (int)Math.min(startX, endX);
		int minY = (int)Math.min(startY, endY);
		
		int maxX = (int)Math.ceil(Math.max(startX, endX));
		int maxY = (int)Math.ceil(Math.max(startY, endY));
		
		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				img.setRGB(x, y, rgba);
			}
		}
	}
	
	
	double clamped(double val, double min, double max) {
		return Math.max(min, Math.min(val, max));
	}

}
