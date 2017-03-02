package at.vintagestory.modelcreator.enums;

public enum EnumEntityActivityStoppedHandling
{
	PlayTillEnd,
	Rewind,
	Stop
	;
	
	
	public int index()
	{
		EnumEntityActivityStoppedHandling[] values = values();
		for (int i = 0; i < values.length; i++) {
			if (values[i] == this) return i;
		}
		return -1;
	}
}
