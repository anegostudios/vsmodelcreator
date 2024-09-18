package at.vintagestory.modelcreator.model;


public class ClipboardWind
{
	//private String location;
	private int[] windSettings;
	
	public ClipboardWind(int[] windSettings)
	{
		//this.location = location;
		this.windSettings = windSettings;
	}

	/*public String getLocation()
	{
		return location;
	}*/

	public int[] getWindSettings()
	{
		return this.windSettings;
	}
}
