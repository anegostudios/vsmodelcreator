package at.vintagestory.modelcreator.model;

import java.util.ArrayList;
import java.util.List;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.interfaces.IDrawable;

public class Keyframe
{
	private int FrameNumber;	
	public ArrayList<IDrawable> Elements = new ArrayList<IDrawable>();
	
	boolean IsKeyFrame;

	
	public Keyframe(boolean IsKeyFrame) {
		this.IsKeyFrame = IsKeyFrame;
	}
	
	
	public void AddElement(KeyframeElement keyfElem) {
		Elements.add(keyfElem);
		if (IsKeyFrame) ModelCreator.DidModify();
	}
	
	public void RemoveElement(KeyframeElement element) {
		KeyframeElement walkElem = (KeyframeElement)element.ParentElement;
		
		if (walkElem == null) {
			Elements.remove(element);
			if (IsKeyFrame) ModelCreator.DidModify();
			return;
		}
		
		while (walkElem.ParentElement != null) {
			 walkElem = (KeyframeElement)walkElem.ParentElement;
		}
		
		Elements.remove(walkElem);
		if (IsKeyFrame) ModelCreator.DidModify();
	}
	
	
	public boolean HasElements() {
		return Elements.size() > 0;
	}
	
	public KeyframeElement GetKeyFrameElement(Element forElem) {
		if (forElem == null) return null;
		List<Element> path = forElem.GetParentPath();
		
		List<IDrawable> elems = Elements;		
		while (path.size() > 0) {
			KeyframeElement kelem = findChildElement(elems, path.get(0));
			if (kelem == null) return null;
			path.remove(0);
			elems = kelem.ChildElements;
		}
		
		return findChildElement(elems, forElem);
	}
	
	KeyframeElement findChildElement(List<IDrawable> elems, Element forElem) {
		KeyframeElement keyframeElem;
		for (int i = 0; i < elems.size(); i++) {
			keyframeElem = (KeyframeElement)elems.get(i);
			if (keyframeElem != null && keyframeElem.AnimatedElement == forElem) return keyframeElem;
		}
		
		return null;
	}

	public int getFrameNumber()
	{
		return FrameNumber;
	}

	public void setFrameNumber(int frameNumber)
	{
		FrameNumber = frameNumber;
		if (IsKeyFrame) ModelCreator.DidModify();
	}
	
}
