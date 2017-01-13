package at.vintagestory.modelcreator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.vintagestory.modelcreator.interfaces.IDrawable;
import at.vintagestory.modelcreator.model.Animation;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.Face;
import at.vintagestory.modelcreator.model.Keyframe;
import at.vintagestory.modelcreator.model.KeyframeElement;

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
		for (Element cuboid : project.rootElements)
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
		
		List<Element> elems = project.rootElements;
		
		for (int i = 0; i < elems.size(); i++)
		{
			writeElement(writer, elems.get(i), 2);
			
			if (i != elems.size() - 1) {
				writer.write(",");
			}
		}
		
		writer.newLine();
		writer.write(space(1) + "]");
		
		if (project.Animations.size() > 0) {
			writer.write(",");
			writer.newLine();
			writer.write(space(1) + "\"animations\": [");

			for (int i = 0; i < project.Animations.size(); i++)
			{
				writeAnimation(writer, project.Animations.get(i));
				
				if (i != project.Animations.size() - 1) {
					writer.write(",");
					writer.newLine();
				}
			}
			
			writer.newLine();
			writer.write(space(1) + "]");
			writer.newLine();
		}
		
		writer.write("}");
	}
	
	
	private void writeAnimation(BufferedWriter writer, Animation animation) throws IOException
	{
		writer.newLine();
		writer.write(space(2) + "{");
		writer.newLine();

		writer.write(space(3) + "\"name\": \"" + animation.name + "\",");
		writer.newLine();
		writer.write(space(3) + "\"quantityframes\": " + animation.GetQuantityFrames() + ",");
		writer.newLine();
		writer.write(space(3) + "\"keyframes\": [");
		writer.newLine();
		for (int i = 0; i < animation.keyframes.length; i++) {
			writeKeyFrame(writer, animation.keyframes[i]);
			
			if (i != animation.keyframes.length - 1) {
				writer.write(",");
				writer.newLine();
			}
		}
		writer.newLine();
		writer.write(space(3) + "]");
		writer.newLine();
		writer.write(space(2) + "}");
	}


	

	private void writeKeyFrame(BufferedWriter writer, Keyframe keyframe) throws IOException
	{
		writer.write(space(4) + "{");
		writer.newLine();
		
		writer.write(space(5) + "\"frame\": " + keyframe.FrameNumber + ",");
		writer.newLine();
		writer.write(space(5) + "\"elements\": [");
		writer.newLine();
		
		for (int i = 0; i < keyframe.Elements.size(); i++) {
			writeKeyFrameElement(writer, (KeyframeElement)keyframe.Elements.get(i), 6);
			
			if (i != keyframe.Elements.size() - 1) {
				writer.write(",");
				writer.newLine();
			}
		}
		
		writer.newLine();
		writer.write(space(5) + "]");
		writer.newLine();
		writer.write(space(4) + "}");
	}
	
	

	private void writeKeyFrameElement(BufferedWriter writer, KeyframeElement kElem, int indent) throws IOException
	{
		writer.write(space(indent) + "{ ");
		writer.newLine();
		
		indent++;
		
		writer.write(space(indent) + "\"animatedElement\": \""+ kElem.AnimatedElement.name + "\"");
		
		if (kElem.PositionSet) {
			writer.write(", ");
			writer.newLine();
			writer.write(space(indent));
			
			writer.write("\"offsetX\": " + kElem.offsetX);
			writer.write(", \"offsetY\": " + kElem.offsetY);
			writer.write(", \"offsetZ\": " + kElem.offsetZ);
		}
		
		if (kElem.RotationSet) {
			writer.write(", ");
			writer.newLine();
			writer.write(space(indent));
			
			writer.write("\"rotationX\": " + kElem.rotationX);
			writer.write(", \"rotationY\": " + kElem.rotationY);
			writer.write(", \"rotationZ\": " + kElem.rotationZ);	
		}
		
		if (kElem.StretchSet) {
			writer.write(", ");
			writer.newLine();
			writer.write(space(indent));

			writer.write("\"stretchX\": " + kElem.stretchX);
			writer.write(", \"stretchY\": " + kElem.stretchY);
			writer.write(", \"stretchZ\": " + kElem.stretchZ);
		}
		
		
		
		if (kElem.ChildElements.size() > 0) {
			writer.write(", ");
			writer.newLine();
			writer.write(space(indent) + "\"children\": [");
			writer.newLine();
			
			for (int i = 0; i < kElem.ChildElements.size(); i++)
			{
				writeKeyFrameElement(writer, (KeyframeElement)kElem.ChildElements.get(i), indent + 1);
				
				if (i != kElem.ChildElements.size() - 1) {
					writer.write(",");
					writer.newLine();
				}
			}
			
			writer.newLine();
			writer.write(space(indent) + "]");
			writer.newLine();
		} else {
			writer.newLine();
		}
		
		indent--;
				
		writer.write(space(indent) + "}");
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

	
	
	private String space(int size)
	{
		String space = "";
		for (int i = 0; i < size; i++)
		{
			space += "\t";
		}
		return space;
	}
}
