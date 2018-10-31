package at.vintagestory.modelcreator.model;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_LINES;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Project;
import at.vintagestory.modelcreator.enums.BlockFacing;
import at.vintagestory.modelcreator.interfaces.IDrawable;
import at.vintagestory.modelcreator.util.GameMath;
import at.vintagestory.modelcreator.util.Mat4f;
import org.newdawn.slick.Color;

public class Element implements IDrawable
{
    /// <summary>
    /// Top, Front/Left, Back/Right, Bottom
    /// </summary>
	protected static float[] DefaultBlockSideBrightness = new float[] {
        1f,
        0.8f,
        0.6f,
        0.4f
    };

    /// <summary>
    /// Shadings by Blockfacing index
    /// </summary>
    public static float[] DefaultBlockSideBrightnessByFacing = new float[] {
        DefaultBlockSideBrightness[2],
        DefaultBlockSideBrightness[1],
        DefaultBlockSideBrightness[1],
        DefaultBlockSideBrightness[2],
        DefaultBlockSideBrightness[0],
        DefaultBlockSideBrightness[3],
    };
    
	static int nextOpenGlName = 0;

	
	
	
	public Element ParentElement;
	public ArrayList<Element> ChildElements = new ArrayList<Element>();
	public ArrayList<AttachmentPoint> AttachmentPoints = new ArrayList<AttachmentPoint>();
	
	public String name = "Cube1";
	
	public int openGlName = 0;
	
	// Face Variables
	protected int selectedFace = 0;
	protected Face[] faces = new Face[6];
	private double texUStart;
	private double texVStart;
	protected boolean autoUnwrap = true;

	// Element Variables
	protected double startX = 0.0, startY = 0.0, startZ = 0.0;
	protected double width = 16.0, height = 1.0, depth = 1.0;

	// Rotation Variables
	protected double originX = 0, originY = 0, originZ = 0;
	protected double rotationX = 0, rotationY = 0, rotationZ = 0;
	
	protected boolean rescale = false;

	// Extra Variables
	protected boolean shade = true;
	protected int tintIndex = 0;
	protected int renderPass = -1;
	protected int unwrapMode;
	protected int unwrapRotation;
	
	
	// Rotation Point Indicator
	protected Sphere sphere = new Sphere();
	
	
	public float[] brightnessByFace = new float[] { 1, 1, 1, 1, 1, 1 };

    
    public Element() {
    	
    }
    
	public Element(double width, double height, double depth)
	{
		this.width = width;
		this.height = height;
		this.depth = depth;
		openGlName = nextOpenGlName++;
		initFaces();
		updateUV();
		recalculateBrightnessValues();
	}
	
	public Element(double width, double height) {
		name = "Face";
		openGlName = nextOpenGlName++;
		this.width = width;
		this.height = height;
		this.depth = 1;
		initFaces();
		for (int i = 0; i < faces.length; i++) {
			faces[i].setEnabled(false);
		}
		faces[2].setEnabled(true);

		updateUV();
		recalculateBrightnessValues();
	}
	

	public Element(Element cuboid)
	{
		openGlName = nextOpenGlName++;
		
		this.name = cuboid.name;
		this.width = cuboid.width;
		this.height = cuboid.height;
		this.depth = cuboid.depth;
		this.startX = cuboid.startX;
		this.startY = cuboid.startY;
		this.startZ = cuboid.startZ;
		this.originX = cuboid.originX;
		this.originY = cuboid.originY;
		this.originZ = cuboid.originZ;
		this.rotationX = cuboid.rotationX;
		this.rotationY = cuboid.rotationY;
		this.rotationZ = cuboid.rotationZ;
		this.renderPass = cuboid.renderPass;
		this.tintIndex = cuboid.tintIndex;
		this.unwrapMode = cuboid.unwrapMode;
		this.unwrapRotation = cuboid.unwrapRotation;
		this.autoUnwrap = cuboid.autoUnwrap;
		
		this.rescale = cuboid.rescale;
		this.shade = cuboid.shade;
		this.selectedFace = cuboid.getSelectedFaceIndex();
		initFaces();
		for (int i = 0; i < faces.length; i++)
		{
			Face oldFace = cuboid.getAllFaces()[i];
			faces[i].setTextureCode(oldFace.getTextureCode());
			//faces[i].setTextureLocation(oldFace.getTextureLocation());
			faces[i].setStartU(oldFace.getStartU());
			faces[i].setStartV(oldFace.getStartV());
			faces[i].setEndU(oldFace.getEndU());
			faces[i].setEndV(oldFace.getEndV());
			faces[i].setEnabled(oldFace.isEnabled());
			faces[i].setAutoUVEnabled(oldFace.isAutoUVEnabled());
			faces[i].setRotation(oldFace.getRotation());
			faces[i].setGlow(oldFace.getGlow());
		}
		
		for (Element child : cuboid.ChildElements) {
			ChildElements.add(new Element(child));
		}
		
		updateUV();
		recalculateBrightnessValues();
	}
	

