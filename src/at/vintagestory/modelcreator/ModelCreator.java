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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import at.vintagestory.modelcreator.gui.GuiMenu;
import at.vintagestory.modelcreator.gui.Icons;
import at.vintagestory.modelcreator.gui.left.LeftKeyFramesPanel;
import at.vintagestory.modelcreator.gui.left.LeftSidebar;
import at.vintagestory.modelcreator.gui.left.LeftUVSidebar;
import at.vintagestory.modelcreator.gui.middle.ModelRenderer;
import at.vintagestory.modelcreator.gui.right.RightTopPanel;
import at.vintagestory.modelcreator.interfaces.IDrawable;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.ITextureCallback;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.PendingTexture;
import at.vintagestory.modelcreator.model.TextureEntry;
import at.vintagestory.modelcreator.util.screenshot.AnimatedGifCapture;
import at.vintagestory.modelcreator.util.screenshot.PendingScreenshot;
import at.vintagestory.modelcreator.util.screenshot.ScreenshotCapture;

import java.util.prefs.Preferences;

public class ModelCreator extends JFrame implements ITextureCallback
{
	public static String windowTitle = "Vintage Story Model Creator"; 
	private static final long serialVersionUID = 1L;
	
	public static ModelCreator Instance;
	
	public static Preferences prefs;
	
	public static Project currentProject;
	public static ProjectChangeHistory changeHistory = new ProjectChangeHistory();
	
	public static boolean ignoreDidModify = false;
	public static boolean ignoreValueUpdates = false;
	public static boolean ignoreFrameUpdates = false;
	
	public static boolean showGrid = true;
	public static boolean transparent = true;
	public static boolean renderTexture = true;
	public static boolean autoreloadTexture = true;
	public static float noTexScale = 2;

	// Canvas Variables
	private final static AtomicReference<Dimension> newCanvasSize = new AtomicReference<Dimension>();
	private final Canvas canvas;
	private int width = 990, height = 700;

	// Swing Components
	private JScrollPane scroll;
	public static IElementManager manager;
	private Element grabbed = null;

	// Texture Loading Cache
	List<PendingTexture> pendingTextures = Collections.synchronizedList(new ArrayList<PendingTexture>());
	private PendingScreenshot screenshot = null;
	public static AnimatedGifCapture gifCapture = null;
	
	
	

	private int lastMouseX, lastMouseY;
	boolean mouseDownOnLeftPanel;
	boolean mouseDownOnCenterPanel;
	
	private boolean grabbing = false;
	private boolean closeRequested = false;

	/* Sidebar Variables */
	private final int SIDEBAR_WIDTH = 4 * 32 + 20;
	
	public LeftSidebar uvSidebar;
	public static GuiMenu guiMain;
	public static LeftKeyFramesPanel leftKeyframesPanel;
	public static boolean renderAttachmentPoints;

	
	public ModelRenderer modelrenderer;
	
