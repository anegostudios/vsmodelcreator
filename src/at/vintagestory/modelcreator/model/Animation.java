package at.vintagestory.modelcreator.model;

import java.util.ArrayList;
import java.util.List;

import at.vintagestory.modelcreator.Project;
import at.vintagestory.modelcreator.interfaces.IDrawable;
import at.vintagestory.modelcreator.util.Vec3f;

public class Animation
{
	// Persistent project data
	int quantityFrames;
	public String name;
	
	public Keyframe[] keyframes = new Keyframe[0];
	public int[] frameNumbers = new int[0];
	
	
	
	// Non-persistent project data 
	public int currentFrame;
	public ArrayList<Keyframe> allFrames = new ArrayList<Keyframe>();
	
	
	public Animation() {
		quantityFrames = 30;
	}
	
	
	public void Export() {
		
	}
	
	
	public void Import() {
		
	}
	


	
	public void calculateAllFrames(Project project) {
		// We'll use a simple, slightly memory intensive but cpu friendly solution
		// Static models are tree hierarchies of boxes
		// So let's just built up one complete static model for each frame (only storing they keyframe data, but still a tree hierarchy and referencing the static box)
		// simply select the right one for a given frame, and add up static model value with keyframe value
		
		
		//System.out.println("calc all frames");
		
		// 1. Build up an empty list of all frames
		allFrames.clear();
		
		for (int frame = 0; frame < quantityFrames; frame++) {
			Keyframe keyframe = new Keyframe();
			keyframe.FrameNumber = frame;
			
			for (Element elem : project.rootElements) {
				keyframe.AddElement(createEmptyFrameForElement(elem, frame));
			}
			
			allFrames.add(frame, keyframe);
		}
		
		
		// 2. Fill in with the interpolated values that we have available
		
		// - Loop through all Key frames
		//   - Loop through all key frame elements
		//     - Loop through all 3 data groups (position, rotation, stretch)
		//       - Get next frame. Interpolate all frames betweeen current and next frame.
		
		for (int i = 0; i < keyframes.length; i++) {
			for (IDrawable drawable : keyframes[i].Elements) {
				KeyframeElement prevkelem = (KeyframeElement)drawable;
				
				lerkKeyFrameElement(i, prevkelem);
			}
		}
		
		//System.out.println("calc all frames done");
	}
	