	public void initFaces()
	{
		for (int i = 0; i < faces.length; i++) {
			faces[i] = new Face(this, i);
		}
	}

	public void setSelectedFace(int face)
	{
		if (face < 0) return;
		this.selectedFace = face;
	}

	public Face getSelectedFace()
	{
		if (selectedFace < 0) return null;
		
		return faces[selectedFace];
	}

	public int getSelectedFaceIndex()
	{
		return selectedFace;
	}
	
	public void setAutoUnwrap(boolean enabled) {
		if (autoUnwrap == enabled) return;
		
		this.autoUnwrap = enabled;
		ModelCreator.DidModify();
	}
	
	
	public boolean isAutoUnwrapEnabled() {
		return autoUnwrap;
	}

	public Face[] getAllFaces()
	{
		return faces;
	}

	public int getLastValidFace()
	{
		int id = 0;
		for (Face face : faces)
		{
			id = face.getSide();
		}
		return id;
	}

	public FaceDimension getFaceDimension(int side)
	{
		switch (side)
		{
		case 0:
			return new FaceDimension(width, height);
		case 1:
			return new FaceDimension(depth, height);
		case 2:
			return new FaceDimension(width, height);
		case 3:
			return new FaceDimension(depth, height);
		case 4:
			return new FaceDimension(width, depth);
		case 5:
			return new FaceDimension(width, depth);
		}
		return null;
	}

	public void clearAllTextures()
	{
		for (Face face : faces)
		{
			face.setTextureCode(null);
			//face.setTextureLocation(ModelCreator.currentProject.EntityTextureMode ? "entities/" : "blocks/");
		}
	}
	
	public void setAllTextures(ClipboardTexture texture)
	{
		setAllTextureNames(texture.getTexture());
	}

	public void setAllTextureNames(String texture)
	{
		for (Face face : faces)
		{
			face.setTextureCode(texture);
			//face.setTextureLocation(location);
		}
	}
	
	
	
	public void recalculateBrightnessValues() {
		for (int i = 0; i < BlockFacing.ALLFACES.length; i++) {
			if (shade) {
				brightnessByFace[i] = getFaceBrightness(BlockFacing.ALLFACES[i]);	
			} else {
				brightnessByFace[i] = 1;
			}
			
		}
	}
	
	public float getFaceBrightness(BlockFacing facing) {
		float[] matrix = Mat4f.Create();
        
        Mat4f.RotateX(matrix, matrix, (float)rotationX * GameMath.DEG2RAD);            
        Mat4f.RotateY(matrix, matrix, (float)rotationY * GameMath.DEG2RAD);
        Mat4f.RotateZ(matrix, matrix, (float)rotationZ * GameMath.DEG2RAD);
        
        float[] pos = new float[] { facing.GetFacingVector().X, facing.GetFacingVector().Y, facing.GetFacingVector().Z, 1 };
        pos = Mat4f.MulWithVec4(matrix, pos);
        
        float brightness = 0;
        
        for (int i = 0; i < BlockFacing.ALLFACES.length; i++) {
        	BlockFacing f = BlockFacing.ALLFACES[i];
        	float angle = (float)Math.acos(f.GetFacingVector().Dot(pos));
        	
        	if (angle >= GameMath.PIHALF) continue;
        	
        	brightness += (1 - angle / GameMath.PIHALF) * DefaultBlockSideBrightnessByFacing[f.GetIndex()]; 
        }
		
		return brightness;
	}
	

