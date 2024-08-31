package at.vintagestory.modelcreator.gui.left;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glVertex2i;

import java.util.ArrayList;
import java.util.Stack;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureImpl;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.enums.EnumFonts;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.Face;
import at.vintagestory.modelcreator.model.RenderFaceTask;
import at.vintagestory.modelcreator.model.Sized;
import at.vintagestory.modelcreator.model.TextureEntry;

public class LeftUVSidebar extends LeftSidebar
{
	private IElementManager manager;

	private static int DEFAULT_WIDTH = 4*32;

	private final Color BLACK_ALPHA = new Color(0, 0, 0, 0.75F);

	private int[] startX = { 0, 0, 0, 0, 0, 0 };
	private int[] startY = { 0, 0, 0, 0, 0, 0 };
	
	
	private int texBoxWidth, texBoxHeight;
	
	float[] brightnessByFace = new float[] { 1, 1, 1, 1, 1, 1 }; 
	
	int canvasHeight;
	
	Stack<RenderFaceTask> renderLastStack = new Stack<RenderFaceTask>();
	Stack<RenderFaceTask> renderLastStack2 = new Stack<RenderFaceTask>();
	
	
	public LeftUVSidebar(String title, IElementManager manager)
	{
		super(title);
		this.manager = manager;
		
		blockFaceTextureSize = DEFAULT_WIDTH;
	}
	
	@Override
	public void onResized()
	{
		/*if (!ModelCreator.currentProject.EntityTextureMode) {
			int extraWidth = GetSidebarWidth() - DEFAULT_WIDTH + 20;
			if (canvasHeight < 6*(10 + DEFAULT_WIDTH + extraWidth)) {
				extraWidth /= 2;
			}
			
			blockFaceTextureWidth = DEFAULT_WIDTH + extraWidth;
		}*/
	}

