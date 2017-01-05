package at.vintagestory.modelcreator;

import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import at.vintagestory.modelcreator.gui.GuiMain;
import at.vintagestory.modelcreator.gui.Icons;
import at.vintagestory.modelcreator.gui.left.LeftSidebar;
import at.vintagestory.modelcreator.gui.left.LeftUVSidebar;
import at.vintagestory.modelcreator.gui.middle.ModelRenderer;
import at.vintagestory.modelcreator.gui.right.RightTopPanel;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.ITextureCallback;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.PendingTexture;
import at.vintagestory.modelcreator.util.screenshot.PendingScreenshot;
import at.vintagestory.modelcreator.util.screenshot.Screenshot;

import java.util.prefs.Preferences;

public class ModelCreator extends JFrame
{
	public static Preferences prefs;
	
	private static final long serialVersionUID = 1L;

	public static boolean transparent = true;
	public static boolean unlockAngles = false;

	// Canvas Variables
	private final static AtomicReference<Dimension> newCanvasSize = new AtomicReference<Dimension>();
	private final Canvas canvas;
	private int width = 990, height = 700;

	// Swing Components
	private JScrollPane scroll;
	private IElementManager manager;
	private Element grabbed = null;

	// Texture Loading Cache
	public List<PendingTexture> pendingTextures = new ArrayList<PendingTexture>();
	private PendingScreenshot screenshot = null;

	private int lastMouseX, lastMouseY;
	private boolean grabbing = false;
	private boolean closeRequested = false;

	/* Sidebar Variables */
	private final int SIDEBAR_WIDTH = 130;
	
	public static LeftSidebar uvSidebar;

	
	public ModelRenderer renderer;
	
