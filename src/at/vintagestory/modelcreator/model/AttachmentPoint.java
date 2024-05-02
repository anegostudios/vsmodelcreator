package at.vintagestory.modelcreator.model;

import static org.lwjgl.opengl.GL11.GL_LINES;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import at.vintagestory.modelcreator.ModelCreator;

public class AttachmentPoint
{
	public String code;
	public Element ParentElem;
	public ArrayList<Element> StepChildElements = new ArrayList<Element>();
	
	public double posX = 0.0, posY = 0.0, posZ = 0.0;
	public double rotationX = 0, rotationY = 0, rotationZ = 0;
	Sphere sphere = new Sphere();
	
	
	public void addPosX(double amt)
	{
		this.posX += amt;
		ModelCreator.DidModify();
	}

	public void addPosY(double amt)
	{
		this.posY += amt;
		ModelCreator.DidModify();
	}

	public void addPosZ(double amt)
	{
		this.posZ += amt;
		ModelCreator.DidModify();
	}
	
	public double getPosX()
	{
		return posX;
	}

	public double getPosY()
	{
		return posY;
	}

	public double getPosZ()
	{
		return posZ;
	}

	

	public void setPosX(double amt)
	{
		if (this.posX == amt) return;
		
		this.posX = amt;
		ModelCreator.DidModify();
	}

	public void setPosY(double amt)
	{
		if (this.posY == amt) return;
		
		this.posY = amt;
		ModelCreator.DidModify();
	}

	public void setPosZ(double amt)
	{
		if (this.posZ == amt) return;
		
		this.posZ = amt;
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

	public void setRotationX(double rotation)
	{
		if (this.rotationX == rotation) return;
		
		this.rotationX = rotation;
		ModelCreator.DidModify();
	}

	public void setRotationY(double rotation)
	{
		if (this.rotationY == rotation) return;
		
		this.rotationY = rotation;
		ModelCreator.DidModify();
	}
	
	public void setRotationZ(double rotation)
	{
		if (this.rotationZ == rotation) return;
		
		this.rotationZ = rotation;
		ModelCreator.DidModify();
	}
	

	public AttachmentPoint clone() {
		AttachmentPoint cloned = new AttachmentPoint();

		cloned.code = code;
		cloned.posX = posX;
		cloned.posY = posY;
		cloned.posZ = posZ;
		
		cloned.rotationX = rotationX;
		cloned.rotationY = rotationY;
		cloned.rotationZ = rotationZ;
		
		
		return cloned;
	}
	
	

	
	public void draw()
	{
		GL11.glLineWidth(0.8f);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glPushMatrix();
		{
			GL11.glTranslated(getPosX(), getPosY(), getPosZ());
			GL11.glColor3f(0.25F, 0.85F, 0.25F);
			
			sphere.draw(0.125F, 16, 16);
			
			GL11.glRotated(getRotationX(), 1, 0, 0);
			GL11.glRotated(getRotationY(), 0, 1, 0);
			GL11.glRotated(getRotationZ(), 0, 0, 1);
			
			GL11.glBegin(GL_LINES);
			{
				// 3 Axes
				GL11.glColor3f(0.8f, 0.4f, 0.4f);
				GL11.glVertex3f(-4, 0, 0);
				GL11.glVertex3f(4, 0, 0);
				
				GL11.glVertex3f(4, 0, 0);
				GL11.glVertex3f(3.6f, 0, 0.4f);
				GL11.glVertex3f(4, 0, 0);
				GL11.glVertex3f(3.6f, 0, -0.4f);
				
				GL11.glColor3f(0.4f, 0.8f, 0.4f);
				GL11.glVertex3f(0, -4, 0);
				GL11.glVertex3f(0, 4, 0);
				
				GL11.glVertex3f(0, 4, 0);
				GL11.glVertex3f(0, 3.6f, 0.4f);
				
				GL11.glVertex3f(0, 4, 0);
				GL11.glVertex3f(0, 3.6f, -0.4f);
				
				GL11.glColor3f(0.4f, 0.4f, 0.8f);
				GL11.glVertex3f(0, 0, -4);
				GL11.glVertex3f(0, 0, 4);
				
				GL11.glVertex3f(0, 0, 4);
				GL11.glVertex3f(0.4f, 0, 3.6f);
				
				GL11.glVertex3f(0, 0, 4);
				GL11.glVertex3f(-0.4f, 0, 3.6f);
			}
			GL11.glEnd();
		}
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	@Override
	public String toString() {
		return code;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
		ModelCreator.DidModify();
	}

}