	public long prevFrameMillisec;
	
	
	static {
		prefs = Preferences.userRoot().node("ModelCreator");
	}
	
	
	public ModelCreator(String title) throws LWJGLException
	{
		super(title);
		
		showGrid = prefs.getBoolean("showGrid", false);
		noTexScale = prefs.getFloat("noTexScale", 2);
		autoreloadTexture = prefs.getBoolean("autoreloadTexture", true);
		
		Instance = this;
		
		currentProject = new Project(null);
		changeHistory.addHistoryState(currentProject);
		
		setDropTarget(getCustomDropTarget());		
		setPreferredSize(new Dimension(1200, 715));
		setMinimumSize(new Dimension(800, 500));
		setLayout(new BorderLayout(10, 0));
		setIconImages(getIcons());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		
		
		canvas = new Canvas();
		
		initComponents();
		
		


		canvas.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				newCanvasSize.set(canvas.getSize());
			}
		});

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				if (currentProject.needsSaving) {
					int	returnVal = JOptionPane.showConfirmDialog(null, "You have not saved your changes yet, would you like to save now?", "Warning", JOptionPane.YES_NO_CANCEL_OPTION);
					
					if (returnVal == JOptionPane.YES_OPTION) {
						if (ModelCreator.currentProject.filePath == null) {
							SaveProjectAs();
						} else {
							SaveProject(new File(ModelCreator.currentProject.filePath));	
						}
						
					}
					
					if (returnVal == JOptionPane.CANCEL_OPTION || returnVal == JOptionPane.CLOSED_OPTION) {
						return;
					}

				}
				
				
				
				closeRequested = true;
			}
		});
		
		
		// Seriously man, fuck java. Mouse listeners on a canvas are just plain not working. 
		// canvas.addMouseListener(ml);
				
		
		
		pack();
		setVisible(true);
		setLocationRelativeTo(null);

		initDisplay();

		
		
		currentProject.LoadIntoEditor(getElementManager());
		updateValues(null);

		
		prevFrameMillisec = System.currentTimeMillis();
		
		try
		{
			Display.create();

			loop();

			Display.destroy();
			dispose();
			System.exit(0);
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			JOptionPane.showMessageDialog(
				null, 
				"Main loop crashed, please make a screenshot of this message and report it, program will exit now. Sorry about that :(\nException: " + e1 + "\n" + stackTraceToString(e1), 
				"Crash!", 
				JOptionPane.ERROR_MESSAGE, 
				null
			);
			System.exit(0);
		}
	}
	
	public static String stackTraceToString(Throwable e) {
	    StringBuilder sb = new StringBuilder();
	    for (StackTraceElement element : e.getStackTrace()) {
	        sb.append(element.toString());
	        sb.append("\n");
	    }
	    return sb.toString();
	}
	

	public static void DidModify() {
		if (ignoreDidModify) return;
		if (currentProject == null) return;
		
		currentProject.needsSaving = true;
		changeHistory.addHistoryState(currentProject);
		updateTitle();
	}

	
	public void initComponents()
	{
		Icons.init(getClass());
		
		manager = new RightTopPanel(this);

		leftKeyframesPanel = new LeftKeyFramesPanel(manager);
		leftKeyframesPanel.setVisible(false);
		add(leftKeyframesPanel, BorderLayout.WEST);

		canvas.setPreferredSize(new Dimension(1000, 850));
		add(canvas, BorderLayout.CENTER);

		canvas.setFocusable(true);
		canvas.setVisible(true);
		canvas.requestFocus();

		modelrenderer = new ModelRenderer(manager);
		
		scroll = new JScrollPane((JPanel) manager);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scroll, BorderLayout.EAST);
		
		uvSidebar = new LeftUVSidebar("UV Editor", manager);
		
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		setJMenuBar(guiMain = new GuiMenu(this));
	}

	private List<Image> getIcons()
	{
		List<Image> icons = new ArrayList<Image>();
		icons.add(Toolkit.getDefaultToolkit().getImage("assets/appicon_16x.png"));
		icons.add(Toolkit.getDefaultToolkit().getImage("assets/appicon_32x.png"));
		icons.add(Toolkit.getDefaultToolkit().getImage("assets/appicon_64x.png"));
		icons.add(Toolkit.getDefaultToolkit().getImage("assets/appicon_128x.png"));
		return icons;
	}

	
	
	public static void updateValues(JComponent byGuiElem)
	{
		if (currentProject == null) return;
		if (ignoreValueUpdates) return;
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() { 
				ignoreValueUpdates = true;
				
				if (currentProject.SelectedAnimation != null) {
					currentProject.SelectedAnimation.SetFramesDirty();
				}
				
				guiMain.updateValues(byGuiElem);
			 	((RightTopPanel)manager).updateValues(byGuiElem);
			 	leftKeyframesPanel.updateValues(byGuiElem);
			 	updateFrame(false);
			 	updateTitle();
			 	
			 	ignoreValueUpdates = false;
			}
		});
	}
	
	static void updateTitle() {
	 	String dash = currentProject.needsSaving ? " * " : " - ";
	 	if (currentProject.filePath == null) {
	 		Instance.setTitle("(untitled)" + dash + windowTitle);
		} else {
			Instance.setTitle(new File(currentProject.filePath).getName() + dash + windowTitle);
		}		
	}
	
	public static void updateFrame() {
		updateFrame(true);
	}
	
	public static void updateFrame(boolean later) {
		if (currentProject == null) return;
		if (ignoreFrameUpdates) return;
		
		if (later) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() { 
					ignoreFrameUpdates = true;
					
					leftKeyframesPanel.updateFrame();
					((RightTopPanel)manager).updateFrame(null);
					updateTitle();
					guiMain.updateFrame();
					
					ignoreFrameUpdates = false;
				}
			});
		} else {
			ignoreFrameUpdates = true;
			
			leftKeyframesPanel.updateFrame();
			((RightTopPanel)manager).updateFrame(null);
			updateTitle();
			guiMain.updateFrame();
			
			ignoreFrameUpdates = false;
		}
	}


	
	public void AddPendingTexture(PendingTexture texture) {
		synchronized (pendingTextures)
		{
			pendingTextures.add(texture);
		}
	}
	
	public void initDisplay() throws LWJGLException
	{
		Display.setParent(canvas);
		Display.setVSyncEnabled(true);
		Display.setInitialBackground(0.92F, 0.92F, 0.93F);
	}

	private void loop() throws Exception
	{
		modelrenderer.camera = new Camera(60F, (float) Display.getWidth() / (float) Display.getHeight(), 0.3F, 1000F);		

		Dimension newDim;
		
		while (!Display.isCloseRequested() && !getCloseRequested())
		{
			Project project = ModelCreator.currentProject;
			
			if (project == null) {
				Thread.sleep(5);
				continue;
			}
			
			synchronized (pendingTextures)
			{
				for (PendingTexture texture : pendingTextures)
				{
					texture.load();
				}
				pendingTextures.clear();				
			}
			
			if (project.SelectedAnimation != null && project.SelectedAnimation.framesDirty) {
				project.SelectedAnimation.calculateAllFrames(project);
			}

			newDim = newCanvasSize.getAndSet(null);

			if (newDim != null)
			{
				width = newDim.width;
				height = newDim.height;
			}

			int leftSidebarWidth = leftSidebarWidth();
			glViewport(leftSidebarWidth, 0, width - leftSidebarWidth, height);
			handleInput(leftSidebarWidth);
			
			
			if (gifCapture != null && !gifCapture.isComplete()) {
				gifCapture.PrepareFrame();
			}

			modelrenderer.Render(leftSidebarWidth, width, height, getHeight());
			

			Display.update();

			if (screenshot != null)
			{
				if (screenshot.getFile() != null)
					ScreenshotCapture.getScreenshot(width, height, screenshot.getCallback(), screenshot.getFile());
				else
					ScreenshotCapture.getScreenshot(width, height, screenshot.getCallback());
				screenshot = null;
			}
			
			
			if (gifCapture != null && !gifCapture.isComplete()) {
				gifCapture.CaptureFrame(width, height);
				
				if (gifCapture.isComplete()) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run()
						{
							JOptionPane.showMessageDialog(null, "Gif export complete");							
						}
					});
					
					gifCapture = null;
				}
			} else {
				
				if (project != null && project.SelectedAnimation != null && project.PlayAnimation) {
					project.SelectedAnimation.NextFrame();
					updateFrame(true);
				}
				

				// Don't run faster than ~30 FPS (1000 / 30 = 33ms)
				long duration = System.currentTimeMillis() - prevFrameMillisec; 
				Thread.sleep(Math.max(33 - duration, 0));
				prevFrameMillisec = System.currentTimeMillis();
				
			}
			
			
		}
	}
	
	public int leftSidebarWidth() {
		Project project = ModelCreator.currentProject;
		
		int leftSpacing = 0;
		if (modelrenderer.renderedLeftSidebar != null) {
			leftSpacing = project.EntityTextureMode || getHeight() < 911 ? SIDEBAR_WIDTH * 2 : SIDEBAR_WIDTH;
		}
		
		return leftSpacing;
	}

	
	boolean zKeyDown;
	boolean yKeyDown;
	boolean sKeyDown;
	boolean rKeyDown;
	boolean tKeyDown;
	
	
	public void handleInput(int leftSidebarWidth)
	{
		final float cameraMod = Math.abs(modelrenderer.camera.getZ());
		
		boolean isOnLeftPanel = Mouse.getX() < leftSidebarWidth;
		
		if (Mouse.isButtonDown(0) | Mouse.isButtonDown(1))
		{
			if (!grabbing)
			{
				lastMouseX = Mouse.getX();
				lastMouseY = Mouse.getY();
				grabbing = true;
			}
			
			if (!mouseDownOnCenterPanel && !mouseDownOnLeftPanel) {
				mouseDownOnLeftPanel = isOnLeftPanel;
				mouseDownOnCenterPanel = !isOnLeftPanel;
			}
		}
		else
		{
			grabbing = false;
			grabbed = null;
			if (mouseDownOnLeftPanel) modelrenderer.renderedLeftSidebar.mouseUp();
			
			mouseDownOnLeftPanel = false;
			mouseDownOnCenterPanel = false;
		}

		
		if (mouseDownOnLeftPanel)
		{
			modelrenderer.renderedLeftSidebar.handleInput();
			return;
		}

		
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
		{
			if (Keyboard.isKeyDown(Keyboard.KEY_Z)) zKeyDown = true;
			else {
				if (zKeyDown) {
					zKeyDown = false;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() { changeHistory.Undo(); } 
					});
				}
			}
			
			
			if (Keyboard.isKeyDown(Keyboard.KEY_Y)) yKeyDown = true;
			else {
				if (yKeyDown) {
					yKeyDown = false;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() { changeHistory.Redo(); } 
					});
					
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) sKeyDown = true;
			else {
				if (sKeyDown) {
					sKeyDown = false;
					
					SwingUtilities.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							if (ModelCreator.currentProject.filePath == null) {
								SaveProjectAs();
							} else {
								SaveProject(new File(ModelCreator.currentProject.filePath));
							}								
						}
					});
					
				}
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_R)) rKeyDown = true;
			else {
				if (rKeyDown) {
					rKeyDown = false;
					SwingUtilities.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							ModelCreator.currentProject.reloadTextures(ModelCreator.Instance);						
						}
					});
				}
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_T)) tKeyDown = true;
			else {
				if (tKeyDown) {
					tKeyDown = false;
					renderTexture = !renderTexture;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() { updateValues(null); } 
					});
				}
			}
			
			
			if (grabbed == null && (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)))
			{
				int openGlName = getElementGLNameAtPos(Mouse.getX(), Mouse.getY());
				if (openGlName >= 0)
				{
					currentProject.selectElementByOpenGLName(openGlName);
					grabbed = manager.getCurrentElement();
				}
			}

			if (grabbing && grabbed != null)
			{
				Element element = grabbed;
				int state = getCameraState(modelrenderer.camera);

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

					if (xMovement != 0) {
						lastMouseX = newMouseX;
					}
					if (yMovement != 0) {
						lastMouseY = newMouseY;
					}
					
					updateValues(null);
					element.updateUV();
				}
			}
		}
		else
		{
			if (Mouse.isButtonDown(0))
			{
				final float modifier = (cameraMod * 0.05f);
				modelrenderer.camera.addX((float) (Mouse.getDX() * 0.01F) * modifier);
				modelrenderer.camera.addY((float) (Mouse.getDY() * 0.01F) * modifier);
			}
			else if (Mouse.isButtonDown(1))
			{
				final float modifier = applyLimit(cameraMod * 0.1f);
				modelrenderer.camera.rotateX(-(float) (Mouse.getDY() * 0.5F) * modifier);
				final float rxAbs = Math.abs(modelrenderer.camera.getRX());
				modelrenderer.camera.rotateY((rxAbs >= 90 && rxAbs < 270 ? -1 : 1) * (float) (Mouse.getDX() * 0.5F) * modifier);
			}

			final float wheel = Mouse.getDWheel();
			if (wheel != 0)
			{
				modelrenderer.camera.addZ(wheel * (cameraMod / 5000F));
			}
		}
	
	}

	public int getElementGLNameAtPos(int x, int y)
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

			modelrenderer.drawGridAndElements();
		}
		GL11.glPopMatrix();
		hits = GL11.glRenderMode(GL11.GL_RENDER);

		selBuffer.get(buffer);
		if (hits > 0)
		{
			int name = buffer[3];
			int depth = buffer[1];
			
			for (int i = 1; i < hits; i++)
			{
				if ((buffer[i * 4 + 1] < depth || name == 0) && buffer[i * 4 + 3] != 0)
				{
					name = buffer[i * 4 + 3];
					depth = buffer[i * 4 + 1];
				}
			}

			return name;
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
		modelrenderer.renderedLeftSidebar = s;
	}
	
	
	public static List<IDrawable> getRootElementsForRender() {
		if (currentProject == null) return null;
		
		try {
			if (leftKeyframesPanel.isVisible()) {
				return currentProject.getCurrentFrameRootElements();
			} else {
				return new ArrayList<IDrawable>(currentProject.rootElements);
			}
		} catch (Exception e) {
			System.out.println(e);
			return new ArrayList<IDrawable>();
		}
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

	

	
	

	private DropTarget getCustomDropTarget()
	{
		 return new DropTarget() {
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
								LoadFile(file.getAbsolutePath());
								return;
							}
							
							if (file.getName().endsWith(".png")) {
								String code = file.getName();
								code = code.substring(0, code.indexOf("."));
								AddPendingTexture(new PendingTexture(code, file, ModelCreator.Instance));
								return;
							}
							
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run()
								{
									JOptionPane.showMessageDialog(null, "Huh? What file is this? I can only read .png and .json :(");							
								}
							});
							
							
							return;
						}
							
					}
					
				} catch (Exception e) {
					System.out.println("Failed reading dropped file. File is probably in an incorrect format.");
					StackTraceElement[] elems = e.getStackTrace();
					String trace = "";
					for (int i = 0; i < elems.length; i++) {
						trace += elems[i].toString() + "\n";
						if (i >= 10) break;
					}
					
		        	JOptionPane.showMessageDialog(null, "Couldn't open this file, something unexpecteded happened\n\n" + e.toString() + "\nat\n" + trace);
		        	e.printStackTrace();
				}
		        		        
		        evt.dropComplete(true);
		    }
		 };
	}

	
	@Override
	public void onTextureLoaded(boolean isNew, String errormessage, String texture)
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

	public void LoadFile(String filePath)
	{
		if (ModelCreator.currentProject.rootElements.size() > 0 && currentProject.needsSaving)
		{
			int returnVal = JOptionPane.showConfirmDialog(null, "Your current unsaved project will be cleared, are you sure you want to continue?", "Warning", JOptionPane.YES_NO_OPTION);		
			if (returnVal == JOptionPane.NO_OPTION || returnVal == JOptionPane.CLOSED_OPTION) return;
		}
				
		ignoreDidModify = true;
		
		if (filePath == null) {
			setTitle("(untitled) - " + windowTitle);
			currentProject = new Project(null);
			currentProject.LoadIntoEditor(ModelCreator.manager);
			
		} else {
			prefs.put("filePath", filePath);
			Importer importer = new Importer(filePath);
			
			//currentProject = null; - why is that here? it crashes the editor!
			ignoreValueUpdates = true;
			Project project = importer.loadFromJSON();
			Project oldproject = currentProject;
			currentProject = project;
			
			for (TextureEntry entry : oldproject.TexturesByCode.values()) {
				entry.Dispose();
			}
			
			ignoreValueUpdates = false;
			currentProject.LoadIntoEditor(ModelCreator.manager);
			
			setTitle(new File(currentProject.filePath).getName() + " - " + windowTitle);
		}
		
		ignoreDidModify = false;
		
		changeHistory.clear();
		changeHistory.addHistoryState(currentProject);
		
		currentProject.needsSaving = false;		
		ModelCreator.updateValues(null);
		currentProject.tree.jtree.updateUI();		
	}
	

	public void SaveProject(File file)
	{
		Exporter exporter = new Exporter(ModelCreator.currentProject);
		exporter.export(file);
		
		ModelCreator.currentProject.filePath = file.getAbsolutePath(); 
		currentProject.needsSaving = false;
		ModelCreator.updateValues(null);
		changeHistory.didSave();
	}
	

	public void SaveProjectAs()
	{
		JFileChooser chooser = new JFileChooser(ModelCreator.prefs.get("filePath", "."));
		chooser.setDialogTitle("Output Directory");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setApproveButtonText("Save");

		FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON (.json)", "json");
		chooser.setFileFilter(filter);

		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			if (chooser.getSelectedFile().exists())
			{
				returnVal = JOptionPane.showConfirmDialog(null, "A file already exists with that name, are you sure you want to override?", "Warning", JOptionPane.YES_NO_OPTION);
			}
			if (returnVal != JOptionPane.NO_OPTION && returnVal != JOptionPane.CLOSED_OPTION)
			{
				String filePath = chooser.getSelectedFile().getAbsolutePath();
				ModelCreator.prefs.put("filePath", filePath);
				
				if (!filePath.endsWith(".json")) {
					chooser.setSelectedFile(new File(filePath + ".json"));
				}
				SaveProject(chooser.getSelectedFile());
			}
		}
	}
}
