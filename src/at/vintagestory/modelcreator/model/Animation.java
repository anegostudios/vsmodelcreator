package at.vintagestory.modelcreator.model;

import java.util.ArrayList;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Project;
import at.vintagestory.modelcreator.enums.*;
import at.vintagestory.modelcreator.interfaces.IDrawable;

public class Animation
{
	// Persistent animation data
	int quantityFrames;
	private String name;
	private String code;
	public AnimationFrame[] keyframes = new AnimationFrame[0];
	public int version;
	
    public EnumEntityActivityStoppedHandling OnActivityStopped = EnumEntityActivityStoppedHandling.Rewind;
    public EnumEntityAnimationEndHandling OnAnimationEnd = EnumEntityAnimationEndHandling.Repeat;
	
		
	// Non-persistent animation data 
	public int currentFrame;
	public ArrayList<AnimationFrame> allFrames = new ArrayList<AnimationFrame>();
	public int[] frameNumbers = new int[0];
	public boolean framesDirty = true;
	
	
	public Animation() {
		quantityFrames = 30;
	}
	
	public Animation(int quantityFrames) {
		this.quantityFrames = quantityFrames;
	}

	public void SetFramesDirty() {
		framesDirty = true;
	}
	
	public void calculateAllFrames(Project project) {
		// We'll use a simple, slightly memory intensive but cpu friendly solution
		// Static models are tree hierarchies of boxes
		// So let's just built up one complete static model for each frame (only storing the keyframe data, but still a tree hierarchy and referencing the static box)
		
		framesDirty = false;
		
		// 1. Build up an empty list of all frames
		allFrames.clear();
		
		if (quantityFrames < 0) return;
		
		for (int frame = 0; frame < quantityFrames; frame++) {
			AnimationFrame keyframe = new AnimationFrame(false);
			keyframe.setFrameNumber(frame);
			
			for (Element elem : project.rootElements) {
				keyframe.AddElementDirectly(createEmptyFrameForElement(elem, frame));
			}
			
			allFrames.add(frame, keyframe);
		}
		
		
		// 2. Fill in with the interpolated values that we have available
		
		// - Loop through all Key frames
		//   - Loop through all key frame elements
		//     - Loop through all 3 data groups (position, rotation, stretch)
		//       - Get next key frame. Interpolate all frames between current and next keyframe.
		
		for (int i = 0; i < keyframes.length; i++) {
			for (IDrawable drawable : keyframes[i].Elements) {
				AnimFrameElement prevkelem = (AnimFrameElement)drawable;
				
				lerpKeyFrameElement(i, prevkelem);
			}
		}
		
		ReloadFrameNumbers();
	}
	

	
	void lerpKeyFrameElement(int keyFrameIndex, AnimFrameElement curKelem) {
		
		for (int flag = 0; flag < 3; flag++) {
			if (!curKelem.IsSet(flag)) continue;
			
			AnimFrameElement nextkelem = getNextKeyFrameElementForFlag(keyFrameIndex, curKelem.AnimatedElement, flag);

			int startFrame;
			int frames;

			if (nextkelem == null || curKelem == nextkelem) {
				startFrame = 0;
				frames = quantityFrames;
				nextkelem = curKelem;
			} else {
				startFrame = curKelem.FrameNumber;
				frames = nextkelem.FrameNumber - curKelem.FrameNumber;
				if (frames < 0) frames = nextkelem.FrameNumber + (quantityFrames - curKelem.FrameNumber);
			}
			
			
			for (int x = 0; x < frames; x++) {
				int frame = (startFrame + x) % quantityFrames;				
				AnimFrameElement kelem = allFrames.get(frame).GetAnimFrameElementRec(curKelem.AnimatedElement);
				if (kelem == null) {
					System.out.println("kelem for frame " + frame + " is null, will crash.");
					kelem = allFrames.get(frame).GetAnimFrameElementRec(curKelem.AnimatedElement);
				}
				lerpKeyFrameElement(kelem, curKelem, nextkelem, flag, x);
			}
		}
		
		
		for (IDrawable childKelem : curKelem.ChildElements) {
			lerpKeyFrameElement(keyFrameIndex, (AnimFrameElement)childKelem);
		}
	}
	
	
	AnimFrameElement getNextKeyFrameElementForFlag(int index, Element forElement, int forFlag) {
		AnimationFrame nextkeyframe;
		
		int j = index + 1;
		int tries = keyframes.length;
		while (tries-- > 0) {
			nextkeyframe = keyframes[j % keyframes.length];
			
			AnimFrameElement kelem = nextkeyframe.GetAnimFrameElementRec(forElement);
			if (kelem != null && kelem.IsSet(forFlag)) {
				return kelem;
			}
				
			j++;
		}	
		
		return null;
	}
	
	
	AnimFrameElement createEmptyFrameForElement(Element element, int frameNumber) {

		AnimFrameElement kelem = new AnimFrameElement(element, false);
		kelem.FrameNumber = frameNumber;
		
		for (Element child : element.ChildElements) {
			AnimFrameElement childKeyFrameElem = createEmptyFrameForElement(child, frameNumber);
			childKeyFrameElem.ParentElement = kelem;
			
			kelem.ChildElements.add(childKeyFrameElem);
		}
		
		for (Element child : element.StepChildElements) {
			AnimFrameElement childKeyFrameElem = createEmptyFrameForElement(child, frameNumber);
			childKeyFrameElem.StepParentElement = kelem;
			
			kelem.StepChildElements.add(childKeyFrameElem);
		}
				
		return kelem;
	}
	
	
	void lerpKeyFrameElement(AnimFrameElement kElem, AnimFrameElement prev, AnimFrameElement next, int forFlag, int relativeFrame) { 
		if (prev == null && next == null) return;
		
		double t = 0;
		
		if (prev != next) {
			double frames = next.FrameNumber - prev.FrameNumber;
			if (frames < 0) frames = next.FrameNumber + (quantityFrames - prev.FrameNumber);
			
			t = relativeFrame / frames;	
		}
		
		if (forFlag == 0) {
			kElem.setOffsetX(lerp(t, prev.getOffsetX(), next.getOffsetX()));
			kElem.setOffsetY(lerp(t, prev.getOffsetY(), next.getOffsetY()));
			kElem.setOffsetZ(lerp(t, prev.getOffsetZ(), next.getOffsetZ()));		
			kElem.PositionSet = true;
		} else if(forFlag == 1) {			
			kElem.setRotationX(lerp(t, prev.getRotationX(), next.getRotationX()));
			kElem.setRotationY(lerp(t, prev.getRotationY(), next.getRotationY()));
			kElem.setRotationZ(lerp(t, prev.getRotationZ(), next.getRotationZ()));
			kElem.RotationSet = true;
		} else {
			kElem.setStretchX(lerp(t, prev.getRotationX(), next.getRotationX()));
			kElem.setStretchY(lerp(t, prev.getRotationY(), next.getRotationY()));
			kElem.setStretchZ(lerp(t, prev.getRotationZ(), next.getRotationZ()));
			kElem.StretchSet = true;
		}
	}
	
	
	
	
	double lerp(double t, double v0, double v1) {
		return v0 + t * (v1 - v0);
	}

	
	public void SetQuantityFrames(int quantity) {
		quantityFrames = quantity;
		SetFramesDirty();
		ModelCreator.DidModify();
	}
	
