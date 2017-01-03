package at.vintagestory.modelcreator;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.util.glu.GLU.gluPerspective;

public class Camera
{
	private float x;
	private float y;
	private float z;
	private float rx;
	private float ry;
	private float rz;

	private float fov;
	private float aspect;
	private float near;
	private float far;

	public Camera(float fov, float aspect, float near, float far)
	{
		x = 0;
		y = 0;
		z = -20;
		rx = 30F;
		ry = 0F;
		rz = 0;

		this.fov = fov;
		this.aspect = aspect;
		this.near = near;
		this.far = far;
		initProjection();
	}

	public void initProjection()
	{
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(fov, aspect, near, far);
		glMatrixMode(GL_MODELVIEW);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glEnable(GL_DEPTH_TEST);
	}

	public void useView()
	{
		glTranslatef(x, y, z);
		glRotatef(rx, 1, 0, 0);
		glRotatef(ry, 0, 1, 0);
		glRotatef(rz, 0, 0, 1);
	}

	public float getX()
	{
		return x;
	}

	public float getY()
	{
		return y;
	}

	public float getZ()
	{
		return z;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public void setY(float y)
	{
		this.y = y;
	}

	public void setZ(float z)
	{
		this.z = z;
	}

	public float getRX()
	{
		return rx;
	}

	public float getRY()
	{
		return ry;
	}

	public float getRZ()
	{
		return rz;
	}

	public void setRX(float rx)
	{
		this.rx = rx;
	}

	public void setRY(float ry)
	{
		this.ry = ry;
	}

	public void setRZ(float rz)
	{
		this.rz = rz;
	}

	public void move(float amt, float dir)
	{
		z += amt * Math.sin(Math.toRadians(ry + 90 * dir));
		x += amt * Math.cos(Math.toRadians(ry + 90 * dir));
	}

	public void addX(float amt)
	{
		x += amt;
	}

	public void addY(float amt)
	{
		y += amt;
	}

	public void addZ(float amt)
	{
		z += amt;
	}

	public void rotateX(float amt)
	{
		rx = ((rx + amt) % 360);
	}

	public void rotateY(float amt)
	{
		ry = ((ry + amt) % 360);
	}
}