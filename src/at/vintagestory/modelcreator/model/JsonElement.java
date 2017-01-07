package at.vintagestory.modelcreator.model;

import java.util.HashMap;
import java.util.Map;

public class JsonElement
{
	public String name;
	public float[] from = new float[3];
	public float[] to = new float[3];
	
	public Map<String, JsonFace> faces = new HashMap<String, JsonFace>();
	
	public float[] rotationOrigin = new float[3];
	public float rotationX = 0; 
	public float rotationY = 0;
	public float rotationZ = 0;
	
	public int tintIndex;
	
	
	
}
