package at.vintagestory.modelcreator.model;

import at.vintagestory.modelcreator.ModelCreator;

public class AttachmentPoint
{
	private String code;
	
	protected double posX = 0.0, posY = 0.0, posZ = 0.0;
	protected double rotationX = 0, rotationY = 0, rotationZ = 0;
	
	
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
		this.posX = amt;
		ModelCreator.DidModify();
	}

	public void setPosY(double amt)
	{
		this.posY = amt;
		ModelCreator.DidModify();
	}

	public void setPosZ(double amt)
	{
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
	

	public AttachmentPoint clone() {
		AttachmentPoint cloned = new AttachmentPoint();

		cloned.posX = posX;
		cloned.posY = posY;
		cloned.posZ = posZ;
		
		cloned.rotationX = rotationX;
		cloned.rotationY = rotationY;
		cloned.rotationZ = rotationZ;
		
		
		return cloned;
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
