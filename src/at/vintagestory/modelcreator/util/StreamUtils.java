package at.vintagestory.modelcreator.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class StreamUtils
{
	public static String convertToString(InputStream inputStream) throws IOException
	{
		if (inputStream != null)
		{
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try
			{
				Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 1024);
				int n;
				while ((n = reader.read(buffer)) != -1)
				{
					writer.write(buffer, 0, n);
				}
			}
			finally
			{
				inputStream.close();
			}
			return writer.toString();
		}
		else
		{
			return "";
		}
	}
}
