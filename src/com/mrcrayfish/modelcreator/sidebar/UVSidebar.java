package com.mrcrayfish.modelcreator.sidebar;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glVertex2i;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureImpl;

import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.element.Face;
import com.mrcrayfish.modelcreator.util.FontManager;

public class UVSidebar extends Sidebar
{
	private ElementManager manager;

	private final int LENGTH = 110;

	private final Color BLACK_ALPHA = new Color(0, 0, 0, 0.75F);

	private int[] startX = { 0, 0, 0, 0, 0, 0 };
	private int[] startY = { 0, 0, 0, 0, 0, 0 };

	public UVSidebar(String title, ElementManager manager)
	{
		super(title);
		this.manager = manager;
	}

	@Override
	public void draw(int sidebarWidth, int canvasWidth, int canvasHeight, int frameHeight)
	{
		super.draw(sidebarWidth, canvasWidth, canvasHeight, frameHeight);

		glPushMatrix();
		{
			glTranslatef(10, 30, 0);

			int count = 0;

			for (int i = 0; i < 6; i++)
			{
				glPushMatrix();
				{
					if (30 + i * (LENGTH + 10) + (LENGTH + 10) > canvasHeight)
					{
						glTranslatef(10 + LENGTH, count * (LENGTH + 10), 0);
						startX[i] = 20 + LENGTH;
						startY[i] = count * (LENGTH + 10) + 40;
						count++;
					}
					else
					{
						glTranslatef(0, i * (LENGTH + 10), 0);
						startX[i] = 10;
						startY[i] = i * (LENGTH + 10) + 40;
					}

					Color color = Face.getFaceColour(i);
					glColor3f(color.getRed(), color.getGreen(), color.getBlue());

					Face[] faces = null;
					if (manager.getSelectedElement() != null)
						faces = manager.getSelectedElement().getAllFaces();

					if (faces != null)
					{
						//TextureEntry entry = TextureManager.getTextureEntry(faces[i].getTextureName());

						/*int passes = 1;
						if (entry != null)
							passes = entry.getPasses();*/

						faces[i].bindTexture(0);

						glBegin(GL_QUADS);
						{
							if (faces[i].isBinded())
								glTexCoord2f(0, 1);
							glVertex2i(0, LENGTH);

							if (faces[i].isBinded())
								glTexCoord2f(1, 1);
							glVertex2i(LENGTH, LENGTH);

							if (faces[i].isBinded())
								glTexCoord2f(1, 0);
							glVertex2i(LENGTH, 0);

							if (faces[i].isBinded())
								glTexCoord2f(0, 0);
							glVertex2i(0, 0);
						}
						glEnd();

						TextureImpl.bindNone();

						glColor3f(1, 1, 1);

						glBegin(GL_LINES);
						{
							glVertex2d(faces[i].getStartU() * (LENGTH / 16D), faces[i].getStartV() * (LENGTH / 16D));
							glVertex2d(faces[i].getStartU() * (LENGTH / 16D), faces[i].getEndV() * (LENGTH / 16D));

							glVertex2d(faces[i].getStartU() * (LENGTH / 16D), faces[i].getEndV() * (LENGTH / 16D));
							glVertex2d(faces[i].getEndU() * (LENGTH / 16D), faces[i].getEndV() * (LENGTH / 16D));

							glVertex2d(faces[i].getEndU() * (LENGTH / 16D), faces[i].getEndV() * (LENGTH / 16D));
							glVertex2d(faces[i].getEndU() * (LENGTH / 16D), faces[i].getStartV() * (LENGTH / 16D));

							glVertex2d(faces[i].getEndU() * (LENGTH / 16D), faces[i].getStartV() * (LENGTH / 16D));
							glVertex2d(faces[i].getStartU() * (LENGTH / 16D), faces[i].getStartV() * (LENGTH / 16D));

						}
						glEnd();

						glEnable(GL_BLEND);
						glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
						FontManager.BEBAS_NEUE_20.drawString(5, 5, Face.getFaceName(i), BLACK_ALPHA);
						glDisable(GL_BLEND);
					}
				}
				glPopMatrix();
			}
		}
		glPopMatrix();
	}

	private int lastMouseX, lastMouseY;
	private int selected = -1;
	private boolean grabbing = false;

	@Override
	public void handleInput(int canvasHeight)
	{
		super.handleInput(canvasHeight);

		if (Mouse.isButtonDown(0) | Mouse.isButtonDown(1))
		{
			if (!grabbing)
			{
				this.lastMouseX = Mouse.getX();
				this.lastMouseY = Mouse.getY();
				grabbing = true;
			}
		}
		else
		{
			grabbing = false;
		}

		if (grabbing)
		{
			int newMouseX = Mouse.getX();
			int newMouseY = Mouse.getY();

			int side = getFace(canvasHeight, newMouseX, newMouseY);
			if (side != -1 | selected != -1)
			{
				if (manager.getSelectedElement() != null)
				{
					Face face = manager.getSelectedElement().getAllFaces()[(selected != -1 ? selected : side)];

					int xMovement = (int) ((newMouseX - this.lastMouseX) / 6);
					int yMovement = (int) ((newMouseY - this.lastMouseY) / 6);

					if (xMovement != 0 | yMovement != 0)
					{
						if (Mouse.isButtonDown(0))
						{
							if ((face.getStartU() + xMovement) >= 0.0 && (face.getEndU() + xMovement) <= 16.0)
								face.moveTextureU(xMovement);
							if ((face.getStartV() - yMovement) >= 0.0 && (face.getEndV() - yMovement) <= 16.0)
								face.moveTextureV(-yMovement);
						}
						else
						{
							face.setAutoUVEnabled(false);

							if ((face.getEndU() + xMovement) <= 16.0)
								face.addTextureXEnd(xMovement);
							if ((face.getEndV() - yMovement) <= 16.0)
								face.addTextureYEnd(-yMovement);

							face.setAutoUVEnabled(false);
						}
						face.updateUV();

						if (xMovement != 0)
							this.lastMouseX = newMouseX;
						if (yMovement != 0)
							this.lastMouseY = newMouseY;
					}
					manager.updateValues();
				}
			}
		}
		else
		{
			selected = -1;
		}
	}

	public int getFace(int canvasHeight, int mouseX, int mouseY)
	{
		for (int i = 0; i < 6; i++)
		{
			if (mouseX >= startX[i] && mouseX <= startX[i] + LENGTH)
			{
				if ((canvasHeight - mouseY - 45) >= startY[i] && (canvasHeight - mouseY - 45) <= startY[i] + LENGTH)
				{
					return i;
				}
			}
		}
		return -1;
	}
}
