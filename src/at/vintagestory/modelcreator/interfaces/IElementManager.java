package at.vintagestory.modelcreator.interfaces;

import java.awt.Frame;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.model.PendingTexture;

public interface IElementManager extends IBasicElementManager
{
	public void addPendingTexture(PendingTexture texture);

	public ModelCreator getCreator();
}