	public void draw(IDrawable selectedElem)
	{
		if (!Render) return;
		
		float b;
		
		GL11.glPushMatrix();
		{
			GL11.glLoadName(openGlName);
			GL11.glEnable(GL_BLEND);
			GL11.glDisable(GL_CULL_FACE);
			GL11.glTranslated(originX, originY, originZ);
			rotateAxis();
			GL11.glTranslated(-originX, -originY, -originZ);
			
			GL11.glTranslated(startX, startY, startZ);
			
			for (int i = 0; i < BlockFacing.ALLFACES.length; i++) {
				if (!faces[i].isEnabled()) continue;
				
				b = brightnessByFace[BlockFacing.ALLFACES[i].GetIndex()];
				Color c = Face.ColorsByFace[i];
				GL11.glColor3f(c.r * b, c.g * b, c.b * b);
								
				faces[i].renderFace(BlockFacing.ALLFACES[i], b);
			}
			
			GL11.glLoadName(0);
			
			for (int i = 0; i < ChildElements.size(); i++) {
				ChildElements.get(i).draw(selectedElem);
			}
		}
		
		if (ModelCreator.renderAttachmentPoints) {
			for (int i = 0; i < AttachmentPoints.size(); i++) {
				AttachmentPoint p = AttachmentPoints.get(i);
				if (p == ModelCreator.currentProject.SelectedAttachmentPoint) {
					p.draw();
				}
			}
		}

		
		GL11.glPopMatrix();
		
		if (selectedElem == this) {
			drawSelectionExtras();
		}
		
	}
	

