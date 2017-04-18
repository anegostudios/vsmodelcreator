package at.vintagestory.modelcreator;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import at.vintagestory.modelcreator.model.Animation;
import at.vintagestory.modelcreator.model.AttachmentPoint;
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
		PrintWriter fw;
		BufferedWriter writer;
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStreamWriter outWriter = new OutputStreamWriter(baos);
			writer = new BufferedWriter(outWriter);
			writeComponents(writer);
			writer.close();
			
			if (!file.exists())
			{
				file.createNewFile();
			}
			
			fw = new PrintWriter(file);
			fw.write(baos.toString());
			fw.close();
			
			outWriter.close();
			baos.close();
			
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
		/*if (!project.AmbientOcclusion)
		{
			writer.write(space(1) + "\"ambientocclusion\": " + project.AmbientOcclusion + ",");
			writer.newLine();
		}*/
		
		if (project.AllAngles) {
			writer.write(space(1) + "\"allAngles\": " + project.AllAngles + ",");
			writer.newLine();
		}
		if (project.SingleTexture) {
			writer.write(space(1) + "\"singleTexture\": " + project.SingleTexture + ",");
			writer.newLine();
		}
		writer.write(space(1) + "\"textureWidth\": " + project.TextureWidth + ",");
		writer.newLine();
		writer.write(space(1) + "\"textureHeight\": " + project.TextureHeight + ",");
		writer.newLine();
		
		
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

		writer.write(space(3) + "\"name\": \"" + animation.getName() + "\",");
		writer.newLine();
		writer.write(space(3) + "\"quantityframes\": " + animation.GetQuantityFrames() + ",");
		writer.newLine();
		
		if (animation.ForActivity != null) {
			writer.write(space(3) + "\"forActivity\": \"" + animation.ForActivity + "\",");
			writer.newLine();			
		}
		
		writer.write(space(3) + "\"onActivityStopped\": \"" + animation.OnActivityStopped + "\",");
		writer.newLine();
		
		writer.write(space(3) + "\"onAnimationEnd\": \"" + animation.OnAnimationEnd + "\",");
		writer.newLine();
		
		writer.write(space(3) + "\"keyframes\": [");
		writer.newLine();
		for (int i = 0; i < animation.keyframes.length; i++) {
			if (animation.keyframes[i].Elements.size() == 0) continue;
			
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
		
		writer.write(space(5) + "\"frame\": " + keyframe.getFrameNumber() + ",");
		writer.newLine();
		writer.write(space(5) + "\"elements\": {");
		writer.newLine();
		
		boolean didwrite = false;
		
		for (int i = 0; i < keyframe.Elements.size(); i++) {
			KeyframeElement kElem = (KeyframeElement)keyframe.Elements.get(i);
			if (didwrite && willWriteKeyFrameElement(kElem)) {
				writer.write(",");
				writer.newLine();
			}
			
			didwrite = writeKeyFrameElement(writer, kElem, 6);
		}
		
		writer.newLine();
		writer.write(space(5) + "}");
		writer.newLine();
		writer.write(space(4) + "}");
	}
	
	
	boolean willWriteKeyFrameElement(KeyframeElement kElem) {
		if (!kElem.IsUseless()) return true;
		
		for (int i = 0; i < kElem.ChildElements.size(); i++)
		{
			if (!((KeyframeElement)kElem.ChildElements.get(i)).IsUseless()) return true;
		}
		
		return false;
	}
	

	private boolean writeKeyFrameElement(BufferedWriter writer, KeyframeElement kElem, int indent) throws IOException
	{
		boolean didwrite = false;
		
		if (!kElem.IsUseless()) {
			writer.write(space(indent) + "\"" + kElem.AnimatedElement.name + "\": { ");
			
			boolean bla = false;
			
			if (kElem.PositionSet) {
				writer.write("\"offsetX\": " + kElem.getOffsetX());
				writer.write(", \"offsetY\": " + kElem.getOffsetY());
				writer.write(", \"offsetZ\": " + kElem.getOffsetZ());
				bla = true;
			}
			
			if (kElem.RotationSet) {
				if (bla) {
					writer.write(", ");
				}
				writer.write("\"rotationX\": " + kElem.getRotationX());
				writer.write(", \"rotationY\": " + kElem.getRotationY());
				writer.write(", \"rotationZ\": " + kElem.getRotationZ());
				bla = true;
			}
			
			if (kElem.StretchSet) {
				if (bla) {
					writer.write(", ");
				}
				writer.write("\"stretchX\": " + kElem.getStretchX());
				writer.write(", \"stretchY\": " + kElem.getStretchY());
				writer.write(", \"stretchZ\": " + kElem.getStretchZ());
			}
			
			writer.write(" }");
			didwrite = true;
		}
		
		boolean didwritechild = false;
		boolean didwriteanychild = false;
		for (int i = 0; i < kElem.ChildElements.size(); i++)
		{
			KeyframeElement childKelem = (KeyframeElement)kElem.ChildElements.get(i);
			if ((didwritechild || (i == 0 && didwrite)) && willWriteKeyFrameElement(childKelem)) {
				writer.write(",");
				writer.newLine();
			}
			
			didwritechild = writeKeyFrameElement(writer, childKelem, indent);
			
			didwriteanychild |= didwritechild;
		}
			
		
		return didwriteanychild || didwrite;
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
		if (cuboid.getTintIndex() > 0)
		{
			writer.write(space(indentation) + "\"tintIndex\": " + cuboid.getTintIndex() + ",");
			writer.newLine();
		}

		
		if (cuboid.getRotationX() != 0 || cuboid.getRotationY() != 0 || cuboid.getRotationZ() != 0 || cuboid.getOriginX() != 0 || cuboid.getOriginY() != 0 || cuboid.getOriginZ() != 0)
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
		
		if (cuboid.AttachmentPoints.size() > 0) {
			writer.write(",");
			writer.newLine();
			writer.write(space(indentation) + "\"attachmentpoints\": [");
			
			for (int i = 0; i < cuboid.AttachmentPoints.size(); i++) {
				if (i > 0) writer.write(",");
				writeAttachmentPoint(writer, cuboid.AttachmentPoints.get(i), indentation + 1);
			}
			
			writer.newLine();
			writer.write(space(indentation) + "]");
		}
		
		
		indentation--;
		
		writer.newLine();
		writer.write(space(indentation) + "}");
	}
	
	
	private void writeAttachmentPoint(BufferedWriter writer, AttachmentPoint point, int indentation) throws IOException {
		writer.newLine();
		writer.write(space(indentation) + "{");
		writer.newLine();
		
		indentation++;
		
		writer.write(space(indentation) + "\"code\": \"" + point.getCode() + "\",");
		writer.newLine();
		writer.write(space(indentation) + "\"posX\": \"" + point.getPosX() + "\",");
		writer.write("\"posY\": \"" + point.getPosY() + "\",");
		writer.write("\"posZ\": \"" + point.getPosZ() + "\",");
		
		writer.newLine();
		writer.write(space(indentation) + "\"rotationX\": \"" + point.getRotationX() + "\",");
		writer.write("\"rotationY\": \"" + point.getRotationY() + "\",");
		writer.write("\"rotationZ\": \"" + point.getRotationZ() + "\"");
		
		indentation--;
		
		writer.newLine();
		writer.write(space(indentation) + "}");
	}

	private void writeBounds(BufferedWriter writer, Element cuboid, int indentation) throws IOException
	{
		writer.write(space(indentation) + "\"from\": [ " + d2s(cuboid.getStartX()) + ", " + d2s(cuboid.getStartY()) + ", " + d2s(cuboid.getStartZ()) + " ], ");
		writer.newLine();
		writer.write(space(indentation) + "\"to\": [ " + d2s(cuboid.getStartX() + cuboid.getWidth()) + ", " + d2s(cuboid.getStartY() + cuboid.getHeight()) + ", " + d2s(cuboid.getStartZ() + cuboid.getDepth()) + " ], ");
	}

	private void writeShade(BufferedWriter writer, Element cuboid, int indentation) throws IOException
	{
		writer.write(space(indentation) + "\"shade\": " + cuboid.isShaded() + ",");
	}

	private void writeRotation(BufferedWriter writer, Element cuboid, int indentation) throws IOException
	{
		writer.write(space(indentation) + "\"rotationOrigin\": [ " + d2s(cuboid.getOriginX()) + ", " + d2s(cuboid.getOriginY()) + ", " + d2s(cuboid.getOriginZ()) + " ],");
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
			writer.write(space(indentation + 1) + "\"" + Face.getFaceName(face.getSide()) + "\": { ");
			writer.write("\"texture\": \"#" + textureList.indexOf(face.getTextureLocation() + face.getTextureName()) + "\"");
			writer.write(", \"uv\": [ " + d2s(face.getStartU()) + ", " + d2s(face.getStartV()) + ", " + d2s(face.getEndU()) + ", " + d2s(face.getEndV()) + " ]");
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
	
	String d2s(double value) {
		return "" + (Math.round(value * 10000) / 10000.0);
	}
}
