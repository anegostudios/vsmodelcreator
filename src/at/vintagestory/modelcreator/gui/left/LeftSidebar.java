package at.vintagestory.modelcreator.gui.left;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2i;

import java.awt.Cursor;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;


import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.enums.EnumFonts;

public abstract class LeftSidebar
{
	/* Sidebar Variables */
	private final int SIDEBAR_WIDTH = 4 * 32 + 20;

	private String title;
	
	public int nowSidebarWidth = SIDEBAR_WIDTH;
	
	boolean nowResizingSidebar;
	int lastGrabMouseX;
	boolean overSidebar;
	
	public int GetSidebarWidth() {
		return nowSidebarWidth;
		
		//Project project = ModelCreator.currentProject;
		//return project.EntityTextureMode || ModelCreator.Instance.getHeight() < 911 ? SIDEBAR_WIDTH * 2 : SIDEBAR_WIDTH;
	}

	public LeftSidebar(String title)
	{
		this.title = title;
	}

	public void draw(int sidebarWidth, int canvasWidth, int canvasHeight, int frameHeight)
	{
		glColor3f(0.266F, 0.266F, 0.294F);
		glBegin(GL_QUADS);
		{
			glVertex2i(0, 0);
			glVertex2i(sidebarWidth, 0);
			glVertex2i(sidebarWidth, canvasHeight);
			glVertex2i(0, canvasHeight);
		}
		glEnd();

		glColor3f(0.166F, 0.166F, 0.194F);
		glBegin(GL_QUADS);
		{
			glVertex2i(sidebarWidth - 2, 0);
			glVertex2i(sidebarWidth, 0);
			glVertex2i(sidebarWidth, canvasHeight);
			glVertex2i(sidebarWidth - 2, canvasHeight);
		}
		glEnd();

		
		drawTitle();
		
		int width = GetSidebarWidth();
		int nowMouseX = Mouse.getX();
		if (width - nowMouseX > 0 && width - nowMouseX < 7) {
			ModelCreator.canvas.setCursor(new java.awt.Cursor(Cursor.E_RESIZE_CURSOR));
			overSidebar = true;
		} else {
			if (overSidebar) {
				ModelCreator.canvas.setCursor(java.awt.Cursor.getDefaultCursor());				
				overSidebar = false;
			}
		}
	}

	private void drawTitle()
	{
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		EnumFonts.BEBAS_NEUE_20.drawString(5, 5, title, new Color(0.5F, 0.5F, 0.6F));
		glDisable(GL_BLEND);
	}

	
	public void onMouseDownOnPanel()
	{
		int width = GetSidebarWidth();
		int nowMouseX = Mouse.getX();
		
		if (Math.abs(nowMouseX - width) < 4) {
			if (Mouse.isButtonDown(0)) {
				if (!nowResizingSidebar) {
					lastGrabMouseX = Mouse.getX(); 
				}
				
				nowResizingSidebar = true;
			}
			
			overSidebar = true;
		}
		
		if (nowResizingSidebar) {
			nowSidebarWidth += nowMouseX - lastGrabMouseX;
			lastGrabMouseX = nowMouseX;
			
			nowSidebarWidth = Math.max(4, Math.min(nowSidebarWidth, ModelCreator.Instance.canvWidth - 1));
			
			onResized();
		}
	}
	
	public void mouseUp() {
		nowResizingSidebar = false;
	}
	
	public void onResized() {
		
	}
}
