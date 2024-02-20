package at.vintagestory.modelcreator;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.opengl.renderer.SGL;

import at.vintagestory.modelcreator.gui.right.ElementTree;
import at.vintagestory.modelcreator.gui.right.RightPanel;
import at.vintagestory.modelcreator.interfaces.IDrawable;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.model.*;

public class Project
{
	// Persistent project data
	public boolean AmbientOcclusion;
	public ArrayList<PendingTexture> PendingTextures = new ArrayList<PendingTexture>();
	public LinkedHashMap<String, TextureEntry> TexturesByCode = new LinkedHashMap<String, TextureEntry>();
	public LinkedHashMap<String, int[]> TextureSizes = new LinkedHashMap<String, int[]>();
	public LinkedHashMap<String, String> MissingTexturesByCode = new LinkedHashMap<String, String>();
	
	public ArrayList<Element> rootElements = new ArrayList<Element>();
	public ArrayList<Animation> Animations = new ArrayList<Animation>();
	
	public int TextureWidth = 16;
	public int TextureHeight = 16;
	public boolean EntityTextureMode;
	public boolean AllAngles;
	public String backDropShape;
	
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
		
		if (ModelCreator.rightTopPanel != null) {
			tree = ((RightPanel)ModelCreator.rightTopPanel).tree;	
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
			SelectedAnimation.SetFramesDirty();
		}
		
