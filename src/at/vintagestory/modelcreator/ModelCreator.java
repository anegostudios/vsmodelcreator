package at.vintagestory.modelcreator;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.*;
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

import at.vintagestory.modelcreator.gui.right.ElementTreeCellRenderer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
import at.vintagestory.modelcreator.gui.right.RightPanel;
import at.vintagestory.modelcreator.gui.right.face.FaceTexturePanel;
import at.vintagestory.modelcreator.input.InputManager;
import at.vintagestory.modelcreator.input.command.FactoryProjectCommand;
import at.vintagestory.modelcreator.input.command.ProjectCommand;
import at.vintagestory.modelcreator.input.key.InputKeyEvent;
import at.vintagestory.modelcreator.input.listener.ListenerKeyPressInterval;
import at.vintagestory.modelcreator.input.listener.ListenerKeyPressOnce;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.ITextureCallback;
import at.vintagestory.modelcreator.model.Animation;
import at.vintagestory.modelcreator.model.AttachmentPoint;
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
	public static Project currentMountBackdropProject;
	public static ProjectChangeHistory changeHistory = new ProjectChangeHistory();
	
	public static int ignoreDidModify = 0;	
	public static boolean ignoreValueUpdates = false;
	public static boolean ignoreFrameUpdates = false;
	
	
	public static boolean showGrid = true;
	public static boolean showTreadmill = false;
	public static boolean showShade = true;
	public static boolean transparent = true;
	public static boolean renderTexture = true;
	public static boolean autoreloadTexture = true;
	public static boolean repositionWhenReparented = true;
	public static boolean darkMode = false;
	public static boolean saratyMode = false;
	public static boolean uvShowNames = false;
	
	public static boolean backdropAnimationsMode = true;
	
	public static int elementTreeHeight = 240;

	
	public static float TreadMillSpeed = 1f;
	
	public static float noTexScale = 2;
	
	public static int currentRightTab;

	// Canvas Variables
	private final static AtomicReference<Dimension> newCanvasSize = new AtomicReference<Dimension>();
	public static Canvas canvas;
	public int canvWidth = 990, canvHeight = 700;

	// Swing Components
	public JScrollPane scroll;
	public static RightPanel rightTopPanel;
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
	
	
	public static double WindWaveCounter;
	public static int WindPreview;
	
	static {
		prefs = Preferences.userRoot().node("ModelCreator");
	}
	
	// Input listener class
	private InputManager manager = new InputManager();
	
	
	public static Project GetProject(String type) {
		if (type == "backdrop") return currentBackdropProject;
		if (type == "mountbackdrop") return currentMountBackdropProject;
		return currentProject;
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
		elementTreeHeight = prefs.getInt("elementTreeHeight", 240);
		
		Instance = this;
		
		currentProject = new Project(null);
		changeHistory.addHistoryState(currentProject);
		
		setDropTarget(getCustomDropTarget());
		setPreferredSize(new Dimension(1200, 780));
		setMinimumSize(new Dimension(800, 500));
		setLayout(new BorderLayout(10, 0));
		setIconImages(getIcons());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		String loadFile = null;
		for (int i = 0; i < args.length; i++) {
			if (Objects.equals(args[i], "-t") && args.length > i + 1) {
				ModelCreator.prefs.put("texturePath", args[i + 1]);
				i++;
			} else if (Objects.equals(args[i], "-s") && args.length > i + 1) {
				ModelCreator.prefs.put("shapePath", args[i + 1]);
				i++;
			} else if (Objects.equals(args[i], "-f") && args.length > i + 1) {
				loadFile = args[i + 1];
				i++;
			}
		}

		String colorPath = prefs.get("colorPath", null);
		if (colorPath != null) {
			LoadColorConfig(colorPath);
		}

		initComponents();
		

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
		
		// Commands
		FactoryProjectCommand factoryCommand = new FactoryProjectCommand();
		ProjectCommand undo = factoryCommand.CreateUndoCommand();
		ProjectCommand redo = factoryCommand.CreateRedoCommand();
		ProjectCommand save = factoryCommand.CreateSaveCommand(this);
		
		ProjectCommand texReload = factoryCommand.CreateReloadTextureCommand();
		ProjectCommand texToggle = factoryCommand.CreateToggleTextureCommand();
		ProjectCommand texRandom = factoryCommand.CreateRandomizeTextureCommand();
		
		ProjectCommand elementUp = factoryCommand.CreateMoveSelectedElementCommandUp(this);
		ProjectCommand elementForward = factoryCommand.CreateMoveSelectedElementCommandForward(this);
		ProjectCommand elementRight = factoryCommand.CreateMoveSelectedElementCommandRight(this);
		ProjectCommand elementBackward = factoryCommand.CreateMoveSelectedElementCommandBackward(this);
		ProjectCommand elementLeft = factoryCommand.CreateMoveSelectedElementCommandLeft(this);
		ProjectCommand elementDown = factoryCommand.CreateMoveSelectedElementCommandDown(this);

		// Add key input listeners
		manager.subscribe(new ListenerKeyPressOnce(undo, Keyboard.KEY_LCONTROL, Keyboard.KEY_Z));
		manager.subscribe(new ListenerKeyPressOnce(redo, Keyboard.KEY_LCONTROL, Keyboard.KEY_Y));
		manager.subscribe(new ListenerKeyPressOnce(save, Keyboard.KEY_LCONTROL, Keyboard.KEY_S));
		manager.subscribe(new ListenerKeyPressOnce(texReload, Keyboard.KEY_LCONTROL, Keyboard.KEY_R));
		manager.subscribe(new ListenerKeyPressOnce(texToggle, Keyboard.KEY_LCONTROL, Keyboard.KEY_T));
		manager.subscribe(new ListenerKeyPressOnce(texRandom, Keyboard.KEY_LCONTROL, Keyboard.KEY_B));
		
		manager.subscribe(new ListenerKeyPressInterval(elementUp, Keyboard.KEY_PRIOR));
		manager.subscribe(new ListenerKeyPressInterval(elementForward, Keyboard.KEY_UP));
		manager.subscribe(new ListenerKeyPressInterval(elementRight, Keyboard.KEY_RIGHT));
		manager.subscribe(new ListenerKeyPressInterval(elementBackward, Keyboard.KEY_DOWN));
		manager.subscribe(new ListenerKeyPressInterval(elementLeft, Keyboard.KEY_LEFT));
		manager.subscribe(new ListenerKeyPressInterval(elementDown, Keyboard.KEY_NEXT));
		
		// Enable repeat events, grants more fine-grained control over keyboard input
		Keyboard.enableRepeatEvents(true);
		
		// Seriously man, fuck java. Mouse listeners on a canvas are just plain not working. 
		// canvas.addMouseListener(ml);
		
		
		pack();
		setVisible(true);
		setLocationRelativeTo(null);

		initDisplay();
		
		
		currentProject.LoadIntoEditor(getElementManager());
		updateValues(null);

		prevFrameMillisec = System.currentTimeMillis();

		if (loadFile != null) {
			LoadFile(loadFile);
		}

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
	
	public static Project CurrentAnimProject() {
		return ModelCreator.backdropAnimationsMode && ModelCreator.currentBackdropProject != null ? ModelCreator.currentBackdropProject : ModelCreator.currentProject;
	}
	
	public static boolean AnimationPlaying() {
		return currentProject != null && currentProject.PlayAnimation; 
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
		if (ignoreDidModify > 0) return;
		if (currentProject == null) return;
		
		currentProject.needsSaving = true;
		
		changeHistory.addHistoryState(currentProject);
		
		updateTitle();
	}

	
	public void initComponents()
	{
		Icons.init(getClass());
		
		rightTopPanel = new RightPanel(this);

		leftKeyframesPanel = new LeftKeyFramesPanel(rightTopPanel);
		leftKeyframesPanel.setVisible(false);
		add(leftKeyframesPanel, BorderLayout.WEST);
		
		// Canvas stuff
		canvas = new Canvas();
		canvas.setFocusable(true);
		canvas.setVisible(true);
		canvas.requestFocus();
		
		canvas.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				newCanvasSize.set(canvas.getSize());
			}
		});
		
		//== Canvas trickery for larger uiScales ==//
		// kinda messy, but works
		final double viewScale = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getDefaultTransform().getScaleX();
		
		// Create a container for our canvas (a simple JPanel) without a layout
		// so that our canvas would not get affected by the base component's layout.
		JPanel panel = new JPanel(null);
		panel.add(canvas);
		add(panel, BorderLayout.CENTER);
		
		// Inherit size from JPanel, apply the ui scale factor
		panel.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				canvas.setSize((int)(panel.getWidth()*viewScale), (int)(panel.getHeight()*viewScale));
			}
		});
		
		//== end Canvas trickery ==//
		
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

				if (currentBackdropProject != null && currentBackdropProject.SelectedAnimation != null) {
					currentBackdropProject.SelectedAnimation.SetFramesDirty();
				}
				
				guiMain.updateValues(byGuiElem);
			 	((RightPanel)rightTopPanel).updateValues(byGuiElem);
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
		if (backdropAnimationsMode && currentBackdropProject != null && currentProject.SelectedAnimation != null && currentBackdropProject.SelectedAnimation != null) {
			currentProject.SelectedAnimation.currentFrame = currentBackdropProject.SelectedAnimation.currentFrame;
		}
		
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
					((RightPanel)rightTopPanel).updateFrame(null);
					updateTitle();
					guiMain.updateFrame();
					
					ignoreFrameUpdates = false;
				}
			});
		} else {
			ignoreFrameUpdates = true;
			
			leftKeyframesPanel.updateFrame();
			((RightPanel)rightTopPanel).updateFrame(null);
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
			
			WindWaveCounter = (frameCounter / 60.0) % 2000;
			
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

			Project bdp = currentBackdropProject;
			if (bdp != null && bdp.SelectedAnimation != null && bdp.SelectedAnimation.framesDirty) {
				bdp.SelectedAnimation.calculateAllFrames(bdp);
			}
			
			Project mountbdp = currentMountBackdropProject;
			if (mountbdp != null && mountbdp.SelectedAnimation != null && mountbdp.SelectedAnimation.framesDirty) {
				mountbdp.SelectedAnimation.calculateAllFrames(mountbdp);
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
			
			handleInputKeyboard();
			handleInputMouse(leftSidebarWidth);
			
			
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
				
				
				if (bdp != null && bdp.SelectedAnimation != null && bdp.PlayAnimation) {
					if (frameCounter % 2 == 0) {
						bdp.SelectedAnimation.NextFrame();
						updateFrame(true);
					}
				}

				if (mountbdp != null && mountbdp.SelectedAnimation != null && project.PlayAnimation) {
					if (frameCounter % 2 == 0) {
						mountbdp.SelectedAnimation.NextFrame();
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

	
	
	public void handleInputKeyboard()
	{
		// Poll the keyboard for input
		Keyboard.poll();
		
		// Process keypresses
		while (Keyboard.next()){
			
			// Read all events from keyboard
			int keyCode = Keyboard.getEventKey();
			char keyChar = Keyboard.getEventCharacter();
			boolean pressed = Keyboard.getEventKeyState();
			boolean down = Keyboard.isRepeatEvent();
			long nano = Keyboard.getEventNanoseconds();
			
			// Create new KeyEvent
			InputKeyEvent event = new InputKeyEvent(keyCode, keyChar, pressed, down, nano);
			
			// Notify all key listeners
			manager.notifyListeners(event);
			
			// To retain compatibility with existing code
			if(event.keyCode() == Keyboard.KEY_LCONTROL)
				leftControl = event.pressed();
		}
	}
	
	public boolean leftControl;
	public boolean isOnRightPanel;
	private void handleInputMouse(int leftSidebarWidth) {
		
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
		
		if (leftControl)
		{	
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
				modelrenderer.camera.addZ(wheel * (cameraMod / 2500F));
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_MINUS) || Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT)) {
				modelrenderer.camera.addZ(-50 * (cameraMod / 2500F));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_ADD)) {
				modelrenderer.camera.addZ(50 * (cameraMod / 2500F));
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
		if (s != null) s.Load();
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
				
				try {

					Transferable transferable = evt.getTransferable();
					Object obj;
					if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						evt.acceptDrop(evt.getDropAction());
						obj = transferable.getTransferData(DataFlavor.javaFileListFlavor);
					} else {
						obj = transferable.getTransferData(transferable.getTransferDataFlavors()[0]);
					}
					

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
								pendingTexture.SetInsertTextureSizeEntry();
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
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane error = new JOptionPane();
					error.setMessage(errormessage);
					JDialog dialog = error.createDialog(canvas, "Texture Error");
					dialog.setLocationRelativeTo(null);
					dialog.setModal(true);
					dialog.setAlwaysOnTop(true);
					dialog.setVisible(true);
			}});
		} else {
			
			if (FaceTexturePanel.dlg != null) {
				if (FaceTexturePanel.dlg.IsOpened()) {
					FaceTexturePanel.dlg.onTextureLoaded(isNew, errormessage, texture);
				}
			}
			
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
				
		ignoreDidModify++;
		
		if (filePath == null) {
			setTitle("(untitled) - " + windowTitle);
			currentProject = new Project(null);
			currentProject.LoadIntoEditor(ModelCreator.rightTopPanel);
			
		} else {
			prefs.put("filePath", filePath);
			Importer importer = new Importer(filePath);
			
			ignoreValueUpdates = true;
			Project project = importer.loadFromJSON("normal");
			Project oldproject = currentProject;
			currentProject = project;
			
			for (TextureEntry entry : oldproject.TexturesByCode.values()) {
				entry.Dispose();
			}
			
			ignoreValueUpdates = false;
			currentProject.LoadIntoEditor(ModelCreator.rightTopPanel);
			
			setTitle(new File(currentProject.filePath).getName() + " - " + windowTitle);
		}
		
		
		changeHistory.clear();
		changeHistory.addHistoryState(currentProject);
		
		currentProject.needsSaving = false;
		currentProject.reloadStepparentRelationShips();
		if (currentBackdropProject != null) {
			currentBackdropProject.reloadStepparentRelationShips();
			currentProject.attachToBackdropProject(currentBackdropProject);
		}
		
		
		ignoreDidModify--;

		
		ModelCreator.updateValues(null);
		currentProject.tree.jtree.updateUI();
		
		if (currentMountBackdropProject != null) {
			AttachmentPoint ap = currentMountBackdropProject.findAttachmentPoint("Rider");
			for (Element rootelem : currentProject.rootElements) {
				ap.StepChildElements.add(rootelem);
			}
		}
	}
	
	
	public void ImportFile(String filePath)
	{
		ignoreDidModify++;
		ignoreValueUpdates = true;
		
		Importer importer = new Importer(filePath);
		Project importedproject = importer.loadFromJSON("normal");
		
		for(Element elem : importedproject.rootElements) {
			currentProject.rootElements.add(elem);
		}
		
		for(Animation anim : importedproject.Animations) {
			currentProject.Animations.add(anim);
		}
		
		for (PendingTexture tex : importedproject.PendingTextures) {
			currentProject.PendingTextures.add(tex);
		}
		
		for (String key : importedproject.TextureSizes.keySet()) {
			currentProject.TextureSizes.put(key, importedproject.TextureSizes.get(key));
		}

		changeHistory.addHistoryState(currentProject);

		ignoreValueUpdates = false;
		
		currentProject.LoadIntoEditor(ModelCreator.rightTopPanel);
		
		ignoreDidModify--;
		
		currentProject.reloadStepparentRelationShips();
		if (currentBackdropProject != null) {
			currentBackdropProject.reloadStepparentRelationShips();
			currentProject.attachToBackdropProject(currentBackdropProject);
		}
		
		currentProject.needsSaving = true;		
		ModelCreator.updateValues(null);
		currentProject.tree.jtree.updateUI();
		
		if (currentMountBackdropProject != null) {
			Element elem = currentMountBackdropProject.findElement("Rider");
			for (Element rootelem : currentProject.rootElements) {
				elem.StepChildElements.add(rootelem);
			}
		}

	}

	public static void LoadColorConfig(String path) {

		ElementTreeCellRenderer.colorConfig.clear();
		JsonParser jp = new JsonParser();
		try {
			File f = new File(path);
			if (!f.exists()) {
				System.out.println("Color Config not found");
				return;
			}
			FileReader fr = new FileReader(path);
			JsonElement je = jp.parse(fr);
			for (Map.Entry<String, JsonElement> entry : je.getAsJsonObject().entrySet()) {
				Color col = Color.decode(entry.getValue().getAsString());
				ElementTreeCellRenderer.colorConfig.put(entry.getKey().toLowerCase(), col);
			}
			if(currentProject.tree != null) {
				currentProject.tree.updateUI();
			}
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage());
		}
	}

	public void LoadBackdropFile(String filePath)
	{		
		ignoreDidModify++;
		ignoreValueUpdates = true;

		Importer importer = new Importer(filePath);
		Project project = importer.loadFromJSON("backdrop");
		project.reloadStepparentRelationShips();

		currentBackdropProject = project;
		currentProject.attachToBackdropProject(currentBackdropProject);
		
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
		ignoreDidModify--;
	}
	
	
	
	
	public void LoadMountBackdropFile(String filePath)
	{		
		ignoreDidModify++;
		ignoreValueUpdates = true;

		Importer importer = new Importer(filePath);
		Project project = importer.loadFromJSON("mountbackdrop");

		currentMountBackdropProject = project;
		
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
		
		currentProject.mountBackDropShape = subPath;
		ignoreValueUpdates = false;
		ignoreDidModify--;

		
		AttachmentPoint ap = currentMountBackdropProject.findAttachmentPoint("Rider");
		for (Element rootelem : currentProject.rootElements) {
			ap.StepChildElements.add(rootelem);
		}
		
		ModelCreator.leftKeyframesPanel.Load();
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
	
	public Camera getCamera() {
		return this.modelrenderer.camera;
	}

	public static void reloadStepparentRelationShips()
	{
		if (ModelCreator.currentBackdropProject != null) {
			ModelCreator.currentBackdropProject.reloadStepparentRelationShips();
		}
		
		ModelCreator.currentProject.reloadStepparentRelationShips();
		
	}

	
}
