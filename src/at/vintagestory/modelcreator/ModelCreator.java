package at.vintagestory.modelcreator;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
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
import javax.swing.tree.DefaultMutableTreeNode;
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
import at.vintagestory.modelcreator.model.Animation;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.PendingTexture;
import at.vintagestory.modelcreator.model.TextureEntry;
import at.vintagestory.modelcreator.util.screenshot.AnimationCapture;
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
	public static Project currentBackdropProject;
	public static ProjectChangeHistory changeHistory = new ProjectChangeHistory();
	
	public static boolean ignoreDidModify = false;	
	public static boolean ignoreValueUpdates = false;
	public static boolean ignoreFrameUpdates = false;
	
	
	public static boolean showGrid = true;
	public static boolean transparent = true;
	public static boolean renderTexture = true;
	public static boolean autoreloadTexture = true;
	public static boolean repositionWhenReparented = true;
	public static boolean darkMode = false;
	public static boolean saratyMode = false;
	public static boolean uvShowNames = false;

	public static float noTexScale = 2;

	// Canvas Variables
	private final static AtomicReference<Dimension> newCanvasSize = new AtomicReference<Dimension>();
	public static Canvas canvas;
	public int canvWidth = 990, canvHeight = 700;

	// Swing Components
	public JScrollPane scroll;
	public static RightTopPanel rightTopPanel;
	private Element grabbedElem = null;

	// Texture Loading Cache
	List<PendingTexture> pendingTextures = Collections.synchronizedList(new ArrayList<PendingTexture>());
	private PendingScreenshot screenshot = null;
	public static AnimationCapture animCapture = null;
	
	
	

	private int lastMouseX, lastMouseY;
	boolean mouseDownOnLeftPanel;
	boolean mouseDownOnCenterPanel;
	boolean mouseDownOnRightPanel;
	
	private boolean grabbing = false;
	private boolean closeRequested = false;
	

	
	public LeftSidebar uvSidebar;
	public static GuiMenu guiMain;
	public static LeftKeyFramesPanel leftKeyframesPanel;
	public static boolean renderAttachmentPoints;

	
	public ModelRenderer modelrenderer;
	
	public long prevFrameMillisec;
	
	
	static {
		prefs = Preferences.userRoot().node("ModelCreator");
	}
	
	
	public ModelCreator(String title, String[] args) throws LWJGLException
	{
		super(title);
		
		EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
		queue.push(new EventQueueProxy());
		
		showGrid = prefs.getBoolean("showGrid", true);
		saratyMode = prefs.getBoolean("uvRotateRename", true);
		uvShowNames = prefs.getBoolean("uvShowNames", true);
		darkMode = prefs.getBoolean("darkMode", false);
		noTexScale = prefs.getFloat("noTexScale", 2);
		autoreloadTexture = prefs.getBoolean("autoreloadTexture", true);
		repositionWhenReparented = prefs.getBoolean("repositionWhenReparented", true);
		
		Instance = this;
		
		currentProject = new Project(null);
		changeHistory.addHistoryState(currentProject);
		
		setDropTarget(getCustomDropTarget());
		setPreferredSize(new Dimension(1200, 715));
		setMinimumSize(new Dimension(800, 500));
		setLayout(new BorderLayout(10, 0));
		setIconImages(getIcons());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		if (args.length > 0) {
			ModelCreator.prefs.put("texturePath", args[0]);			
		}
		
		if (args.length > 1) {
			ModelCreator.prefs.put("shapePath", args[1]);
		}
		
		
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
			//dispose();
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
		
		rightTopPanel = new RightTopPanel(this);

		leftKeyframesPanel = new LeftKeyFramesPanel(rightTopPanel);
		leftKeyframesPanel.setVisible(false);
		add(leftKeyframesPanel, BorderLayout.WEST);
		
		// Canvas stuff
		canvas.setFocusable(true);
		canvas.setVisible(true);
		canvas.requestFocus();
		
		
		//but works
		final double viewScale = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getDefaultTransform().getScaleX();
		
		JPanel panel = new JPanel(null);
		panel.add(canvas);
		add(panel, BorderLayout.CENTER);
		
		panel.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				canvas.setSize((int)(panel.getWidth()*viewScale), (int)(panel.getHeight()*viewScale));
			}
		});
		
		modelrenderer = new ModelRenderer(rightTopPanel);
		
		scroll = new JScrollPane((JPanel) rightTopPanel);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.getVerticalScrollBar().setUnitIncrement(16);

		add(scroll, BorderLayout.EAST);
		
		uvSidebar = new LeftUVSidebar("UV Editor", rightTopPanel);
		
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
			 	((RightTopPanel)rightTopPanel).updateValues(byGuiElem);
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
					((RightTopPanel)rightTopPanel).updateFrame(null);
					updateTitle();
					guiMain.updateFrame();
					
					ignoreFrameUpdates = false;
				}
			});
		} else {
			ignoreFrameUpdates = true;
			
			leftKeyframesPanel.updateFrame();
			((RightTopPanel)rightTopPanel).updateFrame(null);
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

	
	ArrayList<PendingTexture> notLoadedPendingTexs = new ArrayList<PendingTexture>();
	int frameCounter;
	
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
			
			frameCounter++;
			
			synchronized (pendingTextures)
			{
				for (PendingTexture texture : pendingTextures)
				{
					if (texture.LoadDelay > 0) {
						texture.LoadDelay--;
						notLoadedPendingTexs.add(texture);
						continue;
					}
					
					texture.load();
				}
				
				pendingTextures.clear();	
				pendingTextures.addAll(notLoadedPendingTexs);
				notLoadedPendingTexs.clear();
			}
			
			
			if (project.SelectedAnimation != null && project.SelectedAnimation.framesDirty) {
				project.SelectedAnimation.calculateAllFrames(project);
			}

			newDim = newCanvasSize.getAndSet(null);

			if (newDim != null)
			{
				canvWidth = newDim.width;
				canvHeight = newDim.height;
			}

			// glViewPort view must not go negative 
			int leftSidebarWidth = leftSidebarWidth();
			if (canvWidth - leftSidebarWidth < 0)  {
				if (modelrenderer.renderedLeftSidebar != null) {
				 	modelrenderer.renderedLeftSidebar.nowSidebarWidth = canvWidth - 10;
				 	leftSidebarWidth = leftSidebarWidth();
				}
			}
			
			glViewport(leftSidebarWidth, 0, canvWidth - leftSidebarWidth, canvHeight);
			handleInput(leftSidebarWidth);
			
			
			if (animCapture != null && !animCapture.isComplete()) {
				animCapture.PrepareFrame();
			}
			
			
			if (ModelCreator.transparent) {
				GL11.glEnable(GL11.GL_BLEND);
				glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			}

			modelrenderer.Render(leftSidebarWidth, canvWidth, canvHeight, getHeight());
			rightTopPanel.Draw();

			if (ModelCreator.transparent) {
				GL11.glDisable(GL11.GL_BLEND);
			}

			Display.update();

			if (screenshot != null)
			{
				if (screenshot.getFile() != null)
					ScreenshotCapture.getScreenshot(canvWidth, canvHeight, screenshot.getCallback(), screenshot.getFile());
				else
					ScreenshotCapture.getScreenshot(canvWidth, canvHeight, screenshot.getCallback());
				screenshot = null;
			}
			
			
			if (animCapture != null && !animCapture.isComplete()) {
				animCapture.CaptureFrame(canvWidth, canvHeight);
				
				if (animCapture.isComplete()) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run()
						{
							JOptionPane.showMessageDialog(null, "Animation export complete");							
						}
					});
					
					animCapture = null;
				}
			} else {
				
				if (project != null && project.SelectedAnimation != null && project.PlayAnimation) {
					if (frameCounter % 2 == 0) {
						project.SelectedAnimation.NextFrame();
						updateFrame(true);
					}
				}

				// Don't run faster than ~60 FPS (1000 / 60 = 16.67ms)
				long duration = System.currentTimeMillis() - prevFrameMillisec; 
				Thread.sleep(Math.max(16 - duration, 0));
				prevFrameMillisec = System.currentTimeMillis();
				
			}
			
			
		}
	}
	
	public int leftSidebarWidth() {
		int leftSpacing = 0;
		if (modelrenderer.renderedLeftSidebar != null) {
		 	leftSpacing = modelrenderer.renderedLeftSidebar.GetSidebarWidth();
			
		}
		
		return leftSpacing;
	}

	
	boolean zKeyDown;
	boolean yKeyDown;
	boolean sKeyDown;
	boolean rKeyDown;
	boolean tKeyDown;
	boolean bKeyDown;
	public boolean isOnRightPanel;
	
	public void handleInput(int leftSidebarWidth)
	{
		final float cameraMod = Math.abs(modelrenderer.camera.getZ());
		
		boolean isOnLeftPanel = Mouse.getX() < leftSidebarWidth;
		
		
		if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1))
		{
			if (!grabbing)
			{
				lastMouseX = Mouse.getX();
				lastMouseY = Mouse.getY();
				grabbing = true;
			}
			
			if (!mouseDownOnCenterPanel && !mouseDownOnLeftPanel && !mouseDownOnRightPanel) {
				mouseDownOnLeftPanel = isOnLeftPanel;
				mouseDownOnCenterPanel = !isOnLeftPanel && !isOnRightPanel;
				mouseDownOnRightPanel = isOnRightPanel;
			}
		}
		else
		{
			grabbedElem = null;
			
			if (modelrenderer.renderedLeftSidebar != null) {
				modelrenderer.renderedLeftSidebar.mouseUp();
			}
			
			if (!mouseDownOnLeftPanel && grabbing) {
				ModelCreator.changeHistory.endMultichangeHistoryState(ModelCreator.currentProject);
			}
			
			grabbing = false;
			mouseDownOnLeftPanel = false;
			mouseDownOnCenterPanel = false;
			mouseDownOnRightPanel = false;
		}

		
		if (mouseDownOnLeftPanel)
		{
			modelrenderer.renderedLeftSidebar.onMouseDownOnPanel();
			return;
		}
		
		if (mouseDownOnRightPanel) {
			rightTopPanel.onMouseDownOnRightPanel();
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
			
			if (Keyboard.isKeyDown(Keyboard.KEY_B)) bKeyDown = true;
			else {
				if (bKeyDown) {
					bKeyDown = false;
					
					Element elem = ModelCreator.currentProject.SelectedElement;
			    	if (elem != null) {		
						ModelCreator.changeHistory.beginMultichangeHistoryState();
						elem.RandomizeTexture();
						ModelCreator.changeHistory.endMultichangeHistoryState(ModelCreator.currentProject);
					}
					
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() { updateValues(null); } 
					});
				}
			}
			
			
			if (grabbedElem == null && (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)))
			{
				int openGlName = getElementGLNameAtPos(Mouse.getX(), Mouse.getY());
				if (openGlName >= 0)
				{
					currentProject.selectElementAndFaceByOpenGLName(openGlName);
					grabbedElem = rightTopPanel.getCurrentElement();
					currentProject.selectElement(grabbedElem);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() { updateValues(null); } 
					});
				}
			}

			if (grabbing && grabbedElem != null)
			{
				ModelCreator.changeHistory.beginMultichangeHistoryState();
				
				Element element = grabbedElem;
				
				int index = element.getSelectedFaceIndex();
				
				int newMouseX = Mouse.getX();
				int newMouseY = Mouse.getY();

				int xMovement = (int) ((newMouseX - lastMouseX) / 20);
				int yMovement = (int) ((newMouseY - lastMouseY) / 20);

				if (xMovement != 0 | yMovement != 0)
				{				
					if (Mouse.isButtonDown(0))
					{
						switch (index) {
							case 0: // N
							case 2: // S
								element.addStartZ(xMovement);
								break;
							
							case 1: // E
							case 3: // W
								element.addStartX(xMovement);
								break;
							
							case 4:
							case 5:
								element.addStartY(yMovement);
								break;
						}
					}
					else if (Mouse.isButtonDown(1))
					{
						
						switch (index) {
							case 0: // N
								element.addStartZ(-xMovement);
								element.addDepth(xMovement);
								break;
							case 2: // S
								element.addDepth(-xMovement);
								break;
							
							case 1: // E
								element.addWidth(xMovement);
								break;
							case 3: // W
								element.addStartX(-xMovement);
								element.addWidth(xMovement);
								break;
							
							case 4: // U
							case 5: // D
								element.addHeight(yMovement);
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
			//glViewPort view must not go negative 
			int leftSidebarWidth = leftSidebarWidth();
			if (canvWidth - leftSidebarWidth < 0)  {
				if (modelrenderer.renderedLeftSidebar != null) {
				 	modelrenderer.renderedLeftSidebar.nowSidebarWidth = canvWidth - 10;
				 	leftSidebarWidth = leftSidebarWidth();
				}
			}
			GLU.gluPerspective(60F, (float) (canvWidth - leftSidebarWidth) / (float) canvHeight, 0.3F, 1000F);

			modelrenderer.prepareDraw();
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
		return rightTopPanel;
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
				modelrenderer.renderDropTagets = false;
				
				DataFlavor flavor = evt.getCurrentDataFlavors()[0];
				
				try {
					if (flavor.getHumanPresentableName().contains("file")) {
						evt.acceptDrop(evt.getDropAction());						
					}
					
					Object obj = evt.getTransferable().getTransferData(flavor);
					
					if (obj instanceof DefaultMutableTreeNode[]) {
						//evt.rejectDrop();
						return;
					}
					
					
					
					@SuppressWarnings("rawtypes")
					List data = (List)obj;
					
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
								PendingTexture pendingTexture = new PendingTexture(code, file, ModelCreator.Instance, 0);
								
								
								//int x = evt.getLocation().x;
								int y = evt.getLocation().y;
								
								if (y >= canvHeight / 3) {
									
									if (y >= 2 * canvHeight / 3) {
										pendingTexture.SetReplacesAllTextures();	
									} else {
										pendingTexture.SetReplacesSelectElementTextures();
									}
								}
								
								AddPendingTexture(pendingTexture);
								
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
		 
			@Override
			public synchronized void dragEnter(DropTargetDragEvent evt)
			{
				DataFlavor flavor = evt.getCurrentDataFlavors()[0];
				
				try {
					Object obj = evt.getTransferable().getTransferData(flavor);
					
					if (obj instanceof DefaultMutableTreeNode[]) {
						return;
					}
				
					@SuppressWarnings("rawtypes")
					List data = (List)obj;
					
					for (Object elem : data) {
						if (elem instanceof File) {
							File file = (File)elem;							
							if (file.getName().endsWith(".png")) {
								modelrenderer.renderDropTagets = true;
								modelrenderer.dropLocation = evt.getLocation();
							}
							return;
						}
							
					}
					
				} catch (Exception e) {
					
				}
				
				
				super.dragEnter(evt);
			}
			
			@Override
			public synchronized void dragOver(DropTargetDragEvent evt)
			{
				DataFlavor flavor = evt.getCurrentDataFlavors()[0];
				
				try {
					Object obj = evt.getTransferable().getTransferData(flavor);
					
					if (obj instanceof DefaultMutableTreeNode[]) {
						return;
					}
				
					@SuppressWarnings("rawtypes")
					List data = (List)obj;
					
					for (Object elem : data) {
						if (elem instanceof File) {
							File file = (File)elem;							
							if (file.getName().endsWith(".png")) {
								modelrenderer.renderDropTagets = true;
								modelrenderer.dropLocation = evt.getLocation();
							}
							return;
						}
							
					}
					
				} catch (Exception e) {
					
				}
				
				
				super.dragOver(evt);
			}
		
			
			@Override
			public synchronized void dragExit(DropTargetEvent dte)
			{
				modelrenderer.renderDropTagets = false;
				super.dragExit(dte);
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
		ModelCreator.currentBackdropProject = null;
		
		if (ModelCreator.currentProject.rootElements.size() > 0 && currentProject.needsSaving)
		{
			int returnVal = JOptionPane.showConfirmDialog(null, "Your current unsaved project will be cleared, are you sure you want to continue?", "Warning", JOptionPane.YES_NO_OPTION);		
			if (returnVal == JOptionPane.NO_OPTION || returnVal == JOptionPane.CLOSED_OPTION) return;
		}
				
		ignoreDidModify = true;
		
		if (filePath == null) {
			setTitle("(untitled) - " + windowTitle);
			currentProject = new Project(null);
			currentProject.LoadIntoEditor(ModelCreator.rightTopPanel);
			
		} else {
			prefs.put("filePath", filePath);
			Importer importer = new Importer(filePath);
			
			ignoreValueUpdates = true;
			Project project = importer.loadFromJSON();
			Project oldproject = currentProject;
			currentProject = project;
			
			for (TextureEntry entry : oldproject.TexturesByCode.values()) {
				entry.Dispose();
			}
			
			ignoreValueUpdates = false;
			currentProject.LoadIntoEditor(ModelCreator.rightTopPanel);
			
			setTitle(new File(currentProject.filePath).getName() + " - " + windowTitle);
		}
		
		ignoreDidModify = true;
		changeHistory.clear();
		changeHistory.addHistoryState(currentProject);
		
		currentProject.needsSaving = false;
		if (currentBackdropProject != null) {
			currentBackdropProject.reloadStepparentRelationShips();
		}
		currentProject.reloadStepparentRelationShips();
		
		ignoreDidModify = false;

		
		ModelCreator.updateValues(null);
		currentProject.tree.jtree.updateUI();		
	}
	
	
	public void ImportFile(String filePath)
	{
		ignoreDidModify = true;
		ignoreValueUpdates = true;
		
		Importer importer = new Importer(filePath);
		Project importedproject = importer.loadFromJSON();
		
		for(Element elem : importedproject.rootElements) {
			currentProject.rootElements.add(elem);
		}
		
		for(Animation anim : importedproject.Animations) {
			currentProject.Animations.add(anim);
		}
		
		for (PendingTexture tex : importedproject.PendingTextures) {
			currentProject.PendingTextures.add(tex);
		}

		changeHistory.addHistoryState(currentProject);

		ignoreValueUpdates = false;
		
		currentProject.LoadIntoEditor(ModelCreator.rightTopPanel);
		
		ignoreDidModify = false;
		
		currentProject.reloadStepparentRelationShips();
		if (currentBackdropProject != null) {
			currentBackdropProject.reloadStepparentRelationShips();
		}
		
		currentProject.needsSaving = true;		
		ModelCreator.updateValues(null);
		currentProject.tree.jtree.updateUI();		
	}
	

	public void LoadBackdropFile(String filePath)
	{		
		ignoreDidModify = true;
		ignoreValueUpdates = true;

		Importer importer = new Importer(filePath);
		Project project = importer.loadFromJSON();
		project.setIsBackdrop();
		project.reloadStepparentRelationShips();
			
		currentBackdropProject = project;
		
		
		String shapeBasePath = ModelCreator.prefs.get("shapePath", ".");
		
		String subPath = filePath;
		if (filePath.contains(shapeBasePath) && shapeBasePath != ".") {
			subPath = filePath.substring(shapeBasePath.length()  + 1);
		}
		else {
			int index = filePath.indexOf("assets"+File.separator+"shapes"+File.separator);
			if (index>0) subPath = filePath.substring(index + "assets/shapes/".length());
		}
		subPath = subPath.replace('\\', '/').replace(".json", "");
		
		currentProject.backDropShape = subPath;
		ignoreValueUpdates = false;
		ignoreDidModify = false;
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

	public static void reloadStepparentRelationShips()
	{
		if (ModelCreator.currentBackdropProject != null) {
			ModelCreator.currentBackdropProject.reloadStepparentRelationShips();
		}
		
		ModelCreator.currentProject.reloadStepparentRelationShips();
		
	}
}
