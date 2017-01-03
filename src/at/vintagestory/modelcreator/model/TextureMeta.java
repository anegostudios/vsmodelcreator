package at.vintagestory.modelcreator.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import at.vintagestory.modelcreator.old.TextureAnimation;

public class TextureMeta
{
	private TextureAnimation anim = null;
	private TextureProperties props = null;

	public TextureMeta(TextureAnimation anim, TextureProperties props)
	{
		this.anim = anim;
		this.props = props;
	}

	public TextureAnimation getAnimation()
	{
		return anim;
	}

	public TextureProperties getProperties()
	{
		return props;
	}

	public static TextureMeta parse(File meta) throws JsonIOException, JsonSyntaxException, FileNotFoundException
	{
		TextureAnimation anim = null;
		TextureProperties props = null;

		if (meta != null && meta.exists())
		{
			JsonObject animationObj = null;
			JsonObject propertiesObj = null;

			JsonParser parser = new JsonParser();
			JsonElement read = parser.parse(new FileReader(meta));

			if (read.isJsonObject())
			{
				JsonObject mcMeta = read.getAsJsonObject();
				if (mcMeta.has("animation") && mcMeta.get("animation").isJsonObject())
				{
					animationObj = mcMeta.get("animation").getAsJsonObject();
					anim = new TextureAnimation();
				}
				if (mcMeta.has("texture") && mcMeta.get("texture").isJsonObject())
				{
					propertiesObj = mcMeta.get("texture").getAsJsonObject();
					props = new TextureProperties();
				}
			}

			// set variables
			if (anim != null && animationObj != null)
			{
				int frametime = 1;
				if (animationObj.has("frametime") && animationObj.get("frametime").isJsonPrimitive())
				{
					frametime = animationObj.get("frametime").getAsInt();
				}
				anim.setFrameTime(frametime);

				if (animationObj.has("interpolate") && animationObj.get("interpolate").isJsonPrimitive())
				{
					boolean interpolate = animationObj.get("interpolate").getAsBoolean();
					anim.setInterpolate(interpolate);
				}

				// set frames
				if (animationObj.has("frames") && animationObj.get("frames").isJsonArray())
				{
					JsonArray frames = animationObj.get("frames").getAsJsonArray();
					if (frames.size() > 0)
					{
						List<Integer> frameList = new ArrayList<Integer>();
						Map<Integer, Integer> customTimes = new HashMap<Integer, Integer>();

						for (int i = 0; i < frames.size(); i++)
						{
							JsonElement frame = frames.get(i);

							int index = 0;
							int time = frametime;
							if (frame.isJsonPrimitive())
							{
								index = frame.getAsInt();
							}
							else if (frame.isJsonObject())
							{
								JsonObject frameObj = frame.getAsJsonObject();

								if (frameObj.has("index") && frameObj.get("index").isJsonPrimitive())
								{
									index = frameObj.get("index").getAsInt();
								}
								if (frameObj.has("time") && frameObj.get("time").isJsonPrimitive())
								{
									time = frameObj.get("time").getAsInt();
								}
							}

							frameList.add(index);
							if (time != frametime)
							{
								customTimes.put(frameList.size() - 1, time);
							}
						}

						anim.setFrames(frameList);
						anim.setCustomTimes(customTimes);
					}
				}
			}

			// texture vars
			if (props != null && propertiesObj != null)
			{
				if (propertiesObj.has("blur") && propertiesObj.get("blur").isJsonPrimitive())
				{
					boolean blur = propertiesObj.get("blur").getAsBoolean();
					props.setBlurred(blur);
				}
			}
			return new TextureMeta(anim, props);
		}
		return null;
	}
}
