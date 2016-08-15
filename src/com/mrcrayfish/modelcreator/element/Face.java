package com.mrcrayfish.modelcreator.element;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;

import com.mrcrayfish.modelcreator.texture.TextureEntry;
import com.mrcrayfish.modelcreator.texture.TextureManager;

public class Face
{
	private static final Color RED = new Color(1, 0, 0);
	private static final Color GREEN = new Color(0, 1, 0);
	private static final Color BLUE = new Color(0, 0, 1);
	private static final Color YELLOW = new Color(1, 1, 0);
	private static final Color MAGENTA = new Color(1, 0, 1);
	private static final Color CYAN = new Color(0, 1, 1);

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

	public Face(Element cuboid, int side)
	{
		this.cuboid = cuboid;
		this.side = side;
	}

	public void renderNorth(float brightness)
	{
		TextureEntry entry = TextureManager.getTextureEntry(texture);
		int passes = 1;

		if (entry != null)
			passes = entry.getPasses();

		for (int i = 0; i < passes; i++)
		{
			renderNorth(i, brightness);
		}
	}

	private void renderNorth(int pass, float brightness)
	{
		GL11.glPushMatrix();
		{
			startRender(pass);

			if (binded) GL11.glColor3f(brightness, brightness, brightness);
			
			GL11.glBegin(GL11.GL_QUADS);
			{
				if (binded)
					setTexCoord(0);
				GL11.glVertex3d(cuboid.getStartX() + cuboid.getWidth(), cuboid.getStartY(), cuboid.getStartZ());

				if (binded)
					setTexCoord(1);
				GL11.glVertex3d(cuboid.getStartX(), cuboid.getStartY(), cuboid.getStartZ());

				if (binded)
					setTexCoord(2);
				GL11.glVertex3d(cuboid.getStartX(), cuboid.getStartY() + cuboid.getHeight(), cuboid.getStartZ());

				if (binded)
					setTexCoord(3);
				GL11.glVertex3d(cuboid.getStartX() + cuboid.getWidth(), cuboid.getStartY() + cuboid.getHeight(), cuboid.getStartZ());
			}
			GL11.glEnd();

			finishRender();
		}
		GL11.glPopMatrix();
	}

	public void renderEast(float brightness)
	{
		TextureEntry entry = TextureManager.getTextureEntry(texture);
		int passes = 1;

		if (entry != null)
			passes = entry.getPasses();

		for (int i = 0; i < passes; i++)
		{
			renderEast(i, brightness);
		}
	}

	private void renderEast(int pass, float brightness)
	{
		GL11.glPushMatrix();
		{
			startRender(pass);

			if (binded) GL11.glColor3f(brightness, brightness, brightness);
			
			GL11.glBegin(GL11.GL_QUADS);
			{
				if (binded)
					setTexCoord(0);
				GL11.glVertex3d(cuboid.getStartX() + cuboid.getWidth(), cuboid.getStartY(), cuboid.getStartZ() + cuboid.getDepth());

				if (binded)
					setTexCoord(1);
				GL11.glVertex3d(cuboid.getStartX() + cuboid.getWidth(), cuboid.getStartY(), cuboid.getStartZ());

				if (binded)
					setTexCoord(2);
				GL11.glVertex3d(cuboid.getStartX() + cuboid.getWidth(), cuboid.getStartY() + cuboid.getHeight(), cuboid.getStartZ());

				if (binded)
					setTexCoord(3);
				GL11.glVertex3d(cuboid.getStartX() + cuboid.getWidth(), cuboid.getStartY() + cuboid.getHeight(), cuboid.getStartZ() + cuboid.getDepth());
			}
			GL11.glEnd();

			finishRender();
		}
		GL11.glPopMatrix();
	}

	public void renderSouth(float brightness)
	{
		TextureEntry entry = TextureManager.getTextureEntry(texture);
		int passes = 1;

		if (entry != null)
			passes = entry.getPasses();

		for (int i = 0; i < passes; i++)
		{
			renderSouth(i, brightness);
		}
	}

	private void renderSouth(int pass, float brightness)
	{
		GL11.glPushMatrix();
		{
			startRender(pass);
			
			if (binded) GL11.glColor3f(brightness, brightness, brightness);
			
			GL11.glBegin(GL11.GL_QUADS);
			{
				if (binded)
					setTexCoord(0);
				GL11.glVertex3d(cuboid.getStartX(), cuboid.getStartY(), cuboid.getStartZ() + cuboid.getDepth());

				if (binded)
					setTexCoord(1);
				GL11.glVertex3d(cuboid.getStartX() + cuboid.getWidth(), cuboid.getStartY(), cuboid.getStartZ() + cuboid.getDepth());

				if (binded)
					setTexCoord(2);
				GL11.glVertex3d(cuboid.getStartX() + cuboid.getWidth(), cuboid.getStartY() + cuboid.getHeight(), cuboid.getStartZ() + cuboid.getDepth());

				if (binded)
					setTexCoord(3);
				GL11.glVertex3d(cuboid.getStartX(), cuboid.getStartY() + cuboid.getHeight(), cuboid.getStartZ() + cuboid.getDepth());
			}
			GL11.glEnd();

			finishRender();
		}
		GL11.glPopMatrix();
	}

