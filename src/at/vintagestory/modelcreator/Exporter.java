package at.vintagestory.modelcreator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.Face;

public class Exporter
{
	private List<String> textureList = new ArrayList<String>();

	Project project;
	
	public Exporter(Project project)
	{
		this.project = project;
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
			writeComponents(writer);
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
		for (Element cuboid : project.RootElements)
		{
			compileTextureList(cuboid);
		}
	}
	
	private void compileTextureList(Element elem) {
		for (Face face : elem.getAllFaces())
		{
			if (face.getTextureName() != null && !face.getTextureName().equals("null"))
			{
				if (!textureList.contains(face.getTextureLocation() + face.getTextureName()))
				{
					textureList.add(face.getTextureLocation() + face.getTextureName());
				}
			}
		}
		
		for (Element childelem : elem.ChildElements) {
			compileTextureList(childelem);
		}
	}
	
	

	private void writeComponents(BufferedWriter writer) throws IOException
	{
		writer.write("{");
		writer.newLine();
		if (!project.AmbientOcclusion)
		{
			writer.write("\"ambientocclusion\": " + project.AmbientOcclusion + ",");
			writer.newLine();
		}
		writeTextures(writer);
		writer.newLine();
		writer.write(space(1) + "\"elements\": [");
		
		List<Element> elems = project.RootElements;
		
		for (int i = 0; i < elems.size(); i++)
		{
			writeElement(writer, elems.get(i), 2);
			
			if (i != elems.size() - 1) {
				writer.write(",");
			}
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

	private void writeElement(BufferedWriter writer, Element cuboid, int indentation) throws IOException
	{
		writer.newLine();
		writer.write(space(indentation) + "{");
		writer.newLine();
		
		indentation++;
		
		writer.write(space(indentation) + "\"name\": \"" + cuboid.toString() + "\",");
		writer.newLine();
		writeBounds(writer, cuboid, indentation);
		writer.newLine();
		if (!cuboid.isShaded())
		{
			writeShade(writer, cuboid, indentation);
			writer.newLine();
		}
		if (cuboid.getRotationX() != 0 || cuboid.getRotationY() != 0 || cuboid.getRotationZ() != 0)
		{
			writeRotation(writer, cuboid, indentation);
		}
		writeFaces(writer, cuboid, indentation);
		
		if (cuboid.ChildElements.size() > 0) {
			writer.write(",");
			writer.newLine();
			writer.write(space(indentation) + "\"children\": [");
			
			for (int i = 0; i < cuboid.ChildElements.size(); i++) {
				if (i > 0) writer.write(",");
				writeElement(writer, cuboid.ChildElements.get(i), indentation + 1);
			}
			
			writer.newLine();
			writer.write(space(indentation) + "]");
		}
		
		indentation--;
		
		writer.newLine();
		writer.write(space(indentation) + "}");

	}

	private void writeBounds(BufferedWriter writer, Element cuboid, int indentation) throws IOException
	{
		writer.write(space(indentation) + "\"from\": [ " + cuboid.getStartX() + ", " + cuboid.getStartY() + ", " + cuboid.getStartZ() + " ], ");
		writer.newLine();
		writer.write(space(indentation) + "\"to\": [ " + (cuboid.getStartX() + cuboid.getWidth()) + ", " + (cuboid.getStartY() + cuboid.getHeight()) + ", " + (cuboid.getStartZ() + cuboid.getDepth()) + " ], ");
	}

	private void writeShade(BufferedWriter writer, Element cuboid, int indentation) throws IOException
	{
		writer.write(space(indentation) + "\"shade\": " + cuboid.isShaded() + ",");
	}

	private void writeRotation(BufferedWriter writer, Element cuboid, int indentation) throws IOException
	{
		writer.write(space(indentation) + "\"rotationOrigin\": [ " + cuboid.getOriginX() + ", " + cuboid.getOriginY() + ", " + cuboid.getOriginZ() + " ],");
		writer.newLine();
		if (cuboid.getRotationX() != 0) { writer.write(space(indentation) + "\"rotationX\": " + cuboid.getRotationX() + ","); writer.newLine(); }
		if (cuboid.getRotationY() != 0) { writer.write(space(indentation) + "\"rotationY\": " + cuboid.getRotationY() + ","); writer.newLine(); }
		if (cuboid.getRotationZ() != 0) { writer.write(space(indentation) + "\"rotationZ\": " + cuboid.getRotationZ() + ","); writer.newLine(); }
	}

	private void writeFaces(BufferedWriter writer, Element cuboid, int indentation) throws IOException
	{
		writer.write(space(indentation) + "\"faces\": {");
		writer.newLine();
		for (Face face : cuboid.getAllFaces())
		{
			if (face.getExists()) {
				writer.write(space(indentation + 1) + "\"" + Face.getFaceName(face.getSide()) + "\": { ");
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
		writer.write(space(indentation) + "}");
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
