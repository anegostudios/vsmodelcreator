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
	
	
	public boolean AddElementFromImport(Project project, KeyFrameElement keyfElem) {
		Element elem = project.findElement(keyfElem.AnimatedElementName);
		if (elem != null) {
			KeyFrameElement kelem = GetOrCreateKeyFrameElement(elem);
			kelem.setFrom(keyfElem);
			return true;
		}
		
		return false;
	}
	
	public void AddElementDirectly(KeyFrameElement keyfElem) {
		Elements.add(keyfElem);
		if (IsKeyFrame) ModelCreator.DidModify();
	}
	
	public void RemoveElement(KeyFrameElement element) {
		KeyFrameElement parentElem = (KeyFrameElement)element.ParentElement;
		
		if (parentElem == null) {
			Elements.remove(element);
			if (IsKeyFrame) ModelCreator.DidModify();
			return;
		}
		
		parentElem.ChildElements.remove(element);
		
		if (IsKeyFrame) ModelCreator.DidModify();
	}
	
	
	public boolean HasElements() {
		return Elements.size() > 0;
	}
	
	public KeyFrameElement GetKeyFrameElement(Element forElem) {
		return GetKeyFrameElement(Elements, forElem);
	}
	
	KeyFrameElement GetKeyFrameElement(List<IDrawable> elems, Element forElem) {
		KeyFrameElement kElem;
		for (int i = 0; i < elems.size(); i++) {
			kElem = (KeyFrameElement)elems.get(i);
			if (kElem != null && kElem.AnimatedElement == forElem) return kElem;
			
			if (kElem.ChildElements != null) {
				KeyFrameElement childKelem = GetKeyFrameElement(kElem.ChildElements, forElem);
				if (childKelem != null) return childKelem;
			}
		}
		
		return null;
	}
	
	
	
	public KeyFrameElement GetOrCreateKeyFrameElement(Element forElem) { 
		KeyFrameElement keyframeElem = GetKeyFrameElement(forElem);
		
		if (keyframeElem != null) {
			return keyframeElem;
		}
		
		List<Element> path = forElem.GetParentPath();
		
		
		if (path.size() == 0) {
			keyframeElem = new KeyFrameElement(forElem, true);
			AddElementDirectly(keyframeElem);	
			ModelCreator.DidModify();
		} else if (path.size() == 1) {
			KeyFrameElement parent = GetOrCreateKeyFrameElement(path.get(0));
			keyframeElem = parent.GetOrCreateChildElement(forElem);
			
		} else {
			KeyFrameElement parent = GetOrCreateKeyFrameElement(path.get(0));
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
			KeyFrameElement kelem = (KeyFrameElement)elem;
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
			cloned.Elements.add((IDrawable) ((KeyFrameElement)dw).clone(iskeyframe, withElementReference));
		}
		
		
		return cloned;
	}
	
}