	// Bug prevkelem might have no relevancy at this point in time
	void lerkKeyFrameElement(int keyFrameIndex, KeyframeElement prevkelem) {
		//System.out.println("lerp key frame element " + prevkelem.AnimatedElement.name + " for frame " + prevkelem.FrameNumber);
		
		for (int flag = 0; flag < 3; flag++) {
			if (!prevkelem.IsSet(flag)) continue;
			
			KeyframeElement nextkelem = getNextKeyFrameElement(keyFrameIndex, prevkelem.AnimatedElement, flag);

			int startFrame;
			int frames;

			if (nextkelem == null || prevkelem == nextkelem) {
				startFrame = 0;
				frames = quantityFrames;
				nextkelem = prevkelem;
			} else {
				startFrame = prevkelem.FrameNumber;
				frames = nextkelem.FrameNumber - prevkelem.FrameNumber;
				if (frames < 0) frames = nextkelem.FrameNumber + (quantityFrames - prevkelem.FrameNumber);
			}
			
			//System.out.println("lerp frames " + (startFrame) + " - " + (startFrame + frames));
			
			for (int x = 0; x < frames; x++) {
				int frame = (startFrame + x) % quantityFrames;
				
				KeyframeElement kelem = allFrames.get(frame).GetKeyFrameElement(prevkelem.AnimatedElement);
				lerpKeyFrameElement(kelem, prevkelem, nextkelem, flag, x);
			}
		}
		
		
		for (IDrawable childKelem : prevkelem.ChildElements) {
			lerkKeyFrameElement(keyFrameIndex, (KeyframeElement)childKelem);
		}
	}
	
	
	KeyframeElement getNextKeyFrameElement(int index, Element forElement, int forFlag) {
		Keyframe nextkeyframe;
		
		int j = index + 1;
		int tries = keyframes.length;
		while (tries-- > 0) {
			nextkeyframe = keyframes[j % keyframes.length];
			
			KeyframeElement kelem = nextkeyframe.GetKeyFrameElement(forElement);
			if (kelem != null && kelem.IsSet(forFlag)) {
				return kelem;
			}
				
			j++;
		}	
		
		return null;
	}
	
	
	KeyframeElement createEmptyFrameForElement(Element element, int frameNumber) { 
		KeyframeElement elem = new KeyframeElement(element);
		elem.FrameNumber = frameNumber;
		
		for (Element child : element.ChildElements) {
			KeyframeElement childKeyFrameElem = createEmptyFrameForElement(child, frameNumber);
			childKeyFrameElem.ParentElement = elem;
			
			elem.ChildElements.add(childKeyFrameElem);
		}
		
		return elem;
	}
	
	
	void lerpKeyFrameElement(KeyframeElement keyFrameElem, KeyframeElement prev, KeyframeElement next, int forFlag, int relativeFrame) { 
		if (prev == null && next == null) return;
		
		double t = 0;
		
		if (prev != next) {
			double frames = next.FrameNumber - prev.FrameNumber;
			if (frames < 0) frames = next.FrameNumber + (quantityFrames - prev.FrameNumber);
			
			t = relativeFrame / frames;	
		}
		
		if (forFlag == 0) {
			keyFrameElem.offsetX = lerp(t, prev.offsetX, next.offsetX);
			keyFrameElem.offsetY = lerp(t, prev.offsetY, next.offsetY);
			keyFrameElem.offsetZ = lerp(t, prev.offsetZ, next.offsetZ);		
			keyFrameElem.PositionSet = true;
		} else if(forFlag == 1) {			
			keyFrameElem.rotationX = lerp(t, prev.rotationX, next.rotationX);
			keyFrameElem.rotationY = lerp(t, prev.rotationY, next.rotationY);
			keyFrameElem.rotationZ = lerp(t, prev.rotationZ, next.rotationZ);
			keyFrameElem.RotationSet = true;
		} else {
			keyFrameElem.stretchX = lerp(t, prev.rotationX, next.rotationX);
			keyFrameElem.stretchY = lerp(t, prev.rotationY, next.rotationY);
			keyFrameElem.stretchZ = lerp(t, prev.rotationZ, next.rotationZ);
			keyFrameElem.StretchSet = true;
		}
	}
	
	
	
	
	double lerp(double t, double v0, double v1) {
		return v0 + t * (v1 - v0);
	}

	
	public void SetQuantityFrames(int quantity, Project project) {
		quantityFrames = quantity;
		calculateAllFrames(project);
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
		currentFrame = (currentFrame - 1) % quantityFrames;
	}
	
	public void TogglePosition(Element elem, boolean on) {
		KeyframeElement keyframe = GetOrCreateKeyFrameElement(elem);
		keyframe.PositionSet = on;
		if (keyframe.IsUseless()) RemoveKeyFrameElement(keyframe, currentFrame);
	}

	public void ToggleRotation(Element elem, boolean on) {
		KeyframeElement keyframe = GetOrCreateKeyFrameElement(elem);
		keyframe.RotationSet = on;
		if (keyframe.IsUseless()) RemoveKeyFrameElement(keyframe, currentFrame);
	}

	public void ToggleStretch(Element elem, boolean on) {
		KeyframeElement keyframe = GetOrCreateKeyFrameElement(elem);
		keyframe.StretchSet = on;
		if (keyframe.IsUseless()) RemoveKeyFrameElement(keyframe, currentFrame);
	}

	
	public void SetOffset(Element elem, Vec3f position) {
		KeyframeElement keyframe = GetOrCreateKeyFrameElement(elem);
		keyframe.offsetX = position.X;
		keyframe.offsetY = position.Y;
		keyframe.offsetZ = position.Z;
	}
	
	public void SetRotation(Element elem, Vec3f xyzRotation) {
		KeyframeElement keyframe = GetOrCreateKeyFrameElement(elem);
		keyframe.rotationX = xyzRotation.X;
		keyframe.rotationY = xyzRotation.Y;
		keyframe.rotationZ = xyzRotation.Z;
	}
	