	@Override
	public void draw(int sidebarWidth, int canvasWidth, int canvasHeight, int frameHeight)
	{
		super.draw(sidebarWidth, canvasWidth, canvasHeight, frameHeight);

		this.canvasHeight = canvasHeight;


		if (ModelCreator.transparent) {
			GL11.glEnable(GL11.GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		}

		
		if (ModelCreator.currentProject.rootElements.size() == 0) return;

		if (ModelCreator.currentProject.EntityTextureMode) {
			drawRectsEntityTextureMode(canvasHeight);
		} else {
			drawRectsBlockTextureMode(canvasHeight);
		}


		if (ModelCreator.transparent) {
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
	
	ArrayList<String> getTextureCodes() {
		ArrayList<String> codes = new ArrayList<String>();
		
		String prioTextureCode=null;
		if (ModelCreator.currentProject.SelectedElement != null) {
			Face face = ModelCreator.currentProject.SelectedElement.getSelectedFace();
			if (face != null) prioTextureCode = face.getTextureCode();
			if (prioTextureCode != null && ModelCreator.currentProject.TexturesByCode.containsKey(prioTextureCode)) {
				codes.add(prioTextureCode);
			}
		}
		
		for (String textureCode : ModelCreator.currentProject.TexturesByCode.keySet()) {
			if (prioTextureCode != null && prioTextureCode.equals(textureCode)) continue;
			codes.add(textureCode);
		}
		
		return codes;
	}
	
	
	void drawRectsEntityTextureMode(int canvasHeight) {
		
		if (!Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)) {
			grabbedElement = currentHoveredElementEntityTextureMode(ModelCreator.currentProject.rootElements);
		}			
		
		if (ModelCreator.currentProject.TexturesByCode.size() > 0) {
			int offsetY = 0;
			
			glPushMatrix();
			
			for (String textureCode : getTextureCodes()) {
				offsetY = drawRectsAndTexture(textureCode);				
				glTranslatef(0, offsetY, 0);
			}
			
			glPopMatrix();
			
		} else {
			
			drawRectsAndTexture(null);
		}
	}
	
	
	int drawRectsAndTexture(String textureCode) {
		
		double texWidth = ModelCreator.currentProject.TextureWidth;
		double texHeight = ModelCreator.currentProject.TextureHeight;
		
		int[] size = ModelCreator.currentProject.TextureSizes.get(textureCode);
		if (size != null) {
			texWidth = size[0];
			texHeight = size[1];
		}
		
		Sized scale;
		
		TextureEntry texEntry = null;
		if (textureCode != null) {
			texEntry = ModelCreator.currentProject.TexturesByCode.get(textureCode);
		}
		
		scale = Face.getVoxel2PixelScale(ModelCreator.currentProject, texEntry);
		
		texWidth *= scale.W / 2;
		texHeight *= scale.H / 2;
		
		texBoxWidth = (int)Math.max(0, (GetSidebarWidth() - 20));
		texBoxHeight = (int)(texBoxWidth * texHeight / texWidth);
		
		glPushMatrix();
		{
			glTranslatef(10, 30, 0);
			glPushMatrix(); {
				
				glColor3f(1, 1, 1);
				
				Face.bindTexture(texEntry);
				
				float endu = 1f;
				float endv = 1f;
				if (texEntry != null) {
					endu = (float)texEntry.Width / texEntry.texture.getTextureWidth();
					endv = (float)texEntry.Height / texEntry.texture.getTextureHeight();
				}
				
				// Background
				glLineWidth(1F);
				glBegin(GL_QUADS);
				{
					glTexCoord2f(0, endv);
					glVertex2i(0, texBoxHeight);
					
					glTexCoord2f(endu, endv);
					glVertex2i(texBoxWidth, texBoxHeight);
					
					glTexCoord2f(endu, 0);
					glVertex2i(texBoxWidth, 0);

					glTexCoord2f(0, 0);
					glVertex2i(0, 0);
				}
				glEnd();
				TextureImpl.bindNone();
				
				
				// Pixel grid
				if (texEntry == null) {
					glBegin(GL_LINES);
					{
						glColor3f(0.9f, 0.9f, 0.9f);
						int pixelsW = (int)(ModelCreator.currentProject.TextureWidth * scale.W);
						int pixelsH = (int)(ModelCreator.currentProject.TextureHeight * scale.H);
						
						if (pixelsW <= 64 && pixelsH <= 64) {
							double sectionWidth = (double)texBoxWidth / pixelsW;
							for (double i = 0; i <= pixelsW; i++) {
								glVertex2d(i * sectionWidth, 0);
								glVertex2d(i * sectionWidth, texBoxHeight);	
							}
							
							double sectionHeight = (double)texBoxHeight / pixelsH;
							for (double i = 0; i <= pixelsH; i++) {
								glVertex2d(0, i * sectionHeight);
								glVertex2d(texBoxWidth, i * sectionHeight);	
							}
						}
					}
					glEnd();
				}
				
				drawElementList(textureCode, ModelCreator.currentProject.rootElements, texBoxWidth, texBoxHeight, canvasHeight);
				
				// selected face at the very top
				while (renderLastStack2.size() > 0) {
					RenderFaceTask rft = renderLastStack2.pop();
					drawFace(rft.elem, rft.face);
				}

				glPopMatrix();
			}
		}
		
		glPopMatrix();
		
		return texBoxHeight + 20;
	}
	
	
	

	private void drawElementList(String textureCode, ArrayList<Element> elems, double texBoxWidth, double texBoxHeight, int canvasHeight)
	{
		Element selectedElem = ModelCreator.currentProject.SelectedElement;
		
		for (Element elem : elems) {
			if (!elem.getRenderInEditor()) continue;
			
			Face[] faces = elem.getAllFaces();
			
			for (int i = 0; i < 6; i++) {
				Face face = faces[i];
				if (!face.isEnabled() || (textureCode != null && !textureCode.equals(face.getTextureCode()))) continue;
				
				if (elem == selectedElem) {
					if (face == selectedElem.getSelectedFace()) {
						renderLastStack2.push(new RenderFaceTask(face, elem));
						continue;
					}
						
					renderLastStack.push(new RenderFaceTask(face, elem));
					continue;
				}
				
				if (elem == grabbedElement && face.isAutoUVEnabled()) {
					renderLastStack.push(new RenderFaceTask(face, elem));
					continue;
				}
				
				if (elem == grabbedElement && !face.isAutoUVEnabled() && i == grabbedFaceIndex) {
					renderLastStack.push(new RenderFaceTask(face, elem));
					continue;
				}
				
				drawFace(elem, face);
			}
			
			
			drawElementList(textureCode, elem.ChildElements, texBoxWidth, texBoxHeight, canvasHeight);
		}
		
		// Needs to be drawn at the end so the selected highlight is always visible
		while (renderLastStack.size() > 0) {
			RenderFaceTask rft = renderLastStack.pop();
			drawFace(rft.elem, rft.face);
		}	
	}
	
	private void drawFace(Element elem, Face face) {
		Element selectedElem = ModelCreator.currentProject.SelectedElement;
		Face selectedFace = selectedElem == null ? null : selectedElem.getSelectedFace();
		
		Sized uv = face.translateVoxelPosToUvPos(face.getStartU(), face.getStartV(), true);
		Sized uvend = face.translateVoxelPosToUvPos(face.getEndU(), face.getEndV(), true);
		
		int sidei = face.getSide();
		
		Color color = Face.getFaceColour(sidei);
		
		
		GL11.glColor4f(color.r * elem.brightnessByFace[sidei], color.g * elem.brightnessByFace[sidei], color.b * elem.brightnessByFace[sidei], 0.3f);

		glBegin(GL_QUADS);
		{
			glTexCoord2f(0, 1);
			glVertex2d(uv.W * texBoxWidth, uvend.H * texBoxHeight);
			
			glTexCoord2f(1, 1);
			glVertex2d(uvend.W * texBoxWidth, uvend.H * texBoxHeight);
			
			glTexCoord2f(1, 0);
			glVertex2d(uvend.W * texBoxWidth, uv.H * texBoxHeight);

			glTexCoord2f(0, 0);
			glVertex2d(uv.W * texBoxWidth, uv.H * texBoxHeight);
		}
		glEnd();

		
		glColor3f(0.5f, 0.5f, 0.5f);

		
		if (elem == selectedElem) {
			glColor3f(0f, 0f, 1f);			
			if (face == selectedFace) {
				glColor3f(0f, 1f, 0.5f);
			}
		}
		
		if (elem == grabbedElement && face.isAutoUVEnabled()) {
			glColor3f(0f, 0.75f, 1f);
		}
		
		if (elem == grabbedElement && !face.isAutoUVEnabled() && sidei == grabbedFaceIndex) {
			glColor3f(0f, 1f, 0.75f);
		}
		
		glBegin(GL_LINES);
		{
			glVertex2d(uv.W * texBoxWidth, uv.H * texBoxHeight);
			glVertex2d(uv.W * texBoxWidth, uvend.H * texBoxHeight);

			glVertex2d(uv.W * texBoxWidth, uvend.H * texBoxHeight);
			glVertex2d(uvend.W * texBoxWidth, uvend.H * texBoxHeight);

			glVertex2d(uvend.W * texBoxWidth, uvend.H * texBoxHeight);
			glVertex2d(uvend.W * texBoxWidth, uv.H * texBoxHeight);

			glVertex2d(uvend.W * texBoxWidth, uv.H * texBoxHeight);
			glVertex2d(uv.W * texBoxWidth, uv.H * texBoxHeight);
		}
		
		glEnd();
		


		boolean renderName = (ModelCreator.uvShowNames && (!elem.isAutoUnwrapEnabled() || (elem.getUnwrapMode() <= 0 && sidei ==0) || sidei == elem.getUnwrapMode() - 1));
		
		if (renderName) {
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_BLEND);
			
			float width = EnumFonts.BEBAS_NEUE_12.getWidth(elem.getName());
			float height = EnumFonts.BEBAS_NEUE_12.getHeight(elem.getName());
			
			int x = (int)((uv.W + (uvend.W - uv.W) / 2) * texBoxWidth - width/2);
			int y = (int)((uv.H + (uvend.H - uv.H) / 2) * texBoxHeight - height/2);
			
			EnumFonts.BEBAS_NEUE_12.drawString(x, y, elem.getName(), BLACK_ALPHA);
			
			TextureImpl.bindNone();
		}
	}
	
	
	int blockFaceTextureSize;
	int blockFaceTextureMaxWidth;

	void drawRectsBlockTextureMode(int canvasHeight) {
		Element elem = manager.getCurrentElement();
		if (elem == null) return;
		
		float[] bright = elem != null ? elem.brightnessByFace : brightnessByFace;
		
		
		
		Face[] faces = elem.getAllFaces();

		if(!Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)) {
			grabbedFaceIndex = getGrabbedFace(elem, canvasHeight, Mouse.getX(), Mouse.getY());
		}
		
		
		int topPadding = 30;
		int leftPadding = 5;
		int rightpadding = 5;
		int bottompadding = 10;

		int betweenpadding = 10;

		double horizontalSpace = GetSidebarWidth() - rightpadding - leftPadding;
		double verticalSpace = canvasHeight - topPadding - bottompadding - 5*betweenpadding;
		
		boolean doDoubleColumn = horizontalSpace / (verticalSpace/6) >= 2;
		
		boolean onlyTallTextures = true;
		
		blockFaceTextureMaxWidth = 0;
		
		for (int i = 0; i < 6; i++) {
			
			Face face = faces[i];
			if (!face.isEnabled()) continue;
			
			Sized texSize = GetBlockTextureModeTextureSize(face.getTextureCode(), blockFaceTextureSize);
			
			
			onlyTallTextures &=(texSize.H / texSize.W) >= 2;
			doDoubleColumn |= (texSize.H / texSize.W) >= 2;
			
			blockFaceTextureMaxWidth = Math.max(blockFaceTextureMaxWidth, (int)texSize.W);
		}
		
		
		if (doDoubleColumn) {
			horizontalSpace = GetSidebarWidth() - leftPadding - rightpadding - betweenpadding;
			verticalSpace = canvasHeight - topPadding - bottompadding - 2*betweenpadding;
			
			blockFaceTextureSize = (int)Math.min(horizontalSpace / 2, verticalSpace / 3);	
		} else {
			blockFaceTextureSize = (int)Math.min(horizontalSpace, verticalSpace / 6);
		}
		
		if (onlyTallTextures) {
			blockFaceTextureSize *=2;
			blockFaceTextureMaxWidth *= 2;
		}
		
		
		glPushMatrix();
		{
			glTranslatef(leftPadding, topPadding, 0);

			
			int countleft = 0;
			int countright = 0;
			int faceCnt=0;

			for (int i = 0; i < 6; i++) {
				
				Face face = faces[i];
				if (!face.isEnabled()) continue;
				faceCnt++;
				Sized texSize = GetBlockTextureModeTextureSize(face.getTextureCode(), blockFaceTextureSize);
				
				glPushMatrix(); {
					if (faceCnt >= 3 && doDoubleColumn) {
						glTranslatef(betweenpadding + blockFaceTextureMaxWidth, countright * (blockFaceTextureSize + betweenpadding), 0);
						startX[i] = leftPadding + betweenpadding + blockFaceTextureMaxWidth;
						startY[i] = countright * (blockFaceTextureSize + betweenpadding) + topPadding;
						countright++;
					}
					else
					{
						glTranslatef(0, countleft * (blockFaceTextureSize + betweenpadding), 0);
						startX[i] = leftPadding;
						startY[i] = countleft * (blockFaceTextureSize + betweenpadding) + topPadding;
						countleft++;
					}

					Color color = Face.getFaceColour(i);
					glColor3f(color.r * bright[i], color.g * bright[i], color.b * bright[i]);

					// Texture
					face.bindTexture();
					
					float u=1, v=1;
					
					TextureEntry entry = face.getTextureEntry();
					if (entry != null) {
						u = entry.LwJglFuckeryScaleW();
						v = entry.LwJglFuckeryScaleH();
					}

					glBegin(GL_QUADS);
					{
						glTexCoord2f(0, v);
						glVertex2d(0, texSize.H);
						
						glTexCoord2f(u, v);
						glVertex2d(texSize.W, texSize.H);
						
						glTexCoord2f(u, 0);
						glVertex2d(texSize.W, 0);
		
						glTexCoord2f(0, 0);
						glVertex2d(0, 0);
					}
					glEnd();
					
					
					// Pixel grid
					TextureEntry texEntry = face.getTextureEntry();
					if (texEntry == null) {
						Sized scale = Face.getVoxel2PixelScale(ModelCreator.currentProject, texEntry);
						glLineWidth(1F);
						glBegin(GL_LINES);
						{
							GL11.glColor4f(0.9f, 0.9f, 0.9f, 0.3f);
							int pixelsW = (int)(ModelCreator.currentProject.TextureWidth * scale.W);
							int pixelsH = (int)(ModelCreator.currentProject.TextureHeight * scale.H);
							
							double height = blockFaceTextureSize * pixelsH/pixelsW;
							
							if (pixelsW <= 64 && pixelsH <= 64) {
								double sectionWidth = (double)blockFaceTextureSize / pixelsW;
								for (double k= 0; k <= pixelsW; k++) {
									glVertex2d(k * sectionWidth, 0);
									glVertex2d(k * sectionWidth, height);	
								}
								
								double sectionHeight = (double)height / pixelsH;
								for (double k = 0; k <= pixelsH; k++) {
									glVertex2d(0, k * sectionHeight);
									glVertex2d(blockFaceTextureSize, k * sectionHeight);	
								}
							}
							
		
						}
						glEnd();
					}					
					
					
					glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
					EnumFonts.BEBAS_NEUE_20.drawString(5, 5, Face.getFaceName(i), BLACK_ALPHA);
				}
			
				glPopMatrix();
			}
			
			TextureImpl.bindNone();
			
			countleft = 0;
			countright = 0;
			faceCnt=0;
			for (int i = 0; i < 6; i++) {
				
				Face face = faces[i];
				if (!face.isEnabled()) continue;
				faceCnt++;
				
				Sized texSize = GetBlockTextureModeTextureSize(face.getTextureCode(), blockFaceTextureSize);
				
				glPushMatrix(); {
					if (faceCnt >= 3 && doDoubleColumn) {
						glTranslatef(betweenpadding + blockFaceTextureMaxWidth, countright * (blockFaceTextureSize + betweenpadding), 0);
						startX[i] = leftPadding + betweenpadding + blockFaceTextureMaxWidth;
						startY[i] = countright * (blockFaceTextureSize + betweenpadding) + topPadding;
						countright++;
					}
					else
					{
						glTranslatef(0, countleft * (blockFaceTextureSize + betweenpadding), 0);
						startX[i] = leftPadding;
						startY[i] = countleft * (blockFaceTextureSize + betweenpadding) + topPadding;
						countleft++;
					}	

					Sized uv = face.translateVoxelPosToUvPos(face.getStartU(), face.getStartV(), true);
					Sized uvend = face.translateVoxelPosToUvPos(face.getEndU(), face.getEndV(), true);					
					
					glColor3f(1, 1, 1);
					
					if (grabbedFaceIndex == i) {
						glColor3f(0f, 1f, 0.75f);
					}

					glBegin(GL_LINES);
					{
						glVertex2d(uv.W * texSize.W, uv.H * texSize.H);
						glVertex2d(uv.W * texSize.W, uvend.H * texSize.H);
		
						glVertex2d(uv.W * texSize.W, uvend.H * texSize.H);
						glVertex2d(uvend.W * texSize.W, uvend.H * texSize.H);
		
						glVertex2d(uvend.W * texSize.W, uvend.H * texSize.H);
						glVertex2d(uvend.W * texSize.W, uv.H * texSize.H);
		
						glVertex2d(uvend.W * texSize.W, uv.H * texSize.H);
						glVertex2d(uv.W * texSize.W, uv.H * texSize.H);
					}
					glEnd();

				}
			
				glPopMatrix();
			}
		}
		glPopMatrix();
	}
	
	

