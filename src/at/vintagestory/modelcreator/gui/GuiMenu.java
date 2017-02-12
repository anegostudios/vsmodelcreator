package at.vintagestory.modelcreator.gui;

import java.awt.ItemSelectable;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.util.UVMapExporter;
import at.vintagestory.modelcreator.util.screenshot.PendingScreenshot;
import at.vintagestory.modelcreator.util.screenshot.ScreenshotCallback;
import at.vintagestory.modelcreator.util.screenshot.Uploader;

public class GuiMenu extends JMenuBar
{
	private static final long serialVersionUID = 1L;

	private ModelCreator creator;

	/* File */
	private JMenu menuFile;
	private JMenuItem itemNew;
	private JMenuItem itemLoad;
	private JMenuItem itemSave;
	private JMenuItem itemSaveAs;
	private JMenuItem itemExportUvMap;
	private JMenuItem itemTexturePath;
	private JMenuItem itemExit;

	/* Edit */
	private JMenu menuEdit;
	private JMenuItem itemUndo;
	private JMenuItem itemRedo;
	
	/* Options */
	private JMenu menuOptions;
	private JCheckBoxMenuItem itemGrid;
	private JCheckBoxMenuItem itemTransparency;
	private JCheckBoxMenuItem itemUnlockAngles;
	private JCheckBoxMenuItem itemSingleTexture;
	private JMenuItem itemNoTextureSize;
	
	/* Add */
	private JMenu menuAdd;
	private JMenuItem itemAddCube;
	private JMenuItem itemAddFace;


	/* Other */
	private JMenu otherMenu;
	private JMenuItem itemSaveScreenshot;
	private JMenuItem itemReloadTextures;
	private JMenuItem itemImgurLink;
	private JMenuItem itemControls;
	private JMenuItem itemCredits;

	public GuiMenu(ModelCreator creator)
	{
		this.creator = creator;
		initMenu();
	}