	public void drawSelectionExtras()
	{
		GL11.glLineWidth(1f);
		
		if (!ModelCreator.renderAttachmentPoints) {
			GL11.glPushMatrix();
			{
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glTranslated(originX, originY, originZ);
				GL11.glColor3f(0.25F, 0.25F, 0.25F);
				sphere.draw(0.2F, 16, 16);
				rotateAxis();
				GL11.glBegin(GL_LINES);
				{
					// 3 Axes
					GL11.glColor3f(1, 0, 0);
					GL11.glVertex3f(-4, 0, 0);
					GL11.glVertex3f(4, 0, 0);
					
					GL11.glVertex3f(4, 0, 0);
					GL11.glVertex3f(3.6f, 0, 0.4f);
					GL11.glVertex3f(4, 0, 0);
					GL11.glVertex3f(3.6f, 0, -0.4f);
					
					GL11.glColor3f(0, 1, 0);
					GL11.glVertex3f(0, -4, 0);
					GL11.glVertex3f(0, 4, 0);
					
					GL11.glVertex3f(0, 4, 0);
					GL11.glVertex3f(0, 3.6f, 0.4f);
					
					GL11.glVertex3f(0, 4, 0);
					GL11.glVertex3f(0, 3.6f, -0.4f);
					
					GL11.glColor3f(0, 0, 1);
					GL11.glVertex3f(0, 0, -4);
					GL11.glVertex3f(0, 0, 4);
					
					GL11.glVertex3f(0, 0, 4);
					GL11.glVertex3f(0.4f, 0, 3.6f);
					
					GL11.glVertex3f(0, 0, 4);
					GL11.glVertex3f(-0.4f, 0, 3.6f);
				}
				
				GL11.glEnd();
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
			
			GL11.glPopMatrix();			
		}
		


		// Cube highlight
		GL11.glPushMatrix();
		{
			GL11.glTranslated(originX, originY, originZ);
			rotateAxis();
			GL11.glTranslated(-originX, -originY, -originZ);
			
			GL11.glTranslated(startX, startY, startZ);
			
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glBegin(GL11.GL_LINES);
			{
				GL11.glColor4f(0F, 0F, 0F, 0.5f);
				
				float w = (float)width;
				float h = (float)height;
				float d = (float)depth;
				
				GL11.glVertex3f(0, 0, 0);
				GL11.glVertex3f(0, h, 0);
				
				GL11.glVertex3f(w, 0, 0);
				GL11.glVertex3f(w, h, 0);

				GL11.glVertex3f(w, 0, d);
				GL11.glVertex3f(w, h, d);
				
				GL11.glVertex3f(0, 0, d);
				GL11.glVertex3f(0, h, d);
				
				GL11.glVertex3f(0, h, 0);
				GL11.glVertex3f(w, h, 0);
				
				GL11.glVertex3f(w, h, 0);
				GL11.glVertex3f(w, h, d);
				
				GL11.glVertex3f(w, h, d);
				GL11.glVertex3f(0, h, d);
				
				GL11.glVertex3f(0, h, d);
				GL11.glVertex3f(0, h, 0);
				
				
				GL11.glVertex3f(0, 0, 0);
				GL11.glVertex3f(w, 0, 0);
				
				GL11.glVertex3f(w, 0, 0);
				GL11.glVertex3f(w, 0, d);
				
				GL11.glVertex3f(w, 0, d);
				GL11.glVertex3f(0, 0, d);
				
				GL11.glVertex3f(0, 0, d);
				GL11.glVertex3f(0, 0, 0);
			}
			
			GL11.glEnd();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		}
		GL11.glPopMatrix();		
	}

	@Override
	public String toString()
	{
		return name;
	}

	public void updateUV()
	{
		if (ModelCreator.currentProject == null) return;
		
		if (ModelCreator.currentProject.EntityTextureMode && autoUnwrap) {
			setUnwrappedCubeUV();
			return;
		}
		
		for (Face face : faces)
		{
			face.updateUV();
		}
	}

	
	// Cases:
	// N center, E left, W right, U above, D below, S very right
	// E center, S left, N right, U above, D below, W very right
	// S center, W left, E right, U above, D below, N very right
	// W center, N left, S right, U above, D below, E very right
	// U center, W left, E right, S above, N below, D very right
	// D center, E left, W right, N above, S below, U very right
	
	// First index = blockfacing.index
	// Second index:
	// 0 = center
	// 1 = left
	// 2 = right
	// 3 = above
	// 4 = below
	// 5 = very right
	// resulting value = blockfacing index:
	// 0 = N
	// 1 = E
	// 2 = S
	// 3 = W
	// 4 = U
	// 5 = D
	int[][] allUvPositions = new int[][] {
		// N
		new int[] { 0, 1, 3, 4, 5, 2 },
		// E
		new int[] { 1, 2, 0, 4, 5, 3 },
		// S
		new int[] { 2, 3, 1, 4, 5, 0 },
		// W
		new int[] { 3, 0, 2, 4, 5, 1 },
		// U
		new int[] { 4, 3, 1, 0, 2, 5 },
		// D
		new int[] { 5, 1, 3, 0, 2, 4 },
		// U (Saratymode)
		new int[] { 4, 2, 0, 3, 1, 5 }
	};
	
	// Cases:
	// N center, D left, U right, W above, E below, S very right
	// E center, D left, U right, N above, S below, W very right
	// S center, D left, U right, E above, W below, N very right
	// W center, D left, U right, S above, N below, E very right
	// U center, N left, S right, E above, W below, D very right
	// D center, S left, N right, W above, E below, U very right
	int[][] allUvPositionsAlternate = new int[][] {
		// N
		new int[] { 0, 4, 5, 3, 1, 2 },
		// E
		new int[] { 1, 4, 5, 0, 2, 3 },
		// S
		new int[] { 2, 4, 5, 1, 3, 0 },
		// W
		new int[] { 3, 4, 5, 2, 0, 1 },
		// U
		new int[] { 4, 0, 2, 1, 3, 5 },
		// D
		new int[] { 5, 0, 2, 3, 1, 4 },
		// U (Saratymode)
		new int[] { 4, 2, 0, 3, 1, 5 }
	};

	public boolean Render = true;


	void setUnwrappedCubeUV() {
		if (unwrapMode == 0) {
			performDefaultUVUnwrapping();
			return;
		}
		
		
		for (int i = 0; i < 6; i++) {
			faces[i].rotation = unwrapRotation;
		}
		
		if (unwrapMode - 1 == 0) faces[4].rotation = (unwrapRotation + 2) % 4;
		if (unwrapMode - 1 == 2) faces[5].rotation = (unwrapRotation + 2) % 4;
		
		if (unwrapMode - 1 == 0) {
			if (unwrapRotation == 1) faces[2].rotation = (unwrapRotation + 2) % 4;;
		}
		
		if (unwrapMode - 1 == 1) {
			faces[4].rotation = (unwrapRotation + 3) % 4;
			faces[5].rotation = (unwrapRotation + 3) % 4;
			
			if (unwrapRotation == 1) faces[3].rotation = (unwrapRotation + 2) % 4;;
		}
		
		if (unwrapMode - 1 == 2) {
			if (unwrapRotation == 1) faces[0].rotation = (unwrapRotation + 2) % 4;;
		}
		
		if (unwrapMode - 1 == 3) {
			faces[4].rotation = (unwrapRotation + 1) % 4;
			faces[5].rotation = (unwrapRotation + 1) % 4;
			
			if (unwrapRotation == 1) faces[1].rotation = (unwrapRotation + 2) % 4;;
		}
		if (unwrapMode - 1 == 4) {
			faces[0].rotation = (unwrapRotation + 2) % 4;
			faces[1].rotation = (unwrapRotation + 1) % 4;
			faces[3].rotation = (unwrapRotation + 3) % 4;
			
			if (unwrapRotation == 1) {
				faces[5].rotation = (unwrapRotation + 2) % 4;
			}
		}
		if (unwrapMode - 1 == 5) {
			faces[2].rotation = (unwrapRotation + 2) % 4;
			faces[1].rotation = (unwrapRotation + 1) % 4;
			faces[3].rotation = (unwrapRotation + 3) % 4;
			
			if (unwrapRotation == 1) faces[4].rotation = (unwrapRotation + 2) % 4;
		}
		
		if (unwrapMode - 1 == 6) {
			faces[0].rotation = (unwrapRotation) % 4;
			faces[1].rotation = (unwrapRotation + 3) % 4;
			faces[2].rotation = (unwrapRotation + 2) % 4;
			faces[3].rotation = (unwrapRotation + 1) % 4;			
			faces[4].rotation = (unwrapRotation + 2) % 4;
			faces[5].rotation = (unwrapRotation + 0) % 4;
		}
		

		int[] uvPositions = unwrapRotation == 1 ? allUvPositionsAlternate[unwrapMode - 1] : allUvPositions[unwrapMode - 1];
		Sized scale = faces[0].getVoxel2PixelScale();		
		
		Face aboveFace = faces[uvPositions[3]];
		Face veryRightFace = faces[uvPositions[5]];
		Face leftFace = faces[uvPositions[1]];
		Face centerFace = faces[uvPositions[0]];
		Face rightFace = faces[uvPositions[2]];
		Face belowFace = faces[uvPositions[4]];
		
		// Row 1
		double x = getTexUStart();
		double y = getTexVStart();
		
		if (leftFace.isEnabled()) { 
			x += leftFace.uvWidth();
			x = Math.ceil(x * scale.W) / scale.W;
		}
		
		aboveFace.textureU = x;
		aboveFace.textureV = y;
		aboveFace.updateUV();
		
		// Row 2
		if (aboveFace.isEnabled()) y += aboveFace.uvHeight();
		y = Math.ceil(y * scale.H) / scale.H;
		
		x = getTexUStart();
		
		leftFace.textureU = x;
		leftFace.textureV = y;
		leftFace.updateUV();
		
		if (leftFace.isEnabled()) {
			x += leftFace.uvWidth();
			x = Math.ceil(x * scale.W) / scale.W;
		}
		
		centerFace.textureU = x;
		centerFace.textureV = y;
		centerFace.updateUV();
		
		if (centerFace.isEnabled()) {
			x += centerFace.uvWidth();
			x = Math.ceil(x * scale.W) / scale.W;
		}
		
		rightFace.textureU = x;
		rightFace.textureV = y;
		rightFace.updateUV();
		
		if (rightFace.isEnabled()) {
			x += rightFace.uvWidth();
			x = Math.ceil(x * scale.W) / scale.W;
		}
		
		veryRightFace.textureU = x;
		veryRightFace.textureV = y;
		veryRightFace.updateUV();

		
		// Row 3
		x = getTexUStart();
		if (leftFace.isEnabled()) {
			x+= leftFace.uvWidth();
			x = Math.ceil(x * scale.W) / scale.W;
		}
		
		y += Math.max(leftFace.uvHeight(), Math.max(centerFace.uvHeight(), Math.max(rightFace.uvHeight(), veryRightFace.uvHeight())));
		y = Math.ceil(y * scale.H) / scale.H;
		
		belowFace.textureU = x;
		belowFace.textureV = y;
		belowFace.updateUV();
	}
	
	private void performDefaultUVUnwrapping()
	{
		Sized scale = faces[0].getVoxel2PixelScale();
		
		double x = getTexUStart();
		double y = getTexVStart();
		double maxTexHeight = 0;
		
		if (faces[4].isEnabled() && faces[4].isAutoUVEnabled()) {
			faces[4].textureU = x;
			faces[4].textureV = y;
			faces[4].updateUV();
			
			maxTexHeight = faces[4].uvHeight();
			x += faces[4].uvWidth();
		}
		
		x = Math.ceil(x * scale.W) / scale.W;
		
		if (faces[5].isEnabled() && faces[5].isAutoUVEnabled()) {
			faces[5].textureU = x;
			faces[5].textureV = y;
			faces[5].updateUV();
			
			maxTexHeight = Math.max(maxTexHeight, faces[5].uvHeight());
		}
		
		x = getTexUStart();
		y += maxTexHeight;
		
		
		y = Math.ceil(y * scale.H) / scale.H;
		
		for (int side = 0; side < 4; side++) {
			Face face = faces[side];
			if (!face.isEnabled() || !faces[side].isAutoUVEnabled()) continue;

			face.textureU = x;
			face.textureV = y;
			face.updateUV();
			
			x += face.uvWidth();
			x = Math.ceil(x * scale.W) / scale.W;
		}
	}
	

	public void rotateAxis()
	{
		GL11.glRotated(rotationX, 1, 0, 0);
		GL11.glRotated(rotationY, 0, 1, 0);
		GL11.glRotated(rotationZ, 0, 0, 1);
	}

	
	public void addStartX(double amt)
	{
		this.startX += amt;
		ModelCreator.DidModify();
	}

	public void addStartY(double amt)
	{
		this.startY += amt;
		ModelCreator.DidModify();
	}

	public void addStartZ(double amt)
	{
		this.startZ += amt;
		ModelCreator.DidModify();
	}
	
	public double getStartX()
	{
		return startX;
	}

	public double getStartY()
	{
		return startY;
	}

	public double getStartZ()
	{
		return startZ;
	}

	

	public void setStartX(double amt)
	{
		if (amt == startX) return;
		
		this.startX = amt;
		ModelCreator.DidModify();
	}

	public void setStartY(double amt)
	{
		if (amt == startY) return;
		
		this.startY = amt;
		ModelCreator.DidModify();
	}

	public void setStartZ(double amt)
	{
		if (amt == startZ) return;
		
		this.startZ = amt;
		ModelCreator.DidModify();
	}

	public double getWidth()
	{
		return width;
	}

	public double getHeight()
	{
		return height;
	}

	public double getDepth()
	{
		return depth;
	}

	public void addWidth(double amt)
	{
		this.width += amt;
		ModelCreator.DidModify();
	}

	public void addHeight(double amt)
	{
		this.height += amt;
		ModelCreator.DidModify();
	}

	public void addDepth(double amt)
	{
		this.depth += amt;
		ModelCreator.DidModify();
	}

	public void setWidth(double width)
	{
		if (this.width == width) return;
		
		this.width = width;
		ModelCreator.DidModify();
	}

	public void setHeight(double height)
	{
		if (this.height == height) return;
		
		this.height = height;
		ModelCreator.DidModify();
	}

	public void setDepth(double depth)
	{
		if (this.depth == depth) return;
		
		this.depth = depth;
		ModelCreator.DidModify();
	}

	public double getOriginX()
	{
		return originX;
	}

	public double getOriginY()
	{
		return originY;
	}

	public double getOriginZ()
	{
		return originZ;
	}

	public void addOriginX(double amt)
	{
		this.originX += amt;
		ModelCreator.DidModify();
	}

	public void addOriginY(double amt)
	{
		this.originY += amt;
		ModelCreator.DidModify();
	}

	public void addOriginZ(double amt)
	{
		this.originZ += amt;
		ModelCreator.DidModify();
	}

	public void setOriginX(double amt)
	{
		if (this.originX == amt) return;
		
		this.originX = amt;
		ModelCreator.DidModify();
	}

	public void setOriginY(double amt)
	{
		if (this.originY == amt) return;
		
		this.originY = amt;
		ModelCreator.DidModify();
	}

	public void setOriginZ(double amt)
	{
		if (this.originZ == amt) return;
		
		this.originZ = amt;
		ModelCreator.DidModify();
	}

	public double getRotationX()
	{
		return rotationX;
	}

	public double getRotationY()
	{
		return rotationY;
	}

	public double getRotationZ()
	{
		return rotationZ;
	}

	public void setRotationX(double rotation)
	{
		if (this.rotationX == rotation) return;
		
		this.rotationX = rotation;
		recalculateBrightnessValues();
		ModelCreator.DidModify();
	}

	public void setRotationY(double rotation)
	{
		if (this.rotationY == rotation) return;
		
		this.rotationY = rotation;
		recalculateBrightnessValues();
		ModelCreator.DidModify();
	}
	
	public void setRotationZ(double rotation)
	{
		if (this.rotationZ == rotation) return;
		
		this.rotationZ = rotation;
		recalculateBrightnessValues();
		ModelCreator.DidModify();
	}
	

	public void setRescale(boolean rescale)
	{
		this.rescale = rescale;
		ModelCreator.DidModify();
	}

	public boolean shouldRescale()
	{
		return rescale;
	}

	public boolean isShaded()
	{
		return shade;
	}

	public void setShade(boolean shade)
	{
		if (this.shade == shade) return;
		
		this.shade = shade;
		recalculateBrightnessValues();
		ModelCreator.DidModify();
	}

	public void setName(String name)
	{
		this.name = name;
		ModelCreator.DidModify();
	}
	
	
	public List<Element> GetParentPath() {
		ArrayList<Element> path = new ArrayList<Element>();
		Element parentElem = this.ParentElement;
		while (parentElem != null) {
			path.add(parentElem);			
			parentElem = parentElem.ParentElement;
		}		
		Collections.reverse(path);
		return path;
	}

	public int getTintIndex()
	{
		return tintIndex;
	}

	public void setTintIndex(int tintIndex)
	{
		if (this.tintIndex == tintIndex) return;
		
		this.tintIndex = tintIndex;
	}
	

	public int getRenderPass()
	{
		return renderPass;
	}

	public void setRenderPass(int pass)
	{
		if (this.renderPass == pass) return;
		
		this.renderPass = pass;
	}

	
	public Element clone() {
		Element cloned = new Element();
		
		cloned.ParentElement = ParentElement;
		for (Element child : ChildElements) {
			cloned.ChildElements.add(child.clone());
		}
		
		for (AttachmentPoint point : AttachmentPoints) {
			AttachmentPoint clonedpoint = point.clone();
			clonedpoint.ParentElem = cloned;
			cloned.AttachmentPoints.add(clonedpoint);
		}
		
		cloned.name = name;
		cloned.openGlName = openGlName;
		cloned.selectedFace = selectedFace;
		for (int i = 0; i < faces.length; i++) {
			cloned.faces[i] = faces[i].clone(cloned);
		}
		
		cloned.startX = startX;
		cloned.startY = startY;
		cloned.startZ = startZ;
		cloned.width = width;
		cloned.height = height;
		cloned.depth = depth;
		cloned.originX = originX;
		cloned.originY = originY;
		cloned.originZ = originZ;
		cloned.rotationX = rotationX;
		cloned.rotationY = rotationY;
		cloned.rotationZ = rotationZ;
		cloned.rescale = rescale;
		cloned.shade = shade;
		cloned.tintIndex = tintIndex;
		cloned.renderPass = renderPass;
		cloned.sphere = sphere;
		cloned.texUStart = texUStart;
		cloned.texVStart = texVStart;
		cloned.autoUnwrap = autoUnwrap;
		cloned.unwrapMode = unwrapMode;
		cloned.unwrapRotation = unwrapRotation;
		
		for (int i = 0; i < brightnessByFace.length; i++) {
			cloned.brightnessByFace[i] = brightnessByFace[i];			
		}
		
		return cloned;
	}

	public double getTexUStart()
	{
		return texUStart;
	}

	public void setTexUStart(double texUStart)
	{
		setTexUVStart(texUStart, texVStart);
	}

	public double getTexVStart()
	{
		return texVStart;
	}

	public void setTexVStart(double texVStart)
	{
		setTexUVStart(texUStart, texVStart);
	}
	
	public void setTexUVStart(double texUStart, double texVStart)
	{
		this.texUStart = texUStart;
		this.texVStart = texVStart;
		
		ModelCreator.DidModify();
		updateUV();
	}


	public boolean isAttachmentPointCodeUsed(String code, AttachmentPoint exceptPoint)
	{
		for (AttachmentPoint point : AttachmentPoints) {
			if (point == exceptPoint) continue;
			
			if (code.equals(point.getCode())) return true;
		}
		
		return false;
	}

	public AttachmentPoint addNewAttachmentPoint()
	{
		AttachmentPoint point = new AttachmentPoint();
		point.setCode("Point" + Project.nextAttachmentPointNumber++);
		AttachmentPoints.add(point);
		ModelCreator.DidModify();
		return point;
	}

	public void removeCurrentAttachmentPoint()
	{
		AttachmentPoints.remove(ModelCreator.currentProject.SelectedAttachmentPoint);
		ModelCreator.DidModify();
	}

	public void applySingleTextureMode()
	{
		for (int i = 0; i < faces.length; i++) {
			faces[i].applySingleTextureMode();
		}
		
		for (Element elem : ChildElements) {
			elem.applySingleTextureMode();
		}
		
	}

	public void setTexFromFace()
	{
		texUStart = 999;
		texVStart = 999;
		for (int i = 0; i < 6; i++) {
			Face f = faces[i];
			if (!f.isEnabled()) continue;
			
			texUStart = Math.min(texUStart, f.textureU);
			texVStart = Math.min(texVStart, f.textureV);
		}
	}

	public void setUnwrapMode(int selectedIndex)
	{
		unwrapMode = selectedIndex;		
	}
	
	public int getUnwrapMode() {
		return unwrapMode;
	}


	public void setUnwrapRotation(int rot)
	{
		unwrapRotation = rot;
	}
	
	public int getUnwrapRotation() {
		return unwrapRotation;
	}

	public void setAlternateUnrwapDir(boolean b)
	{
		unwrapRotation = b ? 1 : 0;
	}

	public boolean getAlternateUnrwapDir()
	{
		return unwrapRotation > 0;
	}

	public void scaleAll(float size, boolean scaleUV)
	{
		startX *= size;
		startY *= size;
		startZ *= size;
		width *= size;
		height *= size;
		depth *= size;
		
		originX *= size;
		originY *= size;
		originZ *= size;
		
		if (scaleUV) {
			for (int i = 0; i < 6; i++) {
				Face f = faces[i];
				f.scaleSize(size);
			}	
		}
		
	}
	
}
