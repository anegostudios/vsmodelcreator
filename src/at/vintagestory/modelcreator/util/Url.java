package at.vintagestory.modelcreator.util;

import java.awt.Desktop;
import java.net.URL;

public class Url
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
}
