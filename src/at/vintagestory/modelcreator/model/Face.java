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
        1,  1, 0,
        0,  1, 0,
        0,  1,  1,
        1,  1,  1,
                      
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

	
	
	private String textureName = null;
	public double textureU = 0;
	public double textureV = 0;
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
		applySingleTextureMode();
	}
	
	public void applySingleTextureMode() {
		if (ModelCreator.currentProject != null && ModelCreator.currentProject.EntityTextureMode && ModelCreator.currentProject.Textures != null && ModelCreator.currentProject.Textures.size() > 0) {
			//this.textureName = ModelCreator.currentProject.Textures.get(0).name;
			
			this.textureName = ModelCreator.currentProject.Textures.values().iterator().next().name;
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
			int uvBaseIndex = blockFacing.GetIndex() * 8;
			int uvIndex = 0;
			
			double[] scale = textureScale();

			
			GL11.glBegin(GL11.GL_QUADS);
			{
				for (int j = 0; j < 4; j++) {
					GL11.glTexCoord2d(
							(cubeUVCoords[uvBaseIndex + (2 * rotation + uvIndex++) % 8]==0 ? textureU : textureUEnd) / scale[0] / 16, 
							(cubeUVCoords[uvBaseIndex + (2 * rotation + uvIndex++) % 8]==0 ? textureV : textureVEnd) / scale[1] / 16
					);
					GL11.glVertex3d(cuboid.getWidth() * CubeVertices[coordIndex++], cuboid.getHeight() * CubeVertices[coordIndex++], cuboid.getDepth() * CubeVertices[coordIndex++]);
				}
			}
			GL11.glEnd();

			GL11.glDisable(GL_TEXTURE_2D);
		}
		GL11.glPopMatrix();
	}
	

	public double[] textureScale() {
		double scaleX = ModelCreator.noTexScale;
		double scaleY = ModelCreator.noTexScale;
		
		if (ModelCreator.currentProject != null) {
			double texWidth = ModelCreator.currentProject.TextureWidth;
			double texHeight = ModelCreator.currentProject.TextureHeight;
			
			TextureEntry entry = ModelCreator.currentProject.getTextureEntry(textureName);
			if (entry != null) {
				scaleX = entry.Width / texWidth * (texWidth / 32);
				scaleY = entry.Height / texHeight * (texHeight / 32);				
			}
		}
		
		return new double[] { scaleX, scaleY };
	}


	public void setTextureName(String texture)
	{
		this.textureName = texture;
	}

	public void bindTexture()
	{
		TextureImpl.bindNone();
		if (textureName != null)
		{
			TextureEntry entry = ModelCreator.currentProject.getTextureEntry(textureName);
			if (entry != null)
			{
				if (entry.getTexture() != null)
				{
					GL11.glColor3f(1.0F, 1.0F, 1.0F);
					entry.getTexture().bind();
				}

				binded = true;
			}
		} else {
			binded = false;
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
		return textureName;
	}

	public Texture getTexture()
	{
		return ModelCreator.currentProject.getTexture(textureName);
	}
	
	public TextureEntry getTextureEntry()
	{
		return ModelCreator.currentProject.getTextureEntry(textureName);
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
			// We prevent subpixel UV mapping so that one can still resize elements slighty to fix z-fighting
			// without messing up the UV map
			double[] scale = textureScale();
			
			if (rotation == 0 || rotation == 2) {
				textureUEnd = textureU + Math.floor(cuboid.getFaceDimension(side).getWidth() * scale[0] + 0.000001) / scale[0];      // Stupid rounding errors -.-
				textureVEnd = textureV + Math.floor(cuboid.getFaceDimension(side).getHeight() * scale[1] + 0.000001) / scale[1];	
			} else {
				textureUEnd = textureU + Math.floor(cuboid.getFaceDimension(side).getHeight() * scale[0] + 0.000001) / scale[0];
				textureVEnd = textureV + Math.floor(cuboid.getFaceDimension(side).getWidth() * scale[1] + 0.000001) / scale[1];
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

	public Color getFaceColor() {
		return ColorsByFace[side];	
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
		cloned.textureName = textureName;
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
	

	public double TextureWidth() {
		// We prevent subpixel UV mapping so that one can still resize elements slighty to fix z-fighting
		// without messing up the UV map
		double[] scale = textureScale();
		//scale[0] /= 2;

		return Math.floor(Math.abs(textureUEnd - textureU) * scale[0]) / scale[0];
	}
	
	public double TextureHeight() {
		// We prevent subpixel UV mapping so that one can still resize elements slighty to fix z-fighting
		// without messing up the UV map
		double[] scale = textureScale();
		//scale[1] /= 2;

		return Math.floor(Math.abs(textureVEnd - textureV) * scale[1]) / scale[1];
	}

}