	public void SetStretch(Element elem, Vec3f stretch) {
		KeyframeElement keyframe = GetOrCreateKeyFrameElement(elem);
		keyframe.stretchX = stretch.X;
		keyframe.stretchY = stretch.Y;
		keyframe.stretchZ = stretch.Z;
	}


	
	public KeyframeElement GetOrCreateKeyFrameElement(Element elem) {
		Keyframe keyframe = GetKeyFrame(currentFrame);
		
		if (keyframe == null) {
			keyframe = new Keyframe();
			keyframe.FrameNumber = currentFrame;
			
			// Grow array by 1. Insert new keyframe at the right spot
			if (keyframes.length == 0) {
				 keyframes = new Keyframe[] { keyframe };
			} else {
				Keyframe[] newkeyframes = new Keyframe[keyframes.length + 1];
				int j = 0;
				boolean inserted = false;
				for (int i = 0; i < keyframes.length; i++) {
					if (inserted || keyframes[i].FrameNumber < currentFrame) {
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
		
		return GetOrCreateKeyFrameElement(elem, keyframe);
	}
	
	
	
	public KeyframeElement GetOrCreateKeyFrameElement(Element forElem, Keyframe keyframe) { 
		KeyframeElement keyframeElem = keyframe.GetKeyFrameElement(forElem);
		
		if (keyframeElem != null) {
			return keyframeElem;
		}
		
		List<Element> path = forElem.GetParentPath();
		
		
		if (path.size() == 0) {
			keyframeElem = new KeyframeElement(forElem);
			keyframe.AddElement(keyframeElem);	
			
		} else if (path.size() == 1) {
			KeyframeElement parent = GetOrCreateKeyFrameElement(path.get(0), keyframe);
			keyframeElem = parent.GetOrCreateChildElement(forElem);
			
		} else {
			KeyframeElement parent = GetOrCreateKeyFrameElement(path.get(0), keyframe);
			path.remove(0);
			
			while (path.size() > 0) {
				Element childElem = path.get(0);
				path.remove(0);
				keyframeElem = parent.GetOrCreateChildElement(childElem);
				parent = keyframeElem;
			}
			
			keyframeElem = keyframeElem.GetOrCreateChildElement(forElem);
		}
		
		keyframeElem.FrameNumber = currentFrame;
		
		return keyframeElem;
	}
	
	
	
	public KeyframeElement GetKeyFrameElement(Element elem, int forFrame) {
		Keyframe keyframe = GetKeyFrame(forFrame);		
		if (keyframe != null) return keyframe.GetKeyFrameElement(elem);
		return null;
	}
	
	public Keyframe GetKeyFrame(int frameNumber) {
		if (keyframes == null) return null;
		
		for (int i = 0; i < keyframes.length; i++) {
			if (keyframes[i].FrameNumber == frameNumber) return keyframes[i];
		}
		
		return null;
	}
	
	
	void ReloadFrameNumbers() {
		frameNumbers = new int[keyframes.length];
		
		for (int i = 0; i < keyframes.length; i++) {
			frameNumbers[i] = keyframes[i].FrameNumber;
		}
	}


	public void SetFrame(int frameNumber)
	{
		currentFrame = frameNumber;
	}

	public void RemoveElement(Element curElem)
	{
		for (int i = 0; i < keyframes.length; i++) {
			KeyframeElement kelem = keyframes[i].GetKeyFrameElement(curElem);
			if (RemoveKeyFrameElement(kelem, keyframes[i])) {
				i--;
			}
		}
	}
	
	

	private void RemoveKeyFrameElement(KeyframeElement keyframeelem, int forFrame)
	{
		Keyframe keyframe = GetKeyFrame(forFrame);
		
		if (keyframe == null) {
			return;
		}
		
		RemoveKeyFrameElement(keyframeelem, keyframe);
	}
	
	
	private boolean RemoveKeyFrameElement(KeyframeElement keyframeelem, Keyframe keyframe)
	{	
		keyframe.RemoveElement(keyframeelem);
		
		if (!keyframe.HasElements()) {
			
			// Shrink array by 1
			Keyframe[] newkeyframes = new Keyframe[keyframes.length - 1];
			
			int j = 0;
			for (int i = 0; i < keyframes.length; i++) {
				if (keyframes[i] != keyframe) newkeyframes[j++] = keyframes[i];
			}
			
			keyframes = newkeyframes;
			
			ReloadFrameNumbers();
			
			return true;
		}
		
		return false;
	}
	
	
}
