package com.mrcrayfish.modelcreator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.element.Face;

public class Exporter
{
	private List<String> textureList = new ArrayList<String>();

	// Model Variables
	private ElementManager manager;

	public Exporter(ElementManager manager)
	{
		this.manager = manager;
		compileTextureList();
	}

	public File export(File file)
	{
		File path = file.getParentFile();
		if (path.exists() && path.isDirectory())
		{
			writeJSONFile(file);
		}
		return file;
	}

	public File writeJSONFile(File file)
	{
		FileWriter fw;
		BufferedWriter writer;
		try
		{
			if (!file.exists())
			{
				file.createNewFile();
			}
			fw = new FileWriter(file);
			writer = new BufferedWriter(fw);
			writeComponents(writer, manager);
			writer.close();
			fw.close();
			return file;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private void compileTextureList()
	{
		for (Element cuboid : manager.getAllElements())
		{
			for (Face face : cuboid.getAllFaces())
			{
				System.out.println(face.getTextureLocation() + " " + face.getTextureName());
				if (face.getTextureName() != null && !face.getTextureName().equals("null"))
				{
					if (!textureList.contains(face.getTextureLocation() + face.getTextureName()))
					{
						textureList.add(face.getTextureLocation() + face.getTextureName());
					}
				}
			}
		}
	}

	private void writeComponents(BufferedWriter writer, ElementManager manager) throws IOException
	{
		writer.write("{");
		writer.newLine();
		if (!manager.getAmbientOcc())
		{
			writer.write("\"ambientocclusion\": " + manager.getAmbientOcc() + ",");
			writer.newLine();
		}
		writeTextures(writer);
		writer.newLine();
		writer.write(space(1) + "\"elements\": [");
		for (int i = 0; i < manager.getElementCount(); i++)
		{
			writer.newLine();
			writer.write(space(2) + "{");
			writer.newLine();
			writeElement(writer, manager.getElement(i));
			writer.newLine();
			writer.write(space(2) + "}");
			if (i != manager.getElementCount() - 1)
				writer.write(",");
		}
		writer.newLine();
		writer.write(space(1) + "]");
		writer.newLine();
		writer.write("}");
	}

	private void writeTextures(BufferedWriter writer) throws IOException
	{
		writer.write(space(1) + "\"textures\": {");
		writer.newLine();
		if (manager.getParticle() != null)
		{
			writer.write(space(2) + "\"particle\": \"blocks/" + manager.getParticle() + "\"");
			if (textureList.size() > 0)
			{
				writer.write(",");
			}
			writer.newLine();
		}
		for (String texture : textureList)
		{
			writer.write(space(2) + "\"" + textureList.indexOf(texture) + "\": \"" + texture + "\"");
			if (textureList.indexOf(texture) != textureList.size() - 1)
			{
				writer.write(",");
			}
			writer.newLine();
		}
		writer.write(space(1) + "},");
	}

	private void writeElement(BufferedWriter writer, Element cuboid) throws IOException
	{
		writer.write(space(3) + "\"name\": \"" + cuboid.toString() + "\",");
		writer.newLine();
		writeBounds(writer, cuboid);
		writer.newLine();
		if (!cuboid.isShaded())
		{
			writeShade(writer, cuboid);
			writer.newLine();
		}
		if (cuboid.getRotationX() != 0 || cuboid.getRotationY() != 0 || cuboid.getRotationZ() != 0)
		{
			writeRotation(writer, cuboid);
		}
		writeFaces(writer, cuboid);

	}

	private void writeBounds(BufferedWriter writer, Element cuboid) throws IOException
	{
		writer.write(space(3) + "\"from\": [ " + cuboid.getStartX() + ", " + cuboid.getStartY() + ", " + cuboid.getStartZ() + " ], ");
		writer.newLine();
		writer.write(space(3) + "\"to\": [ " + (cuboid.getStartX() + cuboid.getWidth()) + ", " + (cuboid.getStartY() + cuboid.getHeight()) + ", " + (cuboid.getStartZ() + cuboid.getDepth()) + " ], ");
	}

	private void writeShade(BufferedWriter writer, Element cuboid) throws IOException
	{
		writer.write(space(3) + "\"shade\": " + cuboid.isShaded() + ",");
	}

	private void writeRotation(BufferedWriter writer, Element cuboid) throws IOException
	{
		writer.write(space(3) + "\"rotationOrigin\": [ " + cuboid.getOriginX() + ", " + cuboid.getOriginY() + ", " + cuboid.getOriginZ() + " ],");
		writer.newLine();
		if (cuboid.getRotationX() != 0) { writer.write(space(3) + "\"rotationX\": " + cuboid.getRotationX() + ","); writer.newLine(); }
		if (cuboid.getRotationY() != 0) { writer.write(space(3) + "\"rotationY\": " + cuboid.getRotationY() + ","); writer.newLine(); }
		if (cuboid.getRotationZ() != 0) { writer.write(space(3) + "\"rotationZ\": " + cuboid.getRotationZ() + ","); writer.newLine(); }

		/*if (cuboid.shouldRescale())
		{
			writer.write(", \"rescale\": " + cuboid.shouldRescale());
		}*/
	}

	private void writeFaces(BufferedWriter writer, Element cuboid) throws IOException
	{
		writer.write(space(3) + "\"faces\": {");
		writer.newLine();
		for (Face face : cuboid.getAllFaces())
		{
			if (face.getExists()) {
				writer.write(space(4) + "\"" + Face.getFaceName(face.getSide()) + "\": { ");
				writer.write("\"texture\": \"#" + textureList.indexOf(face.getTextureLocation() + face.getTextureName()) + "\"");
				writer.write(", \"uv\": [ " + face.getStartU() + ", " + face.getStartV() + ", " + face.getEndU() + ", " + face.getEndV() + " ]");
				if (face.getRotation() > 0)
					writer.write(", \"rotation\": " + (int) face.getRotation() * 90);
				if (face.isCullfaced())
					writer.write(", \"cullface\": \"" + Face.getFaceName(face.getSide()) + "\"");
				if (face.getGlow() > 0) {
					writer.write(", \"glow\": " + face.getGlow());
				}
				if (!face.isEnabled()) {
					writer.write(", \"enabled\": false");
				}
				
				writer.write(" }");
				if (face.getSide() != cuboid.getLastValidFace())
				{
					writer.write(",");
					writer.newLine();
				}				
			}
		}
		writer.newLine();
		writer.write(space(3) + "}");
	}

	/*
	 * private void writeChild(BufferedWriter writer) throws IOException {
	 * writer.write("{"); writer.newLine(); writer.write(space(1) +
	 * "\"parent\": \"block/" + modelName + "\","); writer.newLine();
	 * writer.write(space(1) + "\"textures\": {"); writer.newLine(); for (int i
	 * = 0; i < textureList.size(); i++) { writer.write(space(2) + "\"" + i +
	 * "\": \"block/" + textureList.get(i) + "\""); if (i != textureList.size()
	 * - 1) { writer.write(","); } writer.newLine(); } writer.write(space(1) +
	 * "}"); writer.write("}"); }
	 */

	private String space(int size)
	{
		String space = "";
		for (int i = 0; i < size; i++)
		{
			space += "    ";
		}
		return space;
	}
}
