package at.vintagestory.modelcreator.model;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_LINES;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;
import org.newdawn.slick.Color;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.enums.BlockFacing;
import at.vintagestory.modelcreator.interfaces.IDrawable;
import at.vintagestory.modelcreator.util.Mat4f;

public class KeyFrameElement implements IDrawable
{
	// Persistent kf-elem data
	public String AnimatedElementName;
	
	public boolean PositionSet;
	public boolean RotationSet;
	public boolean StretchSet;
	public boolean RotShortestDistance;

	private double offsetX = 0.0;
	private double offsetY = 0.0;
	private double offsetZ = 0.0;
	private double stretchX = 1.0;
	private double stretchY = 1.0;
	private double stretchZ = 1.0;
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
	KeyFrameElement ParentElement;
	public boolean IsKeyFrame;
	
	// Rotation Point Indicator
	protected Sphere sphere = new Sphere();

	
	public KeyFrameElement(boolean IsKeyFrame) {
		this.IsKeyFrame = IsKeyFrame; 
	}
	
	
	public KeyFrameElement GetOrCreateChildElement(Element forElement) {
		for (IDrawable elem : ChildElements) {
			if (((KeyFrameElement)elem).AnimatedElement == forElement) return (KeyFrameElement)elem;
		}
		
		KeyFrameElement elem = new KeyFrameElement(forElement, IsKeyFrame);
		ChildElements.add(elem);
		elem.ParentElement = this;
		if (IsKeyFrame) ModelCreator.DidModify();
		
		return elem;
	}
	
	
	public KeyFrameElement(Element cuboid, boolean IsKeyFrame)
	{
		this.AnimatedElement = cuboid;
		this.AnimatedElementName = cuboid.name;
		this.IsKeyFrame = IsKeyFrame;
	}
	
	public boolean IsUseless() {
		boolean useless = !PositionSet && !RotationSet && !StretchSet;
		
		for (IDrawable elem : ChildElements) {
			KeyFrameElement kf = (KeyFrameElement)elem;
			useless &= kf.IsUseless();
		}
		
		return useless;
	}
	
	public boolean IsSet(int flagIndex) {
		if (flagIndex == 0) return PositionSet;
		if (flagIndex == 1) return RotationSet;
		return StretchSet;
	}

	
	
	
	@Override
	public void draw(IDrawable selectedElem)
	{
		if (!AnimatedElement.getRenderInEditor()) return;
		
		float b;
		
		double originX = AnimatedElement.originX + this.getOriginX();
		double originY = AnimatedElement.originY + this.getOriginY();
		double originZ = AnimatedElement.originZ + this.getOriginZ();
		
		double startX = AnimatedElement.startX + getOffsetX();
		double startY = AnimatedElement.startY + getOffsetY();
		double startZ = AnimatedElement.startZ + getOffsetZ();
		
		float[] matrix = Mat4f.Create();
		
		GL11.glPushMatrix();
		{
			GL11.glEnable(GL_BLEND);
			GL11.glDisable(GL_CULL_FACE);
			
			// Correct (new)
			/*GL11.glTranslated(originX, originY, originZ);
			GL11.glTranslated(startX, startY, startZ);
			GL11.glScaled(stretchX, stretchY, stretchZ);
			
			rotateAxis();
			
			GL11.glTranslated(-originX, -originY, -originZ);*/
			
			// Wrong (old)
			GL11.glTranslated(originX, originY, originZ);
			rotateAxis();
			
			GL11.glScaled(stretchX, stretchY, stretchZ);
			
			GL11.glTranslated(-originX, -originY, -originZ);
			
			GL11.glTranslated(startX, startY, startZ);
			

			
			
			for (int i = 0; i < BlockFacing.ALLFACES.length; i++) {
				if (!AnimatedElement.faces[i].isEnabled()) continue;
				
				b = AnimatedElement.brightnessByFace[BlockFacing.ALLFACES[i].GetIndex()];
				Color c = Face.ColorsByFace[i];
				GL11.glColor3f(c.r * b, c.g * b, c.b * b);
								
				AnimatedElement.faces[i].renderFace(BlockFacing.ALLFACES[i], b, false, matrix);
			}
			GL11.glLoadName(0);
			
			for (int i = 0; i < ChildElements.size(); i++) {
				ChildElements.get(i).draw(selectedElem);
			}

		}
		GL11.glPopMatrix();
		
		
		if (selectedElem == AnimatedElement) {
			drawSelectionExtras();
		}
	}
	

