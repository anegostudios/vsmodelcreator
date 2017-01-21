package at.vintagestory.modelcreator.model;

import at.vintagestory.modelcreator.ModelCreator;

public class AttachmentPoint
{
	protected double startX = 0.0, startY = 0.0, startZ = 0.0;
	protected double rotationX = 0, rotationY = 0, rotationZ = 0;
	
	
	public void addStartX(double amt)
	{
		this.startX += amt;
		ModelCreator.DidModify();
	}

	public void addStartY(double amt)
	{
		this.startY += amt;
		ModelCreator.DidModify();
	}

	public void addStartZ(double amt)
	{
		this.startZ += amt;
		ModelCreator.DidModify();
	}
	
	public double getStartX()
	{
		return startX;
	}

	public double getStartY()
	{
		return startY;
	}

	public double getStartZ()
	{
		return startZ;
	}

	

	public void setStartX(double amt)
	{
		this.startX = amt;
		ModelCreator.DidModify();
	}

	public void setStartY(double amt)
	{
		this.startY = amt;
		ModelCreator.DidModify();
	}

	public void setStartZ(double amt)
	{
		this.startZ = amt;
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
		this.rotationX = rotation;
		ModelCreator.DidModify();
	}

	public void setRotationY(double rotation)
	{
		this.rotationY = rotation;
		ModelCreator.DidModify();
	}
	
	public void setRotationZ(double rotation)
	{
		this.rotationZ = rotation;
		ModelCreator.DidModify();
	}
	


}
