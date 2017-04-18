package at.vintagestory.modelcreator.model;

import java.util.ArrayList;
import java.util.List;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Project;
import at.vintagestory.modelcreator.interfaces.IDrawable;

public class Keyframe
{
	private int FrameNumber;	
	public ArrayList<IDrawable> Elements = new ArrayList<IDrawable>();
	
	public boolean IsKeyFrame;

	
	public Keyframe(boolean IsKeyFrame) {
		this.IsKeyFrame = IsKeyFrame;
	}
	
	
	public void AddElementFromImport(Project project, KeyframeElement keyfElem) {
		Element elem = project.findElement(keyfElem.AnimatedElementName);
		KeyframeElement kelem = GetOrCreateKeyFrameElement(elem);
		kelem.setFrom(keyfElem);
	}
	
	public void AddElementDirectly(KeyframeElement keyfElem) {
		Elements.add(keyfElem);
		if (IsKeyFrame) ModelCreator.DidModify();
	}
	
	public void RemoveElement(KeyframeElement element) {
		KeyframeElement parentElem = (KeyframeElement)element.ParentElement;
		
		if (parentElem == null) {
			Elements.remove(element);
			if (IsKeyFrame) ModelCreator.DidModify();
			return;
		}
		
		parentElem.ChildElements.remove(parentElem);
		
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
	
	
	
	public KeyframeElement GetOrCreateKeyFrameElement(Element forElem) { 
		KeyframeElement keyframeElem = GetKeyFrameElement(forElem);
		
		if (keyframeElem != null) {
			return keyframeElem;
		}
		
		List<Element> path = forElem.GetParentPath();
		
		
		if (path.size() == 0) {
			keyframeElem = new KeyframeElement(forElem, true);
			AddElementDirectly(keyframeElem);	
			ModelCreator.DidModify();
		} else if (path.size() == 1) {
			KeyframeElement parent = GetOrCreateKeyFrameElement(path.get(0));
			keyframeElem = parent.GetOrCreateChildElement(forElem);
			
		} else {
			KeyframeElement parent = GetOrCreateKeyFrameElement(path.get(0));
			path.remove(0);
			
			while (path.size() > 0) {
				Element childElem = path.get(0);
				path.remove(0);
				keyframeElem = parent.GetOrCreateChildElement(childElem);
				parent = keyframeElem;
			}
			
			keyframeElem = keyframeElem.GetOrCreateChildElement(forElem);
		}
		
		keyframeElem.FrameNumber = FrameNumber;
		
		return keyframeElem;
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
		setFrameNumber(frameNumber, Elements);
		
		if (IsKeyFrame) ModelCreator.DidModify();
	}
	
	void setFrameNumber(int frameNumber, List<IDrawable> elements) {
		for (IDrawable elem : elements) {
			KeyframeElement kelem = (KeyframeElement)elem;
			kelem.FrameNumber = frameNumber;
			setFrameNumber(frameNumber, kelem.ChildElements);
		}
	}
	
	
	public Keyframe clone(boolean withElementReference) {
		return clone(IsKeyFrame, withElementReference);
	}
	
	public Keyframe clone(boolean iskeyframe, boolean withElementReference) {
		Keyframe cloned = new Keyframe(iskeyframe);
		cloned.FrameNumber = FrameNumber;
		
		for (IDrawable dw : Elements) {
			cloned.Elements.add((IDrawable) ((KeyframeElement)dw).clone(iskeyframe, withElementReference));
		}
		
		
		return cloned;
	}
	
}
