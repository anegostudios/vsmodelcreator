package at.vintagestory.modelcreator.util;

public class Parser
{
	
	public static boolean isDouble(String text) {
		try
		{
			Double.parseDouble(text);
		}
		catch (NumberFormatException e)
		{
			return false;			
		}
		
		return true;
	}
	
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
