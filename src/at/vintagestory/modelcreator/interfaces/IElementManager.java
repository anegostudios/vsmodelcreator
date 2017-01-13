package at.vintagestory.modelcreator.interfaces;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.PendingTexture;

public interface IElementManager
{
	public void addPendingTexture(PendingTexture texture);

	public ModelCreator getCreator();
	
	public Element getCurrentElement();
}
