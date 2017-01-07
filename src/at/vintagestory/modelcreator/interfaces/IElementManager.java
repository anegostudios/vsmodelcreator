package at.vintagestory.modelcreator.interfaces;

import java.util.List;

import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.PendingTexture;

public interface IElementManager
{
	public Element getSelectedElement();

	public void selectElementByOpenGLName(int pos);

	public List<Element> getRootElements();

	public void clearElements();

	public void updateValues();

	public void addPendingTexture(PendingTexture texture);

	public boolean getAmbientOcc();

	public void setAmbientOcc(boolean occ);

	public void addElementAsChild(Element e);
	
	public void addRootElement(Element e);
		
	public void reset();
}