	private int lastMouseX, lastMouseY;
	private boolean grabbing = false;
	Element grabbedElement;
	int grabbedFaceIndex = -1;

	@Override
	public void mouseUp() {
		super.mouseUp();
		
		if (grabbing) {
			ModelCreator.changeHistory.endMultichangeHistoryState(ModelCreator.currentProject);
		}
		grabbing = false;
	}
	
	@Override
	public void onMouseDownOnPanel()
	{
		boolean nowGrabbing = Mouse.isButtonDown(0) || Mouse.isButtonDown(1);
		
		if (!grabbing && !ModelCreator.currentProject.EntityTextureMode && nowGrabbing) {
			int width = GetSidebarWidth();
			int nowMouseX = Mouse.getX();
			
			if (Math.abs(nowMouseX - width) < 4) {
				grabbedElement = null;
			}
		}
		
		if (grabbedElement == null) {
			super.onMouseDownOnPanel();
		}
		
		if (nowResizingSidebar) return;
		
		
		
		if (ModelCreator.currentProject.EntityTextureMode && !nowGrabbing) {
			grabbedElement = currentHoveredElementEntityTextureMode(ModelCreator.currentProject.rootElements);
		}
		
		if (!ModelCreator.currentProject.EntityTextureMode && !nowGrabbing) {
			grabbedElement = ModelCreator.currentProject.SelectedElement;
		}
		
		
		
		if (!grabbing && nowGrabbing) {
			if (ModelCreator.currentProject.EntityTextureMode) {
				grabbedElement = currentHoveredElementEntityTextureMode(ModelCreator.currentProject.rootElements);
			}
			else {
				grabbedElement = ModelCreator.currentProject.SelectedElement;
			}
			
			if (grabbedElement == null) return;
			
			this.lastMouseX = Mouse.getX();
			this.lastMouseY = Mouse.getY();
			
			if (!ModelCreator.currentProject.EntityTextureMode) {
				grabbedFaceIndex = getGrabbedFace(grabbedElement, canvasHeight, lastMouseX, lastMouseY);	
			}

			
			if (grabbedElement.getSelectedFaceIndex() != grabbedFaceIndex) {
				grabbedElement.setSelectedFace(grabbedFaceIndex);
			}
			ModelCreator.currentProject.selectElement(grabbedElement);	
		}
		
		grabbing = nowGrabbing;
		if (grabbedElement == null) return;
		
		
		if (grabbing)
		{
			ModelCreator.changeHistory.beginMultichangeHistoryState();
			
			int newMouseX = Mouse.getX();
			int newMouseY = Mouse.getY();			
			int xMovement = 0;
			int yMovement = 0;
			TextureEntry texEntry = null;
			
			if (grabbedFaceIndex >= 0) {
				texEntry = grabbedElement.getAllFaces()[grabbedFaceIndex].getTextureEntry();
			}
			
			if (!ModelCreator.currentProject.EntityTextureMode && grabbedFaceIndex >= 0) {
				Sized texSize = GetBlockTextureModeTextureSize(texEntry == null ? null : texEntry.code, blockFaceTextureSize);
				texBoxWidth = (int)texSize.W;
				texBoxHeight = (int)texSize.H;
			}
			
			Sized scale = Face.getVoxel2PixelScale(ModelCreator.currentProject, texEntry);
			

			float mousedx = (newMouseX - this.lastMouseX);
			float mousedy = (newMouseY - this.lastMouseY);
			
			int pixelsW = (int)(ModelCreator.currentProject.TextureWidth * scale.W);
			int pixelsH = (int)(ModelCreator.currentProject.TextureHeight * scale.H);
			
			if (texEntry != null) {			
				int[] size = ModelCreator.currentProject.TextureSizes.get(texEntry.code);
				if (size != null) {
					pixelsW = (int)(size[0] * scale.W);
					pixelsH = (int)(size[1] * scale.H);
				}
			}
			
			double sectionWidth = (double)texBoxWidth / pixelsW;
			double sectionHeight = (double)texBoxHeight / pixelsH;
			
			xMovement = (int)(mousedx / sectionWidth);
			yMovement = (int)(mousedy / sectionHeight);

			
			if (ModelCreator.currentProject.EntityTextureMode) {
				if ((xMovement != 0 || yMovement != 0) && grabbedElement != null && Mouse.isButtonDown(0))
				{
					Face face = null; 
					if (grabbedFaceIndex >=0) face = grabbedElement.getAllFaces()[grabbedFaceIndex];
					if (face != null && !grabbedElement.isAutoUnwrapEnabled()) {
						
						face.moveTextureU(xMovement / scale.W);
						face.moveTextureV(-yMovement / scale.H);
						
					} else {
						grabbedElement.setTexUVStart(grabbedElement.getTexUStart() + xMovement / scale.W, grabbedElement.getTexVStart() - yMovement / scale.H);	
					}
				}
			} else {

				if (grabbedFaceIndex == -1) return;
				Face face = grabbedElement.getAllFaces()[grabbedFaceIndex];
				
				
				if (Mouse.isButtonDown(0))
				{
					face.moveTextureU(xMovement / scale.W);
					face.moveTextureV(-yMovement / scale.H);
				}
				else
				{
					face.setAutoUVEnabled(false);

					face.addTextureUEnd(xMovement / scale.W);
					face.addTextureVEnd(-yMovement / scale.H);

					face.setAutoUVEnabled(false);
				}
				
				face.updateUV();
			}

			
			if (xMovement != 0) {
				this.lastMouseX += xMovement * sectionWidth;   // Add *sectionWidth because otherwise the rect moves too quickly
			}
			
			if (yMovement != 0) {
				this.lastMouseY += yMovement * sectionHeight;
			}
			
			if (xMovement != 0 || yMovement != 0) {
				ModelCreator.updateValues(null);	
			}
			
		}
	}
	
	
	
