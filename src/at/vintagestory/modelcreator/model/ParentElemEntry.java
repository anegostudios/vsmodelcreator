package at.vintagestory.modelcreator.model;

public class ParentElemEntry {
	public String ElemName;
	public String DisplayName;
	
	public ParentElemEntry(String displayName, String name)
	{
		this.DisplayName = displayName;
		this.ElemName = name;
	}

	@Override
	public String toString()
	{
		return DisplayName;
	}
	
}

