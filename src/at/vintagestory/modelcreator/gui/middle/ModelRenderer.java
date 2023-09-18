package at.vintagestory.modelcreator.gui.middle;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.Color;
import at.vintagestory.modelcreator.Camera;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Project;
import at.vintagestory.modelcreator.enums.EnumFonts;
import at.vintagestory.modelcreator.gui.left.LeftSidebar;
import at.vintagestory.modelcreator.interfaces.IDrawable;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.model.Element;
import org.lwjgl.util.glu.Sphere;

public class ModelRenderer
{
	public Camera camera;
	private int width = 990, height = 700;
	public LeftSidebar renderedLeftSidebar = null;
	
	public IElementManager manager;
	
	Sphere sphere = new Sphere();
	
	public boolean renderDropTagets = false;
	public Point dropLocation;

	public ModelRenderer(IElementManager manager) {
		this.manager = manager;
	}

	public void Render(int leftSidebarWidth, int width, int height, int frameHeight) {
		this.width = width;
		this.height = height;

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(60F, (float) (width - leftSidebarWidth) / (float) height, 0.3F, 1000F);

		prepareDraw();
		
		drawGridAndElements();
		
		drawTreadmillGrid();
		
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_LIGHTING);

		glViewport(0, 0, width, height);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluOrtho2D(0, width, height, 0);
		
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		renderLeftPane(leftSidebarWidth, frameHeight);
		drawCompass();
		
