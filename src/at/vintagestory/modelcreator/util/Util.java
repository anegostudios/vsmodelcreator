package at.vintagestory.modelcreator.util;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.old.ProjectManager;

public class Util
{
	public static void openUrl(String url)
	{
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
		{
			try
			{
				desktop.browse(new URL(url).toURI());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void loadModelFromJar(IElementManager manager, Class<?> clazz, String name)
	{
		try
		{
			InputStream is = clazz.getClassLoader().getResourceAsStream(name + ".model");
			File file = File.createTempFile(name + ".model", "");
			FileOutputStream fos = new FileOutputStream(file);

			byte[] buffer = new byte[1024];

			int len;
			while ((len = is.read(buffer)) > 0)
			{
				fos.write(buffer, 0, len);
			}

			fos.close();
			is.close();

			ProjectManager.loadProject(manager, file.getAbsolutePath());
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
}
