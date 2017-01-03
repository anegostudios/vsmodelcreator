package at.vintagestory.modelcreator.enums;

import java.awt.Font;
import java.io.InputStream;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import at.vintagestory.modelcreator.ModelCreator;

public enum EnumFonts
{
	BEBAS_NEUE_20("bebas_neue.otf", 20), BEBAS_NEUE_50("bebas_neue.otf", 50);

	private TrueTypeFont font;

	EnumFonts(String name, float size)
	{
		loadFont(name, size);
	}

	private void loadFont(String name, float size)
	{
		try
		{
			InputStream is = ModelCreator.class.getClassLoader().getResourceAsStream(name);
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			this.font = new TrueTypeFont(font.deriveFont(size), false);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void drawString(int x, int y, String text)
	{
		font.drawString(x, y, text);
	}

	public void drawString(int x, int y, String text, Color color)
	{
		font.drawString(x, y, text, color);
	}
}