	public void renderWest(float brightness)
	{
		TextureEntry entry = TextureManager.getTextureEntry(texture);
		int passes = 1;

		if (entry != null)
			passes = entry.getPasses();

		for (int i = 0; i < passes; i++)
		{
			renderWest(i, brightness);
		}
	}

	private void renderWest(int pass, float brightness)
	{
		GL11.glPushMatrix();
		{
			startRender(pass);
			
			if (binded) GL11.glColor3f(brightness, brightness, brightness);

			GL11.glBegin(GL11.GL_QUADS);
			{
				if (binded)
					setTexCoord(0);
				GL11.glVertex3d(cuboid.getStartX(), cuboid.getStartY(), cuboid.getStartZ());

				if (binded)
					setTexCoord(1);
				GL11.glVertex3d(cuboid.getStartX(), cuboid.getStartY(), cuboid.getStartZ() + cuboid.getDepth());

				if (binded)
					setTexCoord(2);
				GL11.glVertex3d(cuboid.getStartX(), cuboid.getStartY() + cuboid.getHeight(), cuboid.getStartZ() + cuboid.getDepth());

				if (binded)
					setTexCoord(3);
				GL11.glVertex3d(cuboid.getStartX(), cuboid.getStartY() + cuboid.getHeight(), cuboid.getStartZ());
			}
			GL11.glEnd();

			finishRender();
		}
		GL11.glPopMatrix();
	}

	public void renderUp(float brightness)
	{
		TextureEntry entry = TextureManager.getTextureEntry(texture);
		int passes = 1;

		if (entry != null)
			passes = entry.getPasses();

		for (int i = 0; i < passes; i++)
		{
			renderUp(i, brightness);
		}
	}

	private void renderUp(int pass, float brightness)
	{
		GL11.glPushMatrix();
		{
			startRender(pass);
			
			if (binded) GL11.glColor3f(brightness, brightness, brightness);

			GL11.glBegin(GL11.GL_QUADS);
			{
				
				if (binded)
					setTexCoord(0);
				GL11.glVertex3d(cuboid.getStartX(), cuboid.getStartY() + cuboid.getHeight(), cuboid.getStartZ() + cuboid.getDepth());

				if (binded)
					setTexCoord(1);
				GL11.glVertex3d(cuboid.getStartX() + cuboid.getWidth(), cuboid.getStartY() + cuboid.getHeight(), cuboid.getStartZ() + cuboid.getDepth());

				if (binded)
					setTexCoord(2);
				GL11.glVertex3d(cuboid.getStartX() + cuboid.getWidth(), cuboid.getStartY() + cuboid.getHeight(), cuboid.getStartZ());

				if (binded)
					setTexCoord(3);
				GL11.glVertex3d(cuboid.getStartX(), cuboid.getStartY() + cuboid.getHeight(), cuboid.getStartZ());
			}
			GL11.glEnd();

			finishRender();
		}
		GL11.glPopMatrix();
	}

	public void renderDown(float brightness)
	{
		TextureEntry entry = TextureManager.getTextureEntry(texture);
		int passes = 1;

		if (entry != null)
			passes = entry.getPasses();

		for (int i = 0; i < passes; i++)
		{
			renderDown(i, brightness);
		}
	}

	public void renderDown(int pass, float brightness)
	{
		GL11.glPushMatrix();
		{
			startRender(pass);
			
			if (binded) GL11.glColor3f(brightness, brightness, brightness);
			
			GL11.glBegin(GL11.GL_QUADS);
			{
				if (binded)
					setTexCoord(0);
				GL11.glVertex3d(cuboid.getStartX(), cuboid.getStartY(), cuboid.getStartZ());

				if (binded)
					setTexCoord(1);
				GL11.glVertex3d(cuboid.getStartX() + cuboid.getWidth(), cuboid.getStartY(), cuboid.getStartZ());

				if (binded)
					setTexCoord(2);
				GL11.glVertex3d(cuboid.getStartX() + cuboid.getWidth(), cuboid.getStartY(), cuboid.getStartZ() + cuboid.getDepth());

				if (binded)
					setTexCoord(3);
				GL11.glVertex3d(cuboid.getStartX(), cuboid.getStartY(), cuboid.getStartZ() + cuboid.getDepth());
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
			textureUEnd = textureU + cuboid.getFaceDimension(side).getWidth();
			textureVEnd = textureV + cuboid.getFaceDimension(side).getHeight();
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

	public static Color getFaceColour(int side)
	{
		switch (side)
		{
		case 0:
			return RED;
		case 1:
			return GREEN;
		case 2:
			return BLUE;
		case 3:
			return YELLOW;
		case 4:
			return CYAN;
		case 5:
			return MAGENTA;
		}
		return RED;
	}

	public int getRotation()
	{
		return rotation;
	}

	public void setRotation(int rotation)
	{
		this.rotation = rotation;
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
