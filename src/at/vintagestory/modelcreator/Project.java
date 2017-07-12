package at.vintagestory.modelcreator;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.swing.ImageIcon;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import at.vintagestory.modelcreator.gui.right.ElementTree;
import at.vintagestory.modelcreator.gui.right.RightTopPanel;
import at.vintagestory.modelcreator.interfaces.IDrawable;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.model.*;

public class Project
{
	// Persistent project data
	public boolean AmbientOcclusion;
	public ArrayList<PendingTexture> PendingTextures = new ArrayList<PendingTexture>();
	public HashMap<String, TextureEntry> Textures = new HashMap<String, TextureEntry>();
	public ArrayList<Element> rootElements = new ArrayList<Element>();
	public ArrayList<Animation> Animations = new ArrayList<Animation>();
	
	public int TextureWidth = 32;
	public int TextureHeight = 32;
	public boolean EntityTextureMode;
	public boolean AllAngles;
	
	// Non-persistent project data
	public AttachmentPoint SelectedAttachmentPoint;
	public Element SelectedElement;
	public Animation SelectedAnimation;
	public boolean PlayAnimation = false;
	public ElementTree tree;
	public boolean needsSaving;
	
	public static int nextAttachmentPointNumber = 1;
	
	public String filePath;
	
	
	public Project(String filePath) {
		this.filePath = filePath;
		
		if (ModelCreator.manager != null) {
			tree = ((RightTopPanel)ModelCreator.manager).tree;	
		}	
	}
	
	
	public void LoadIntoEditor(IElementManager manager)
	{
		for (Animation anim : Animations) {
			anim.ResolveRelations(this);
		}
		
		for (PendingTexture ptex : PendingTextures) {
			ModelCreator.Instance.AddPendingTexture(ptex);	
		}
		
		ModelCreator.ignoreValueUpdates = true;
		tree.clearElements();
		ModelCreator.ignoreValueUpdates = false;
		
		for (Element elem : rootElements) {
			tree.addRootElement(elem);
		}
		
		tree.selectElement(SelectedElement);
		
		if (Animations.size() > 0) {
			SelectedAnimation = Animations.get(0);
			SelectedAnimation.calculateAllFrames(this);
		}
		
	}	
	
	public int getSelectedAnimationIndex()
	{
		for (int i = 0; i < Animations.size(); i++) {
			if (Animations.get(i) == SelectedAnimation) return i;
		}
		
		return -1;
	}
	
	public KeyframeElement getSelectedKeyFrameElement() {
		if (SelectedAnimation == null || SelectedElement == null) return null;
		
		return SelectedAnimation.GetOrCreateKeyFrameElement(SelectedElement);
	}
	
	
	public Keyframe GetKeyFrame(int frameNumber) {
		if (SelectedAnimation == null) return null;
		return SelectedAnimation.keyframes[frameNumber];
	}
	
	
	public KeyframeElement GetKeyFrameElement(Element elem, int index) {
		if (SelectedAnimation == null) return null;		
		Keyframe keyFrame = SelectedAnimation.keyframes[index];
		if (keyFrame == null) return null;
		
		return keyFrame.GetKeyFrameElement(elem);
	}
	
	
	public int[] GetFrameNumbers() {
		if (SelectedAnimation == null) return null;
		return SelectedAnimation.frameNumbers;
	}
	
	
	public int GetKeyFrameCount() {
		if (SelectedAnimation == null) return 0;
		return SelectedAnimation.frameNumbers == null ? 0 : SelectedAnimation.frameNumbers.length;
	}

	
	public int GetFrameCount()
	{
		if (SelectedAnimation == null) return 0;
		return SelectedAnimation.GetQuantityFrames();
	}



	public void calculateCurrentFrameElements() {
		if (SelectedAnimation == null) return;
		Keyframe keyFrame = SelectedAnimation.keyframes[SelectedAnimation.currentFrame];
		if (keyFrame == null) return;
		
		
	}

	public List<IDrawable> getCurrentFrameRootElements()
	{
		if (SelectedAnimation == null || SelectedAnimation.keyframes.length == 0) return new ArrayList<IDrawable>(rootElements);
		
		if (SelectedAnimation.allFrames.size() == 0 || SelectedAnimation.currentFrame >= SelectedAnimation.allFrames.size()) {
			SelectedAnimation.calculateAllFrames(this);
			SelectedAnimation.currentFrame = Math.max(0, Math.min(SelectedAnimation.currentFrame - 1, SelectedAnimation.allFrames.size()));
			ModelCreator.updateFrame();
		}
		
		return SelectedAnimation.allFrames.get(SelectedAnimation.currentFrame).Elements;
	}
	
	

