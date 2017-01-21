package at.vintagestory.modelcreator.model;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.enums.BlockFacing;
import at.vintagestory.modelcreator.gui.texturedialog.TextureDialog;

public class Face
{
	// NS = Z
	// WE = X
	// UD = Y
	public static final Color[] ColorsByFace = new Color[] {
			new Color(179, 193, 255),
			new Color(255, 179, 179),
			new Color(179, 193, 255), 
			new Color(255, 179, 179),
			new Color(187, 255, 179),
			new Color(187, 255, 179)
	};
	
	public static float[] CubeVertices = {
        // North face
        0, 0, 0,
        0,  1, 0,
        1,  1, 0,
        1, 0, 0,

        // East face
        1, 0,  1,     // bot right
        1, 0, 0,     // bot left
        1,  1, 0,     // top left
        1,  1,  1,     // top right

        // South face
        0, 0,  1,
        1, 0,  1,
        1,  1,  1,
        0,  1,  1,

        // West face
        0, 0, 0,
        0, 0,  1,
        0,  1,  1,
        0,  1, 0,
        
        // Top face
        0,  1,  1,
        1,  1,  1,
        1,  1, 0,
        0,  1, 0,
                      
        // Bottom face
        0, 0, 0,
        1, 0, 0,
        1, 0,  1,
        0, 0,  1
    };
	
	public static int[] cubeUVCoords = {
            // North
            1, 1,
            1, 0,
            0, 0,
            0, 1,

            // East 
            0, 1,
            1, 1,
            1, 0,
            0, 0,

            // South
            0, 1,
            1, 1,
            1, 0,
            0, 0,
            
            // West
            0, 1,
            1, 1,
            1, 0,
            0, 0,

            // Top face
            1, 0,
            0, 0,
            0, 1,
            1, 1,

            // Bottom face
            1, 0,
            0, 0,
            0, 1,
            1, 1,
	};

	
	
	private String texture = null;
	private String textureLocation = "blocks/";
	private double textureU = 0;
	private double textureV = 0;
	private double textureUEnd = 16;
	private double textureVEnd = 16;
	private boolean binded = false;
	private boolean cullface = false;
	private boolean enabled = true;
	private boolean autoUV = true;
	private int rotation;

	private Element cuboid;
	private int side;
	private int glow;

	public Face() {
		
	}
	
	public Face(Element cuboid, int side)
	{
		this.cuboid = cuboid;
		this.side = side;
		
		if (ModelCreator.singleTextureMode) {
			if (ModelCreator.currentProject.Textures.size() > 0) {
				this.texture = ModelCreator.currentProject.Textures.get(0).name;
			}
			setUnwrappedCubeUV();
			updateUV();
		}
	}
	
	public void renderFace(BlockFacing blockFacing, float brightness)
	{
		GL11.glPushMatrix();
		{
			GL11.glEnable(GL_TEXTURE_2D);
			GL11.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			bindTexture();

			if (binded) GL11.glColor3f(brightness, brightness, brightness);
			
			int coordIndex = blockFacing.GetIndex() * 12;
			int uvIndex = blockFacing.GetIndex() * 8;
			
			GL11.glBegin(GL11.GL_QUADS);
			{
				for (int j = 0; j < 4; j++) {
					GL11.glTexCoord2d(
							(cubeUVCoords[uvIndex++]==0 ? textureU : textureUEnd) / 16, 
							(cubeUVCoords[uvIndex++]==0 ? textureV : textureVEnd) / 16
					);
					GL11.glVertex3d(cuboid.getWidth() * CubeVertices[coordIndex++], cuboid.getHeight() * CubeVertices[coordIndex++], cuboid.getDepth() * CubeVertices[coordIndex++]);
				}
			}
			GL11.glEnd();

			GL11.glDisable(GL_TEXTURE_2D);
		}
		GL11.glPopMatrix();
	}
	



	public void setTexture(String texture)
	{
		this.texture = texture;
	}

	public void bindTexture()
	{
		TextureImpl.bindNone();
		if (texture != null)
		{
			TextureEntry entry = TextureDialog.getTextureEntry(texture);
			if (entry != null)
			{
				if (entry.getTexture() != null)
				{
					GL11.glColor3f(1.0F, 1.0F, 1.0F);
					entry.getTexture().bind();
				}

				binded = true;
			}
		}
	}

	public void moveTextureU(double amt)
	{
		this.textureU += amt;
		this.textureUEnd += amt;
		ModelCreator.DidModify();
	}

	public void moveTextureV(double amt)
	{
		this.textureV += amt;
		this.textureVEnd += amt;
		ModelCreator.DidModify();
	}

	public void addTextureX(double amt)
	{
		this.textureU += amt;
		ModelCreator.DidModify();
	}

	public void addTextureY(double amt)
	{
		this.textureV += amt;
		ModelCreator.DidModify();
	}

	public void addTextureXEnd(double amt)
	{
		this.textureUEnd += amt;
		ModelCreator.DidModify();
	}

	public void addTextureYEnd(double amt)
	{
		this.textureVEnd += amt;
		ModelCreator.DidModify();
	}

	public double getStartU()
	{
		return textureU;
	}

	public double getStartV()
	{
		return textureV;
	}

