package at.vintagestory.modelcreator.model;

import java.util.ArrayList;
import java.util.List;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Project;
import at.vintagestory.modelcreator.interfaces.IDrawable;

public class AnimationFrame
{
	private int FrameNumber;
	
	// For IsKeyFrame == true, this is a flat list, otherwise a tree hierarchy 
	public ArrayList<IDrawable> Elements = new ArrayList<IDrawable>();
	
	public boolean IsKeyFrame;

	
	public AnimationFrame(boolean IsKeyFrame) {
		this.IsKeyFrame = IsKeyFrame;
	}
	
	
	public boolean AddKeyFrameElementFromImport(Project project, AnimFrameElement frameElem) {
		Element elem = project.findElement(frameElem.AnimatedElementName);
		if (elem != null) {
			AnimFrameElement kelem = GetOrCreateKeyFrameElementFlat(FrameNumber, elem);
			kelem.setFrom(frameElem);
			return true;
		}
		
		return false;
	}
	
	public void AddElementDirectly(AnimFrameElement frameElem) {
		Elements.add(frameElem);
		if (IsKeyFrame) ModelCreator.DidModify();
	}
	
	public void RemoveElement(AnimFrameElement element) {
		AnimFrameElement parentElem = (AnimFrameElement)element.ParentElement;
		AnimFrameElement stepParentElem = (AnimFrameElement)element.StepParentElement;
		
		if (parentElem == null && stepParentElem == null) {
			Elements.remove(element);
			if (IsKeyFrame) ModelCreator.DidModify();
			return;
		}
		
		parentElem.ChildElements.remove(element);
		stepParentElem.StepChildElements.remove(element);
		
		if (IsKeyFrame) ModelCreator.DidModify();
	}
	
	
	public boolean HasElements() {
		return Elements.size() > 0;
	}
	
	public AnimFrameElement GetKeyFrameElementFlat(Element forElem) {
		AnimFrameElement kElem;
		for (int i = 0; i < Elements.size(); i++) {
			kElem = (AnimFrameElement)Elements.get(i);
			
			if (kElem != null && kElem.AnimatedElement == forElem) return kElem;
		}
		
		return null;
	}
	
	public AnimFrameElement GetOrCreateKeyFrameElementFlat(int frameNumber, Element forElem) {
		AnimFrameElement kElem = GetKeyFrameElementFlat(forElem);
		
		if (kElem == null) {
			kElem = new AnimFrameElement(true);
			kElem.FrameNumber=frameNumber;
			kElem.AnimatedElement=forElem;
			kElem.AnimatedElementName=forElem.name;
			kElem.IsKeyFrame=true;
			Elements.add(kElem);
		}
		
		return kElem;
	}
	
	
	
	public AnimFrameElement GetAnimFrameElementRec(Element animatedElement)
	{
		return GetAnimFrameElementRec(animatedElement, Elements); 
	}
	
	
	
	private AnimFrameElement GetAnimFrameElementRec(Element animatedElement, List<IDrawable> elements)
	{
		for (IDrawable ele : elements) {
			AnimFrameElement animFrameEle = (AnimFrameElement)ele;
			
			if (animFrameEle.AnimatedElement == animatedElement) return animFrameEle;
			
			AnimFrameElement targetFrEle = GetAnimFrameElementRec(animatedElement, animFrameEle.ChildElements);
			if (targetFrEle != null) return targetFrEle;
			
			targetFrEle = GetAnimFrameElementRec(animatedElement, animFrameEle.StepChildElements);
			if (targetFrEle != null) return targetFrEle;
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
			AnimFrameElement kelem = (AnimFrameElement)elem;
			kelem.FrameNumber = frameNumber;
			setFrameNumber(frameNumber, kelem.ChildElements);
		}
	}
	
	
	public AnimationFrame clone() {
		return clone(IsKeyFrame);
	}
	
	public AnimationFrame clone(boolean iskeyframe) {
		AnimationFrame cloned = new AnimationFrame(iskeyframe);
		cloned.FrameNumber = FrameNumber;
		
		for (IDrawable dw : Elements) {
			cloned.Elements.add((IDrawable) ((AnimFrameElement)dw).clone(iskeyframe));
		}
		
		
		return cloned;
	}



}
