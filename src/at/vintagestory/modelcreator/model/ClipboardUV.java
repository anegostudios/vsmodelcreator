package at.vintagestory.modelcreator.model;

public class ClipboardUV
{
	//private String location;
	private double us;
	private double ue;
	private double vs;
	private double ve;
	
	public ClipboardUV(double UStart, double VStart, double UEnd, double VEnd)
	{
		//this.location = location;
		this.us = UStart;
		this.ue = UEnd;
		this.vs = VStart;
		this.ve = VEnd;
	}

	/*public String getLocation()
	{
		return location;
	}*/

	public double getUStart()
	{
		return us;
	}
	public double getUEnd()
	{
		return ue;
	}
	public double getVStart()
	{
		return vs;
	}
	public double getVEnd()
	{
		return ve;
	}
}
