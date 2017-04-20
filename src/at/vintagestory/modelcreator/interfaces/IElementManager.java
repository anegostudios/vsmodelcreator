package at.vintagestory.modelcreator.interfaces;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.model.Element;

public interface IElementManager
{
	public ModelCreator getCreator();
	
	public Element getCurrentElement();
}