	public int GetQuantityFrames() {
		return quantityFrames;
	}
	
	public void NextFrame() {
		if (quantityFrames == 0) return;
		currentFrame = (currentFrame + 1) % quantityFrames;
	}
	
	public void PrevFrame() {
		if (quantityFrames == 0) return;
		
		currentFrame = mod(currentFrame - 1, quantityFrames);
	}
	
	public AnimFrameElement TogglePosition(Element elem, boolean on) {
		AnimFrameElement keyframe = GetOrCreateKeyFrameElement(currentFrame, elem);
		
		if (keyframe.PositionSet == on) return keyframe;
		
		keyframe.PositionSet = on;
		ModelCreator.ignoreDidModify = true;
		if (!on) RemoveKeyFramesIfUseless(keyframe);
		ModelCreator.ignoreDidModify = false;
		ModelCreator.DidModify();
		
		return keyframe;
	}

	public AnimFrameElement ToggleRotation(Element elem, boolean on) {
		AnimFrameElement keyframe = GetOrCreateKeyFrameElement(currentFrame, elem);
		
		if (keyframe.RotationSet == on) return keyframe;
		
		keyframe.RotationSet = on;
		ModelCreator.ignoreDidModify = true;
		if (!on) RemoveKeyFramesIfUseless(keyframe);
		ModelCreator.ignoreDidModify = false;
		ModelCreator.DidModify();
		
		return keyframe;
	}

