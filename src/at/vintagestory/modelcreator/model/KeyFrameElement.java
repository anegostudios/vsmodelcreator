package at.vintagestory.modelcreator.model;

import java.util.ArrayList;

public class KeyFrameElement extends Element
{
	public Element AnimatedElement;
	public int FrameNumber;
	
	public boolean PositionSet;
	public boolean RotationSet;
	public boolean StretchSet;
	
	public ArrayList<KeyFrameElement> ChildKeyFrameElements = new ArrayList<KeyFrameElement>();
	
	public KeyFrameElement(Element cuboid)
	{
		super(cuboid);
		this.AnimatedElement = cuboid;
	}
	
	public boolean IsUseless() {
		return !PositionSet && !RotationSet && !StretchSet;
	}
	

}
