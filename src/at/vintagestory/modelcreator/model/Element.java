package at.vintagestory.modelcreator.model;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_LINES;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import at.vintagestory.modelcreator.enums.BlockFacing;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.util.GameMath;
import at.vintagestory.modelcreator.util.Mat4f;

public class Element
{
	private String name = "Cube";

	// Face Variables
	private int selectedFace = 0;
	private Face[] faces = new Face[6];

	// Element Variables
	private double startX = 0.0, startY = 0.0, startZ = 0.0;
	private double width = 16.0, height = 1.0, depth = 1.0;

	// Rotation Variables
	private double originX = 8, originY = 8, originZ = 8;
	private double rotationX = 0, rotationY = 0, rotationZ = 0;
	
	private boolean rescale = false;

	// Extra Variables
	private boolean shade = true;

	// Rotation Point Indicator
	private Sphere sphere = new Sphere();
	
	float[] brightnessByFace = new float[] { 1, 1, 1, 1, 1, 1 };

    /// <summary>
    /// Top, Front/Left, Back/Right, Bottom
    /// </summary>
    public static float[] DefaultBlockSideBrightness = new float[] {
        1f,
        0.7f,
        0.6f,
        0.4f
    };

    /// <summary>
    /// Shadings by Blockfacing index
    /// </summary>
    public static float[] DefaultBlockSideBrightnessByFacing = new float[] {
        DefaultBlockSideBrightness[2],
        DefaultBlockSideBrightness[1],
        DefaultBlockSideBrightness[2],
        DefaultBlockSideBrightness[1],
        DefaultBlockSideBrightness[0],
        DefaultBlockSideBrightness[3],
    };
    
    
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
		this.width = cuboid.getWidth();
		this.height = cuboid.getHeight();
		this.depth = cuboid.getDepth();
		this.startX = cuboid.getStartX();
		this.startY = cuboid.getStartY();
		this.startZ = cuboid.getStartZ();
		this.originX = cuboid.getOriginX();
		this.originY = cuboid.getOriginY();
		this.originZ = cuboid.getOriginZ();
		this.rotationX = cuboid.getRotationX();
		this.rotationY = cuboid.getRotationY();
		this.rotationZ = cuboid.getRotationZ();
		
		this.rescale = cuboid.shouldRescale();
		this.shade = cuboid.isShaded();
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
			return new FaceDimension(getWidth(), getHeight());
		case 1:
			return new FaceDimension(getDepth(), getHeight());
		case 2:
			return new FaceDimension(getWidth(), getHeight());
		case 3:
			return new FaceDimension(getDepth(), getHeight());
		case 4:
			return new FaceDimension(getWidth(), getDepth());
		case 5:
			return new FaceDimension(getWidth(), getDepth());
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
	

	public void draw()
	{
		float b;
		
		GL11.glPushMatrix();
		{
			GL11.glEnable(GL_BLEND);
			GL11.glDisable(GL_CULL_FACE);
			GL11.glTranslated(getOriginX(), getOriginY(), getOriginZ());
			rotateAxis();
			GL11.glTranslated(-getOriginX(), -getOriginY(), -getOriginZ());
			
			// North
			if (faces[0].isEnabled())
			{
				b = brightnessByFace[BlockFacing.NORTH.GetIndex()];
				
				GL11.glColor3f(b, 0, 0);
								
				faces[0].renderNorth(b);
			}

			// East
			if (faces[1].isEnabled())
			{
				b = brightnessByFace[BlockFacing.EAST.GetIndex()];
				
				GL11.glColor3f(0, b, 0);
				faces[1].renderEast(b);
			}

			// South
			if (faces[2].isEnabled())
			{
				b = brightnessByFace[BlockFacing.SOUTH.GetIndex()];
				
				GL11.glColor3f(0, 0, b);
				faces[2].renderSouth(b);
			}

			// West
			if (faces[3].isEnabled())
			{
				b = brightnessByFace[BlockFacing.WEST.GetIndex()];
				
				GL11.glColor3f(b, b, 0);
				faces[3].renderWest(b);
			}

			// Top
			if (faces[4].isEnabled())
			{
				b = brightnessByFace[BlockFacing.UP.GetIndex()];
				
				GL11.glColor3f(0, b, b);
				faces[4].renderUp(b);
			}

			// Bottom
			if (faces[5].isEnabled())
			{
				b = brightnessByFace[BlockFacing.DOWN.GetIndex()];
				
				GL11.glColor3f(b, 0, b);
				faces[5].renderDown(b);
			}
		}
		GL11.glPopMatrix();
	}

	public void drawExtras(IElementManager manager)
	{
		if (manager.getSelectedElement() == this)
		{
			GL11.glPushMatrix();
			{
				GL11.glTranslated(getOriginX(), getOriginY(), getOriginZ());
				GL11.glColor3f(0.25F, 0.25F, 0.25F);
				sphere.draw(0.2F, 16, 16);
				rotateAxis();
				GL11.glLineWidth(2F);
				GL11.glBegin(GL_LINES);
				{
					GL11.glColor3f(1, 0, 0);
					GL11.glVertex3i(-4, 0, 0);
					GL11.glVertex3i(4, 0, 0);
					GL11.glColor3f(0, 1, 0);
					GL11.glVertex3i(0, -4, 0);
					GL11.glVertex3i(0, 4, 0);
					GL11.glColor3f(0, 0, 1);
					GL11.glVertex3i(0, 0, -4);
					GL11.glVertex3i(0, 0, 4);
				}
				GL11.glEnd();
			}
			GL11.glPopMatrix();
		}
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
		GL11.glRotated(getRotationX(), 1, 0, 0);
		GL11.glRotated(getRotationY(), 0, 1, 0);
		GL11.glRotated(getRotationZ(), 0, 0, 1);
	}


	public Element copy()
	{
		return new Element(getWidth(), getHeight(), getDepth());
	}
}