	public double getEndU()
	{
		return textureUEnd;
	}

	public double getEndV()
	{
		return textureVEnd;
	}

	public void setStartU(double u)
	{
		textureU = u;
		ModelCreator.DidModify();
	}

	public void setStartV(double v)
	{
		textureV = v;
		ModelCreator.DidModify();
	}

	public void setEndU(double ue)
	{
		textureUEnd = ue;
		ModelCreator.DidModify();
	}

	public void setEndV(double ve)
	{
		textureVEnd = ve;
		ModelCreator.DidModify();
	}

	public String getTextureName()
	{
		return texture;
	}

	public Texture getTexture()
	{
		return TextureDialog.getTexture(texture);
	}

	public String getTextureLocation()
	{
		return textureLocation;
	}

	public void setTextureLocation(String textureLocation)
	{
		this.textureLocation = textureLocation;
		ModelCreator.DidModify();
	}

	public int getSide()
	{
		return side;
	}

	public boolean isCullfaced()
	{
		return cullface;
	}

	public void setCullface(boolean cullface)
	{
		this.cullface = cullface;
		ModelCreator.DidModify();
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
		ModelCreator.DidModify();
	}

	public boolean isAutoUVEnabled()
	{
		return autoUV;
	}

	public void setAutoUVEnabled(boolean enabled)
	{
		this.autoUV = enabled;
		ModelCreator.DidModify();
	}

	public boolean isBinded()
	{
		return binded;
	}

	public void updateUV()
	{
		if (autoUV)
		{
			if (rotation == 0 || rotation == 2) {
				textureUEnd = textureU + cuboid.getFaceDimension(side).getWidth();
				textureVEnd = textureV + cuboid.getFaceDimension(side).getHeight();	
			} else {
				textureUEnd = textureU + cuboid.getFaceDimension(side).getHeight();
				textureVEnd = textureV + cuboid.getFaceDimension(side).getWidth();
			}
			
		}
	}
	
	public boolean isCompatibleToAutoUV() {
		return
				(
					(rotation == 0 || rotation == 2) &&
					textureUEnd == textureU + cuboid.getFaceDimension(side).getWidth() && 
					textureVEnd == textureV + cuboid.getFaceDimension(side).getHeight()
				) ||
				(
					(rotation == 1 || rotation == 3) &&
					textureUEnd == textureU + cuboid.getFaceDimension(side).getHeight() && 
					textureVEnd == textureV + cuboid.getFaceDimension(side).getWidth()
				)
		;
	}

	public static String getFaceName(int face)
	{
		switch (face)
		{
		case 0:
			return "north";
		case 1:
			return "east";
		case 2:
			return "south";
		case 3:
			return "west";
		case 4:
			return "up";
		case 5:
			return "down";
		}
		return null;
	}

	public static int getFaceSide(String name)
	{
		switch (name)
		{
		case "north":
			return 0;
		case "east":
			return 1;
		case "south":
			return 2;
		case "west":
			return 3;
		case "up":
			return 4;
		case "down":
			return 5;
		}
		return -1;
	}

	public static Color getFaceColour(int side)
	{
		return ColorsByFace[side];
	}

	public int getGlow() {
		return glow;
	}
	
	public void setGlow(int glow) {
		this.glow = glow;
		ModelCreator.DidModify();
	}
	
	public int getRotation()
	{
		return rotation;
	}

	public void setRotation(int rotation)
	{
		this.rotation = rotation;
		ModelCreator.DidModify();
		updateUV();
	}
	
	
	public Face clone(Element forElement) {
		Face cloned = new Face();
		cloned.texture = texture;
		cloned.textureLocation = textureLocation;
		cloned.textureU = textureU;
		cloned.textureV = textureV;
		cloned.textureUEnd = textureUEnd;
		cloned.textureVEnd = textureVEnd;
		cloned.binded = binded;
		cloned.cullface = cullface;
		cloned.enabled = enabled;
		cloned.autoUV = autoUV;
		cloned.rotation = rotation;
		cloned.cuboid = forElement;
		cloned.side = side;
		cloned.glow = glow;
		return cloned;
	}
	
	
	void setUnwrappedCubeUV() {
		
		switch (side) {
			case 0: // N 
				textureU = 0;
				textureV = cuboid.getFaceDimension(5).getHeight();
				break;
			
			case 1: // E
				textureU = cuboid.getFaceDimension(0).getWidth();
				textureV = cuboid.getFaceDimension(5).getHeight();
				break;
			
			case 2: // S
				textureU = cuboid.getFaceDimension(0).getWidth() + cuboid.getFaceDimension(1).getWidth();
				textureV = cuboid.getFaceDimension(5).getHeight();
				break;
			
			case 3: // W
				textureU = cuboid.getFaceDimension(0).getWidth() + cuboid.getFaceDimension(1).getWidth() + cuboid.getFaceDimension(2).getWidth();
				textureV = cuboid.getFaceDimension(5).getHeight();
				break;
			case 4: // U
				textureU = cuboid.getFaceDimension(0).getWidth();
				textureV = 0;
				break;
			case 5: // D
				textureU = cuboid.getFaceDimension(0).getWidth() + cuboid.getFaceDimension(1).getWidth();
				textureV = 0;
				break;
		}
		
	}

}