	public Sized GetBlockTextureModeTextureSize(String textureCode, float maxHeight) {
		double texWidth = ModelCreator.currentProject.TextureWidth;
		double texHeight = ModelCreator.currentProject.TextureHeight;
		
		if (textureCode != null) {
			int[] size = ModelCreator.currentProject.TextureSizes.get(textureCode);
			if (size != null) {
				texWidth = size[0];
				texHeight = size[1];
			}
		}
		
		int texBoxWidth = (int)(blockFaceTextureSize);
		int texBoxHeight = (int)(texBoxWidth * texHeight / texWidth);
		

		if (texBoxHeight > blockFaceTextureSize) {
			double div = texBoxHeight / blockFaceTextureSize;
			texBoxWidth /= div;
			texBoxHeight /= div;
		}

		return new Sized(texBoxWidth, texBoxHeight);
	}
	

	public int getGrabbedFace(Element elem, int canvasHeight, int mouseX, int mouseY)
	{
		if (elem == null) return -1;
		Face[] faces = elem.getAllFaces();
		if (faces == null) return -1;
		
		for (int i = 0; i < 6; i++)
		{
			if (faces[i] == null || !faces[i].isEnabled()) {
				continue;
			}
			
			if (mouseX >= startX[i] && mouseX <= startX[i] + blockFaceTextureMaxWidth)
			{
				if ((canvasHeight - mouseY + 10) >= startY[i] && (canvasHeight - mouseY + 10) <= startY[i] + blockFaceTextureSize)
				{
					return i;
				}
			}
		}
		
		return -1;
	}
	
	
	public int Clamp(int val, int min, int max) {
		return Math.min(Math.max(val, min), max);
	}
	
