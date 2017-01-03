package at.vintagestory.modelcreator.old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextureAnimation
{
	private int width = 16, height = 16;
	
	private List<Integer> frames = new ArrayList<Integer>();
	private Map<Integer, Integer> customTimes = new HashMap<Integer, Integer>();
	private int frametime = 1;
	private boolean interpolate = false;

	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setFrameTime(int frametime)
	{
		this.frametime = frametime;
	}

	public void setFrames(List<Integer> frameList)
	{
		frames = new ArrayList<Integer>();
		frames.addAll(frameList);
	}

	public void setCustomTimes(Map<Integer, Integer> times)
	{
		customTimes = new HashMap<Integer, Integer>();
		customTimes.putAll(times);
	}

	public void setInterpolate(boolean interpolate)
	{
		this.interpolate = interpolate;
	}

	public boolean isInterpolated()
	{
		return interpolate;
	}

	public int getFrameCount()
	{
		return frames.size();
	}

	public int getCurrentAnimationFrame()
	{
		long maxTime = 0;
		for (int i = 0; i < frames.size(); i++)
		{
			maxTime += getFrameTime(i);
		}

		long animTime = System.currentTimeMillis() % maxTime;

		for (int i = 0; i < frames.size(); i++)
		{
			if (animTime <= getFrameTime(i))
			{
				return frames.get(i);
			}
			animTime -= getFrameTime(i);
		}

		return 0;
	}

	public int getNextAnimationFrame()
	{
		if (frames.size() < 1)
		{
			return 0;
		}

		long maxTime = 0;
		for (int i = 0; i < frames.size(); i++)
		{
			maxTime += getFrameTime(i);
		}

		long animTime = System.currentTimeMillis() % maxTime;

		for (int i = 0; i < frames.size(); i++)
		{
			if (animTime <= getFrameTime(i))
			{
				if (i < frames.size() - 1)
					return frames.get(i + 1);
				else
					return frames.get(0);
			}
			animTime -= getFrameTime(i);
		}

		return 0;
	}

	public long getFrameTime(int frame)
	{
		if (customTimes != null && customTimes.containsKey(frame))
		{
			return customTimes.get(frame) * 50L;
		}
		return frametime * 50L;
	}

	public double getFrameInterpolation()
	{
		long maxTime = 0;
		for (int i = 0; i < frames.size(); i++)
		{
			maxTime += getFrameTime(i);
		}

		long animTime = System.currentTimeMillis() % maxTime;

		for (int i = 0; i < frames.size(); i++)
		{
			if (animTime <= getFrameTime(i))
			{
				double percent = animTime / (double) getFrameTime(i);
				return percent;
			}
			animTime -= getFrameTime(i);
		}

		return 0;
	}

	public int getPasses()
	{
		if (isInterpolated() && getFrameCount() > 1)
		{
			return 2;
		}
		return 1;
	}
}
