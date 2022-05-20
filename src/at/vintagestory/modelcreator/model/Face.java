package at.vintagestory.modelcreator.model;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.glu.Sphere;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Project;
import at.vintagestory.modelcreator.enums.BlockFacing;
import at.vintagestory.modelcreator.util.GameMath;
import at.vintagestory.modelcreator.util.Mat4f;
import at.vintagestory.modelcreator.util.Vec3f;

public class Face
{
	static int nextOpenGlName = 0;

	static Random rand = new Random();

	
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
            -1, -1, -1,
            -1,  1, -1,
            1,  1, -1,
            1, -1, -1,

            // East face
            1, -1, -1,     // bot left
            1,  1, -1,     // top left
            1,  1,  1,     // top right
            1, -1,  1,     // bot right

            // South face
            -1, -1,  1,
            1, -1,  1,
            1,  1,  1,
            -1,  1,  1,

            // West face
            -1, -1, -1,
            -1, -1,  1,
            -1,  1,  1,
            -1,  1, -1,
            
            // Top face
            -1,  1, -1,
            -1,  1,  1,
            1,  1,  1,
            1,  1, -1,
                          
            // Bottom face
            -1, -1, -1,
            1, -1, -1,
            1, -1,  1,
            -1, -1,  1
    };
	
	
	public static int[] cubeUVCoords = {
			// North ... this is has flipped V -.-
            /*1, 0,
            1, 1,
            0, 1,
            0, 0,*/
			
			1, 1,
            1, 0,
            0, 0,
            0, 1,

            // East 
            1, 1,
            1, 0,
            0, 0,
            0, 1,

            // South ... this is has flipped u and v -.-
            /*0, 0,
            1, 0,
            1, 1,
            0, 1,*/
            
            0, 1,
            1, 1,
            1, 0,
            0, 0,
            
            // West ... flipped V
           /* 0, 0,
            1, 0,
            1, 1,
            0, 1,*/
            
            0, 1,
            1, 1,
            1, 0,
            0, 0,

            // Top face
            0, 0,
            0, 1,
            1, 1,
            1, 0,

            // Top face - 3 hours later, I have fkin no idea why this order differs from the one in VS....
/*            0, 1,
            0, 1,
            1, 1,
            1, 1,*/

            // Bottom face - flipped V
            1, 0,
            0, 0,
            0, 1,
            1, 1
	};

	
	
	private String textureCode = null;
	public double textureU = 0;
	public double textureV = 0;
	public double textureUEnd = 16;
	public double textureVEnd = 16;
	private boolean textureBound = false;
	private boolean cullface = false;
	private boolean enabled = true;
	private boolean autoUV = true;
	private boolean snapUV = true;
	public int rotation;
	public int openGlName = 0;
	public int[] WindModes = null;
	public int[] WindData = null;
	
	private Element cuboid;
	private int side;
	private int glow;
	
	public int HoveredVertex=-1;
	
	public int reflectiveMode;
	
	
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
	}
	

	static FloatBuffer color = BufferUtils.createFloatBuffer(4);
	static {
		color.rewind();
		color.put(new float[] {1,0,1,1});
		color.rewind();
	}
	
	static int[][] uvRotations = new int[][] {
        new int[] { 0, 1, 2, 3 },
        new int[] { 1, 2, 3, 0 },
        new int[] { 2, 3, 0, 1 },
        new int[] { 3, 0, 1, 2 }
    };
    
    public Vec3f sizeXyz = new Vec3f();
	public Vec3f centerVec = new Vec3f();
	
    
	public void renderFace(BlockFacing blockFacing, float brightness, boolean windAnimate, float[] matrix)
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
			int uvPos = blockFacing.GetIndex() * 8;
			int uvIndex = 0;
			
			
            int[] uvRotation = uvRotations[rotation % 4];
            
            
            double sizeU = textureUEnd - textureU;
            double sizeV = textureVEnd - textureV;
            
            
            sizeXyz.Set(
                (float)(cuboid.width) / 1f,
                (float)(cuboid.height) / 1f,
                (float)(cuboid.depth) / 1f
            );
            
            // Relative center, because transformation matrix already translated to the from position
            
            centerVec.Set(sizeXyz.X / 2, sizeXyz.Y / 2, sizeXyz.Z / 2);
            
            
            int[] tris = new int[] { 0, 1, 2, 0, 2, 3};

            
			GL11.glBegin(GL11.GL_TRIANGLES);
			{
				for (int j = 0; j < tris.length; j++) {
					
					int c = tris[j];
					
					Sized uv = translateVoxelPosToUvPos(project, entry,
							textureU + sizeU * cubeUVCoords[(2 * uvRotation[c] + uvPos)],
							textureV + sizeV * cubeUVCoords[(2 * uvRotation[c] + uvPos + 1)],
							false
					);
					
					float x = centerVec.X + sizeXyz.X * CubeVertices[coordIndex + c*3] / 2;
					float y = centerVec.Y + sizeXyz.Y * CubeVertices[coordIndex + c*3 + 1] / 2;
					float z = centerVec.Z + sizeXyz.Z * CubeVertices[coordIndex + c*3 + 2] / 2;
					
					double offX = 0;
					double heightBend = 0;
					if (WindModes != null && WindModes[c] > 0) {
						
						float[] facematrix = Mat4f.Translate(new float[16], matrix, new float[] {0,0,0});
						float[] sdf = Mat4f.MulWithVec4(facematrix, new float[] { x, y, z, 1 });
						float ypos = sdf[1] / 16f;
						float yfract = Math.abs(ypos - (int)ypos);
						
						heightBend = 0;
						if (WindData != null) heightBend = WindData[c];
						heightBend += yfract;
						
						if (windAnimate) {
							offX = heightBend * 16 / 7.0 * (0.8 + Math.sin(ModelCreator.WindWaveCounter * 1.5)) / 2.0;

							if (WindModes[c]==9) { heightBend=0; offX = 0;}
							
							if (WindModes[c] != 4 && WindModes[c] != 5) {
								double div = 3.0;
								if (WindModes[c] == 1) div = 10.0;
								if (WindModes[c] == 7) div = 20.0;
								
								offX += heightBend + Math.sin(ModelCreator.WindWaveCounter * 10) / div;
							}
						}
						
					}
					
					float[] invmat = Mat4f.Invert(new float[16], matrix);
					float[] out = Mat4f.MulWithVec4(invmat, new float[] { (float)offX, 0, 0, 0, 1 });
					
					
					

					GL11.glTexCoord2d(uv.W, uv.H);
					
					GL11.glVertex3d(
							out[0] + x, 
							out[1] + y, 
							out[2] + z
					);
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
			double textureVoxelWidth = project.TextureWidth;
			double textureVoxelHeight = project.TextureHeight;
			
			int[] size = project.TextureSizes.get(entry.code);
			if (size != null) {
				textureVoxelWidth = size[0];
				textureVoxelHeight = size[1];
			}
			
			return new Sized(
				(double)entry.Width / textureVoxelWidth,
				(double)entry.Height / textureVoxelHeight
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

	public void addTextureU(double amt)
	{
		this.textureU += amt;
		cuboid.updateUV();
		
		if (amt != 0) ModelCreator.DidModify();
	}

	public void addTextureV(double amt)
	{
		this.textureV += amt;
		cuboid.updateUV();
		
		if (amt != 0) ModelCreator.DidModify();
	}

	public void addTextureUEnd(double amt)
	{
		if (this.autoUV) {
			this.textureV += amt;
		}
		
		this.textureUEnd += amt;
		cuboid.updateUV();
		
		if (amt != 0) ModelCreator.DidModify();
	}

	public void addTextureVEnd(double amt)
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
		cloned.WindData = WindData == null ? null : WindData.clone();
		cloned.WindModes = WindModes == null ? null : WindModes.clone();
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

	public void RandomizeTexture()
	{
		double texWidth = ModelCreator.currentProject.TextureWidth;
		double texHeight = ModelCreator.currentProject.TextureHeight;
		Sized scale = getVoxel2PixelScale();
		
		if (textureCode != null) {
			TextureEntry entry = getTextureEntry();
			if (entry != null) {
				texWidth = entry.Width  / scale.W;
				texHeight = entry.Height / scale.H;
			}
		}
		
		double facewidth = Math.abs(textureUEnd - textureU);
		double faceheight = Math.abs(textureVEnd - textureV);
		
		double ustart = Math.floor(rand.nextFloat() * (texWidth - facewidth) * scale.W) / scale.W;
		double vstart = Math.floor(rand.nextFloat() * (texHeight - faceheight) * scale.H) / scale.H;
		
		setStartU(ustart);
		setStartV(vstart);
		
		setEndU(ustart + facewidth); 
		setEndV(vstart + faceheight);
		
	}

	public void AutoguessWindMode(int windMode, int facesEnabled, float[] modelMat)
	{
		if (windMode == 0) {
			this.WindModes = null;
			return;
		}
		
		if (WindModes == null) {
			WindModes = new int[4];
		}
		
		Vec3f sizeXyz = new Vec3f(
            (float)(cuboid.getWidth()) / 1f,
            (float)(cuboid.getHeight()) / 1f,
            (float)(cuboid.getDepth()) / 1f
        );
		Vec3f centerVec = new Vec3f(sizeXyz.X / 2, sizeXyz.Y / 2, sizeXyz.Z / 2);
		
		WindData = new int[4];

		int hereMode = windMode;
		if (windMode == -1) {
			hereMode = facesEnabled == 1 || cuboid.name.contains("leaves") ? 1 : 4;
		}
		
		if (windMode == 1 && (cuboid.name.contains("trunk") || cuboid.name.contains("stem"))) hereMode=4;
		
		int coordIndex = side * 12;
		
		for (int i = 0; i < 4; i++) {
			
			float x = centerVec.X + sizeXyz.X * Face.CubeVertices[coordIndex + i*3] / 2;
			float y = centerVec.Y + sizeXyz.Y * Face.CubeVertices[coordIndex + i*3 + 1] / 2;
			float z = centerVec.Z + sizeXyz.Z * Face.CubeVertices[coordIndex + i*3 + 2] / 2;
			
			float[] facematrix = Mat4f.Translate(new float[16], modelMat, new float[] {0,0,0});
			float[] sdf = Mat4f.MulWithVec4(facematrix, new float[] { x, y, z, 1 });
			float ypos = sdf[1] / 16f;
			
			WindData[i] = (int)ypos;
			
			int mode = 0;
			if (sdf[1] > 3f) mode = hereMode;
			
			WindModes[i] = mode;
		}
		
		if (WindData[0] == 0 && WindData[1] == 0 && WindData[2] == 0 && WindData[3] == 0) WindData = null;
		
	}

	public void setReflectiveMode(int selectedIndex)
	{
		this.reflectiveMode = selectedIndex;
		ModelCreator.DidModify();
	}

}
