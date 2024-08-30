package at.vintagestory.modelcreator.util;

import at.vintagestory.modelcreator.model.ClipboardTexture;
import at.vintagestory.modelcreator.model.ClipboardUV;

public class Clipboard
{
	private static ClipboardTexture texture;
	private static ClipboardUV uv;

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
}