	public void addElementAsChild(Element elem)
	{
		elem.ParentElement = SelectedElement;
		EnsureUniqueElementName(elem);
		
		if (elem.ParentElement == null) {
			rootElements.add(elem);
		}
		
		tree.addElementAsChild(elem);
		SelectedElement = elem;
		
		ModelCreator.DidModify();
		ModelCreator.updateValues(null);
		tree.updateUI();
	}
	
	
	public void addRootElement(Element e)
	{
		e.ParentElement = null;
		EnsureUniqueElementName(e);
		
		rootElements.add(e);
		tree.addRootElement(e);
		SelectedElement = tree.getSelectedElement();
		
		ModelCreator.DidModify();
		ModelCreator.updateValues(null);
		tree.jtree.updateUI();
	}
	
	
	public void duplicateCurrentElement() {
		if (SelectedElement != null) {
			Element newElem = new Element(SelectedElement);
			newElem.ParentElement = SelectedElement.ParentElement;
			EnsureUniqueElementName(newElem);
			
			tree.addElementAsSibling(newElem);
			tree.selectElement(newElem);
			
			if (newElem.ParentElement == null) {
				rootElements.add(newElem);
			}
		}
		
		SelectedElement = tree.getSelectedElement();
		ModelCreator.DidModify();
		ModelCreator.updateValues(null);
	}
	
	
	public void removeCurrentElement() {
		Element curElem = SelectedElement;
		tree.removeCurrentElement();
		
		if (curElem.ParentElement == null) {
			rootElements.remove(curElem);
		}
		
		if (SelectedAnimation != null) {
			SelectedAnimation.RemoveElement(curElem);
			SelectedAnimation.calculateAllFrames(this);
		}
		
		SelectedElement = tree.getSelectedElement();
		ModelCreator.DidModify();
		ModelCreator.updateValues(null);
		
	}
	

	public void clear()
	{
		ModelCreator.ignoreValueUpdates = true;
		AmbientOcclusion = true;
		rootElements.clear();
		Animations.clear();
		SelectedElement = null;
		PendingTextures.clear();
		for (TextureEntry entry : Textures.values()) {
			entry.Dispose();
		}
		Textures.clear();
		tree.clearElements();
		SelectedAnimation = null;
		ModelCreator.ignoreValueUpdates = false;
		ModelCreator.updateValues(null);
	}
	
	
	public void selectElementByOpenGLName(int pos)
	{
		tree.selectElementByOpenGLName(pos);
		SelectedElement = tree.getSelectedElement();
		ModelCreator.updateValues(null);
	}
	
	public void selectElement(Element element)
	{
		tree.selectElement(element);
		SelectedElement = tree.getSelectedElement();
		ModelCreator.updateValues(null);
	}
	

	void EnsureUniqueElementName(Element elem) {
		IntRef tq = new IntRef();
		EnsureUniqueElementName(elem, tq);
	}
	
	
	void EnsureUniqueElementName(Element elem, IntRef totalQuantityElems) {
		if (IsElementNameUsed(elem.name, elem)) {
			
			String numberStr = "";
			int pos = elem.name.length() - 1;
			while (pos > 0) {
				if (Character.isDigit(elem.name.charAt(pos))) {
					numberStr = elem.name.charAt(pos) + numberStr;
				} else break;
				pos--;
			}
			
			int nextNumber = TotalQuantityElements() + 1 + totalQuantityElems.value;
			String baseName = elem.name.substring(0, elem.name.length() - numberStr.length());
			
			elem.name = baseName + nextNumber;
			while(IsElementNameUsed(elem.name, elem)) {
				nextNumber++;
				elem.name = baseName + nextNumber;
			}
			
			totalQuantityElems.value++;
		}
		
		for (Element childElem : elem.ChildElements) {
			EnsureUniqueElementName(childElem, totalQuantityElems);
		}
	}

	
	public boolean IsElementNameUsed(String name, Element exceptElement) {
		return IsElementNameUsed(name, rootElements, exceptElement);
	}
	
	public boolean IsAttachmentPointCodeUsed(String code, AttachmentPoint exceptPoint)
	{
		return IsAttachmentPointCodeUsed(code, rootElements, exceptPoint);	
	}
	
	public boolean IsAttachmentPointCodeUsed(String code, ArrayList<Element> elems, AttachmentPoint exceptPoint) {
		for (Element elem : elems) {
			if (elem.isAttachmentPointCodeUsed(code, exceptPoint)) return true;
		}
		return false;
	}

	
	boolean IsElementNameUsed(String name, ArrayList<Element> elems, Element exceptElement) {
		for (Element elem : elems) {
			if (elem == exceptElement) continue;
			
			if (elem.name.equals(name)) return true;
			if (IsElementNameUsed(name, elem.ChildElements, exceptElement)) return true;
		}
		
		return false;
	}
	
	
	int TotalQuantityElements() {
		return TotalQuantityElements(rootElements);
	}
	
	
	int TotalQuantityElements(ArrayList<Element> elems) {
		int quantity = 0;
		for (Element elem : elems) {
			quantity++;
			quantity+= TotalQuantityElements(elem.ChildElements);
		}
		return quantity;
	}
	
	
	public Element findElement(String elementName) {
		return findElement(elementName, rootElements);
	}
	