	static {
		prefs = Preferences.systemNodeForPackage(ModelCreator.class);
	}
	
	
	public ModelCreator(String title)
	{
		super(title);
		
		setDropTarget(new DropTarget() {
			private static final long serialVersionUID = 1L;
			
			@Override
		    public synchronized void drop(DropTargetDropEvent evt) {
				DataFlavor flavor = evt.getCurrentDataFlavors()[0];
		        evt.acceptDrop(evt.getDropAction());
				
		        
		        
				try {
					@SuppressWarnings("rawtypes")
					List data = (List)evt.getTransferable().getTransferData(flavor);
					
					for (Object elem : data) {
						if (elem instanceof File) {
							File file = (File)elem;
							
							if (file.getName().endsWith(".json")) {								
								Importer importer = new Importer(getElementManager(), file.getAbsolutePath());
								importer.importFromJSON();
							}
							
							if (file.getName().endsWith(".png")) {								
								File meta = new File(file.getAbsolutePath() + ".mcmeta");
								
								getElementManager().addPendingTexture(new PendingTexture(file, meta, new ITextureCallback()
								{
									@Override
									public void callback(boolean isnew, String errormessage, String texture)
									{										
										if (errormessage != null)
										{
											JOptionPane error = new JOptionPane();
											error.setMessage(errormessage);
											JDialog dialog = error.createDialog(canvas, "Texture Error");
											dialog.setLocationRelativeTo(null);
											dialog.setModal(false);
											dialog.setVisible(true);
										}
									}
								}));
							}
							
							
							return;
						}
							
					}
					
					
				} catch (Exception e) {
					System.out.println(e);
				}
		        		        
		        evt.dropComplete(true);
		    }
			
		});
		//setTransferHandler(new JsonTransferHandler());
		setPreferredSize(new Dimension(1200, 715));
		setMinimumSize(new Dimension(800, 500));
		setLayout(new BorderLayout(10, 0));
		setIconImages(getIcons());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		canvas = new Canvas();

		initComponents();

		uvSidebar = new LeftUVSidebar("UV Editor", manager);

		canvas.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				newCanvasSize.set(canvas.getSize());
			}
		});

		addWindowFocusListener(new WindowAdapter()
		{
			@Override
			public void windowGainedFocus(WindowEvent e)
			{
				canvas.requestFocusInWindow();
			}
		});

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				closeRequested = true;
			}
		});

		manager.updateValues();

		pack();
		setVisible(true);
		setLocationRelativeTo(null);

		initDisplay();

		
		
		try
		{
			Display.create();

			//WelcomeDialog.show(ModelCreator.this);

			loop();

			Display.destroy();
			dispose();
			System.exit(0);
		}
		catch (LWJGLException e1)
		{
			e1.printStackTrace();
		}
	}

	public void initComponents()
	{
		Icons.init(getClass());
		setupMenuBar();

		canvas.setPreferredSize(new Dimension(1000, 790));
		add(canvas, BorderLayout.CENTER);

		canvas.setFocusable(true);
		canvas.setVisible(true);
		canvas.requestFocus();

		manager = new RightTopPanel(this);
		renderer = new ModelRenderer(manager);
		scroll = new JScrollPane((JPanel) manager);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scroll, BorderLayout.EAST);
	}

	private List<Image> getIcons()
	{
		List<Image> icons = new ArrayList<Image>();
		icons.add(Toolkit.getDefaultToolkit().getImage("res/icons/set/icon_16x.png"));
		icons.add(Toolkit.getDefaultToolkit().getImage("res/icons/set/icon_32x.png"));
		icons.add(Toolkit.getDefaultToolkit().getImage("res/icons/set/icon_64x.png"));
		icons.add(Toolkit.getDefaultToolkit().getImage("res/icons/set/icon_128x.png"));
		return icons;
	}

	private void setupMenuBar()
	{
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		setJMenuBar(new GuiMain(this));
	}

	public void initDisplay()
	{
		try
		{
			Display.setParent(canvas);
			Display.setVSyncEnabled(true);
			Display.setInitialBackground(0.92F, 0.92F, 0.93F);
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
		}
	}

	private void loop() throws LWJGLException
	{
		renderer.camera = new Camera(60F, (float) Display.getWidth() / (float) Display.getHeight(), 0.3F, 1000F);		

		Dimension newDim;

		while (!Display.isCloseRequested() && !getCloseRequested())
		{
			for (PendingTexture texture : pendingTextures)
			{
				texture.load();
			}
			pendingTextures.clear();

			newDim = newCanvasSize.getAndSet(null);

			if (newDim != null)
			{
				width = newDim.width;
				height = newDim.height;
			}

			int leftSpacing = renderer.activeSidebar == null ? 0 : getHeight() < 805 ? SIDEBAR_WIDTH * 2 : SIDEBAR_WIDTH;

			glViewport(leftSpacing, 0, width - leftSpacing, height);

			handleInput(leftSpacing);

			renderer.Render(leftSpacing, width, height, getHeight());
			

			Display.update();

			if (screenshot != null)
			{
				if (screenshot.getFile() != null)
					Screenshot.getScreenshot(width, height, screenshot.getCallback(), screenshot.getFile());
				else
					Screenshot.getScreenshot(width, height, screenshot.getCallback());
				screenshot = null;
			}
		}
	}

	
	public void handleInput(int offset)
	{
		final float cameraMod = Math.abs(renderer.camera.getZ());

		if (Mouse.isButtonDown(0) | Mouse.isButtonDown(1))
		{
			if (!grabbing)
			{
				lastMouseX = Mouse.getX();
				lastMouseY = Mouse.getY();
				grabbing = true;
			}
		}
		else
		{
			grabbing = false;
			grabbed = null;
		}

		if (Mouse.getX() < offset)
		{
			renderer.activeSidebar.handleInput(getHeight());
		}
		else
		{

			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
			{
				if (grabbed == null)
				{
					if (Mouse.isButtonDown(0) | Mouse.isButtonDown(1))
					{
						int sel = select(Mouse.getX(), Mouse.getY());
						if (sel >= 0)
						{
							grabbed = manager.getAllElements().get(sel);
							manager.setSelectedElement(sel);
						}
					}
				}
				else
				{
					Element element = grabbed;
					int state = getCameraState(renderer.camera);

					int newMouseX = Mouse.getX();
					int newMouseY = Mouse.getY();

					int xMovement = (int) ((newMouseX - lastMouseX) / 20);
					int yMovement = (int) ((newMouseY - lastMouseY) / 20);

					if (xMovement != 0 | yMovement != 0)
					{
						if (Mouse.isButtonDown(0))
						{
							switch (state)
							{
							case 0:
								element.addStartX(xMovement);
								element.addStartY(yMovement);
								break;
							case 1:
								element.addStartZ(xMovement);
								element.addStartY(yMovement);
								break;
							case 2:
								element.addStartX(-xMovement);
								element.addStartY(yMovement);
								break;
							case 3:
								element.addStartZ(-xMovement);
								element.addStartY(yMovement);
								break;
							case 4:
								element.addStartX(xMovement);
								element.addStartZ(-yMovement);
								break;
							case 5:
								element.addStartX(yMovement);
								element.addStartZ(xMovement);
								break;
							case 6:
								element.addStartX(-xMovement);
								element.addStartZ(yMovement);
								break;
							case 7:
								element.addStartX(-yMovement);
								element.addStartZ(-xMovement);
								break;
							}
						}
						else if (Mouse.isButtonDown(1))
						{
							switch (state)
							{
							case 0:
								element.addHeight(yMovement);
								element.addWidth(xMovement);
								break;
							case 1:
								element.addHeight(yMovement);
								element.addDepth(xMovement);
								break;
							case 2:
								element.addHeight(yMovement);
								element.addWidth(-xMovement);
								break;
							case 3:
								element.addHeight(yMovement);
								element.addDepth(-xMovement);
								break;
							case 4:
								element.addDepth(-yMovement);
								element.addWidth(xMovement);
								break;
							case 5:
								element.addDepth(xMovement);
								element.addWidth(yMovement);
								break;
							case 6:
								element.addDepth(yMovement);
								element.addWidth(-xMovement);
								break;
							case 7:
								element.addDepth(-xMovement);
								element.addWidth(-yMovement);
								break;
							case 8:
								element.addDepth(-yMovement);
								element.addWidth(xMovement);
								break;
							}
						}

						if (xMovement != 0)
							lastMouseX = newMouseX;
						if (yMovement != 0)
							lastMouseY = newMouseY;

						manager.updateValues();
						element.updateUV();
					}
				}
			}
			else
			{
				if (Mouse.isButtonDown(0))
				{
					final float modifier = (cameraMod * 0.05f);
					renderer.camera.addX((float) (Mouse.getDX() * 0.01F) * modifier);
					renderer.camera.addY((float) (Mouse.getDY() * 0.01F) * modifier);
				}
				else if (Mouse.isButtonDown(1))
				{
					final float modifier = applyLimit(cameraMod * 0.1f);
					renderer.camera.rotateX(-(float) (Mouse.getDY() * 0.5F) * modifier);
					final float rxAbs = Math.abs(renderer.camera.getRX());
					renderer.camera.rotateY((rxAbs >= 90 && rxAbs < 270 ? -1 : 1) * (float) (Mouse.getDX() * 0.5F) * modifier);
				}

				final float wheel = Mouse.getDWheel();
				if (wheel != 0)
				{
					renderer.camera.addZ(wheel * (cameraMod / 5000F));
				}
			}
		}
	}

	public int select(int x, int y)
	{
		IntBuffer selBuffer = ByteBuffer.allocateDirect(1024).order(ByteOrder.nativeOrder()).asIntBuffer();
		int[] buffer = new int[256];

		IntBuffer viewBuffer = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
		int[] viewport = new int[4];

		int hits;
		GL11.glGetInteger(GL11.GL_VIEWPORT, viewBuffer);
		viewBuffer.get(viewport);

		GL11.glSelectBuffer(selBuffer);
		GL11.glRenderMode(GL11.GL_SELECT);
		GL11.glInitNames();
		GL11.glPushName(0);
		GL11.glPushMatrix();
		{
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GLU.gluPickMatrix(x, y, 1, 1, IntBuffer.wrap(viewport));
			GLU.gluPerspective(60F, (float) (width) / (float) height, 0.3F, 1000F);

			renderer.drawGridAndElements();
		}
		GL11.glPopMatrix();
		hits = GL11.glRenderMode(GL11.GL_RENDER);

		selBuffer.get(buffer);
		if (hits > 0)
		{
			int choose = buffer[3];
			int depth = buffer[1];

			for (int i = 1; i < hits; i++)
			{
				if ((buffer[i * 4 + 1] < depth || choose == 0) && buffer[i * 4 + 3] != 0)
				{
					choose = buffer[i * 4 + 3];
					depth = buffer[i * 4 + 1];
				}
			}

			if (choose > 0)
			{
				return choose - 1;
			}
		}

		return -1;
	}

	public float applyLimit(float value)
	{
		if (value > 0.4F)
		{
			value = 0.4F;
		}
		else if (value < 0.15F)
		{
			value = 0.15F;
		}
		return value;
	}

	public int getCameraState(Camera camera)
	{
		int cameraRotY = (int) (camera.getRY() >= 0 ? camera.getRY() : 360 + camera.getRY());
		int state = (int) ((cameraRotY * 4.0F / 360.0F) + 0.5D) & 3;

		if (camera.getRX() > 45)
		{
			state += 4;
		}
		if (camera.getRX() < -45)
		{
			state += 8;
		}
		return state;
	}


	public void startScreenshot(PendingScreenshot screenshot)
	{
		this.screenshot = screenshot;
	}

	public void setSidebar(LeftSidebar s)
	{
		renderer.activeSidebar = s;
	}

	public IElementManager getElementManager()
	{
		return manager;
	}
	
	public void close()
	{
		this.closeRequested = true;
	}

	public boolean getCloseRequested()
	{
		return closeRequested;
	}

	

}
