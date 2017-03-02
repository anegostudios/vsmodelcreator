package at.vintagestory.modelcreator.enums;

public enum EnumEntityAnimationEndHandling
{
    Repeat,
    Hold,
    Stop;
	
	
	public int index()
	{
		EnumEntityAnimationEndHandling[] values = values();
		for (int i = 0; i < values.length; i++) {
			if (values[i] == this) return i;
		}
		return -1;
	}
}
