package at.vintagestory.modelcreator.util;

public class Parser
{
	public static double parseDouble(String text, double def)
	{
		double value;
		try
		{
			value = Double.parseDouble(text);
		}
		catch (NumberFormatException e)
		{
			value = def;
		}
		return value;
	}
}
