package at.vintagestory.modelcreator;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
	public ArrayList<TextureEntry> Textures = new ArrayList<TextureEntry>();
	public ArrayList<Element> rootElements = new ArrayList<Element>();
	public ArrayList<Animation> Animations = new ArrayList<Animation>();
	
	public int TextureWidth = 32;
	public int TextureHeight = 32;
	public boolean SingleTexture;
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
			manager.addPendingTexture(ptex);	
		}
		
		ModelCreator.ignoreValueUpdates = true;
		tree.clearElements();
		ModelCreator.ignoreValueUpdates = false;
		
		for (Element elem : rootElements) {
			tree.addRootElement(elem);
		}
		
		tree.SelectElement(SelectedElement);
		
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
		
		if (SelectedAnimation.allFrames.size() == 0) {
			SelectedAnimation.calculateAllFrames(this);
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
		ModelCreator.updateValues();
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
		ModelCreator.updateValues();
		tree.jtree.updateUI();
	}
	
	
	public void duplicateCurrentElement() {
		if (SelectedElement != null) {
			Element newElem = new Element(SelectedElement);
			newElem.ParentElement = SelectedElement.ParentElement;
			EnsureUniqueElementName(newElem);
			
			tree.addElementAsSibling(newElem);
			tree.SelectElement(newElem);
			
			if (newElem.ParentElement == null) {
				rootElements.add(newElem);
			}
		}
		
		SelectedElement = tree.getSelectedElement();
		ModelCreator.DidModify();
		ModelCreator.updateValues();
	}
	
	
	public void removeCurrentElement() {
		Element curElem = SelectedElement;
		tree.removeCurrentElement();
		
		if (curElem.ParentElement == null) {
			rootElements.remove(curElem);
		}
		
		if (SelectedAnimation != null) SelectedAnimation.RemoveElement(curElem);
		
		SelectedElement = tree.getSelectedElement();
		ModelCreator.DidModify();
		ModelCreator.updateValues();
	}
	

	public void clear()
	{
		ModelCreator.ignoreValueUpdates = true;
		AmbientOcclusion = true;
		rootElements.clear();
		Animations.clear();
		SelectedElement = null;
		PendingTextures.clear();
		Textures.clear();
		tree.clearElements();
		SelectedAnimation = null;
		ModelCreator.ignoreValueUpdates = false;
		ModelCreator.updateValues();
	}
	
	
	public void selectElementByOpenGLName(int pos)
	{
		tree.selectElementByOpenGLName(pos);
		SelectedElement = tree.getSelectedElement();
		ModelCreator.updateValues();
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
	


	public Project clone()
	{
		Project cloned = new Project(filePath);
		cloned.AmbientOcclusion = AmbientOcclusion;
		cloned.Textures = Textures;
		/*for (TextureEntry entry : Textures) {
			cloned.Textures.add(entry.clone());
		}*/
		
		for (Element elem : rootElements) {
			cloned.rootElements.add(elem.clone());
		}
		
		for (Animation anim : Animations) {
			cloned.Animations.add(anim.clone());
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
		for (TextureEntry entry : Textures)
		{
			if (entry.getName().equalsIgnoreCase(name))
			{
				return entry;
			}
		}
		return null;
	}

	public Texture getTexture(String name)
	{
		for (TextureEntry entry : Textures)
		{
			if (entry.getName().equalsIgnoreCase(name))
			{
				return entry.getTexture();
			}
		}
		return null;
	}

	public String getTextureLocation(String name)
	{
		for (TextureEntry entry : Textures)
		{
			if (entry.getName().equalsIgnoreCase(name))
			{
				return entry.getFilePath();
			}
		}
		return null;
	}
	

	public ImageIcon getIcon(String name)
	{
		for (TextureEntry entry : Textures)
		{
			if (entry.getName().equalsIgnoreCase(name))
			{
				return entry.getIcon();
			}
		}
		return null;
	}
	

	
	public void reloadTextures(ModelCreator creator) {
		for (TextureEntry entry : ModelCreator.currentProject.Textures) {
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
	

	public String loadTexture(File image) throws IOException
	{
		FileInputStream is = new FileInputStream(image);
		Texture texture = TextureLoader.getTexture("PNG", is);
		is.close();

		if (texture.getImageHeight() % 16 != 0 || texture.getImageWidth() % 16 != 0)
		{
			texture.release();
			return "Not a multiple of 16 ("+texture.getImageHeight()+","+texture.getImageWidth()+")";
		}
		
		ImageIcon icon = upscaleIcon(new ImageIcon(image.getAbsolutePath()), 256);
		
		ModelCreator.currentProject.Textures.add(new TextureEntry(image.getName().replace(".png", ""), texture, icon, image.getAbsolutePath()));
		
		if (ModelCreator.currentProject.Textures.size() == 1 && SingleTexture) {
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