		if (backDropShape != null) {
			if (new File(backDropShape + ".json").exists()) {
				ModelCreator.Instance.LoadBackdropFile(backDropShape + ".json");
			} else {
				String shapeBasePath = ModelCreator.prefs.get("shapePath", ".");
				String path = shapeBasePath + File.separator + backDropShape + ".json";
				if (new File(path).exists()) {
					ModelCreator.Instance.LoadBackdropFile(path);
				} else {
					
					JFileChooser chooser = new JFileChooser(ModelCreator.prefs.get("shapePath", "."));
					chooser.setDialogTitle("Back drop shape file not found, select desired back drop shape file");
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					
					int returnVal = chooser.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION)
					{
						ModelCreator.Instance.LoadBackdropFile(chooser.getSelectedFile().getAbsolutePath());
					}					
				}
			}
		}
	}	
	
	public int getSelectedAnimationIndex()
	{
		for (int i = 0; i < Animations.size(); i++) {
			if (Animations.get(i) == SelectedAnimation) return i;
		}
		
		return -1;
	}
	
	public AnimationFrame GetKeyFrame(int frameNumber) {
		if (SelectedAnimation == null) return null;
		return SelectedAnimation.keyframes[frameNumber];
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
		AnimationFrame keyFrame = SelectedAnimation.keyframes[SelectedAnimation.currentFrame];
		if (keyFrame == null) return;
	}

	public List<IDrawable> getCurrentFrameRootElements()
	{
		if (SelectedAnimation == null || SelectedAnimation.keyframes.length == 0) return new ArrayList<IDrawable>(rootElements);
		
		if (SelectedAnimation.allFrames.size() == 0 || SelectedAnimation.currentFrame >= SelectedAnimation.allFrames.size()) {
			SelectedAnimation.SetFramesDirty();
			SelectedAnimation.currentFrame = Math.max(0, Math.min(SelectedAnimation.currentFrame - 1, SelectedAnimation.allFrames.size()));
			ModelCreator.updateFrame();
		}
		
		return SelectedAnimation.allFrames.get(SelectedAnimation.currentFrame).Elements;
	}
	
	public int CurrentAnimVersion() {
		if (SelectedAnimation == null || SelectedAnimation.keyframes.length == 0) return 0;
		
		return SelectedAnimation.version;
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
		ModelCreator.ignoreDidModify = true;
		
		if (SelectedElement != null) {
			Element newElem = new Element(SelectedElement);
			newElem.ParentElement = SelectedElement.ParentElement;
			if (newElem.ParentElement != null) {
				newElem.ParentElement.ChildElements.add(newElem);
			}

			if (newElem.ParentElement == null) {
				rootElements.add(newElem);
			}
			
			EnsureUniqueElementName(newElem);
			
			tree.addElementAsSibling(newElem);
			tree.selectElement(newElem);
			
		}
		
		ModelCreator.ignoreDidModify = false;
		
		SelectedElement = tree.getSelectedElement();
		ModelCreator.DidModify();
		ModelCreator.updateValues(null);
		
		ModelCreator.reloadStepparentRelationShips();
	}
	
	
	public void removeCurrentElement() {
		ModelCreator.ignoreDidModify = true;
		
		Element curElem = SelectedElement;
		Element nextElem = tree.getNextSelectedElement();
		
		tree.removeCurrentElement();
		
		if (curElem.ParentElement == null) {
			rootElements.remove(curElem);
		}
		
		for (int i = 0; i < Animations.size(); i++) {
			Animations.get(i).RemoveKeyFrameElement(curElem);
			
			if (Animations.get(i) == SelectedAnimation) {
				Animations.get(i).SetFramesDirty();	
			}
		}
		
		curElem.onRemoved();
		
		ModelCreator.ignoreDidModify = false;
		
		if (nextElem != null) {
			tree.selectElement(nextElem);
		}
		
		SelectedElement = tree.getSelectedElement();
		ModelCreator.DidModify();
		ModelCreator.updateValues(null);
		
		ModelCreator.reloadStepparentRelationShips();
	}
	

	public void clear()
	{
		ModelCreator.ignoreValueUpdates = true;
		AmbientOcclusion = true;
		rootElements.clear();
		Animations.clear();
		SelectedElement = null;
		PendingTextures.clear();
		for (TextureEntry entry : TexturesByCode.values()) {
			entry.Dispose();
		}
		TexturesByCode.clear();
		tree.clearElements();
		SelectedAnimation = null;
		ModelCreator.ignoreValueUpdates = false;
		ModelCreator.updateValues(null);
	}
	
	
	public void selectElementAndFaceByOpenGLName(int openGlName)
	{
		tree.selectElementByOpenGLName(openGlName);
		SelectedElement = tree.getSelectedElement();
		
		if (SelectedElement != null) {
			for (int i = 0; i < SelectedElement.getAllFaces().length; i++) {
				if (SelectedElement.getAllFaces()[i].openGlName == openGlName) {
					SelectedElement.setSelectedFace(i);
					break;
				}
			}
		}
		
		ModelCreator.updateValues(null);
	}
	
	public void selectElement(Element element)
	{
		tree.selectElement(element);
		SelectedElement = tree.getSelectedElement();
		ModelCreator.updateValues(null);
	}
	
	
	void EnsureUniqueElementName(Element elem) {
		String numberStr = "";
		int pos = elem.getName().length() - 1;
		while (pos > 0) {
			if (Character.isDigit(elem.getName().charAt(pos))) {
				numberStr = elem.getName().charAt(pos) + numberStr;
			} else break;
			pos--;
		}
		String baseName = elem.getName().substring(0, elem.getName().length() - numberStr.length());

		if (numberStr.length() == 0) numberStr = "1";
		int nextNumber = Integer.parseInt(numberStr) + 1;

		String newName = baseName + nextNumber;
		while (IsElementNameUsed(newName, elem)) {
			newName = baseName + nextNumber;
			nextNumber++;
		}
		elem.setName(newName);
		
		for (Element childElem : elem.ChildElements) {
			EnsureUniqueElementName(childElem);
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
			
			if (elem.getName().equals(name)) return true;
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
			if (elem.getName().equals(elementName)) return elem;
			
			Element foundElem = findElement(elementName, elem.ChildElements);
			if (foundElem != null) return foundElem;
		}
		
		return null;
	}
	


	public Project clone(boolean withElementReference)
	{
		Project cloned = new Project(filePath);
		cloned.AmbientOcclusion = AmbientOcclusion;
		cloned.TexturesByCode = TexturesByCode;
		cloned.AllAngles = AllAngles;
		cloned.EntityTextureMode = EntityTextureMode;
		cloned.TextureWidth = TextureWidth;
		cloned.TextureHeight = TextureHeight;
		
		for (String key : TextureSizes.keySet()) {
			cloned.TextureSizes.put(key, TextureSizes.get(key));
		}
		
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
		
	

	public void UpdateTextureCode(String oldCode, String newCode) {
		TextureEntry entry = TexturesByCode.get(oldCode);
		if (entry == null) return;
		
		TexturesByCode.remove(oldCode);
		TexturesByCode.put(newCode, entry);
		entry.code = newCode;
		UpdateTextureCode(rootElements, oldCode, newCode);
		
		ModelCreator.DidModify();
		ModelCreator.updateTitle();
	}
	
	void UpdateTextureCode(ArrayList<Element> elems, String oldCode, String newCode) {
		for (int i = 0; i < elems.size(); i++) {
			Element elem = elems.get(i);
			
			for (Face face : elem.getAllFaces()) {
				if (oldCode.equals(face.getTextureCode())) {
					face.setTextureCode(newCode);
				}
			}
			
			if (elem.ChildElements != null) {
				UpdateTextureCode(elem.ChildElements, oldCode, newCode);
			}
			
		}
	}

	public TextureEntry getTextureEntryByCode(String code)
	{
		return TexturesByCode.get(code);
	}

	public Texture getTextureByCode(String code)
	{
		TextureEntry entry = getTextureEntryByCode(code);
		if (entry == null) return null;
		
		return entry.getTexture();
	}
	
	

	public String getTextureFilepathByCode(String code)
	{
		TextureEntry entry = getTextureEntryByCode(code);
		if (entry == null) return null;
		return entry.getFilePath();
	}
	

	public ImageIcon getIconByCode(String code)
	{
		TextureEntry entry = getTextureEntryByCode(code);
		if (entry == null) return null;
		return entry.getIcon();
	}
	

	
	public void reloadTextures(ModelCreator creator) {
		for (TextureEntry entry : TexturesByCode.values()) {
			try {
				creator.pendingTextures.add(new PendingTexture(entry, 0));
			} catch (Exception e) {}
		}
	}
	
	
	public void reloadExternalTexture(TextureEntry entry) throws IOException {
		FileInputStream is = new FileInputStream(entry.getFilePath());
		Texture texture = TextureLoader.getTexture("PNG", is);
		is.close();
		
		if (texture.getImageHeight() % 8 != 0 | texture.getImageWidth() % 8 != 0)
		{
			texture.release();
			return;
		}
		
		entry.icon = upscaleIcon(new ImageIcon(entry.getFilePath()), 256);
		entry.texture = texture;
	}
	

	public String loadTexture(String textureCode, File image, BooleanParam isNew, boolean fromBackdrop, boolean doReplaceAll, boolean doReplacedForSelectedElement) throws IOException
	{
		FileInputStream is = new FileInputStream(image);
		Texture texture;
		try {
			texture = TextureLoader.getTexture("PNG", is);
		} catch (Throwable e) {
			return "Unabled to load this texture, is this a valid png file?";
		}
		
		texture.setTextureFilter(SGL.GL_NEAREST);
		is.close();

		if (texture.getImageHeight() % 8 != 0 || texture.getImageWidth() % 8 != 0)
		{
			texture.release();
			return "Cannot load this texture, the width or length is not a multiple of 8 ("+texture.getImageHeight()+"x"+texture.getImageWidth()+")";
		}
		
		
		ImageIcon icon = upscaleIcon(new ImageIcon(image.getAbsolutePath()), 256);
		
		if (textureCode == null) {
			textureCode = image.getName().replace(".png", "");
		}
		
		ArrayList<String> nowFoundTextures = new ArrayList<String>(); 
		
		if (!TexturesByCode.containsKey(textureCode)) {
			// Try and match by filename if not matched by code
			for (String key : MissingTexturesByCode.keySet()) {
				String val = MissingTexturesByCode.get(key);
				String filename = val.substring(val.lastIndexOf("/")+1);
				if (filename.equalsIgnoreCase(textureCode)) {
					TexturesByCode.put(key, new TextureEntry(key, texture, icon, image.getAbsolutePath(), fromBackdrop));
					nowFoundTextures.add(key);
				}	
			}
		}
		
		if (nowFoundTextures.size() == 0) {
			
			if (TexturesByCode.containsKey(textureCode)) {
				TextureEntry entry = TexturesByCode.get(textureCode);
				if (entry.getFilePath().equalsIgnoreCase(image.getAbsolutePath())) {
					TexturesByCode.put(textureCode, new TextureEntry(textureCode, texture, icon, image.getAbsolutePath(), fromBackdrop));
				} else {
					
					int i = 2;
					while (true) {
						
						String altimgName = textureCode + i;
						if (!TexturesByCode.containsKey(altimgName)) {
							TexturesByCode.put(altimgName, new TextureEntry(altimgName, texture, icon, image.getAbsolutePath(), fromBackdrop));
							textureCode = altimgName;
							break;
						}					
						i++;
					}
				}
				
				isNew.Value = false;
			} else {
				isNew.Value = true;
				TexturesByCode.put(textureCode, new TextureEntry(textureCode, texture, icon, image.getAbsolutePath(), fromBackdrop));	
			}			
		} else {
			
			isNew.Value = true;
			
			for (String key : nowFoundTextures) {
				MissingTexturesByCode.remove(key);
			}			
		}
		
		if (doReplaceAll || (doReplacedForSelectedElement && SelectedElement != null)) {
			ModelCreator.changeHistory.beginMultichangeHistoryState();
		
			if (doReplaceAll) {
				for (Element elem : rootElements) {
					elem.setTextureCode(textureCode, true);
				}
			}
			if (doReplacedForSelectedElement && SelectedElement != null) {
				SelectedElement.setTextureCode(textureCode, true);
			}
			ModelCreator.DidModify();
			ModelCreator.changeHistory.endMultichangeHistoryState(this);
		}
		
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public ImageIcon upscaleIcon(ImageIcon source, int length)
	{
		Image img = source.getImage();
		Image newimg = img.getScaledInstance(length, -1, java.awt.Image.SCALE_FAST);
		return new ImageIcon(newimg);
	}




	public void setIsBackdrop()
	{
		for (Element elem : rootElements) {
			elem.setIsBackdrop();
		}

		for (PendingTexture tex : PendingTextures) {
			tex.SetIsBackDrop();
			ModelCreator.Instance.AddPendingTexture(tex);
		}
	}

	public void clearStepparentRelationShips() {
		for (Element elem : rootElements) {
			elem.clearStepparentRelationShip();
		}
	}
	
	public void reloadStepparentRelationShips() {
		for (Element elem : rootElements) {
			elem.reloadStepparentRelationShip();
		}
	}
	

	public void ReduceDecimals() {
		ModelCreator.changeHistory.beginMultichangeHistoryState();
		
		for (Element elem : rootElements) {
			elem.reduceDecimals();
		}
		
		ModelCreator.DidModify();
		ModelCreator.updateValues(null);
		ModelCreator.changeHistory.endMultichangeHistoryState(this);
	}
	

	public void TryGenSnowLayer()
	{
		ModelCreator.changeHistory.beginMultichangeHistoryState();
		
		for (Element elem : rootElements) {
			TryGenSnowLayer(elem);
		}

		if (!TexturesByCode.containsKey("snowcover")) {
			loadSnowTexture();
		}
		
		
		ModelCreator.DidModify();
		ModelCreator.updateValues(null);

		ModelCreator.ignoreValueUpdates = true;
		tree.clearElements();
		ModelCreator.ignoreValueUpdates = false;
		
		for (Element elem : rootElements) {
			tree.addRootElement(elem);
		}
		
		tree.selectElement(SelectedElement);
		
		ModelCreator.changeHistory.endMultichangeHistoryState(this);
	}

	

	private void TryGenSnowLayer(Element elem)
	{
		for (Element celem : elem.ChildElements) {
			if (celem.getName().contains("-snow")) return;
			
			TryGenSnowLayer(celem);
		}
	
		if (Math.abs(elem.getRotationX()) < 15 && Math.abs(elem.getRotationZ()) < 15) {
			if (!elem.getAllFaces()[4].isEnabled()) return;
			if (elem.getName().contains("-snow")) return;
			
			Element snowElem = elem.clone();
			snowElem.ChildElements.clear();
			snowElem.setRotationX(0);
			snowElem.setRotationY(0);
			snowElem.setRotationZ(0);
			snowElem.setStartX(0);
			snowElem.setStartZ(0);
			snowElem.setStartY(elem.getHeight() + 0.01);
			snowElem.setHeight(2);
			snowElem.setName(elem.getName() + "-snow");
			snowElem.setAutoUnwrap(true);
			snowElem.updateUV();
			
			for (int i = 0; i < 6; i++) {
				snowElem.getAllFaces()[i].setTextureCode("snowcover");
			}
			
			elem.ChildElements.add(snowElem);
		}
	}
	
	private void loadSnowTexture()
	{
		File textureFile = new File("block"+ File.separator +"snow"+ File.separator +"normal1.png");

		if (textureFile.exists() && textureFile.isFile())
		{
			synchronized (ModelCreator.Instance.pendingTextures) {
				ModelCreator.Instance.pendingTextures.add(new PendingTexture("snowcover", textureFile, 0));
			}
			return;
		}

		
		String textureBasePath = ModelCreator.prefs.get("texturePath", ".");
		File f = new File(textureBasePath + File.separator + "block"+ File.separator +"snow"+ File.separator +"normal1.png");
		
		if (f.exists())
		{
			synchronized (ModelCreator.Instance.pendingTextures) {
				ModelCreator.Instance.pendingTextures.add(new PendingTexture("snowcover", f, 0));
			}
			return;
		}
	}


	public int countTriangles()
	{
		int cnt = 0;
		for (Element elem : rootElements) {
			cnt += elem.countTriangles();
		}
		
		return cnt;
	}


	public void attachToBackdropProject(Project backDropProject)
	{
		insertStepChildren(rootElements, backDropProject);
		
		for (Animation anim : Animations) {
			anim.loadKeyFramesIntoProject(backDropProject);
		}
	}


	private void insertStepChildren(ArrayList<Element> myElements, Project backDropProject)
	{
		for (Element myElem : myElements) {
			if (myElem.stepparentName != null) {
				insertStepChild(myElem, backDropProject);
			}
			
			insertStepChildren(myElem.ChildElements, backDropProject);
		}
	}


	private void insertStepChild(Element myElem, Project backDropProject)
	{
		Element hisElem = backDropProject.findElement(myElem.stepparentName);
		if (hisElem != null) {
			myElem.ParentElement = hisElem;
			if (!hisElem.StepChildElements.contains(myElem)) {
				hisElem.StepChildElements.add(myElem);
			}
		}
	}

}
