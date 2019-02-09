package at.vintagestory.modelcreator.util.screenshot;

public abstract class AnimationCapture
{
	public abstract boolean isComplete();
	
	public abstract void PrepareFrame();
	
	public abstract void CaptureFrame(int width, int height);
	
	
}
