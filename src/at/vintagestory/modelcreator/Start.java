package at.vintagestory.modelcreator;

import java.awt.Color;
import java.io.File;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.lwjgl.LWJGLUtil;
import com.jtattoo.plaf.hifi.HiFiLookAndFeel;

public class Start
{
	public static Color BorderColor = new Color(220, 220, 220);
	public static javax.swing.border.Border Border = BorderFactory.createLineBorder(BorderColor, 0);
	
	public static void main(String[] args)
	{
		Double version = Double.parseDouble(System.getProperty("java.specification.version"));
		if (version < 1.8)
		{
			JOptionPane.showMessageDialog(null, "You need Java 1.8 or higher to run this program.");
			return;
		}

		System.setProperty("org.lwjgl.util.Debug", "true");

		File JGLLib = null;
		switch(LWJGLUtil.getPlatform())
		{
		    case LWJGLUtil.PLATFORM_WINDOWS:
		    {
		        JGLLib = new File("./natives/windows/");
		    }
		    break;

		    case LWJGLUtil.PLATFORM_LINUX:
		    {
		        JGLLib = new File("./natives/linux/");
		    }
		    break;

		    case LWJGLUtil.PLATFORM_MACOSX:
		    {
		        JGLLib = new File("./natives/macosx/");
		    }
		    break;
		}

		System.setProperty("org.lwjgl.librarypath", JGLLib.getAbsolutePath());
		
		try
		{
			Properties props = new Properties();
			props.put("logoString", "HiFi");
			props.put("centerWindowTitle", "on");
			/*props.put("buttonBackgroundColor", "127 132 145");
			props.put("buttonForegroundColor", "255 255 255");
			props.put("windowTitleBackgroundColor", "97 102 115");
			props.put("windowTitleForegroundColor", "255 255 255");
			props.put("backgroundColor", "221 221 228");
			props.put("menuBackgroundColor", "221 221 228");
			props.put("controlForegroundColor", "120 120 120");
			props.put("windowBorderColor", "97 102 110");*/
			HiFiLookAndFeel.setTheme(props);
			UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		new ModelCreator("(untitled) - " + ModelCreator.windowTitle);
	}
}
