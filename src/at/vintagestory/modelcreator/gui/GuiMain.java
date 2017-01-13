package at.vintagestory.modelcreator.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import at.vintagestory.modelcreator.Exporter;
import at.vintagestory.modelcreator.Importer;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Project;
import at.vintagestory.modelcreator.gui.texturedialog.TextureDialog;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.util.screenshot.PendingScreenshot;
import at.vintagestory.modelcreator.util.screenshot.ScreenshotCallback;
import at.vintagestory.modelcreator.util.screenshot.Uploader;

public class GuiMain extends JMenuBar
{
	private static final long serialVersionUID = 1L;

	private ModelCreator creator;

	/* File */
	private JMenu menuFile;
	private JMenuItem itemNew;
	private JMenuItem itemImport;
	private JMenuItem itemExport;
	private JMenuItem itemTexturePath;
	private JMenuItem itemExit;

	/* Options */
	private JMenu menuOptions;
	private JCheckBoxMenuItem itemTransparency;
	private JCheckBoxMenuItem itemUnlockAngles;
	
	/* Add */
	private JMenu menuAdd;
	private JMenuItem itemAddCube;
	private JMenuItem itemAddFace;


	/* Other */
	private JMenu otherMenu;
	private JMenuItem itemSaveToDisk;
	private JMenuItem reloadTextures;
	private JMenuItem itemImgurLink;
	private JMenuItem credits;

	public GuiMain(ModelCreator creator)
	{
		this.creator = creator;
		initMenu();
	}

	private void initMenu()
	{
		menuFile = new JMenu("File");
		{
			itemNew = createItem("New", "New Model", KeyEvent.VK_N, new ImageIcon(getClass().getClassLoader().getResource("icons/new.png")));
			itemImport = createItem("Open JSON...", "Open JSON", KeyEvent.VK_I, new ImageIcon(getClass().getClassLoader().getResource("icons/import.png")));
			itemExport = createItem("Save JSON...", "Save JSON", KeyEvent.VK_E, new ImageIcon(getClass().getClassLoader().getResource("icons/export.png")));
			itemTexturePath = createItem("Set Texture Path...", "Set the base path to look for textures", KeyEvent.VK_S, new ImageIcon(getClass().getClassLoader().getResource("icons/texture.png")));
			itemExit = createItem("Exit", "Exit Application", KeyEvent.VK_E, new ImageIcon(getClass().getClassLoader().getResource("icons/exit.png")));
		}

		menuOptions = new JMenu("Options");
		{
			itemTransparency = createCheckboxItem("Transparency", "Toggles transparent rendering in program", KeyEvent.VK_E, Icons.transparent);
			itemTransparency.setSelected(ModelCreator.transparent);
			
			itemUnlockAngles = createCheckboxItem("Unlock all Angles", "Disabling this allows angle stepping of single degrees. Suggested to unlock this only for entities.", KeyEvent.VK_A, Icons.transparent);
			itemUnlockAngles.setSelected(ModelCreator.unlockAngles);
		}

		menuAdd = new JMenu("Add");
		{
			itemAddCube = createItem("Add cube", "Add new cube", KeyEvent.VK_C, Icons.cube);
			itemAddFace = createItem("Add face", "Add single face", KeyEvent.VK_C, Icons.cube);
		}

		
		otherMenu = new JMenu("Other");
		{
			reloadTextures = createItem("Reload textures", "Reload textures", KeyEvent.VK_F5, Icons.new_);
			itemSaveToDisk = createItem("Save Screenshot to Disk...", "Save screenshot to disk.", KeyEvent.VK_S, Icons.disk);
			itemImgurLink = createItem("Get Imgur Link", "Get an Imgur link of your screenshot to share.", KeyEvent.VK_G, Icons.imgur);
			credits = createItem("Credits", "Who made this tol", 0, Icons.new_);
		}

		initActions();



		menuOptions.add(itemTransparency);
		menuOptions.add(itemUnlockAngles);
		
		menuAdd.add(itemAddCube);
		menuAdd.add(itemAddFace);

		otherMenu.add(reloadTextures);
		otherMenu.add(itemSaveToDisk);
		otherMenu.add(itemImgurLink);
		otherMenu.add(credits);

		menuFile.add(itemNew);
		menuFile.addSeparator();
		menuFile.add(itemImport);
		menuFile.add(itemExport);
		menuFile.addSeparator();
		menuFile.add(itemTexturePath);
		menuFile.addSeparator();
		menuFile.add(itemExit);

		add(menuFile);
		add(menuOptions);
		add(menuAdd);
		add(otherMenu);
	}

	private void initActions()
	{
		itemNew.addActionListener(a ->
		{
			int returnVal = JOptionPane.showConfirmDialog(creator, "You current work will be cleared, are you sure?", "Note", JOptionPane.YES_NO_OPTION);
			if (returnVal == JOptionPane.YES_OPTION)
			{
				ModelCreator.currentProject = new Project();
				ModelCreator.updateValues();
			}
		});


		itemImport.addActionListener(e ->
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
				if (ModelCreator.currentProject.rootElements.size() > 0)
				{
					returnVal = JOptionPane.showConfirmDialog(null, "Your current project will be cleared, are you sure you want to continue?", "Warning", JOptionPane.YES_NO_OPTION);
				}
				
				if (returnVal != JOptionPane.NO_OPTION && returnVal != JOptionPane.CLOSED_OPTION)
				{
					String filePath = chooser.getSelectedFile().getAbsolutePath();
					ModelCreator.prefs.put("filePath", filePath);
					Importer importer = new Importer(filePath);
					ModelCreator.currentProject = importer.loadFromJSON();
				}
				
				ModelCreator.updateValues();
			}
		});

		itemExport.addActionListener(e ->
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
					Exporter exporter = new Exporter(ModelCreator.currentProject);
					exporter.export(chooser.getSelectedFile());
				}
			}
		});

		itemTexturePath.addActionListener(e ->
		{
			JFileChooser chooser = new JFileChooser(ModelCreator.prefs.get("texturePath", "."));
			chooser.setDialogTitle("Texture Path");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				ModelCreator.prefs.put("texturePath", chooser.getSelectedFile().getAbsolutePath());
			}
		});

		itemExit.addActionListener(e ->
		{
			creator.close();
		});

		itemTransparency.addActionListener(a ->
		{
			ModelCreator.transparent = itemTransparency.isSelected();
		});
		
		itemUnlockAngles.addActionListener(a ->
		{
			ModelCreator.unlockAngles = itemUnlockAngles.isSelected();
			ModelCreator.updateValues();
		});

		
		reloadTextures.addActionListener(a -> {
			TextureDialog.reloadTextures(creator);
		});

		itemSaveToDisk.addActionListener(a ->
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
		});



		itemImgurLink.addActionListener(a ->
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

								JDialog dialog = message.createDialog(GuiMain.this, title);
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
		});
		
		credits.addActionListener(a ->
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
}