	public void drawSelectionExtras()
	{
		double originX = AnimatedElement.originX + this.getOriginX();
		double originY = AnimatedElement.originY + this.getOriginY();
		double originZ = AnimatedElement.originZ + this.getOriginZ();
		
		double startX = AnimatedElement.startX + getOffsetX();
		double startY = AnimatedElement.startY + getOffsetY();
		double startZ = AnimatedElement.startZ + getOffsetZ();

		
		GL11.glLineWidth(1f);
		
		if (!ModelCreator.renderAttachmentPoints) {
			GL11.glPushMatrix();
			{
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glTranslated(originX, originY, originZ);
				GL11.glColor3f(0.25F, 0.25F, 0.25F);
				sphere.draw(0.2F, 16, 16);
				rotateAxis();
				GL11.glBegin(GL_LINES);
				{
					// 3 Axes
					GL11.glColor3f(1, 0, 0);
					GL11.glVertex3f(-4, 0, 0);
					GL11.glVertex3f(4, 0, 0);
					
					GL11.glVertex3f(4, 0, 0);
					GL11.glVertex3f(3.6f, 0, 0.4f);
					GL11.glVertex3f(4, 0, 0);
					GL11.glVertex3f(3.6f, 0, -0.4f);
					
					GL11.glColor3f(0, 1, 0);
					GL11.glVertex3f(0, -4, 0);
					GL11.glVertex3f(0, 4, 0);
					
					GL11.glVertex3f(0, 4, 0);
					GL11.glVertex3f(0, 3.6f, 0.4f);
					
					GL11.glVertex3f(0, 4, 0);
					GL11.glVertex3f(0, 3.6f, -0.4f);
					
					GL11.glColor3f(0, 0, 1);
					GL11.glVertex3f(0, 0, -4);
					GL11.glVertex3f(0, 0, 4);
					
					GL11.glVertex3f(0, 0, 4);
					GL11.glVertex3f(0.4f, 0, 3.6f);
					
					GL11.glVertex3f(0, 0, 4);
					GL11.glVertex3f(-0.4f, 0, 3.6f);
				}
				
				GL11.glEnd();
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
			
			GL11.glPopMatrix();			
		}
		


		// Cube highlight
		GL11.glPushMatrix();
		{
			GL11.glTranslated(originX, originY, originZ);
			rotateAxis();
			GL11.glTranslated(-originX, -originY, -originZ);
			
			GL11.glTranslated(startX, startY, startZ);
			
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glBegin(GL11.GL_LINES);
			{
				GL11.glColor4f(0F, 0F, 0F, 0.5f);
				
				float w = (float)AnimatedElement.width;
				float h = (float)AnimatedElement.height;
				float d = (float)AnimatedElement.depth;
				
				GL11.glVertex3f(0, 0, 0);
				GL11.glVertex3f(0, h, 0);
				
				GL11.glVertex3f(w, 0, 0);
				GL11.glVertex3f(w, h, 0);

				GL11.glVertex3f(w, 0, d);
				GL11.glVertex3f(w, h, d);
				
				GL11.glVertex3f(0, 0, d);
				GL11.glVertex3f(0, h, d);
				
				GL11.glVertex3f(0, h, 0);
				GL11.glVertex3f(w, h, 0);
				
				GL11.glVertex3f(w, h, 0);
				GL11.glVertex3f(w, h, d);
				
				GL11.glVertex3f(w, h, d);
				GL11.glVertex3f(0, h, d);
				
				GL11.glVertex3f(0, h, d);
				GL11.glVertex3f(0, h, 0);
				
				
				GL11.glVertex3f(0, 0, 0);
				GL11.glVertex3f(w, 0, 0);
				
				GL11.glVertex3f(w, 0, 0);
				GL11.glVertex3f(w, 0, d);
				
				GL11.glVertex3f(w, 0, d);
				GL11.glVertex3f(0, 0, d);
				
				GL11.glVertex3f(0, 0, d);
				GL11.glVertex3f(0, 0, 0);
			}
			
			GL11.glEnd();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		
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
		if (IsKeyFrame) ModelCreator.DidModify();
	}
	
	public void setOffsetY(double offsetY)
	{
		this.offsetY = offsetY;
		if (IsKeyFrame) ModelCreator.DidModify();
	}

	public void setOffsetZ(double offsetZ)
	{
		this.offsetZ = offsetZ;
		if (IsKeyFrame) ModelCreator.DidModify();
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
		if (IsKeyFrame) ModelCreator.DidModify();
	}

	public void setStretchY(double stretchY)
	{
		this.stretchY = stretchY;
		if (IsKeyFrame) ModelCreator.DidModify();
	}

	public void setStretchZ(double stretchZ)
	{
		this.stretchZ = stretchZ;
		if (IsKeyFrame) ModelCreator.DidModify();
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
		if (IsKeyFrame) ModelCreator.DidModify();
	}

	public void setRotationY(double rotationY)
	{
		this.rotationY = rotationY;
		if (IsKeyFrame) ModelCreator.DidModify();
	}

	public void setRotationZ(double rotationZ)
	{
		this.rotationZ = rotationZ;
		if (IsKeyFrame) ModelCreator.DidModify();
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
		if (IsKeyFrame) ModelCreator.DidModify();
	}

	public void setOriginY(double originY)
	{
		this.originY = originY;
		if (IsKeyFrame) ModelCreator.DidModify();
	}

	public void setOriginZ(double originZ)
	{
		this.originZ = originZ;
		if (IsKeyFrame) ModelCreator.DidModify();
	}

	
	public KeyFrameElement clone(boolean iskeyframe, boolean withElementReference) {
		KeyFrameElement cloned = new KeyFrameElement(iskeyframe);
		
		cloned.AnimatedElementName = AnimatedElement == null ? AnimatedElementName : ((Element)AnimatedElement).name;
		cloned.AnimatedElement = AnimatedElement;
		cloned.PositionSet = PositionSet;
		cloned.RotationSet = RotationSet;
		cloned.StretchSet = StretchSet;
		cloned.rotationX = rotationX;
		cloned.rotationY = rotationY;
		cloned.rotationZ = rotationZ;
		cloned.offsetX = offsetX;
		cloned.offsetY = offsetY;
		cloned.offsetZ = offsetZ;
		cloned.stretchX = stretchX;
		cloned.stretchY = stretchY;
		cloned.stretchZ = stretchZ;
		cloned.originX = originX;
		cloned.originY = originY;
		cloned.originZ = originZ;
		cloned.RotShortestDistance = RotShortestDistance;
		
		for (IDrawable dw : ChildElements) {
			cloned.ChildElements.add((IDrawable)((KeyFrameElement)dw).clone(iskeyframe, withElementReference));
		}
		
		cloned.FrameNumber = FrameNumber;
		return cloned;
	}
	
	
	public void setFrom(KeyFrameElement kelem) {
		AnimatedElementName = kelem.AnimatedElementName;
		PositionSet = kelem.PositionSet;
		RotationSet = kelem.RotationSet;
		StretchSet = kelem.StretchSet;
		offsetX = kelem.offsetX;
		offsetY = kelem.offsetY;
		offsetZ = kelem.offsetZ;
		stretchX = kelem.stretchX;
		stretchY = kelem.stretchY;
		stretchZ = kelem.stretchZ;
		originX = kelem.originX;
		originY = kelem.originY;
		originZ = kelem.originZ;
		rotationX = kelem.rotationX;
		rotationY = kelem.rotationY;
		rotationZ = kelem.rotationZ;
		RotShortestDistance = kelem.RotShortestDistance;
	}


	public void scaleAll(float size)
	{
		offsetX *= size;
		offsetY *= size;
		offsetZ *= size;
		stretchX *= size;
		stretchY *= size;
		stretchZ *= size;
		originX *= size;
		originY *= size;
		originZ *= size;
	}
}
