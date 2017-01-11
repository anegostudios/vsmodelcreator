package at.vintagestory.modelcreator.model;

import java.util.ArrayList;

import at.vintagestory.modelcreator.util.Vec3f;

public class Animation
{
	// Persistent project data
	int quantityFrames;
	public String Name;
	
	public Keyframe[] KeyFrames = new Keyframe[0];
	public int[] FrameNumbers = new int[0];
	
	//public LinkedHashMap<Integer, ArrayList<KeyFrameElement>> KeyFrames = new LinkedHashMap<Integer, ArrayList<KeyFrameElement>>();
	
	// Non-persistent project data 
	public int CurrentFrame;
	
	
	public void Export() {
		
	}
	
	
	public void Import() {
		
	}
	
	
	public void SetQuantityFrames(int quantity) {
		quantityFrames = quantity;
		
		Keyframe[] newKeyFrames = new Keyframe[quantity];
		for (int i = 0; i < Math.min(quantity, KeyFrames.length); i++) {
			newKeyFrames[i] = KeyFrames[i];
		}
		
		this.KeyFrames = newKeyFrames;
	}
	
	public int GetQuantityFrames() {
		return quantityFrames;
	}
	
	public void NextFrame() {
		if (quantityFrames == 0) return;
		CurrentFrame = (CurrentFrame + 1) % quantityFrames;
	}
	
	public void PrevFrame() {
		if (quantityFrames == 0) return;
		CurrentFrame = (CurrentFrame - 1) % quantityFrames;
	}
	
	public void TogglePosition(Element elem, boolean on) {
		KeyFrameElement keyframe = GetOrCreateKeyFrameElement(elem);
		keyframe.PositionSet = on;
		if (keyframe.IsUseless()) RemoveKeyFrameElement(keyframe);
	}

	public void ToggleRotation(Element elem, boolean on) {
		KeyFrameElement keyframe = GetOrCreateKeyFrameElement(elem);
		keyframe.RotationSet = on;
		if (keyframe.IsUseless()) RemoveKeyFrameElement(keyframe);
	}

	public void ToggleStretch(Element elem, boolean on) {
		KeyFrameElement keyframe = GetOrCreateKeyFrameElement(elem);
		keyframe.StretchSet = on;
		if (keyframe.IsUseless()) RemoveKeyFrameElement(keyframe);
	}

	
	public void SetPosition(Element elem, Vec3f position) {
		KeyFrameElement keyframe = GetOrCreateKeyFrameElement(elem);
		keyframe.setStartX(position.X);
		keyframe.setStartY(position.Y);
		keyframe.setStartZ(position.Z);
	}
	
	public void SetRotation(Element elem, Vec3f xyzRotation) {
		KeyFrameElement keyframe = GetOrCreateKeyFrameElement(elem);
		keyframe.setRotationX(xyzRotation.X);
		keyframe.setRotationY(xyzRotation.Y);
		keyframe.setRotationZ(xyzRotation.Z);
	}
	
	public void SetStretch(Element elem, Vec3f stretch) {
		KeyFrameElement keyframe = GetOrCreateKeyFrameElement(elem);
		keyframe.setRotationX(stretch.X);
		keyframe.setRotationY(stretch.Y);
		keyframe.setRotationZ(stretch.Z);
	}
	

	private void RemoveKeyFrameElement(KeyFrameElement keyframeelem)
	{
		Keyframe keyframe = KeyFrames.length > 0 ? KeyFrames[CurrentFrame] : null;
		
		if (keyframe == null) {
			keyframe = new Keyframe();
			KeyFrames[CurrentFrame] = keyframe;
		}
		
		keyframe.RemoveElement(keyframeelem);
		if (!keyframe.HasElements()) {
			KeyFrames[CurrentFrame] = null;
			ReloadFrameNumbers();
		}
	}
	
	public KeyFrameElement GetOrCreateKeyFrameElement(Element elem) {
		Keyframe keyframe = KeyFrames.length > 0 ? KeyFrames[CurrentFrame] : null;
		
		if (keyframe == null) {
			keyframe = new Keyframe();
			KeyFrames[CurrentFrame] = keyframe;
			ReloadFrameNumbers();
		}
		
		KeyFrameElement keyframeElem = keyframe.GetKeyFrameElement(elem);
		if (keyframeElem != null) {
			return keyframeElem;
		}
		
		keyframeElem = new KeyFrameElement(elem);
		keyframe.AddElement(keyframeElem);
		
		return keyframeElem;
	}
	
	
	public KeyFrameElement GetKeyFrameElement(Element elem) {
		if (KeyFrames.length == 0) return null;
		
		Keyframe keyframe = KeyFrames[CurrentFrame];
		
		if (keyframe == null) {
			return null;
		}
		
		return keyframe.GetKeyFrameElement(elem);
	}
	
	
	void ReloadFrameNumbers() {
		ArrayList<Integer> nums = new ArrayList<Integer>();
		
		for (int i = 0; i < KeyFrames.length; i++) {
			if (KeyFrames[i] != null) nums.add(i);
		}
		
		FrameNumbers = new int[nums.size()];
		for (int i = 0; i < nums.size(); i++) {
			FrameNumbers[i] = nums.get(i);
		}
	}
	

	
}