	private void initMenu()
	{
		menuFile = new JMenu("File");
		{
			itemNew = createItem("New", "New Model", KeyEvent.VK_N, new ImageIcon(getClass().getClassLoader().getResource("icons/new.png")));
			itemLoad = createItem("Open...", "Open JSON", KeyEvent.VK_I, new ImageIcon(getClass().getClassLoader().getResource("icons/load.png")));
			itemSave = createItem("Save...", "Save JSON", KeyEvent.VK_S, new ImageIcon(getClass().getClassLoader().getResource("icons/disk.png")));
			itemSaveAs = createItem("Save as...", "Save JSON", KeyEvent.VK_E, new ImageIcon(getClass().getClassLoader().getResource("icons/export.png")));
			itemTexturePath = createItem("Set Texture Path...", "Set the base path to look for textures", KeyEvent.VK_P, new ImageIcon(getClass().getClassLoader().getResource("icons/texture.png")));
			itemExportUvMap = createItem("Export UV Map...", "Lets you export a UV map when in single texture mode", KeyEvent.VK_U, new ImageIcon(getClass().getClassLoader().getResource("icons/texture.png")));
			itemExit = createItem("Exit", "Exit Application", KeyEvent.VK_Q, new ImageIcon(getClass().getClassLoader().getResource("icons/exit.png")));
		}
		
		menuEdit = new JMenu("Edit");
		{
			itemUndo = createItem("Undo", "Undo the last action", 0, new ImageIcon(getClass().getClassLoader().getResource("icons/arrow_undo.png")));
			itemRedo = createItem("Redo", "Redo the last action", 0, new ImageIcon(getClass().getClassLoader().getResource("icons/arrow_redo.png")));
		}


		menuOptions = new JMenu("Options");
		{
			itemGrid = createCheckboxItem("Show Grid", "Toggles the voxel grid", KeyEvent.VK_G, Icons.transparent);
			itemGrid.setSelected(ModelCreator.transparent);
			
			itemTransparency = createCheckboxItem("Transparency", "Toggles transparent rendering in program", KeyEvent.VK_T, Icons.transparent);
			itemTransparency.setSelected(ModelCreator.transparent);
			
			itemUnlockAngles = createCheckboxItem("Unlock all Angles", "Disabling this allows angle stepping of single degrees. Suggested to unlock this only for entities.", KeyEvent.VK_A, Icons.transparent);
			itemUnlockAngles.setSelected(ModelCreator.unlockAngles);
			
			itemSingleTexture = createCheckboxItem("Single Texture for all Faces", "When creating entities, it is often more useful to use only a single texture.", 0, Icons.transparent);
			itemNoTextureSize = createItem("Texture Size...", "The size of the textured previewed in the UV Pane when no texture is loaded", 0, Icons.transparent);
			
			itemGrid.setSelected(ModelCreator.showGrid);
			itemTransparency.setSelected(ModelCreator.transparent);
			itemUnlockAngles.setSelected(ModelCreator.unlockAngles);
			itemSingleTexture.setSelected(ModelCreator.singleTextureMode);
		}

		menuAdd = new JMenu("Add");
		{
			itemAddCube = createItem("Add cube", "Add new cube", KeyEvent.VK_C, Icons.cube);
			itemAddFace = createItem("Add face", "Add single face", KeyEvent.VK_F, Icons.cube);
		}

		
		otherMenu = new JMenu("Other");
		{
			itemReloadTextures = createItem("Reload textures", "Reload textures", KeyEvent.VK_F5, Icons.reload);
			itemSaveScreenshot = createItem("Save Screenshot to Disk...", "Save screenshot to disk.", KeyEvent.VK_F12, Icons.disk);
			itemImgurLink = createItem("Get Imgur Link", "Get an Imgur link of your screenshot to share.", KeyEvent.VK_F11, Icons.imgur);
			itemControls = createItem("Quick Controls", "Some useful controls", 0, Icons.keyboard);
			itemCredits = createItem("Credits", "Who made this tool", 0, Icons.drink);
		}

		initActions();

	
		menuOptions.add(itemGrid);
		menuOptions.add(itemTransparency);
		menuOptions.add(itemUnlockAngles);
		menuOptions.add(itemSingleTexture);
		menuOptions.add(itemNoTextureSize);
		
		menuAdd.add(itemAddCube);
		menuAdd.add(itemAddFace);
		
		menuEdit.add(itemUndo);
		menuEdit.add(itemRedo);

		otherMenu.add(itemReloadTextures);
		otherMenu.add(itemSaveScreenshot);
		otherMenu.add(itemImgurLink);
		otherMenu.add(itemControls);
		otherMenu.add(itemCredits);

		menuFile.add(itemNew);
		menuFile.addSeparator();
		menuFile.add(itemLoad);
		menuFile.add(itemSave);
		menuFile.add(itemSaveAs);
		menuFile.addSeparator();
		menuFile.add(itemTexturePath);
		menuFile.add(itemExportUvMap);
		menuFile.addSeparator();
		menuFile.add(itemExit);

		add(menuFile);
		add(menuEdit);
		add(menuOptions);
		add(menuAdd);
		add(otherMenu);
	}

	private void initActions()
	{
		KeyStroke[] strokes = new KeyStroke[] {
			KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
			KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
			KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
			KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
			KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
			KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
		};
		
		itemNew.setAccelerator(strokes[0]);
		itemLoad.setAccelerator(strokes[1]);
		itemSave.setAccelerator(strokes[2]);
		itemReloadTextures.setAccelerator(strokes[3]);

		// So much code for setting up a hotkey
		// Java swing is so mentally retarded -_-
		Action buttonAction = new AbstractAction("Undo") {		 
			private static final long serialVersionUID = 1L;

			@Override
		    public void actionPerformed(ActionEvent evt) {
		    	ModelCreator.changeHistory.Undo();
		    }
		};
		String key = "Undo";
		itemUndo.setAction(buttonAction);
		itemUndo.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/arrow_undo.png")));
		buttonAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_Z);
		itemUndo.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(strokes[4], key);
		itemUndo.getActionMap().put(key, buttonAction);
		
		 
		
