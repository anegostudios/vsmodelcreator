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
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Project;
import at.vintagestory.modelcreator.enums.BlockFacing;

public class Face
{
	static int nextOpenGlName = 0;


	
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
	private boolean textureBound = false;
	private boolean cullface = false;
	private boolean enabled = true;
	private boolean autoUV = true;
	private boolean snapUV = true;
	public int rotation;
	public int openGlName = 0;
	
	private Element cuboid;
	private int side;
	private int glow;
	
	
	public boolean isInBackdropProject;
	
	public Project getProject() {
		return isInBackdropProject ? ModelCreator.currentBackdropProject : ModelCreator.currentProject;
	}

	public Face() {
		openGlName = nextOpenGlName++;
	}
	
	public Face(Element cuboid, int side)
	{
		openGlName = nextOpenGlName++;
		this.cuboid = cuboid;
		this.side = side;
		//applyEntityTextureMode();
	}
	
	/*public void applyEntityTextureMode() {
		Project project = getProject();
		if (project != null && project.EntityTextureMode && project.TexturesByCode != null && project.TexturesByCode.size() > 0) {
			//this.textureName = ModelCreator.currentProject.Textures.get(0).name;
			
			this.textureCode = project.TexturesByCode.values().iterator().next().code;
		}		
	}*/
	
	static FloatBuffer color = BufferUtils.createFloatBuffer(4);
	static {
		color.rewind();
		color.put(new float[] {1,0,1,1});
		color.rewind();
	}
	
	
	public void renderFace(BlockFacing blockFacing, float brightness)
	{	
		Project project = getProject();
		TextureEntry entry = project == null ? null : project.getTextureEntryByCode(textureCode);

		GL11.glPushMatrix();
		{
			if (!isInBackdropProject) GL11.glLoadName(openGlName);
			
			GL11.glEnable(GL_TEXTURE_2D);
			GL11.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);
			GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
			
			
			GL11.glTexParameter(GL_TEXTURE_2D, GL11.GL_TEXTURE_BORDER_COLOR, color);
			
			bindTexture();

			if (textureBound) GL11.glColor3f(brightness, brightness, brightness);
			
			int coordIndex = blockFacing.GetIndex() * 12;
			int uvBaseIndex = blockFacing.GetIndex() * 8;
			int uvIndex = 0;
			
			GL11.glBegin(GL11.GL_QUADS);
			{
				for (int j = 0; j < 4; j++) {
					
					Sized uv = translateVoxelPosToUvPos(project, entry,
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
			
			if (!isInBackdropProject) GL11.glLoadName(0);
			
		}
		GL11.glPopMatrix();
	}
	
	
	public Sized translateVoxelPosToUvPos(double voxelU, double voxelV, boolean actualPosition) {
		Project project = getProject();
		return translateVoxelPosToUvPos(project, project == null ? null : project.getTextureEntryByCode(textureCode), voxelU, voxelV, actualPosition);
	}
	
	public static Sized translateVoxelPosToUvPos(Project project, TextureEntry entry, double voxelU, double voxelV, boolean actualPosition) {
		double textureVoxelWidth = project.TextureWidth;
		double textureVoxelHeight = project.TextureHeight;
		
		if (entry != null && !actualPosition) {
			textureVoxelWidth = entry.VoxelWidthWithLwJglFuckery(project);
			textureVoxelHeight = entry.VoxelHeighthWithLwJglFuckery(project);
		} else if(entry != null) {
			
			int[] size = project.TextureSizes.get(entry.code);
			if (size != null) {
				textureVoxelWidth = size[0];
				textureVoxelHeight = size[1];
			}
		}
		
		
		return new Sized(voxelU / textureVoxelWidth, voxelV / textureVoxelHeight);
	}
	
	
	public Sized getVoxel2PixelScale() {
		Project project = getProject();
		return getVoxel2PixelScale(project, project == null ? null : project.getTextureEntryByCode(textureCode));
	}
	
	public static Sized getVoxel2PixelScale(Project project, TextureEntry entry) {
		if (entry != null) {
			return new Sized(
				(double)entry.Width / project.TextureWidth,
				(double)entry.Height / project.TextureHeight
			);
		}
		
		return new Sized(ModelCreator.noTexScale, ModelCreator.noTexScale); 
	}
	
	

	public void setTextureCode(String textureCode)
	{
		this.textureCode = textureCode;
	}
	
	public void bindTexture() {
		TextureEntry entry = isInBackdropProject ? ModelCreator.currentBackdropProject.getTextureEntryByCode(textureCode) : ModelCreator.currentProject.getTextureEntryByCode(textureCode);
		textureBound = bindTexture(entry);
	}
	
	public static boolean bindTexture(TextureEntry entry)
	{
		TextureImpl.bindNone();
		
		if (entry != null && ModelCreator.renderTexture)
		{
			if (entry != null)
			{
				if (entry.getTexture() != null)
				{
					GL11.glColor3f(1.0F, 1.0F, 1.0F);
					entry.getTexture().bind();
				}

				return true;
			}
		}
		
		return false;
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
		if (this.autoUV) {
			this.textureV += amt;
		}
		
		this.textureUEnd += amt;
		cuboid.updateUV();
		
		if (amt != 0) ModelCreator.DidModify();
	}

	public void addTextureYEnd(double amt)
	{
		if (this.autoUV) {
			this.textureV += amt;
		}
		
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
		return textureBound;
	}

	public void updateUV()
	{
		if (autoUV)
		{
			if (snapUV) {
				// We prevent subpixel UV mapping so that one can still resize elements slighty to fix z-fighting
				// without messing up the UV map
				Sized scale = getVoxel2PixelScale(); 
				
				textureU = (int)Math.round(textureU * scale.W) / scale.W;  
				textureV = (int)Math.round((textureV * scale.H)) / scale.H;
				
				double width = cuboid.getFaceDimension(side).getWidth();
				double height = cuboid.getFaceDimension(side).getHeight();
				
				if (rotation == 0 || rotation == 2) {
					// Math.max because if the element is not even a full pixel wide, we should still use a single pixel to texture it
					
					textureUEnd = textureU + Math.max(1/scale.W, Math.floor(width * scale.W + 0.000001) / scale.W);      // Stupid rounding errors -.-
					textureVEnd = textureV + Math.max(1/scale.H, Math.floor(height * scale.H + 0.000001) / scale.H);	
				} else {
					textureUEnd = textureU + Math.max(1/scale.H, Math.floor(height * scale.W + 0.000001) / scale.W);
					textureVEnd = textureV + Math.max(1/scale.W, Math.floor(width * scale.H + 0.000001) / scale.H);
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
		cloned.textureBound = textureBound;
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

	public void setIsBackdrop()
	{
		isInBackdropProject = true;
	}

}
