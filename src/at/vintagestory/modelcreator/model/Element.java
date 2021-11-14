package at.vintagestory.modelcreator.model;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_LINES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import at.vintagestory.modelcreator.util.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Project;
import at.vintagestory.modelcreator.enums.BlockFacing;
import at.vintagestory.modelcreator.interfaces.IDrawable;

import org.lwjgl.util.vector.Quaternion;
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
    
	
	public Element ParentElement;
	public ArrayList<Element> ChildElements = new ArrayList<Element>();
	public ArrayList<AttachmentPoint> AttachmentPoints = new ArrayList<AttachmentPoint>();
	
	public ArrayList<Element> StepChildElements = new ArrayList<Element>();
	Element stepParentElement;
	
	protected String name = "Cube1";
	public String stepparentName;
	protected boolean renderInEditor = true;
	
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
	protected double rotationX = 0;
	protected double rotationY = 0;
	protected double rotationZ = 0;
	
	protected boolean rescale = false;

	// Extra Variables
	protected boolean shade = true;
	protected boolean reflective = false;
	protected boolean gradientShade = false;
	protected String climateColorMap = null;
	protected String seasonColorMap = null;
	
	protected int renderPass = -1;
	protected int unwrapMode;
	protected int unwrapRotation;
	protected int ZOffset = 0;
	
	
	public int windMode = -1;
	public int windData = 0;
	public boolean DisableRandomDrawOffset = false;
	
	
	// Rotation Point Indicator
	protected Sphere sphere = new Sphere();
	
	
	public BlockFacing[] rotatedfaces = new BlockFacing[] { BlockFacing.NORTH, BlockFacing.EAST, BlockFacing.SOUTH, BlockFacing.WEST, BlockFacing.UP, BlockFacing.DOWN };
	
	
	public float[] brightnessByFace = new float[] { 1, 1, 1, 1, 1, 1 };
    
    public Element() {
    	
    }
    
	public Element(double width, double height, double depth)
	{
		this.width = width;
		this.height = height;
		this.depth = depth;
		initFaces();
		updateUV();
		recalculateBrightnessValues();
	}
	
	public Element(double width, double height) {
		name = "Face";
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
		this.windMode = cuboid.windMode;
		this.windData = cuboid.windData;
		this.climateColorMap = cuboid.climateColorMap;
		this.seasonColorMap = cuboid.climateColorMap;
		this.unwrapMode = cuboid.unwrapMode;
		this.unwrapRotation = cuboid.unwrapRotation;
		this.autoUnwrap = cuboid.autoUnwrap;
		this.texUStart = cuboid.texUStart;
		this.texVStart = cuboid.texVStart;
		
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
		
		if (enabled) {		
			for (int i = 0; i < faces.length; i++) {
				faces[i].setAutoUVEnabled(enabled);
			}
		}
		
		ModelCreator.DidModify();
	}
	
	
	public boolean isAutoUnwrapEnabled() {
		return autoUnwrap;
	}
	
	public void setRenderInEditor(boolean enabled) {
		if (renderInEditor == enabled) return;
		
		this.renderInEditor = enabled;
		ModelCreator.DidModify();
	}
	
	
	public boolean getRenderInEditor() {
		return renderInEditor;
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
		}
	}
	
	public void setTexture(ClipboardTexture texture, boolean recursive)
	{
		setTextureCode(texture.getTexture(), recursive);
	}

	public void setTextureCode(String texture, boolean recursive)
	{
		for (Face face : faces)
		{
			face.setTextureCode(texture);
		}
		
		if (recursive) {		
			for (Element elem : ChildElements) {
				elem.setTextureCode(texture, recursive);
			}
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
		draw(selectedElem, false, Mat4f.Identity_(new float[16]));
	}

	
	public void draw(IDrawable selectedElem, float[] mat)
	{
		draw(selectedElem, false, mat);
	}
	
	public void draw(IDrawable selectedElem, boolean drawCallFromStepParent, float[] mat) {
		if (!renderInEditor || (stepParentElement != null && !drawCallFromStepParent)) return;
		
		float b;
		
		GL11.glPushMatrix();
		{
			GL11.glEnable(GL_BLEND);
			GL11.glDisable(GL_CULL_FACE);
			
			ApplyTransform(mat);
			
			GL11.glTranslated(originX, originY, originZ);
			rotateAxis();
			GL11.glTranslated(-originX, -originY, -originZ);
			GL11.glTranslated(startX, startY, startZ);
			
			
			for (int i = 0; i < BlockFacing.ALLFACES.length; i++) {
				Vec3f bface = BlockFacing.ALLFACES[i].GetFacingVector();

				float[] out = Mat4f.MulWithVec4(mat, new float[] { bface.X, bface.Y, bface.Z, 0 });
				rotatedfaces[i] = BlockFacing.FromNormal(new Vec3f(out[0], out[1], out[2]));

				if (!faces[i].isEnabled()) continue;

				b = brightnessByFace[BlockFacing.ALLFACES[i].GetIndex()];
				Color c = Face.ColorsByFace[i];
				GL11.glColor3f(c.r * b, c.g * b, c.b * b);
								
				faces[i].renderFace(BlockFacing.ALLFACES[i], b, ModelCreator.WindPreview == 2 || (ModelCreator.WindPreview == 1 && selectedElem == this), mat);
			}
						
			for (int i = 0; i < ChildElements.size(); i++) {
				ChildElements.get(i).draw(selectedElem, Mat4f.CloneIt(mat));
			}
			
			for (int i = 0; i < StepChildElements.size(); i++) {
				StepChildElements.get(i).draw(selectedElem, true, Mat4f.CloneIt(mat));
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
			GL11.glLineWidth(1f);
			GL11.glBegin(GL11.GL_LINES);
			{
				if (ModelCreator.darkMode) {
					GL11.glColor4f(1f,1f,0.5f,1f);
				}
				else {
					GL11.glColor4f(0F, 0F, 0F, 0.5f);
				}
				
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
		
		
		GL11.glPushMatrix();
		{
		Face face = getSelectedFace();
			if (face != null && face.HoveredVertex >= 0) {
				int coordIndex = selectedFace * 12;
				
				GL11.glTranslated(originX, originY, originZ);
				rotateAxis();
				GL11.glTranslated(-originX, -originY, -originZ);
				GL11.glTranslated(startX, startY, startZ);

				
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glTranslated(
					0 + face.centerVec.X + face.sizeXyz.X * Face.CubeVertices[coordIndex + face.HoveredVertex*3] / 2, 
					face.centerVec.Y + face.sizeXyz.Y * Face.CubeVertices[coordIndex + face.HoveredVertex*3 + 1] / 2, 
					face.centerVec.Z + face.sizeXyz.Z * Face.CubeVertices[coordIndex + face.HoveredVertex*3 + 2] / 2
				);
				GL11.glColor3f(0.25F, 1f, 0.25F);
				sphere.draw(0.2F, 16, 16);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
		}
		GL11.glPopMatrix();
		
	}

	@Override
	public String toString()
	{
		return name;
	}

	public void rotate90DegAroundCenter(int xSteps, int ySteps, int zSteps)
	{
		// center offset
		double co = 8;
		originX -= co; originY -= co; originZ -= co;
		startX -= co; startY -= co; startZ -= co;

		rotate90Deg(xSteps, ySteps, zSteps);

		// move back
		originX += co; originY += co; originZ += co;
		startX += co; startY += co; startZ += co;
	}

	public void rotate90Deg(int xSteps, int ySteps, int zSteps)
	{
		Quaternion q = QUtil.IntrinsicXYZToQuaternion(
				xSteps * GameMath.PIHALF,
				ySteps * GameMath.PIHALF,
				zSteps * GameMath.PIHALF);

		float[] translation = new float[3];
		rotate90DegRecurse(q, translation);
	}

	private void rotate90DegRecurse(Quaternion q, float[] translation)
	{
		float[] newTranslation = rotate90DegImpl(q, translation);

		for (Element child : ChildElements) {
			child.rotate90DegRecurse(q, newTranslation);
		}
	}

	private float[] rotate90DegImpl(Quaternion q, float[] translation)
	{
		float[] matrix = Mat4f.Create();
		Mat4f.FromRotationTranslation(matrix,
				new float[] { q.x, q.y, q.z, q.w },
				translation);

		float[] vFrom = new float[] { (float) startX, (float) startY, (float) startZ, 1 };
		float[] vSize = new float[] { (float) width, (float) height, (float) depth, 0 };
		float[] vOrigin = new float[] { (float) originX, (float) originY, (float) originZ, 1 };

		float[] vRotationAxis = new float[] { 0, 0, 0, 0 };
		double theta = QUtil.IntrinsicXYZToAxisAngle(
				GameMath.DEG2RAD * rotationX,
				GameMath.DEG2RAD * rotationY,
				GameMath.DEG2RAD * rotationZ,
				vRotationAxis);

		vFrom = Mat4f.MulWithVec4(matrix, vFrom);
		vSize = Mat4f.MulWithVec4(matrix, vSize);
		vOrigin = Mat4f.MulWithVec4(matrix, vOrigin);
		vRotationAxis = Mat4f.MulWithVec4(matrix, vRotationAxis);

		double[] xyzRotation =
				Arrays.stream(QUtil.AxisAngleToIntrinsicXYZEuler(vRotationAxis, theta))
						.map(x -> GameMath.RAD2DEG * x)
						.map(x -> GameMath.round(x, 6))
						.toArray();

		VecUtil.Round(vFrom, 6);
		VecUtil.Round(vSize, 6);
		VecUtil.Round(vOrigin, 6);

		float[] newTranslation = fixSign(vFrom, vSize);

		startX = vFrom[0]; startY = vFrom[1]; startZ = vFrom[2];
		width = vSize[0]; height = vSize[1]; depth = vSize[2];
		originX = vOrigin[0]; originY = vOrigin[1]; originZ = vOrigin[2];
		rotationX = xyzRotation[0]; rotationY = xyzRotation[1]; rotationZ = xyzRotation[2];

		rotateFaces(matrix);
		rotateAnimationElements(matrix);

		ModelCreator.DidModify();
		return newTranslation;
	}

	// this has to be implemented here because we need the translation info from fixSign
	private void rotateAnimationElements(float[] matrix) {
    	float[] vOrigin = new float[] { 0, 0, 0, 1 };
    	float[] vOffset = new float[] { 0, 0, 0, 0 };
    	float[] vStretch = new float[] { 0, 0, 0, 0 };
    	float[] vRotationAxis = new float[] { 0, 0, 0, 0 };

    	for (Animation anim : ModelCreator.currentProject.Animations) {
    		for (Keyframe keyframe : anim.keyframes) {
    			if (keyframe == null) continue;

    			KeyFrameElement e = keyframe.GetKeyFrameElement(this);
    			if (e == null) continue;

    			vOrigin[0] = (float) e.getOriginX();
    			vOrigin[1] = (float) e.getOriginY();
    			vOrigin[2] = (float) e.getOriginZ();

    			vOffset[0] = (float) e.getOffsetX();
    			vOffset[1] = (float) e.getOffsetY();
    			vOffset[2] = (float) e.getOffsetZ();

    			vStretch[0] = (float) e.getStretchX();
    			vStretch[1] = (float) e.getStretchY();
    			vStretch[2] = (float) e.getStretchZ();

    			double theta = QUtil.IntrinsicXYZToAxisAngle(
						GameMath.DEG2RAD * e.getRotationX(),
						GameMath.DEG2RAD * e.getRotationY(),
						GameMath.DEG2RAD * e.getRotationZ(),
						vRotationAxis
				);

    			vOrigin = Mat4f.MulWithVec4(matrix, vOrigin);
    			vOffset = Mat4f.MulWithVec4(matrix, vOffset);
    			vStretch = Mat4f.MulWithVec4(matrix, vStretch);
    			vRotationAxis = Mat4f.MulWithVec4(matrix, vRotationAxis);

    			double[] xyzRotation =
						Arrays.stream(QUtil.AxisAngleToIntrinsicXYZEuler(vRotationAxis, theta))
								.map(x -> GameMath.RAD2DEG * x)
								.map(x -> GameMath.round(x, 6))
								.toArray();

				VecUtil.Round(vOrigin, 6);
				VecUtil.Round(vOffset, 6);
				VecUtil.Round(vStretch, 6);

				e.setOriginX(vOrigin[0]); e.setOriginY(vOrigin[1]); e.setOriginZ(vOrigin[2]);
				e.setOffsetX(vOffset[0]); e.setOffsetY(vOffset[1]); e.setOffsetZ(vOffset[2]);
				e.setStretchX(vStretch[0]); e.setStretchY(vStretch[1]); e.setStretchZ(vStretch[2]);
				e.setRotationX(xyzRotation[0]); e.setRotationY(xyzRotation[1]); e.setRotationZ(xyzRotation[2]);
			}

    		anim.framesDirty = true;
		}
	}

	private void rotateFaces(float[] matrix) {
		Face[] newFaces = new Face[faces.length];
		float[] normal = new float[] { 0, 0, 0, 0 };
		for (int i = 0; i < faces.length; ++i) {

			BlockFacing.ALLFACES[i].GetFacingVector().ToArray(normal);

			normal = Mat4f.MulWithVec4(matrix, normal);

			int face = BlockFacing.FromNormal(new Vec3f(normal)).GetIndex();
			newFaces[face] = faces[i];
		}

		System.arraycopy(newFaces, 0, faces, 0, faces.length);
	}

	private float[] fixSign(float[] vFrom, float[] vSize)
	{
		float[] translation = new float[3];

		for (int i = 0; i < 3; ++i) {
			if (vSize[i] >= 0) continue;

			float diff = -vSize[i];
			vFrom[i] -= diff;
			vSize[i] = diff;
			translation[i] = diff;
		}

		return translation;
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

	


	void setUnwrappedCubeUV() {
		if (unwrapMode <= 0) {
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
			// Fix any float imprecision first
			x = Math.round(x * 1000.0) / 1000.0;
			// Now round to the next closest pixel
			x = Math.ceil(x * scale.W) / scale.W;
		}
		
		aboveFace.textureU = x;
		aboveFace.textureV = y;
		aboveFace.updateUV();
		
		// Row 2
		if (aboveFace.isEnabled()) y += aboveFace.uvHeight();
		
		// Fix any float imprecision first
		y = Math.round(y * 1000.0) / 1000.0;
		// Now round to the next closest pixel
		y = Math.ceil(y * scale.H) / scale.H;
		
		x = getTexUStart();
		
		leftFace.textureU = x;
		leftFace.textureV = y;
		leftFace.updateUV();
		
		if (leftFace.isEnabled()) {
			x += leftFace.uvWidth();
			// Fix any float imprecision first
			x = Math.round(x * 1000.0) / 1000.0;
			// Now round to the next closest pixel
			x = Math.ceil(x * scale.W) / scale.W;
		}
		
		centerFace.textureU = x;
		centerFace.textureV = y;
		centerFace.updateUV();
		
		if (centerFace.isEnabled()) {
			x += centerFace.uvWidth();
			// Fix any float imprecision first
			x = Math.round(x * 1000.0) / 1000.0;
			// Now round to the next closest pixel
			x = Math.ceil(x * scale.W) / scale.W;
		}
		
		rightFace.textureU = x;
		rightFace.textureV = y;
		rightFace.updateUV();
		
		if (rightFace.isEnabled()) {
			x += rightFace.uvWidth();
			// Fix any float imprecision first
			x = Math.round(x * 1000.0) / 1000.0;
			// Now round to the next closest pixel
			x = Math.ceil(x * scale.W) / scale.W;
		}
		
		veryRightFace.textureU = x;
		veryRightFace.textureV = y;
		veryRightFace.updateUV();

		
		// Row 3
		x = getTexUStart();
		if (leftFace.isEnabled()) {
			x+= leftFace.uvWidth();
			// Fix any float imprecision first
			x = Math.round(x * 1000.0) / 1000.0;
			// Now round to the next closest pixel			
			x = Math.ceil(x * scale.W) / scale.W;
		}
		
		y += Math.max(leftFace.uvHeight(), Math.max(centerFace.uvHeight(), Math.max(rightFace.uvHeight(), veryRightFace.uvHeight())));
		// Fix any float imprecision first
		y = Math.round(y * 1000.0) / 1000.0;
		// Now round to the next closest pixel
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
		this.width = Math.max(width + amt, 0);
		ModelCreator.DidModify();
	}

	public void addHeight(double amt)
	{
		this.height = Math.max(height + amt, 0);
		ModelCreator.DidModify();
	}

	public void addDepth(double amt)
	{
		this.depth = Math.max(depth + amt, 0);;
		ModelCreator.DidModify();
	}

	public void setWidth(double width)
	{
		if (this.width == Math.max(0, width)) return;
		
		this.width = Math.max(0, width);
		ModelCreator.DidModify();
	}

	public void setHeight(double height)
	{
		if (this.height == Math.max(0, height)) return;
		
		this.height = Math.max(0, height);
		ModelCreator.DidModify();
	}

	public void setDepth(double depth)
	{
		if (this.depth == Math.max(0, depth)) return;
		
		this.depth = Math.max(0, depth);
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
	
	public boolean isReflective()
	{
		return reflective;
	}
	
	public boolean isGradientShaded()
	{
		return gradientShade;
	}

	public void setShade(boolean shade)
	{
		if (this.shade == shade) return;
		
		this.shade = shade;
		recalculateBrightnessValues();
		ModelCreator.DidModify();
	}
	
	
	public void setReflective(boolean reflective)
	{
		if (this.reflective == reflective) return;
		
		this.reflective = reflective;
		ModelCreator.DidModify();
	}
	
	public void setGradientShade(boolean shade)
	{
		this.gradientShade = shade;
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

	public String getClimateColorMap()
	{
		return climateColorMap;
	}

	public void setClimateColorMap(String climateColorMap)
	{
		if (this.climateColorMap == climateColorMap) return;
		
		this.climateColorMap = climateColorMap;
	}
	
	
	public String getSeasonColorMap()
	{
		return seasonColorMap;
	}

	public void setSeasonColorMap(String seasonColorMap)
	{
		if (this.seasonColorMap == seasonColorMap) return;
		
		this.seasonColorMap = seasonColorMap;
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
	
	public int getWindMode()
	{
		return windMode;
	}

	public void setWindMode(int mode)
	{
		if (this.windMode == mode) return;
		
		this.windMode = mode;
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
		cloned.climateColorMap = climateColorMap;
		cloned.seasonColorMap = seasonColorMap;
		cloned.renderPass = renderPass;
		cloned.sphere = sphere;
		cloned.texUStart = texUStart;
		cloned.texVStart = texVStart;
		cloned.autoUnwrap = autoUnwrap;
		cloned.unwrapMode = unwrapMode;
		cloned.unwrapRotation = unwrapRotation;
		cloned.stepparentName = stepparentName;
		cloned.renderInEditor = renderInEditor;
		cloned.windData = windData;
		cloned.windMode = windMode;
		
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

	public void ApplyTransform(float[] matrix)
	{
		Mat4f.Translate(matrix, matrix, new float[] { (float)originX, (float)originY, (float)originZ });
		Mat4f.RotateX(matrix, matrix, (float)rotationX * GameMath.DEG2RAD);
		Mat4f.RotateY(matrix, matrix, (float)rotationY * GameMath.DEG2RAD);
		Mat4f.RotateZ(matrix, matrix, (float)rotationZ * GameMath.DEG2RAD);
		Mat4f.Translate(matrix, matrix, new float[] { (float)-originX, (float)-originY, (float)-originZ });
		
		Mat4f.Translate(matrix, matrix, new float[] { (float)startX, (float)startY, (float)startZ });
	}

	
	public void setIsBackdrop()
	{
		for (Face face : faces) {
			face.setIsBackdrop();
		}
		
		for (Element elem : ChildElements) {
			elem.setIsBackdrop();
		}
	}
	
	
	public void onRemoved() {
		setStepParent(null);
		for (Element elem : ChildElements) {
			elem.onRemoved();
		}
		
		for (Element elem : StepChildElements) {
			elem.setStepParent(null);
		}
	}

	public String getStepParent() {
		return stepparentName;
	}
	
	
	public String getName() {
		return name;
	}
	
	public int getZOffset() {
		return ZOffset;
	}
	
	public void setZOffset(int value) {
		this.ZOffset = value;
	}
	
	public void setName(String name) {
		this.name = name;
		
		for (Element elem : StepChildElements) {
			elem.stepparentName = name;
		}
		
		ModelCreator.DidModify();
	}
	
	

	public void setStepParent(String elemName)
	{
		Element prevStepElem = stepParentElement;
		
		if (stepParentElement != null) {
			stepParentElement.StepChildElements.remove(stepParentElement);
			stepParentElement = null;
		}
		
		if (elemName == null && stepparentName != null) {
			stepparentName = null;
			ModelCreator.DidModify();
			return;
		}
		
		stepparentName = elemName;
		
		if (elemName != null) {		
			Element element = ModelCreator.currentProject.findElement(elemName);
			
			if (element != null) {
				element.StepChildElements.add(this);
				stepParentElement = element;
				if (element != prevStepElem) ModelCreator.DidModify();				
				return;
			}
			
			
			if (ModelCreator.currentBackdropProject != null) {
				element = ModelCreator.currentBackdropProject.findElement(elemName);
			
				if (element != null) {
					element.StepChildElements.add(this);
					stepParentElement = element;
					if (element != prevStepElem) ModelCreator.DidModify();
				}
			}
		}
	}

	
	public void reloadStepparentRelationShip() {
		
		clearStepparentRelationShip();
		
		for (Element elem : ChildElements) {
			elem.reloadStepparentRelationShip();
		}
		
		setStepParent(stepparentName);
	}

	public void clearStepparentRelationShip()
	{
		for (Element elem : ChildElements) {
			elem.clearStepparentRelationShip();
		}
		StepChildElements.clear();
	}

	public void RandomizeTexture()
	{
		if (ModelCreator.currentProject.EntityTextureMode && autoUnwrap) {
			double uMin = 9999, vMin = 9999, uMax = -9999, vMax = -9999;
			
			Sized scale=null;
			
			double texWidth = ModelCreator.currentProject.TextureWidth;
			double texHeight = ModelCreator.currentProject.TextureHeight;
			
			
			for (int i = 0; i < faces.length; i++) {
				Face face = faces[i];
				if (!face.isEnabled()) continue;
				
				uMin = Math.min(face.textureU, uMin);
				vMin = Math.min(face.textureV, vMin);
				
				uMax = Math.max(face.textureUEnd, uMax);
				vMax = Math.max(face.textureVEnd, vMax);
				
				TextureEntry entry = face.getTextureEntry();
				scale = face.getVoxel2PixelScale();
				if (entry != null) {
					texWidth = entry.Width  / scale.W;
					texHeight = entry.Height / scale.H;
				}				
			}
			
			double wiggleRoomU = texWidth - (uMax - uMin); 			
			double wiggleRoomV = texHeight - (vMax - vMin);
			
			double ustart = Math.floor(Face.rand.nextFloat() * wiggleRoomU * scale.W) / scale.W;
			double vstart = Math.floor(Face.rand.nextFloat() * wiggleRoomV * scale.H) / scale.H;

			texUStart = ustart;
			texVStart = vstart;
			updateUV();
			
		} else {
			for (int i = 0; i < faces.length; i++) {
				faces[i].RandomizeTexture();
			}	
		}
		
		for (Element elem : ChildElements) { 
			elem.RandomizeTexture();
		}
		
		ModelCreator.DidModify();
	}

	
	
	
	public void reduceDecimals()
	{
		startX = ((int)(startX * 10)) / 10.0; 
		startY = ((int)(startY * 10)) / 10.0; 
		startZ = ((int)(startZ * 10)) / 10.0;
		width = ((int)(width * 10)) / 10.0;
		height = ((int)(height * 10)) / 10.0;
		depth = ((int)(depth * 10)) / 10.0;;

		originX = ((int)(originX * 10)) / 10.0;
		originY = ((int)(originY * 10)) / 10.0;
		originZ = ((int)(originZ * 10)) / 10.0;
		rotationX = ((int)(rotationX * 10)) / 10.0;
		rotationY = ((int)(rotationY * 10)) / 10.0;
		rotationZ = ((int)(rotationZ * 10)) / 10.0;
		
		for (Element elem : ChildElements) { 
			elem.reduceDecimals();
		}
	}
	
	
	
	
	
	
	
	
}