	public AnimFrameElement ToggleStretch(Element elem, boolean on) {
		AnimFrameElement keyframe = GetOrCreateKeyFrameElement(currentFrame, elem);
		keyframe.StretchSet = on;
		
		if (keyframe.StretchSet == on) return keyframe;
		ModelCreator.ignoreDidModify = true;
		if (!on) RemoveKeyFramesIfUseless(keyframe);
		ModelCreator.ignoreDidModify = false;
		ModelCreator.DidModify();
		
		return keyframe;
	}
	
	
	public void RemoveKeyFramesIfUseless(AnimFrameElement keyframe) {
		if (keyframe.IsUseless()) {
			RemoveKeyFrameElement(keyframe, currentFrame);
			Element parentElem;
			while ((parentElem = keyframe.AnimatedElement.ParentElement) != null) {
				AnimFrameElement parentKf = GetKeyFrameElement(parentElem, currentFrame);
				if (parentKf != null && parentKf.IsUseless()) {
					RemoveKeyFrameElement(parentKf, currentFrame);
				} else break;
			}
		}
	}
	
	
	public AnimationFrame GetOrCreateAnimationFrame(int frame) {
		AnimationFrame keyframe = GetKeyFrame(frame);
		
		if (keyframe == null) {
			keyframe = new AnimationFrame(true);
			keyframe.setFrameNumber(frame);
			
			// Grow array by 1. Insert new keyframe at the right spot
			if (keyframes.length == 0) {
				 keyframes = new AnimationFrame[] { keyframe };
			} else {
				AnimationFrame[] newkeyframes = new AnimationFrame[keyframes.length + 1];
				int j = 0;
				boolean inserted = false;
				for (int i = 0; i < keyframes.length; i++) {
					if (inserted || keyframes[i].getFrameNumber() < frame) {
						newkeyframes[j++] = keyframes[i];
					} else {
						newkeyframes[j++] = keyframe;
						i--;
						inserted = true;
					}
				}
				
				if (!inserted) {
					newkeyframes[j++] = keyframe;
				}
				
				keyframes = newkeyframes;				
			}
			
			ReloadFrameNumbers();
		}		
		
		return keyframe;
	}

	
	public AnimFrameElement GetOrCreateKeyFrameElement(int frame, Element elem) {
		AnimationFrame keyframe = GetOrCreateAnimationFrame(frame);		
		return keyframe.GetOrCreateKeyFrameElementFlat(frame, elem);
	}
	
	
	public AnimFrameElement GetKeyFrameElement(Element elem, int forFrame) {
		AnimationFrame keyframe = GetKeyFrame(forFrame);		
		if (keyframe != null) return keyframe.GetKeyFrameElementFlat(elem);
		return null;
	}
	
	
	public AnimationFrame GetKeyFrame(int frameNumber) {
		if (keyframes == null) return null;
		
		for (int i = 0; i < keyframes.length; i++) {
			if (keyframes[i].getFrameNumber() == frameNumber) return keyframes[i];
		}
		
		return null;
	}
	
	
	public void ReloadFrameNumbers() {
		frameNumbers = new int[keyframes.length];
		
		for (int i = 0; i < keyframes.length; i++) {
			frameNumbers[i] = keyframes[i].getFrameNumber();
		}
	}


	public void SetFrame(int frameNumber)
	{
		currentFrame = frameNumber;
	}

	public void RemoveKeyFrameElement(Element curElem)
	{
		for (int i = 0; i < keyframes.length; i++) {
			AnimFrameElement kelem = keyframes[i].GetKeyFrameElementFlat(curElem);
			if (kelem == null) {
				continue;
			}
			
			if (RemoveKeyFrameElement(kelem, keyframes[i])) {
				i--;
			}
		}
	}
	
	

	private void RemoveKeyFrameElement(AnimFrameElement keyframeelem, int forFrame)
	{
		AnimationFrame keyframe = GetKeyFrame(forFrame);
		
		if (keyframe == null) {
			return;
		}
		
		RemoveKeyFrameElement(keyframeelem, keyframe);
	}
	
	
	private boolean RemoveKeyFrameElement(AnimFrameElement keyframeelem, AnimationFrame keyframe)
	{	
		keyframe.RemoveElement(keyframeelem);
		
		if (!keyframe.HasElements()) {
			RemoveKeyFrame(keyframe);
			return true;
		}
		
		return false;
	}
	
	
	public void RemoveKeyFrame(AnimationFrame keyframe) {
		// Shrink array by 1
		AnimationFrame[] newkeyframes = new AnimationFrame[keyframes.length - 1];
		
		int j = 0;
		for (int i = 0; i < keyframes.length; i++) {
			if (keyframes[i] != keyframe) newkeyframes[j++] = keyframes[i];
		}
		
		keyframes = newkeyframes;
		
		ReloadFrameNumbers();
	}

	
	public void ResolveRelations(Project project)
	{
		ReloadFrameNumbers();
		
		for (int i = 0; i < keyframes.length; i++) {
			AnimationFrame keyframe = keyframes[i];
			
			for (IDrawable kElem : keyframe.Elements) {
				ResolveElem(project, keyframe, (AnimFrameElement)kElem);
			}
		}
	}


