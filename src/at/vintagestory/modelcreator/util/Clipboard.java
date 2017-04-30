package at.vintagestory.modelcreator.util;

import at.vintagestory.modelcreator.model.ClipboardTexture;

public class Clipboard
{
	private static ClipboardTexture texture;

	public static void copyTexture(String texture)
	{
		Clipboard.texture = new ClipboardTexture(texture);
	}

	public static ClipboardTexture getTexture()
	{
		return Clipboard.texture;
	}
}
