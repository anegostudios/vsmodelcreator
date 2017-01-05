package at.vintagestory.modelcreator.model;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import at.vintagestory.modelcreator.TextureManager;
import at.vintagestory.modelcreator.enums.BlockFacing;

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
        1, 0, 0,     // bot left
        1,  1, 0,     // top left
        1,  1,  1,     // top right
        1, 0,  1,     // bot right

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
        0,  1, 0,
        0,  1,  1,
        1,  1,  1,
        1,  1, 0,
                      
        // Bottom face
        0, 0, 0,
        1, 0, 0,
        1, 0,  1,
        0, 0,  1
    };

	private String texture = null;
	private String textureLocation = "blocks/";
	private double textureU = 0;
	private double textureV = 0;
	private double textureUEnd = 16;
	private double textureVEnd = 16;
	private boolean fitTexture = false;
	private boolean binded = false;
	private boolean cullface = false;
	private boolean enabled = true;
	private boolean exists = true;
	private boolean autoUV = true;
	private int rotation;

	private Element cuboid;
	private int side;
	private int glow;

	public Face(Element cuboid, int side)
	{
		this.cuboid = cuboid;
		this.side = side;
	}
	
	public void renderFace(BlockFacing blockFacing, float brightness) {
		TextureEntry entry = TextureManager.getTextureEntry(texture);
		int passes = 1;

		if (entry != null) {
			passes = entry.getPasses();
		}

		for (int i = 0; i < passes; i++)
		{
			renderFace(blockFacing, i, brightness);
		}
	}
	

	public void renderFace(BlockFacing blockFacing, int pass, float brightness)
	{
		GL11.glPushMatrix();
		{
			startRender(pass);

			if (binded) GL11.glColor3f(brightness, brightness, brightness);
			
			int i = blockFacing.GetIndex() * 12;
			
			GL11.glBegin(GL11.GL_QUADS);
			{
				for (int j = 0; j < 4; j++) {
					if (binded) setTexCoord(j);
					GL11.glVertex3d(cuboid.getStartX() + cuboid.getWidth() * CubeVertices[i++], cuboid.getStartY() + cuboid.getHeight() * CubeVertices[i++], cuboid.getStartZ() + cuboid.getDepth() * CubeVertices[i++]);					
				}
			}
			GL11.glEnd();

			finishRender();
		}
		GL11.glPopMatrix();
	}
	

	public void setTexCoord(int corner)
	{
		setTexCoord(corner, false);
	}

	public void setTexCoord(int corner, boolean forceFit)
	{
		int coord = corner + rotation;
		if (coord == 0 | coord == 4)
			GL11.glTexCoord2d(fitTexture | forceFit ? 0 : (textureU / 16), fitTexture | forceFit ? 1 : (textureVEnd / 16));
		if (coord == 1 | coord == 5)
			GL11.glTexCoord2d(fitTexture | forceFit ? 1 : (textureUEnd / 16), fitTexture | forceFit ? 1 : (textureVEnd / 16));
		if (coord == 2 | coord == 6)
			GL11.glTexCoord2d(fitTexture | forceFit ? 1 : (textureUEnd / 16), fitTexture | forceFit ? 0 : (textureV / 16));
		if (coord == 3)
			GL11.glTexCoord2d(fitTexture | forceFit ? 0 : (textureU / 16), fitTexture | forceFit ? 0 : (textureV / 16));
	}

	public void startRender(int pass)
	{
		GL11.glEnable(GL_TEXTURE_2D);
		GL11.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		bindTexture(pass);
	}

	public void finishRender()
	{
		GL11.glDisable(GL_TEXTURE_2D);
	}

	public void setTexture(String texture)
	{
		this.texture = texture;
	}

	public void bindTexture(int pass)
	{
		TextureImpl.bindNone();
		if (texture != null)
		{
			TextureEntry entry = TextureManager.getTextureEntry(texture);
			if (entry != null)
			{
				if (pass == 0)
				{
					if (entry.getTexture() != null)
					{
						GL11.glColor3f(1.0F, 1.0F, 1.0F);
						entry.getTexture().bind();
					}
				}
				else if (pass == 1)
				{
					if (entry.isAnimated() && entry.getNextTexture() != null)
					{
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						GL11.glDepthFunc(GL11.GL_EQUAL);

						entry.getNextTexture().bind();
						GL11.glColor4d(1.0D, 1.0D, 1.0D, entry.getAnimation().getFrameInterpolation());
					}
				}

				if (entry.hasProperties() && entry.getProperties().isBlurred())
				{
					GL11.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
					GL11.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				}

				binded = true;
			}
		}
	}

	public void moveTextureU(double amt)
	{
		this.textureU += amt;
		this.textureUEnd += amt;
	}

	public void moveTextureV(double amt)
	{
		this.textureV += amt;
		this.textureVEnd += amt;
	}

	public void addTextureX(double amt)
	{
		this.textureU += amt;
	}

	public void addTextureY(double amt)
	{
		this.textureV += amt;
	}

	public void addTextureXEnd(double amt)
	{
		this.textureUEnd += amt;
	}

	public void addTextureYEnd(double amt)
	{
		this.textureVEnd += amt;
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
	}

	public void setStartV(double v)
	{
		textureV = v;
	}

	public void setEndU(double ue)
	{
		textureUEnd = ue;
	}

	public void setEndV(double ve)
	{
		textureVEnd = ve;
	}

	public String getTextureName()
	{
		return texture;
	}

	public Texture getTexture()
	{
		return TextureManager.getTexture(texture);
	}

	public String getTextureLocation()
	{
		return textureLocation;
	}

	public void setTextureLocation(String textureLocation)
	{
		this.textureLocation = textureLocation;
	}

	public void fitTexture(boolean fitTexture)
	{
		this.fitTexture = fitTexture;
	}

	public boolean shouldFitTexture()
	{
		return fitTexture;
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
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
		if (enabled) exists = true;
	}

	public boolean isAutoUVEnabled()
	{
		return autoUV;
	}

	public void setAutoUVEnabled(boolean enabled)
	{
		this.autoUV = enabled;
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
	}
	
	public int getRotation()
	{
		return rotation;
	}

	public void setRotation(int rotation)
	{
		this.rotation = rotation;
		updateUV();
	}

	public void setExists(boolean exists)
	{
		this.exists = exists;
		if (!exists) enabled = false;
	}
	
	public boolean getExists() {
		return exists;
	}

}
