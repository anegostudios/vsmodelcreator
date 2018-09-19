package at.vintagestory.modelcreator.model;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.renderer.SGL;

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

	
	
	private String textureCode = null;
	public double textureU = 0;
	public double textureV = 0;
	private double textureUEnd = 16;
	private double textureVEnd = 16;
	private boolean binded = false;
	private boolean cullface = false;
	private boolean enabled = true;
	private boolean autoUV = true;
	private boolean snapUV = true;
	public int rotation;

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
		if (ModelCreator.currentProject != null && ModelCreator.currentProject.EntityTextureMode && ModelCreator.currentProject.TexturesByCode != null && ModelCreator.currentProject.TexturesByCode.size() > 0) {
			//this.textureName = ModelCreator.currentProject.Textures.get(0).name;
			
			this.textureCode = ModelCreator.currentProject.TexturesByCode.values().iterator().next().code;
		}		
	}
	
	static FloatBuffer color = BufferUtils.createFloatBuffer(4);
	static {
		color.rewind();
		color.put(new float[] {1,0,1,1});
		color.rewind();
	}
	
	public void renderFace(BlockFacing blockFacing, float brightness)
	{		
		TextureEntry entry = ModelCreator.currentProject == null ? null : ModelCreator.currentProject.getTextureEntryByCode(textureCode);

		GL11.glPushMatrix();
		{
			GL11.glEnable(GL_TEXTURE_2D);
			GL11.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);
			GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
			
			
			GL11.glTexParameter(GL_TEXTURE_2D, GL11.GL_TEXTURE_BORDER_COLOR, color);
			
			bindTexture();

			if (binded) GL11.glColor3f(brightness, brightness, brightness);
			
			int coordIndex = blockFacing.GetIndex() * 12;
			int uvBaseIndex = blockFacing.GetIndex() * 8;
			int uvIndex = 0;
			
			GL11.glBegin(GL11.GL_QUADS);
			{
				for (int j = 0; j < 4; j++) {
					
					Sized uv = translateVoxelPosToUvPos(entry,
							(cubeUVCoords[uvBaseIndex + (2 * rotation + uvIndex++) % 8]==0 ? textureU : textureUEnd),
							(cubeUVCoords[uvBaseIndex + (2 * rotation + uvIndex++) % 8]==0 ? textureV : textureVEnd),
							false
					);
					
					GL11.glTexCoord2d(uv.W, uv.H);
					GL11.glVertex3d(cuboid.getWidth() * CubeVertices[coordIndex++], cuboid.getHeight() * CubeVertices[coordIndex++], cuboid.getDepth() * CubeVertices[coordIndex++]);
				}
			}
			GL11.glEnd();

			GL11.glDisable(GL_TEXTURE_2D);
		}
		GL11.glPopMatrix();
	}
	
	
	public Sized translateVoxelPosToUvPos(double voxelU, double voxelV, boolean actualPosition) { 
		return translateVoxelPosToUvPos(ModelCreator.currentProject == null ? null : ModelCreator.currentProject.getTextureEntryByCode(textureCode), voxelU, voxelV, actualPosition);
	}
	
	public static Sized translateVoxelPosToUvPos(TextureEntry entry, double voxelU, double voxelV, boolean actualPosition) {
		double textureVoxelWidth = ModelCreator.currentProject.TextureWidth;
		double textureVoxelHeight = ModelCreator.currentProject.TextureHeight;
		
		if (entry != null && !actualPosition) {
			textureVoxelWidth = entry.VoxelWidthWithLwJglFuckery();
			textureVoxelHeight = entry.VoxelHeighthWithLwJglFuckery();
		}
		
		return new Sized(voxelU / textureVoxelWidth, voxelV / textureVoxelHeight);
	}
	
	
	public Sized getVoxel2PixelScale() {
		return getVoxel2PixelScale(ModelCreator.currentProject == null ? null : ModelCreator.currentProject.getTextureEntryByCode(textureCode));
	}
	
	public static Sized getVoxel2PixelScale(TextureEntry entry) {
		if (entry != null) {
			return new Sized(
				(double)entry.Width / ModelCreator.currentProject.TextureWidth,
				(double)entry.Height / ModelCreator.currentProject.TextureHeight
			);
		}
		
		return new Sized(ModelCreator.noTexScale, ModelCreator.noTexScale); 
	}
	
	

	public void setTextureCode(String textureCode)
	{
		this.textureCode = textureCode;
	}

	public void bindTexture()
	{
		TextureImpl.bindNone();
		if (textureCode != null && ModelCreator.renderTexture)
		{
			TextureEntry entry = ModelCreator.currentProject.getTextureEntryByCode(textureCode);
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
		cuboid.updateUV();
		
		if (amt != 0) ModelCreator.DidModify();
	}

	public void moveTextureV(double amt)
	{
		this.textureV += amt;
		this.textureVEnd += amt;
		cuboid.updateUV();
		
		if (amt != 0) ModelCreator.DidModify();
	}

	public void addTextureX(double amt)
	{
		this.textureU += amt;
		cuboid.updateUV();
		
		if (amt != 0) ModelCreator.DidModify();
	}

	public void addTextureY(double amt)
	{
		this.textureV += amt;
		cuboid.updateUV();
		
		if (amt != 0) ModelCreator.DidModify();
	}

	public void addTextureXEnd(double amt)
	{
		this.textureUEnd += amt;
		cuboid.updateUV();
		
		if (amt != 0) ModelCreator.DidModify();
	}

	public void addTextureYEnd(double amt)
	{
		this.textureVEnd += amt;
		cuboid.updateUV();
		
		if (amt != 0) ModelCreator.DidModify();
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
		if (u == textureU) return;
		
		textureU = u;
		ModelCreator.DidModify();
	}

	public void setStartV(double v)
	{
		if (v == textureV) return;
		
		textureV = v;
		ModelCreator.DidModify();
	}

	public void setEndU(double ue)
	{
		if (textureUEnd == ue) return;
		
		textureUEnd = ue;
		ModelCreator.DidModify();
	}

	public void setEndV(double ve)
	{
		if (textureVEnd == ve) return;
		
		textureVEnd = ve;
		ModelCreator.DidModify();
	}

	public String getTextureCode()
	{
		return textureCode;
	}

	public Texture getTexture()
	{
		return ModelCreator.currentProject.getTextureByCode(textureCode);
	}
	
	public TextureEntry getTextureEntry()
	{
		return ModelCreator.currentProject.getTextureEntryByCode(textureCode);
	}


	public int getSide()
	{
		return side;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		if (this.enabled == enabled) return;
		
		this.enabled = enabled;
		ModelCreator.DidModify();
	}

	public boolean isAutoUVEnabled()
	{
		return autoUV;
	}

	public void setAutoUVEnabled(boolean enabled)
	{
		if (this.autoUV == enabled) return;
		
		this.autoUV = enabled;
		ModelCreator.DidModify();
	}
	
	
	public void setSnapUVEnabled(boolean enabled)
	{
		if (this.snapUV == enabled) return;
		
		this.snapUV = enabled;
		ModelCreator.DidModify();
	}
	
	public boolean isSnapUvEnabled() {
		return snapUV;
	}
	
	

	public boolean isBinded()
	{
		return binded;
	}

	public void updateUV()
	{
		if (autoUV)
		{
			if (snapUV) {
				// We prevent subpixel UV mapping so that one can still resize elements slighty to fix z-fighting
				// without messing up the UV map
				Sized scale = getVoxel2PixelScale(); 
				
				if (rotation == 0 || rotation == 2) {
					textureUEnd = textureU + Math.floor(cuboid.getFaceDimension(side).getWidth() * scale.W + 0.000001) / scale.W;      // Stupid rounding errors -.-
					textureVEnd = textureV + Math.floor(cuboid.getFaceDimension(side).getHeight() * scale.H + 0.000001) / scale.H;	
				} else {
					textureUEnd = textureU + Math.floor(cuboid.getFaceDimension(side).getHeight() * scale.W + 0.000001) / scale.W;
					textureVEnd = textureV + Math.floor(cuboid.getFaceDimension(side).getWidth() * scale.H + 0.000001) / scale.H;
				}
			} else {
				
				
				if (rotation == 0 || rotation == 2) {
					textureUEnd = textureU + cuboid.getFaceDimension(side).getWidth();
					textureVEnd = textureV + cuboid.getFaceDimension(side).getHeight();	
				} else {
					textureUEnd = textureU + cuboid.getFaceDimension(side).getHeight();
					textureVEnd = textureV + cuboid.getFaceDimension(side).getWidth();
				}
			}
		}
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
		if (this.glow == glow) return;
		
		this.glow = glow;
		ModelCreator.DidModify();
	}
	
	public int getRotation()
	{
		return rotation;
	}

	public void setRotation(int rotation)
	{
		if (this.rotation == rotation) return;
		
		this.rotation = rotation;
		ModelCreator.DidModify();
		updateUV();
	}
	
	
	public Face clone(Element forElement) {
		Face cloned = new Face();
		cloned.textureCode = textureCode;
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
		cloned.snapUV = snapUV;
		return cloned;
	}
	
	public double uvWidth() {
		if (!snapUV) return Math.abs(textureUEnd - textureU);
		
		// We prevent subpixel UV mapping so that one can still resize elements slighty to fix z-fighting
		// without messing up the UV map
		Sized scale = getVoxel2PixelScale();

		return Math.floor(Math.abs(textureUEnd - textureU) * scale.W) / scale.W;
	}
	
	public double uvHeight() {
		if (!snapUV) return Math.abs(textureVEnd - textureV);
		
		// We prevent subpixel UV mapping so that one can still resize elements slighty to fix z-fighting
		// without messing up the UV map
		Sized scale = getVoxel2PixelScale();

		return Math.floor(Math.abs(textureVEnd - textureV) * scale.H) / scale.H;
	}

	public void scaleSize(float size)
	{
		double width = textureUEnd - textureU;
		double height = textureVEnd - textureV;
		
		textureUEnd = textureU + size * width;
		textureVEnd = textureV + size * height;
	}

}
