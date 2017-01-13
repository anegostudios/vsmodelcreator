package at.vintagestory.modelcreator.model;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import at.vintagestory.modelcreator.enums.BlockFacing;
import at.vintagestory.modelcreator.interfaces.IDrawable;

public class KeyframeElement implements IDrawable
{
	public Element AnimatedElement;
	public int FrameNumber;
	
	public boolean PositionSet;
	public boolean RotationSet;
	public boolean StretchSet;
	
	public double offsetX = 0.0, offsetY = 0.0, offsetZ = 0.0;
	public double stretchX = 0.0, stretchY = 0.0, stretchZ = 0.0;
	public double rotationX = 0, rotationY = 0, rotationZ = 0;
	public double originX = 0, originY = 0, originZ = 0;

	IDrawable ParentElement;
	public List<IDrawable> ChildElements = new ArrayList<IDrawable>();
	
	
	
	public KeyframeElement GetOrCreateChildElement(Element forElement) {
		for (IDrawable elem : ChildElements) {
			if (((KeyframeElement)elem).AnimatedElement == forElement) return (KeyframeElement)elem;
		}
		
		KeyframeElement elem = new KeyframeElement(forElement);
		ChildElements.add(elem);
		elem.ParentElement = this;
		
		return elem;
	}
	
	
	public KeyframeElement(Element cuboid)
	{
		this.AnimatedElement = cuboid;
		
	}
	
	public boolean IsUseless() {
		return !PositionSet && !RotationSet && !StretchSet;
	}
	
	public boolean IsSet(int flagIndex) {
		if (flagIndex == 0) return PositionSet;
		if (flagIndex == 1) return RotationSet;
		return StretchSet;
	}

	
	
	
	@Override
	public void draw(IDrawable selectedElem)
	{
		float b;
		
		double originX = AnimatedElement.originX + this.originX;
		double originY = AnimatedElement.originY + this.originY;
		double originZ = AnimatedElement.originZ + this.originZ;
		
		double startX = AnimatedElement.startX + offsetX;
		double startY = AnimatedElement.startY + offsetY;
		double startZ = AnimatedElement.startZ + offsetZ;
		
		
		GL11.glPushMatrix();
		{
			//GL11.glLoadName(openGlName);
			GL11.glEnable(GL_BLEND);
			GL11.glDisable(GL_CULL_FACE);
			GL11.glTranslated(originX, originY, originZ);
			rotateAxis();
			GL11.glTranslated(-originX, -originY, -originZ);
			
			GL11.glTranslated(startX, startY, startZ);
			
			for (int i = 0; i < BlockFacing.ALLFACES.length; i++) {
				if (!AnimatedElement.faces[i].isEnabled()) continue;
				
				b = AnimatedElement.brightnessByFace[BlockFacing.ALLFACES[i].GetIndex()];
				Color c = Face.ColorsByFace[i];
				GL11.glColor3f(c.r * b, c.g * b, c.b * b);
								
				AnimatedElement.faces[i].renderFace(BlockFacing.ALLFACES[i], b);
			}
			GL11.glLoadName(0);
			
			for (int i = 0; i < ChildElements.size(); i++) {
				ChildElements.get(i).draw(selectedElem);
			}

		}
		GL11.glPopMatrix();
	}
	
	
	public void rotateAxis()
	{
		GL11.glRotated(AnimatedElement.rotationX + rotationX, 1, 0, 0);
		GL11.glRotated(AnimatedElement.rotationY + rotationY, 0, 1, 0);
		GL11.glRotated(AnimatedElement.rotationZ + rotationZ, 0, 0, 1);
	}

}