	private void ResolveElem(Project project, AnimationFrame keyframe, AnimFrameElement kElem)
	{
		kElem.AnimatedElement = project.findElement(kElem.AnimatedElementName);
		
		if (kElem.AnimatedElement == null) {
			System.out.println("Couldn't resolve animated element name " + kElem.AnimatedElementName);
		}
		
		kElem.FrameNumber = keyframe.getFrameNumber();
		
		for (IDrawable childElem : kElem.ChildElements) {
			((AnimFrameElement)childElem).ParentElement = kElem;
			((AnimFrameElement)childElem).FrameNumber = keyframe.getFrameNumber();
			
			ResolveElem(project, keyframe, (AnimFrameElement)childElem);
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
		ModelCreator.DidModify();
	}
	
	public String getCode()
	{
		if (code == null || code == "null") return name.toLowerCase(); 
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
		ModelCreator.DidModify();
	}

	public void MoveSelectedFrame(int direction)
	{
		ModelCreator.ignoreDidModify = true;
		
		AnimationFrame curFrame = null;
		AnimationFrame prevFrame = null;
		AnimationFrame nextFrame = null;
		
		for (int i = 0; i < keyframes.length; i++) {
			if (keyframes[i].getFrameNumber() == this.currentFrame) {
				curFrame = keyframes[i];
				prevFrame = keyframes[mod(i-1, keyframes.length)];
				nextFrame = keyframes[mod(i+1, keyframes.length)];
			}
		}
		
		int nextFrameNumber = mod(curFrame.getFrameNumber() + direction, quantityFrames);
		
		
		if (direction > 0 && curFrame != nextFrame && curFrame.getFrameNumber() < nextFrame.getFrameNumber()) {
			nextFrameNumber = Math.min(curFrame.getFrameNumber() + direction, nextFrame.getFrameNumber() - 1);
		}

		if (direction < 0 && curFrame != prevFrame && prevFrame.getFrameNumber() < curFrame.getFrameNumber()) {
			nextFrameNumber = Math.max(curFrame.getFrameNumber() + direction, prevFrame.getFrameNumber() + 1);
		}
		
		
		
		curFrame.setFrameNumber(nextFrameNumber);
		this.currentFrame = nextFrameNumber;
		ReloadFrameNumbers();
		
		ModelCreator.ignoreDidModify = false;
		
		ModelCreator.DidModify();
		ModelCreator.updateValues(null);
	}

	public void DeleteCurrentFrame()
	{
		RemoveKeyFrame(GetKeyFrame(currentFrame));
		ReloadFrameNumbers();
		ModelCreator.DidModify();
		ModelCreator.updateValues(null);
	}

	public boolean IsCurrentFrameKeyFrame()
	{
		return GetKeyFrame(currentFrame) != null;
	}

	private int mod(int x, int y)
	{
	    int result = x % y;
	    return result < 0? result + y : result;
	}
	
	
	public Animation clone(boolean withElementReference) {
		Animation cloned = new Animation();
		
		cloned.name = name;
		cloned.quantityFrames = quantityFrames;
		cloned.code = code;
		cloned.OnActivityStopped = OnActivityStopped;
		cloned.OnAnimationEnd = OnAnimationEnd;
		
		cloned.keyframes = new AnimationFrame[keyframes.length];
		
		for (int i = 0; i < keyframes.length; i++) {
			cloned.keyframes[i] = keyframes[i].clone(withElementReference);
		}
		
		
		return cloned;
	}

	public void InsertKeyFrame(AnimationFrame sourceFrame)
	{
		AnimationFrame[] newKeyframes = new AnimationFrame[keyframes.length + 1];
		
		int j = 0;
		for (int i = 0; i < keyframes.length; i++) {
			if (sourceFrame != null && sourceFrame.getFrameNumber() < keyframes[i].getFrameNumber()) {
				newKeyframes[j++] = sourceFrame;
				sourceFrame = null;
				i--;
			} else {
				newKeyframes[j++] = keyframes[i];
			}
		}
		if (sourceFrame != null) newKeyframes[j++] = sourceFrame;
		
		
		this.keyframes = newKeyframes;
		ReloadFrameNumbers();
		ModelCreator.DidModify();
		ModelCreator.updateValues(null);
	}

	public void loadKeyFramesIntoProject(Project project)
	{	
		Animation hisanim = project.findAnimation(this.name);
		if (hisanim == null) return;

		for (AnimationFrame mykeyframe : keyframes) {
			AnimationFrame hiskeyframe = hisanim.GetOrCreateAnimationFrame(mykeyframe.getFrameNumber());
			
			for (IDrawable mydrawable : mykeyframe.Elements) {
				AnimFrameElement myFrameEle = (AnimFrameElement)mydrawable;
				
				AnimFrameElement hisFrameEle = hiskeyframe.GetKeyFrameElementFlat(myFrameEle.AnimatedElement);
				if (hisFrameEle == null) hiskeyframe.Elements.add(myFrameEle);
			}
		}
	}
	
}