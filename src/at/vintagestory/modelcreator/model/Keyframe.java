package at.vintagestory.modelcreator.model;

import java.util.ArrayList;

public class Keyframe
{
	private ArrayList<KeyFrameElement> Elements = new ArrayList<KeyFrameElement>();
	
	private ArrayList<KeyFrameElement> InterpolatedElements = new ArrayList<KeyFrameElement>();
	
	
	public void AddElement(KeyFrameElement element) {
		Elements.add(element);
		InterpolatedElements.add(new KeyFrameElement(element));
	}
	
	public void RemoveElement(KeyFrameElement element) {
		int index = Elements.indexOf(element);
		Elements.remove(element);
		if (index > 0) InterpolatedElements.remove(index);
	}
	
	public boolean HasElements() {
		return Elements.size() > 0;
	}
	
	public KeyFrameElement GetKeyFrameElement(Element elem) {
		KeyFrameElement keyframeElem;
		for (int i = 0; i < Elements.size(); i++) {
			keyframeElem = Elements.get(i);
			if (keyframeElem != null && keyframeElem.AnimatedElement == elem) return keyframeElem;
		}
		
		return null;
	}
	
}