	private Element currentHoveredElementEntityTextureMode(ArrayList<Element> elems)
	{
		if (texBoxHeight == 0 || texBoxWidth == 0) return null;
		
		int mouseX = Mouse.getX() - 10;
		int mouseY = (canvasHeight - Mouse.getY()) - 30;
		String currentHoveredTextureCode = null;
		boolean found=false;
		
		for (String textureCode : getTextureCodes()) {
			double texWidth = ModelCreator.currentProject.TextureWidth;
			double texHeight = ModelCreator.currentProject.TextureHeight;
			Sized scale;
			
			TextureEntry texEntry = null;
			if (textureCode != null) {
				texEntry = ModelCreator.currentProject.TexturesByCode.get(textureCode);
				int[] size = ModelCreator.currentProject.TextureSizes.get(textureCode);
				if (size != null) {
					texWidth = size[0];
					texHeight = size[1];
				}
			}
			
			scale = Face.getVoxel2PixelScale(ModelCreator.currentProject, texEntry);
			
			texWidth *= scale.W / 2;
			texHeight *= scale.H / 2;
			
			texBoxWidth = (int)(GetSidebarWidth() - 20);
			texBoxHeight = (int)(texBoxWidth * texHeight / texWidth);
			
			if (mouseY >= 0 && mouseY < texBoxHeight) {
				currentHoveredTextureCode = textureCode;
				found=true;
				break;
			}
			
			mouseY -= texBoxHeight + 20;
		}
		
		if (!found) return null; 
		
		
		double mouseU = (double)mouseX / texBoxWidth;
		double mouseV = (double)mouseY / texBoxHeight; 
				
		for (Element elem : elems) {
			Face[] faces = elem.getAllFaces();
			if (!elem.getRenderInEditor()) continue;
			
			for (int i = 0; i < 6; i++) {
				Face face = faces[i];
				if (!face.isEnabled() || (currentHoveredTextureCode != null && !currentHoveredTextureCode.equals(face.getTextureCode()))) continue;
				
				Sized uv = face.translateVoxelPosToUvPos(face.getStartU(), face.getStartV(), true);
				Sized uvend = face.translateVoxelPosToUvPos(face.getEndU(), face.getEndV(), true);

				if (mouseU >= uv.W && mouseV >= uv.H && mouseU <= uvend.W && mouseV <= uvend.H) {
					grabbedFaceIndex = i;
					return elem;
				}
			}
			
			Element foundElem = currentHoveredElementEntityTextureMode(elem.ChildElements);
			if (foundElem != null) return foundElem;
		}
		
		return null;
	}

	
}