		itemUndo.setAccelerator(strokes[4]);
		
		
		
		Action buttonAction2 = new AbstractAction("Redo") {
			private static final long serialVersionUID = 1L;

			@Override
		    public void actionPerformed(ActionEvent evt) {
		    	ModelCreator.changeHistory.Redo();
		    }
		};
		key = "Redo";
		itemRedo.setAction(buttonAction2);
		
		buttonAction2.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_Y);
		itemRedo.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/arrow_redo.png")));
		itemRedo.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(strokes[5], key);
		itemRedo.getActionMap().put(key, buttonAction2);
		itemRedo.setAccelerator(strokes[5]);
		

		ActionListener listener = a -> { OnNewModel(); }; 
		itemNew.addActionListener(listener);


		listener = e -> { OnLoadFile(); };	
		itemLoad.addActionListener(listener);
		

		listener = e -> {
			if (ModelCreator.currentProject.filePath == null) {
				creator.SaveProjectAs();
			} else {
				creator.SaveProject(new File(ModelCreator.currentProject.filePath));
			}
		};
		
		itemSave.addActionListener(listener);
		

		itemSaveAs.addActionListener(e -> { creator.SaveProjectAs(); });

		
		
		listener = e ->
		{
			JFileChooser chooser = new JFileChooser(ModelCreator.prefs.get("texturePath", "."));
			chooser.setDialogTitle("Texture Path");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				ModelCreator.prefs.put("texturePath", chooser.getSelectedFile().getAbsolutePath());
			}
		};

		itemTexturePath.addActionListener(listener);

		itemExportUvMap.setEnabled(ModelCreator.singleTextureMode);
		itemExportUvMap.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser(ModelCreator.prefs.get("filePath", "."));
			chooser.setDialogTitle("Output Directory");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setApproveButtonText("Export");

			FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG (.png)", "png");
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
					
					if (!filePath.endsWith(".png")) {
						chooser.setSelectedFile(new File(filePath + ".png"));
					}
					
					UVMapExporter exporter = new UVMapExporter();
					exporter.Export(chooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		
		itemExit.addActionListener(e ->
		{
			creator.close();
		});
		
		
	

		itemGrid.addActionListener(a ->
		{
			ModelCreator.showGrid = itemGrid.isSelected();
			ModelCreator.prefs.putBoolean("showGrid", ModelCreator.showGrid);
		});
		
		itemTransparency.addActionListener(a ->
		{
			ModelCreator.transparent = itemTransparency.isSelected();
		});
		
		itemUnlockAngles.addActionListener(a ->
		{
			ModelCreator.unlockAngles = itemUnlockAngles.isSelected();
			ModelCreator.prefs.putBoolean("unlockAngles", ModelCreator.unlockAngles);
			ModelCreator.updateValues();
		});
		
		itemSingleTexture.addActionListener(a ->
		{
			ModelCreator.singleTextureMode = itemSingleTexture.isSelected();
			ModelCreator.prefs.putBoolean("singleTextureMode", ModelCreator.singleTextureMode);
			if (ModelCreator.singleTextureMode) ModelCreator.currentProject.applySingleTextureMode();
			ModelCreator.updateValues();
		});
		
		itemNoTextureSize.addActionListener(a -> {
			TextureSizeDialog.show(creator);
		});
		itemNoTextureSize.setEnabled(ModelCreator.singleTextureMode);

		
		itemReloadTextures.addActionListener(a -> {
			ModelCreator.currentProject.reloadTextures(creator);
		});

		itemSaveScreenshot.addActionListener(a ->
		{
			saveScreenshot();
			
		});

		itemImgurLink.addActionListener(a ->
		{
			CreateImgurLink();
			
		});
		
		
		itemControls.addActionListener(a ->
		{
			ControlsDialog.show(creator);
		});
		
		itemCredits.addActionListener(a ->
		{
			CreditsDialog.show(creator);
		});

		
		itemAddCube.addActionListener(a ->
		{
			ModelCreator.currentProject.addElementAsChild(new Element(1, 1, 1));
		});
		
		itemAddFace.addActionListener(a ->
		{
			ModelCreator.currentProject.addElementAsChild(new Element(1, 1));
		});
		
		
		
	}
	
	

	private void OnLoadFile()
	{
		JFileChooser chooser = new JFileChooser(ModelCreator.prefs.get("filePath", "."));
		chooser.setDialogTitle("Input File");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setApproveButtonText("Open");

		FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON (.json)", "json");
		chooser.setFileFilter(filter);

		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			String filePath = chooser.getSelectedFile().getAbsolutePath();
			creator.LoadFile(filePath);
		}
	}

	private void OnNewModel()
	{
		creator.LoadFile(null);
	}


	private JMenuItem createItem(String name, String tooltip, int mnemonic, Icon icon)
	{
		JMenuItem item = new JMenuItem(name);
		item.setToolTipText(tooltip);
		item.setMnemonic(mnemonic);
		item.setIcon(icon);
		return item;
	}
	
	private JCheckBoxMenuItem createCheckboxItem(String name, String tooltip, int mnemonic, Icon icon)
	{
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(name);
		item.setToolTipText(tooltip);
		item.setMnemonic(mnemonic);
		item.setIcon(icon);
		return item;
	}
	
	
	


	private void saveScreenshot()
	{
		JFileChooser chooser = new JFileChooser(ModelCreator.prefs.get("filePath", "."));
		chooser.setDialogTitle("Output Directory");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setApproveButtonText("Save");

		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG (.png)", "png");
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
				if (!filePath.endsWith(".png")) {
					chooser.setSelectedFile(new File(filePath + ".png"));
				}
				
				ModelCreator.prefs.put("filePath", filePath);
				
				creator.modelrenderer.renderedLeftSidebar = null;
				creator.startScreenshot(new PendingScreenshot(chooser.getSelectedFile(), null));
			}
		}		
	}

	private void CreateImgurLink()
	{
		creator.modelrenderer.renderedLeftSidebar = null;
		creator.startScreenshot(new PendingScreenshot(null, new ScreenshotCallback()
		{
			@Override
			public void callback(File file)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							String url = Uploader.upload(file);

							JOptionPane message = new JOptionPane();
							String title;

							if (url != null && !url.equals("null"))
							{
								StringSelection text = new StringSelection(url);
								Toolkit.getDefaultToolkit().getSystemClipboard().setContents(text, null);
								title = "Success";
								message.setMessage("<html><b>" + url + "</b> has been copied to your clipboard.</html>");
							}
							else
							{
								title = "Error";
								message.setMessage("Failed to upload screenshot. Check your internet connection then try again.");
							}

							JDialog dialog = message.createDialog(GuiMenu.this, title);
							dialog.setLocationRelativeTo(null);
							dialog.setModal(false);
							dialog.setVisible(true);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
			}
		}));
	}

	public void updateValues()
	{
		boolean enabled = !ModelCreator.currentProject.PlayAnimation;
		
		itemUndo.setEnabled(enabled && ModelCreator.changeHistory.CanUndo());
		itemRedo.setEnabled(enabled && ModelCreator.changeHistory.CanRedo());
		
		itemAddCube.setEnabled(enabled);
		itemAddFace.setEnabled(enabled);
		
		itemNoTextureSize.setEnabled(ModelCreator.singleTextureMode);
		itemExportUvMap.setEnabled(ModelCreator.singleTextureMode);
	}
	
	public void updateFrame() {
		updateValues();
	}

}