	public Element findElement(String elementName, List<Element> list) {
		for (Element elem : list) {
			if (elem.name.equals(elementName)) return elem;
			
			Element foundElem = findElement(elementName, elem.ChildElements);
			if (foundElem != null) return foundElem;
		}
		
		return null;
	}
	


	public Project clone(boolean withElementReference)
	{
		Project cloned = new Project(filePath);
		cloned.AmbientOcclusion = AmbientOcclusion;
		cloned.Textures = Textures;
		cloned.AllAngles = AllAngles;
		cloned.EntityTextureMode = EntityTextureMode;
		cloned.TextureWidth = TextureWidth;
		cloned.TextureHeight = TextureHeight;
		
		for (Element elem : rootElements) {
			cloned.rootElements.add(elem.clone());
		}
		
		for (Animation anim : Animations) {
			cloned.Animations.add(anim.clone(withElementReference));
		}
		
		cloned.needsSaving = needsSaving;
		
		
		return cloned;
	}


	public Animation findAnimation(String name)
	{
		for (int i = 0; i < Animations.size(); i++) {
			if (Animations.get(i).getName().equals(name)) return Animations.get(i);
		}
		return null;
	}
		
	


	public TextureEntry getTextureEntry(String name)
	{
		return Textures.get(name);
	}

	public Texture getTexture(String name)
	{
		TextureEntry entry = getTextureEntry(name);
		if (entry == null) return null;
		
		return entry.getTexture();
	}

	public String getTextureLocation(String name)
	{
		TextureEntry entry = getTextureEntry(name);
		if (entry == null) return null;
		return entry.getFilePath();
	}
	

	public ImageIcon getIcon(String name)
	{
		TextureEntry entry = getTextureEntry(name);
		if (entry == null) return null;
		return entry.getIcon();
	}
	

	
	public void reloadTextures(ModelCreator creator) {
		for (TextureEntry entry : Textures.values()) {
			try {
				creator.pendingTextures.add(new PendingTexture(entry));
			} catch (Exception e) {}
		}
	}
	
	
	public void reloadExternalTexture(TextureEntry entry) throws IOException {
		FileInputStream is = new FileInputStream(entry.getFilePath());
		Texture texture = TextureLoader.getTexture("PNG", is);
		is.close();
		
		if (texture.getImageHeight() % 16 != 0 | texture.getImageWidth() % 16 != 0)
		{
			texture.release();
			return;
		}
		
		entry.icon = upscaleIcon(new ImageIcon(entry.getFilePath()), 256);
		entry.textures = Arrays.asList(texture);
	}
	

	public String loadTexture(String textureName, File image) throws IOException
	{
		FileInputStream is = new FileInputStream(image);
		Texture texture = TextureLoader.getTexture("PNG", is);
		is.close();

		if (texture.getImageHeight() % 16 != 0 || texture.getImageWidth() % 16 != 0)
		{
			texture.release();
			return "Cannot load this texture, the width or length is not a multiple of 16 ("+texture.getImageHeight()+"x"+texture.getImageWidth()+")";
		}
		
		ImageIcon icon = upscaleIcon(new ImageIcon(image.getAbsolutePath()), 256);
		
		if (textureName == null) {
			textureName = image.getName().replace(".png", "");	
		}
		
		if (EntityTextureMode) {
			for(TextureEntry entry : Textures.values()) entry.Dispose();
			Textures.clear();
		}
		
		if (Textures.containsKey(textureName)) {
			TextureEntry entry = Textures.get(textureName);
			if (entry.getFilePath().equals(image.getAbsolutePath())) {
				Textures.put(textureName, new TextureEntry(textureName, texture, icon, image.getAbsolutePath()));
			} else {
				
				int i = 2;
				while (true) {
					
					String altimgName = textureName + i;
					if (!Textures.containsKey(altimgName)) {
						Textures.put(altimgName, new TextureEntry(altimgName, texture, icon, image.getAbsolutePath()));
						break;
					}					
					i++;
				}
			}
			
			
		} else {
			Textures.put(textureName, new TextureEntry(textureName, texture, icon, image.getAbsolutePath()));	
		}
		
		
		
		if (Textures.size() == 1 && EntityTextureMode) {
			applySingleTextureMode();
		}
		
		return null;
	}

	public ImageIcon upscaleIcon(ImageIcon source, int length)
	{
		Image img = source.getImage();
		Image newimg = img.getScaledInstance(length, -1, java.awt.Image.SCALE_FAST);
		return new ImageIcon(newimg);
	}


	public void applySingleTextureMode()
	{
		for (Element elem : rootElements) {
			elem.applySingleTextureMode();
		}
	}




}
