package at.vintagestory.modelcreator;

import java.util.ArrayList;
import java.util.List;

import at.vintagestory.modelcreator.gui.right.ElementTree;
import at.vintagestory.modelcreator.gui.right.RightTopPanel;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.model.*;

public class Project
{
	// Persistent project data
	public boolean AmbientOcclusion;
	public ArrayList<PendingTexture> PendingTextures = new ArrayList<PendingTexture>();
	public ArrayList<TextureEntry> Textures = new ArrayList<TextureEntry>();
	public ArrayList<Element> RootElements = new ArrayList<Element>();
	public ArrayList<Animation> Animations = new ArrayList<Animation>();
	
	
	// Non-persistent project data 
	public Element SelectedElement;
	public Animation SelectedAnimation;
	public boolean PlayAnimation = false;
	public ElementTree tree;
	
	
	public Project() {
		if (ModelCreator.manager != null) {
			tree = ((RightTopPanel)ModelCreator.manager).tree;	
		}
		
	}
	
	public void LoadIntoEditor(IElementManager manager)
	{
		clear();
		
		for (PendingTexture ptex : PendingTextures) {
			manager.addPendingTexture(ptex);	
		}		
	}



	
	public int getSelectedAnimationIndex()
	{
		for (int i = 0; i < Animations.size(); i++) {
			if (Animations.get(i) == SelectedAnimation) return i;
		}
		
		return -1;
	}
	
	public KeyFrameElement getSelectedKeyFrameElement() {
		if (SelectedAnimation == null || SelectedElement == null) return null;
		
		return SelectedAnimation.GetOrCreateKeyFrameElement(SelectedElement);
	}
	
	public Keyframe GetKeyFrame(int frameNumber) {
		if (SelectedAnimation == null) return null;
		return SelectedAnimation.KeyFrames[frameNumber];
	}
	
	public KeyFrameElement GetKeyFrameElement(Element elem, int frameNumber) {
		if (SelectedAnimation == null) return null;
		Keyframe keyFrame = SelectedAnimation.KeyFrames[frameNumber];
		if (keyFrame == null) return null;
		
		return keyFrame.GetKeyFrameElement(elem);
	}
	
	public int[] GetFrameNumbers() {
		if (SelectedAnimation == null) return null;
		return SelectedAnimation.FrameNumbers;
	}
	
	public int GetKeyFrameCount() {
		if (SelectedAnimation == null) return 0;
		return SelectedAnimation.FrameNumbers == null ? 0 : SelectedAnimation.FrameNumbers.length;
	}

	public int GetFrameCount()
	{
		if (SelectedAnimation == null) return 0;
		return SelectedAnimation.GetQuantityFrames();
	}




	public List<Element> getAnimatedRootElements()
	{
		return RootElements;	
	}
	
	

	public void addElementAsChild(Element elem)
	{
		tree.addElementAsChild(elem);
		if (elem.ParentElement == null) {
			RootElements.add(elem);
		}		
		SelectedElement = elem;
		ModelCreator.updateValues();
		tree.updateUI();
	}
	
	public void addRootElement(Element e)
	{
		RootElements.add(e);
		tree.addRootElement(e);
		SelectedElement = tree.getSelectedElement();
		ModelCreator.updateValues();
		tree.jtree.updateUI();
	}
	
	public void duplicateCurrentElement() {
		Element elem = tree.getSelectedElement();
		if (elem != null) {
			Element newElem = new Element(elem);
			tree.addElementAsSibling(newElem);
			tree.SelectElement(newElem);
			
			if (newElem.ParentElement == null) {
				RootElements.add(newElem);
			}
		}
		
		SelectedElement = tree.getSelectedElement();
		ModelCreator.updateValues();
	}
	
	public void removeCurrentElement() {
		Element curElem = SelectedElement;
		tree.removeCurrentElement();
		
		if (curElem.ParentElement == null) {
			RootElements.remove(curElem);
		}
		
		SelectedElement = tree.getSelectedElement();
		ModelCreator.updateValues();
	}

	public void clear()
	{
		AmbientOcclusion = true;
		RootElements.clear();
		Animations.clear();
		SelectedElement = null;
		PendingTextures.clear();
		Textures.clear();
		tree.clearElements();
		ModelCreator.updateValues();
	}
	
	public void selectElementByOpenGLName(int pos)
	{
		tree.selectElementByOpenGLName(pos);
		SelectedElement = tree.getSelectedElement();
		ModelCreator.updateValues();
	}

	

	
	
}