		if (renderDropTagets) {
			renderDropTargets(width, height);
		}
	}
	
	
	public void prepareDraw()
	{
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_DEPTH_TEST);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		camera.useView();

		if (ModelCreator.darkMode){
			glClearColor(0.22F, 0.22F, 0.22F, 1.0F);
		} else {
			glClearColor(0.92F, 0.92F, 0.93F, 1.0F);
		}
	}

	private void renderDropTargets(int width, int height)
	{
		GL11.glPushMatrix();			
		int mouseX = -10;
		int mouseY = -10;
		
		if (dropLocation != null) {
			mouseX = dropLocation.x;
			mouseY = dropLocation.y - 40;
		}
		
		String texts[] = new String[] {
			"Only Load Texture",
			"Load Texture and Apply to Selected Element",
			"Load Texture and Apply to all Elements",
		};
		
		float[][] colors = new float[][] {
			new float[] { 0.8f, 0.8f, 1f,   0.6f, 0.6f, 0.9f },
			new float[] { 0.8f, 1.0f, 1f,   0.6f, 0.9f, 0.9f },
			new float[] { 1.0f, 0.8f, 1f,   0.9f, 0.6f, 0.9f },
		};
		
		for (int i = 0; i < 3; i++) {
			GL11.glEnable(GL11.GL_BLEND);
			
			float[] color = colors[i]; 
			
			if (mouseX < width && mouseY > 0 && mouseY >= 0 && mouseY < height / 3) {
				glColor4f(color[0], color[1], color[2], 0.85f);
			} else {
				glColor4f(color[3], color[4], color[5], 0.5f);	
			}
			
			glBegin(GL_QUADS);
			{
				glVertex2i(0, 0);
				glVertex2i(width, 0);
				glVertex2i(width, height/3);
				glVertex2i(0, height/3);
			}
			glEnd();
			
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			String text = texts[i];
			float strwdt = EnumFonts.BEBAS_NEUE_50.getWidth(text);
			float strhgt = EnumFonts.BEBAS_NEUE_50.getHeight(text);
			EnumFonts.BEBAS_NEUE_50.drawString((int)(width/2 - strwdt/2), (int)(height/3/2 - strhgt/2), text, new Color(0.3F, 0.3F, 0.3F));
			
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			
			GL11.glTranslated(0, height/3, 0);
			mouseY -= height/3;
			
		}
				
		GL11.glPopMatrix();
	}

	public void drawGridAndElements()
	{
		drawPerspectiveGrid();

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.05f);
		
		glTranslatef(-8, 0, -8);
		
		List<IDrawable> rootelems = getRootElementsForRender(ModelCreator.currentProject);
		if (rootelems == null) return;
		Element selectedElem = manager.getCurrentElement();

		int version = ModelCreator.currentProject.CurrentAnimVersion();
		for (int i = 0; i < rootelems.size(); i++)
		{
			rootelems.get(i).draw(selectedElem, false, version);
		}
		
		if (ModelCreator.currentBackdropProject != null) {
			List<IDrawable> elems = getRootElementsForRender(ModelCreator.currentBackdropProject);
			version = ModelCreator.currentBackdropProject.CurrentAnimVersion();
			for (int i = 0; i < elems.size(); i++) {
				elems.get(i).draw(selectedElem, false, version);
			}
		}
		
		GL11.glDisable(GL11.GL_ALPHA_TEST);

		
		if (!ModelCreator.showGrid) return;
		
		GL11.glPushMatrix();
		{
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GL11.glPushMatrix();
			{
				GL11.glTranslated(0, 0, 16);
				GL11.glScaled(0.018, 0.018, 0.018);
				GL11.glRotated(90, 1, 0, 0);
				EnumFonts.BEBAS_NEUE_50.drawString(8, 0, "VS Model Creator", new Color(0.5F, 0.5F, 0.6F));
			}
			GL11.glPopMatrix();
			
			if (ModelCreator.currentProject.EntityTextureMode) {
				GL11.glTranslated(-1, 0, 8 + 2);
				GL11.glScaled(0.018, 0.018, 0.018);
				GL11.glRotated(90, 0, 1, 0);
				GL11.glRotated(90, 1, 0, 0);
				EnumFonts.BEBAS_NEUE_50.drawString(8, 0, "Entity front", new Color(0.5F, 0.5F, 0.6F));
			}

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glDisable(GL11.GL_BLEND);
		}
		GL11.glPopMatrix();
	}
	


	public void drawPerspectiveGrid()
	{
		if (!ModelCreator.showGrid) return;
		
		float d1 = 0.8f;
		float d2 = 0.6f;
		if (ModelCreator.darkMode) { d1 = 1.2f; d2 = 1.4f; }
		
		glPushMatrix();
		{
			glColor3f(0.55F, 0.55F, 0.60F);
			glTranslatef(-8, 0, -8);

			// Thin inside lines
			glLineWidth(1F);
			glBegin(GL_LINES);
			{
				for (int i = 1; i <= 16; i++)
				{
					glVertex3i(i, 0, 0);
					glVertex3i(i, 0, 16);
				}

				for (int i = 1; i <= 16; i++)
				{
					glVertex3i(0, 0, i);
					glVertex3i(16, 0, i);
				}
			}
			glEnd();
			
			
			glColor3f(0.55F * d2, 0.55F * d2, 0.60F * d2);
			
			// Bold outside lines
			glLineWidth(2F);
			glBegin(GL_LINES);
			{
				glVertex3i(0, 0, 0);
				glVertex3i(0, 0, 16);
				glVertex3i(16, 0, 0);
				glVertex3i(16, 0, 16);
				glVertex3i(0, 0, 16);
				glVertex3i(16, 0, 16);
				glVertex3i(0, 0, 0);
				glVertex3i(16, 0, 0);
			}
			glEnd();

			glColor3f(0.55F * d1, 0.55F * d1, 0.60F * d1);
			
			// Bold center cross
			glLineWidth(2F);
			glBegin(GL_LINES);
			{
				glVertex3i(8, 0, 0);
				glVertex3i(8, 0, 16);
				glVertex3i(0, 0, 8);
				glVertex3i(16, 0, 8);
			}
			glEnd();

			glColor3f(0.55F, 0.55F, 0.60F);
			
			// Thin half transparent line to show block size
			glLineWidth(1F);
			glColor3f(0.55F / d1, 0.55F / d1, 0.55F / d1);
			
			
			glBegin(GL_LINES);
			{
				glVertex3i(0, 0, 0);
				glVertex3i(0, 16, 0);
				
				glVertex3i(16, 0, 0);
				glVertex3i(16, 16, 0);

				glVertex3i(16, 0, 16);
				glVertex3i(16, 16, 16);
				
				glVertex3i(0, 0, 16);
				glVertex3i(0, 16, 16);
				
				glVertex3i(0, 16, 0);
				glVertex3i(16, 16, 0);
				
				glVertex3i(16, 16, 0);
				glVertex3i(16, 16, 16);
				
				glVertex3i(16, 16, 16);
				glVertex3i(0, 16, 16);
				
				glVertex3i(0, 16, 16);
				glVertex3i(0, 16, 0);
			}
			glEnd();
		}
		glPopMatrix();
	}


	public void renderLeftPane(int sidebarWidth, int frameHeight)
	{
		
		glPushMatrix();
		{
			if (ModelCreator.darkMode) {
				glColor3f(0.08F, 0.08F, 0.08F);	
			} else {
				glColor3f(0.58F, 0.58F, 0.58F);
			}
			
			glLineWidth(2F);
			glBegin(GL_LINES);
			{
				glVertex2i(sidebarWidth, 0);
				glVertex2i(width, 0);
				glVertex2i(width, 0);
				glVertex2i(width, height);
				glVertex2i(sidebarWidth, height);
				glVertex2i(sidebarWidth, 0);
				glVertex2i(sidebarWidth, height);
				glVertex2i(width, height);
			}
			glEnd();
		}
		glPopMatrix();

		if (renderedLeftSidebar != null) {
			renderedLeftSidebar.draw(sidebarWidth, width, height, frameHeight);
		}
	}
	
	
	public void drawCompass() {
		if (!ModelCreator.showGrid) return;
		
		glPushMatrix();
		{
			glTranslatef(width - 70, height - 70, 0);
			glLineWidth(2F);
			glRotated(-camera.getRY(), 0, 0, 1);
			GL11.glScaled(0.85, 0.85, 0.85);

			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			EnumFonts.BEBAS_NEUE_20.drawString(-5, -75, "N", new Color(1, 1, 1));
			EnumFonts.BEBAS_NEUE_20.drawString(-5, 55, "S", new Color(1, 1, 1));
			EnumFonts.BEBAS_NEUE_20.drawString(-70, -10, "W", new Color(1, 1, 1));
			EnumFonts.BEBAS_NEUE_20.drawString(55, -10, "E", new Color(1, 1, 1));
			GL11.glDisable(GL11.GL_BLEND);

			glColor3d(0.6, 0.6, 0.6);
			glBegin(GL_LINES);
			{
				glVertex2i(0, -50);
				glVertex2i(0, 50);
				glVertex2i(-50, 0);
				glVertex2i(50, 0);
			}
			glEnd();

			glColor3d(0.3, 0.3, 0.6);
			
			glBegin(GL_TRIANGLES);
			{
				glVertex2i(-5, -40);
				glVertex2i(0, -53);
				glVertex2i(5, -40);

				glVertex2i(-5, 40);
				glVertex2i(0, 53);
				glVertex2i(5, 40);

				glVertex2i(-40, -5);
				glVertex2i(-53, 0);
				glVertex2i(-40, 5);

				glVertex2i(40, -5);
				glVertex2i(53, 0);
				glVertex2i(40, 5);
			}
			glEnd();
			
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
		
		glPopMatrix();
		
	}


	
	
	
	
	
	
	

	float accum = 0f;
	
	
	public void drawTreadmillGrid()
	{
		if (!ModelCreator.showTreadmill) return;
		
		accum += 1/60f * ModelCreator.TreadMillSpeed;
		if (accum > 1) accum -= 1;
		
		float d1 = 0.8f;
		float d2 = 0.6f;
		if (ModelCreator.darkMode) { d1 = 0.8f; d2 = 1.0f; }
	
		
		for (int dx = -8; dx <= 8; dx++) {
			for (int dz = -8; dz <= 8; dz++) {
				glPushMatrix();
				glTranslated(dx * 16 + accum * 16, 0, dz * 16);
				
				glColor3f(0.55F, 0.55F, 0.60F);
				glTranslatef(-8, 0, -8);

				// Thin inside lines
				glLineWidth(1F);
				glBegin(GL_LINES);
				{
					for (int i = 1; i <= 16; i++)
					{
						glVertex3i(i, 0, 0);
						glVertex3i(i, 0, 16);
					}

					for (int i = 1; i <= 16; i++)
					{
						glVertex3i(0, 0, i);
						glVertex3i(16, 0, i);
					}
				}
				glEnd();
				
				glColor3f(0.55F * d2, 0.55F * d2, 0.60F * d2);
				
				// Bold outside lines
				glLineWidth(2F);
				glBegin(GL_LINES);
				{
					glVertex3i(0, 0, 0);
					glVertex3i(0, 0, 16);
					glVertex3i(16, 0, 0);
					glVertex3i(16, 0, 16);
					glVertex3i(0, 0, 16);
					glVertex3i(16, 0, 16);
					glVertex3i(0, 0, 0);
					glVertex3i(16, 0, 0);
				}
				glEnd();

				glColor3f(0.55F * d1, 0.55F * d1, 0.60F * d1);
				
				// Bold center cross
				glLineWidth(2F);
				glBegin(GL_LINES);
				{
					glVertex3i(8, 0, 0);
					glVertex3i(8, 0, 16);
					glVertex3i(0, 0, 8);
					glVertex3i(16, 0, 8);
				}
				glEnd();

				glColor3f(0.55F, 0.55F, 0.60F);
				
				glPopMatrix();
				
			}
		}
		
	}

	
	public static List<IDrawable> getRootElementsForRender(Project project) {
		if (project == null) return null;
		
		try {
			if (ModelCreator.leftKeyframesPanel.isVisible()) {
				return project.getCurrentFrameRootElements();
			} else {
				return new ArrayList<IDrawable>(project.rootElements);
			}
		} catch (Exception e) {
			System.out.println(e);
			return new ArrayList<IDrawable>();
		}
	}
	
	
}
