package at.vintagestory.modelcreator.model;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.enums.BlockFacing;
import at.vintagestory.modelcreator.interfaces.IDrawable;

public class KeyframeElement implements IDrawable
{
	// Persistent kf-elem data
	public String AnimatedElementName;
	
	public boolean PositionSet;
	public boolean RotationSet;
	public boolean StretchSet;

	private double offsetX = 0.0;
	private double offsetY = 0.0;
	private double offsetZ = 0.0;
	private double stretchX = 0.0;
	private double stretchY = 0.0;
	private double stretchZ = 0.0;
	private double rotationX = 0;
	private double rotationY = 0;
	private double rotationZ = 0;
	private double originX = 0;
	private double originY = 0;
	private double originZ = 0;

	public List<IDrawable> ChildElements = new ArrayList<IDrawable>();

	
	// Non-persistent kf-elem data 
	
	public Element AnimatedElement;
	public int FrameNumber;
	IDrawable ParentElement;

	
	public KeyframeElement() {
		
	}
	
	
	public KeyframeElement GetOrCreateChildElement(Element forElement) {
		for (IDrawable elem : ChildElements) {
			if (((KeyframeElement)elem).AnimatedElement == forElement) return (KeyframeElement)elem;
		}
		
		KeyframeElement elem = new KeyframeElement(forElement);
		ChildElements.add(elem);
		elem.ParentElement = this;
		ModelCreator.DidModify();
		
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
		
		double originX = AnimatedElement.originX + this.getOriginX();
		double originY = AnimatedElement.originY + this.getOriginY();
		double originZ = AnimatedElement.originZ + this.getOriginZ();
		
		double startX = AnimatedElement.startX + getOffsetX();
		double startY = AnimatedElement.startY + getOffsetY();
		double startZ = AnimatedElement.startZ + getOffsetZ();
		
		
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
		GL11.glRotated(AnimatedElement.rotationX + getRotationX(), 1, 0, 0);
		GL11.glRotated(AnimatedElement.rotationY + getRotationY(), 0, 1, 0);
		GL11.glRotated(AnimatedElement.rotationZ + getRotationZ(), 0, 0, 1);
	}


	public double getOffsetX()
	{
		return offsetX;
	}
	public double getOffsetY()
	{
		return offsetY;
	}
	public double getOffsetZ()
	{
		return offsetZ;
	}



	public void setOffsetX(double offsetX)
	{
		this.offsetX = offsetX;
		ModelCreator.DidModify();
	}
	
	public void setOffsetY(double offsetY)
	{
		this.offsetY = offsetY;
		ModelCreator.DidModify();
	}

	public void setOffsetZ(double offsetZ)
	{
		this.offsetZ = offsetZ;
		ModelCreator.DidModify();
	}


	public double getStretchX()
	{
		return stretchX;
	}
	public double getStretchY()
	{
		return stretchY;
	}
	public double getStretchZ()
	{
		return stretchZ;
	}


	public void setStretchX(double stretchX)
	{
		this.stretchX = stretchX;
		ModelCreator.DidModify();
	}

	public void setStretchY(double stretchY)
	{
		this.stretchY = stretchY;
		ModelCreator.DidModify();
	}

	public void setStretchZ(double stretchZ)
	{
		this.stretchZ = stretchZ;
		ModelCreator.DidModify();
	}


	public double getRotationX()
	{
		return rotationX;
	}
	public double getRotationY()
	{
		return rotationY;
	}
	public double getRotationZ()
	{
		return rotationZ;
	}
	
	
	public void setRotationX(double rotationX)
	{
		this.rotationX = rotationX;
		ModelCreator.DidModify();
	}

	public void setRotationY(double rotationY)
	{
		this.rotationY = rotationY;
		ModelCreator.DidModify();
	}

	public void setRotationZ(double rotationZ)
	{
		this.rotationZ = rotationZ;
		ModelCreator.DidModify();
	}

	public double getOriginX()
	{
		return originX;
	}
	public double getOriginY()
	{
		return originY;
	}
	public double getOriginZ()
	{
		return originZ;
	}


	public void setOriginX(double originX)
	{
		this.originX = originX;
		ModelCreator.DidModify();
	}

	public void setOriginY(double originY)
	{
		this.originY = originY;
		ModelCreator.DidModify();
	}

	public void setOriginZ(double originZ)
	{
		this.originZ = originZ;
		ModelCreator.DidModify();
	}

}
