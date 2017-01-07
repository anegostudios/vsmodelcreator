package at.vintagestory.modelcreator.old;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import at.vintagestory.modelcreator.Exporter;
import at.vintagestory.modelcreator.Importer;
import at.vintagestory.modelcreator.TextureManager;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.Face;
import at.vintagestory.modelcreator.model.PendingTexture;

public class ProjectManager
{
	public static void loadProject(IElementManager manager, String modelFile)
	{
		manager.clearElements();

		Project project = new Project(manager, extractFiles(modelFile));

		Importer importer = new Importer(manager, project.getModel().getAbsolutePath());
		importer.ignoreTextureLoading();
		importer.importFromJSON();

		for (ProjectTexture texture : project.getTextures())
		{
			manager.addPendingTexture(new PendingTexture(texture.getTexture(), texture.getMeta()));
		}
	}

	private static File[] extractFiles(String modelFile)
	{
		List<File> files = new ArrayList<File>();
		try
		{
			ZipInputStream zis = new ZipInputStream(new FileInputStream(modelFile));

			ZipEntry ze = null;
			while ((ze = zis.getNextEntry()) != null)
			{
				File file = File.createTempFile(ze.getName(), "");
				file.mkdirs();

				byte[] buffer = new byte[1024];
				FileOutputStream fos = new FileOutputStream(file);

				int len;
				while ((len = zis.read(buffer)) > 0)
				{
					fos.write(buffer, 0, len);
				}

				fos.close();

				files.add(file);
			}

			zis.closeEntry();
			zis.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return files.toArray(new File[0]);
	}

	public static void saveProject(IElementManager manager, String name)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(name);
			ZipOutputStream zos = new ZipOutputStream(fos);

			File file = getSaveFile(manager);
			addToZipFile(file, zos);
			file.delete();

			for (String textureLocation : getTextureLocations(manager))
			{
				if (textureLocation != null)
				{
					File texture = new File(textureLocation);
					if (texture.exists())
					{
						addToZipFile(texture, zos, "textures/");
					}
				}
			}
			
			for(String metaLocation : getMetaLocations(manager))
			{
				if (metaLocation != null)
				{
					File texture = new File(metaLocation);
					if (texture.exists())
					{
						addToZipFile(texture, zos, "textures/");
					}
				}
			}

			zos.close();
			fos.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static String[] getTextureLocations(IElementManager manager)
	{
		List<String> locations = new ArrayList<String>();
		for (Element cuboid : manager.getRootElements())
		{
			for (Face face : cuboid.getAllFaces())
			{
				String location = TextureManager.getTextureLocation(face.getTextureName());
				if (!locations.contains(location))
				{
					locations.add(location);
				}
			}
		}
		return locations.toArray(new String[0]);
	}
	
	private static String[] getMetaLocations(IElementManager manager)
	{
		List<String> locations = new ArrayList<String>();
		for (Element cuboid : manager.getRootElements())
		{
			for (Face face : cuboid.getAllFaces())
			{
				String location = TextureManager.getMetaLocation(face.getTextureName());
				if (!locations.contains(location))
				{
					locations.add(location);
				}
			}
		}
		return locations.toArray(new String[0]);
	}

	private static File getSaveFile(IElementManager manager)
	{
		return new Exporter(manager).writeJSONFile(new File("model.json"));
	}

	private static void addToZipFile(File file, ZipOutputStream zos) throws FileNotFoundException, IOException
	{
		addToZipFile(file, zos, "");
	}

	private static void addToZipFile(File file, ZipOutputStream zos, String folder) throws FileNotFoundException, IOException
	{
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(folder + file.getName());
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0)
		{
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}

	private static class Project
	{
		public File model;
		private List<ProjectTexture> textures;

		public Project(IElementManager manager, File[] files)
		{
			textures = new ArrayList<ProjectTexture>();

			for (File file : files)
			{
				if (file.getAbsolutePath().contains("model.json"))
				{
					this.model = file;
				}
				else if (!file.getAbsolutePath().contains(".mcmeta"))
				{
					File metaFile = null;
					for (File mfile : files)
					{
						if (mfile.getAbsolutePath().startsWith(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))))
						{
							if (mfile.getAbsolutePath().contains(".mcmeta"))
							{
								metaFile = mfile;
							}
						}
					}
					textures.add(new ProjectTexture(file, metaFile));
				}
			}
		}

		public File getModel()
		{
			return model;
		}

		public List<ProjectTexture> getTextures()
		{
			return textures;
		}
	}

	private static class ProjectTexture
	{
		private File texture;
		private File meta;

		public ProjectTexture(File texture, File meta)
		{
			this.texture = texture;
			this.meta = meta;
		}

		public File getTexture()
		{
			return texture;
		}

		public File getMeta()
		{
			return meta;
		}
	}
}
