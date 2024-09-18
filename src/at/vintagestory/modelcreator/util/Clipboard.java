package at.vintagestory.modelcreator.util;

import at.vintagestory.modelcreator.model.ClipboardTexture;
import at.vintagestory.modelcreator.model.ClipboardUV;
import at.vintagestory.modelcreator.model.ClipboardWind;

public class Clipboard
{
	private static ClipboardTexture texture;
	private static ClipboardUV uv;
	private static ClipboardWind wind;

	public static void copyTexture(String texture)
	{
		Clipboard.texture = new ClipboardTexture(texture);
	}

	public static ClipboardTexture getTexture()
	{
		return Clipboard.texture;
	}
	
	public static void copyUV(double UStart, double VStart, double UEnd, double VEnd)
	{
		Clipboard.uv = new ClipboardUV(UStart, VStart, UEnd, VEnd);
	}
	
	public static ClipboardUV getUV()
	{
		return Clipboard.uv;
	}
	
	public static void copyWindSettings(int[] windSettings)
	{
		Clipboard.wind = new ClipboardWind(windSettings);
	}
	
	public static ClipboardWind getWindSettings()
	{
		return Clipboard.wind;
	}
}
