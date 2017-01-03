package at.vintagestory.modelcreator.util.screenshot;

import java.io.File;

public class PendingScreenshot
{
	private File file = null;
	private ScreenshotCallback callback;
	
	public PendingScreenshot(File file, ScreenshotCallback callback)
	{
		this.file = file;
		this.callback = callback;
	}

	public File getFile()
	{
		return file;
	}

	public ScreenshotCallback getCallback()
	{
		return callback;
	}
}
