package at.vintagestory.modelcreator.model;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_LINES;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;
import at.vintagestory.modelcreator.enums.BlockFacing;
import at.vintagestory.modelcreator.util.GameMath;
import at.vintagestory.modelcreator.util.Mat4f;
import org.newdawn.slick.Color;

public class Element
{
	static int nextOpenGlName = 0;
	
	public Element ParentElement;
	public ArrayList<Element> ChildElements = new ArrayList<Element>();
	
	
	public String name = "Cube";
	
	public int openGlName = 0;
	
	// Face Variables
	protected int selectedFace = 0;
	protected Face[] faces = new Face[6];

	// Element Variables
	protected double startX = 0.0, startY = 0.0, startZ = 0.0;
	protected double width = 16.0, height = 1.0, depth = 1.0;

	// Rotation Variables
	protected double originX = 0, originY = 0, originZ = 0;
	protected double rotationX = 0, rotationY = 0, rotationZ = 0;
	
	protected boolean rescale = false;

	// Extra Variables
	protected boolean shade = true;

	// Rotation Point Indicator
	protected Sphere sphere = new Sphere();
	
	public float[] brightnessByFace = new float[] { 1, 1, 1, 1, 1, 1 };

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
	
	public Element(Element cuboid) {
		this(cuboid, false);
	}

	public Element(Element cuboid, boolean keepName)
	{
		if (keepName) {
			this.name = cuboid.name;
		} else {	
			String numberStr = "";
			int pos = cuboid.name.length() - 1;
			while (pos > 0) {
				if (Character.isDigit(cuboid.name.charAt(pos))) {
					numberStr = cuboid.name.charAt(pos) + numberStr;
				} else break;
				pos--;
			}
			
			int number = numberStr.length() > 0 ? Integer.parseInt(numberStr) : 2;
			
			this.name = cuboid.name.substring(0, cuboid.name.length() - numberStr.length()) + number;
		}
		
		
		openGlName = nextOpenGlName++;
		
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
		
		this.rescale = cuboid.rescale;
		this.shade = cuboid.shade;
		this.selectedFace = cuboid.getSelectedFaceIndex();
		initFaces();
		for (int i = 0; i < faces.length; i++)
		{
			Face oldFace = cuboid.getAllFaces()[i];
			faces[i].fitTexture(oldFace.shouldFitTexture());
			faces[i].setTexture(oldFace.getTextureName());
			faces[i].setTextureLocation(oldFace.getTextureLocation());
			faces[i].setStartU(oldFace.getStartU());
			faces[i].setStartV(oldFace.getStartV());
			faces[i].setEndU(oldFace.getEndU());
			faces[i].setEndV(oldFace.getEndV());
			faces[i].setCullface(oldFace.isCullfaced());
			faces[i].setEnabled(oldFace.isEnabled());
			faces[i].setAutoUVEnabled(oldFace.isAutoUVEnabled());
			faces[i].setRotation(oldFace.getRotation());
		}
		
		for (Element child : cuboid.ChildElements) {
			ChildElements.add(new Element(child, true));
		}
		
		updateUV();
		recalculateBrightnessValues();
		
		
	}

	public void initFaces()
	{
		for (int i = 0; i < faces.length; i++)
			faces[i] = new Face(this, i);
	}

	public void setSelectedFace(int face)
	{
		this.selectedFace = face;
	}

	public Face getSelectedFace()
	{
		return faces[selectedFace];
	}

	public int getSelectedFaceIndex()
	{
		return selectedFace;
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
			if (face.getExists())
			{
				id = face.getSide();
			}
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
			face.setTexture(null);
			face.setTextureLocation("blocks/");
		}
	}
	
	public void setAllTextures(ClipboardTexture texture)
	{
		setAllTextures(texture.getLocation(), texture.getTexture());
	}

	public void setAllTextures(String location, String texture)
	{
		for (Face face : faces)
		{
			face.setTexture(texture);
			face.setTextureLocation(location);
		}
	}
	
	
	
	public void recalculateBrightnessValues() {
		for (int i = 0; i < BlockFacing.ALLFACES.length; i++) {
			if (shade) {
				brightnessByFace[i] = getFaceBrightness(BlockFacing.ALLFACES[i], (float)rotationX, (float)rotationY, (float)rotationZ);	
			} else {
				brightnessByFace[i] = 1;
			}
			
		}
	}
	
	public float getFaceBrightness(BlockFacing facing, float rotX, float rotY, float rotZ) {
		float[] matrix = Mat4f.Create();
        
        Mat4f.RotateX(matrix, matrix, rotX * GameMath.DEG2RAD);            
        Mat4f.RotateY(matrix, matrix, rotY * GameMath.DEG2RAD);
        Mat4f.RotateZ(matrix, matrix, rotZ * GameMath.DEG2RAD);
        
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
	

	public void draw(Element selectedEleme)
	{
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
				ChildElements.get(i).draw(selectedEleme);
			}

		}
		GL11.glPopMatrix();
		
		if (selectedEleme == this) {
			drawSelectionExtras();
		}
		
	}

	public void drawSelectionExtras()
	{
		GL11.glLineWidth(1f);
		GL11.glPushMatrix();
		{
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
		}
		GL11.glPopMatrix();

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
		for (Face face : faces)
		{
			face.updateUV();
		}
	}

	public void rotateAxis()
	{
		GL11.glRotated(rotationX, 1, 0, 0);
		GL11.glRotated(rotationY, 0, 1, 0);
		GL11.glRotated(rotationZ, 0, 0, 1);
	}


	public Element copy()
	{
		return new Element(width, height, depth);
	}
	

	public void addStartX(double amt)
	{
		this.startX += amt;
	}

	public void addStartY(double amt)
	{
		this.startY += amt;
	}

	public void addStartZ(double amt)
	{
		this.startZ += amt;
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
		this.startX = amt;
	}

	public void setStartY(double amt)
	{
		this.startY = amt;
	}

	public void setStartZ(double amt)
	{
		this.startZ = amt;
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
	}

	public void addHeight(double amt)
	{
		this.height += amt;
	}

	public void addDepth(double amt)
	{
		this.depth += amt;
	}

	public void setWidth(double width)
	{
		this.width = width;
	}

	public void setHeight(double height)
	{
		this.height = height;
	}

	public void setDepth(double depth)
	{
		this.depth = depth;
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
	}

	public void addOriginY(double amt)
	{
		this.originY += amt;
	}

	public void addOriginZ(double amt)
	{
		this.originZ += amt;
	}

	public void setOriginX(double amt)
	{
		this.originX = amt;
	}

	public void setOriginY(double amt)
	{
		this.originY = amt;
	}

	public void setOriginZ(double amt)
	{
		this.originZ = amt;
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
		this.rotationX = rotation;
		recalculateBrightnessValues();
	}

	public void setRotationY(double rotation)
	{
		this.rotationY = rotation;
		recalculateBrightnessValues();
	}
	
	public void setRotationZ(double rotation)
	{
		this.rotationZ = rotation;
		recalculateBrightnessValues();
	}
	

	public void setRescale(boolean rescale)
	{
		this.rescale = rescale;
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
		this.shade = shade;
		recalculateBrightnessValues();
	}

	public void setName(String name)
	{
		this.name = name;
	}
	

}
