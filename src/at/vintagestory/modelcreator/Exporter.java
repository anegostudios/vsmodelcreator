package at.vintagestory.modelcreator;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import at.vintagestory.modelcreator.interfaces.IDrawable;
import at.vintagestory.modelcreator.model.Animation;
import at.vintagestory.modelcreator.model.AttachmentPoint;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.Face;
import at.vintagestory.modelcreator.model.AnimationFrame;
import at.vintagestory.modelcreator.model.AnimFrameElement;
import at.vintagestory.modelcreator.model.TextureEntry;

public class Exporter
{
	private Map<String, String> textureMap = new HashMap<String, String>();
	
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
			if (face.getTextureCode() == null || face.getTextureCode().equals("null")) continue;
			if (textureMap.containsKey(face.getTextureCode())) continue;
		
			TextureEntry tex = face.getTextureEntry();
			if (tex == null) continue;
			
			String textureBasePath = ModelCreator.prefs.get("texturePath", ".");
			
			String subPath = tex.getFilePath();
			if (subPath.contains(textureBasePath)) subPath = tex.getFilePath().substring(textureBasePath.length()  + 1);
			else {
				int index = tex.getFilePath().indexOf("assets"+File.separator+"textures"+File.separator);
				if (index>0) subPath = tex.getFilePath().substring(index + "assets/textures/".length());
			}
			subPath = subPath.replace('\\', '/').replace(".png", "");
			
			textureMap.put(face.getTextureCode(), subPath);
		}
		
		for (Element childelem : elem.ChildElements) {
			compileTextureList(childelem);
		}
	}
	
	

	private void writeComponents(BufferedWriter writer) throws IOException
	{
		writer.write("{");
		writer.newLine();
		
		writer.write(space(1) + "\"editor\": {");
		writer.newLine();
		if (project.backDropShape != null) {
			writer.write(space(2) + "\"backDropShape\": \"" + project.backDropShape + "\",");
			writer.newLine();
		}
		if (ModelCreator.rightTopPanel.tree.collapsedPaths.size() > 0) {
			writer.write(space(2) + "\"collapsedPaths\": \"" + ModelCreator.rightTopPanel.tree.saveCollapsedPaths() + "\",");
			writer.newLine();			
		}
		writer.write(space(2) + "\"allAngles\": " + project.AllAngles + ",");
		writer.newLine();
		writer.write(space(2) + "\"entityTextureMode\": " + project.EntityTextureMode + "");
		writer.newLine();	
		writer.write(space(1) + "},");
		writer.newLine();
		writer.write(space(1) + "\"textureWidth\": " + project.TextureWidth + ",");
		writer.newLine();
		writer.write(space(1) + "\"textureHeight\": " + project.TextureHeight + ",");
		writer.newLine();
		
		writeTextureSizes(writer);

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
		if (animation.version > 0) {
			writer.write(space(3) + "\"version\": \"" + animation.version + "\",");
			writer.newLine();
		}
		writer.write(space(3) + "\"code\": \"" + animation.getCode() + "\",");
		writer.newLine();
		writer.write(space(3) + "\"quantityframes\": " + animation.GetQuantityFrames() + ",");
		writer.newLine();
		
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


	

	private void writeKeyFrame(BufferedWriter writer, AnimationFrame keyframe) throws IOException
	{
		writer.write(space(4) + "{");
		writer.newLine();
		
		writer.write(space(5) + "\"frame\": " + keyframe.getFrameNumber() + ",");
		writer.newLine();
		writer.write(space(5) + "\"elements\": {");
		writer.newLine();
		
		
		List<AnimFrameElement> keyframeElementsFlat = new ArrayList<AnimFrameElement>();
		
		collapseKeyFrameElements(keyframe.Elements, keyframeElementsFlat);
		
		int k = 0;
		for (int i = 0; i < keyframeElementsFlat.size(); i++) {
			AnimFrameElement kElem = keyframeElementsFlat.get(i);
			if (!kElem.PositionSet && !kElem.RotationSet && !kElem.StretchSet) continue;

			if (k > 0) {
				writer.write(",");
				writer.newLine();
			}
			k++;
			
			writeKeyFrameElement(writer, kElem, 6);
		}
		
		writer.newLine();
		writer.write(space(5) + "}");
		writer.newLine();
		writer.write(space(4) + "}");
	}
	
	
	private void collapseKeyFrameElements(List<IDrawable> kfTree, List<AnimFrameElement> kfList)
	{
		for (int i = 0; i < kfTree.size(); i++) {
			AnimFrameElement kElem = (AnimFrameElement)kfTree.get(i);
			
			if (!kElem.IsUseless()) {
				kfList.add(kElem);
			}
			
			collapseKeyFrameElements(kElem.ChildElements, kfList);
		}		
	}
	
	double rndVal(double value) {
		return (double)Math.round(value * 1000d) / 1000d;
	}


	private void writeKeyFrameElement(BufferedWriter writer, AnimFrameElement kElem, int indent) throws IOException
	{
		writer.write(space(indent) + "\"" + kElem.AnimatedElement.getName() + "\": { ");
		
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
			writer.write("\"rotationX\": " + rndVal(kElem.getRotationX()));
			writer.write(", \"rotationY\": " + rndVal(kElem.getRotationY()));
			writer.write(", \"rotationZ\": " + rndVal(kElem.getRotationZ()));
			bla = true;
		}
		
		if (kElem.StretchSet) {
			if (bla) {
				writer.write(", ");
			}
			writer.write("\"stretchX\": " + kElem.getStretchX());
			writer.write(", \"stretchY\": " + kElem.getStretchY());
			writer.write(", \"stretchZ\": " + kElem.getStretchZ());
			bla=true;
		}
		
		if (kElem.RotShortestDistanceX) {
			if (bla) {
				writer.write(", ");
			}
			writer.write("\"rotShortestDistanceX\": true");
		}
		if (kElem.RotShortestDistanceY) {
			if (bla) {
				writer.write(", ");
			}
			writer.write("\"rotShortestDistanceY\": true");
		}
		if (kElem.RotShortestDistanceZ) {
			if (bla) {
				writer.write(", ");
			}
			writer.write("\"rotShortestDistanceZ\": true");
		}
		
		
		writer.write(" }");
	}
	

	private void writeTextures(BufferedWriter writer) throws IOException
	{
		writer.write(space(1) + "\"textures\": {");
		writer.newLine();
		int i = 0;
		
		LinkedHashMap<String, String> map = sortByValue(textureMap);
		
		for (String texturename : map.keySet())
		{
			writer.write(space(2) + "\"" + texturename + "\": \"" + textureMap.get(texturename) + "\"");
			if (i < textureMap.size() - 1)
			{
				writer.write(",");
			}
			writer.newLine();
			i++;
		}
		writer.write(space(1) + "},");
	}
	
	private static <K, V> LinkedHashMap<K, V> sortByValue(Map<K, V> map) {
	    List<Entry<K, V>> list = new LinkedList<>(map.entrySet());
	    Collections.sort(list, new Comparator<Object>() {
	        @SuppressWarnings("unchecked")
	        public int compare(Object o1, Object o2) {
	            return ((Comparable<V>) ((Map.Entry<K, V>) (o1)).getValue()).compareTo(((Map.Entry<K, V>) (o2)).getValue());
	        }
	    });

	    LinkedHashMap<K, V> result = new LinkedHashMap<>();
	    for (Iterator<Entry<K, V>> it = list.iterator(); it.hasNext();) {
	        Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
	        result.put(entry.getKey(), entry.getValue());
	    }

	    return result;
	}
	
	private void writeTextureSizes(BufferedWriter writer) throws IOException
	{
		writer.write(space(1) + "\"textureSizes\": {");
		writer.newLine();
		int i = 0;
		for (String texturename : project.TextureSizes.keySet())
		{
			writer.write(space(2) + "\"" + texturename + "\": [" + project.TextureSizes.get(texturename)[0] + "," + project.TextureSizes.get(texturename)[1] + "]");
			if (i < project.TextureSizes.size() - 1)
			{
				writer.write(",");
			}
			writer.newLine();
			i++;
		}
		writer.write(space(1) + "},");
	}

	
	private void writeElement(BufferedWriter writer, Element cuboid, int indentation) throws IOException
	{
		writer.newLine();
		writer.write(space(indentation) + "{");
		writer.newLine();
		
		indentation++;
		
		writer.write(space(indentation) + "\"name\": \"" + cuboid.getName() + "\",");
		writer.newLine();
		
		if (cuboid.stepparentName != null) {
			writer.write(space(indentation) + "\"stepParentName\": \"" + cuboid.stepparentName + "\",");
			writer.newLine();			
		}
		
		writeBounds(writer, cuboid, indentation);
		writer.newLine();
		if (!cuboid.isShaded())
		{
			writeShade(writer, cuboid, indentation);
			writer.newLine();
		}
		if (cuboid.isGradientShaded())
		{
			writeGradientShade(writer, cuboid, indentation);
			writer.newLine();
		}
		
		if (cuboid.getClimateColorMap() != null)
		{
			writer.write(space(indentation) + "\"climateColorMap\": \"" + cuboid.getClimateColorMap() + "\",");
			writer.newLine();
		}
		
		if (cuboid.getSeasonColorMap() != null)
		{
			writer.write(space(indentation) + "\"seasonColorMap\": \"" + cuboid.getSeasonColorMap() + "\",");
			writer.newLine();
		}
		
		if (cuboid.getZOffset() != 0) {
			writer.write(space(indentation) + "\"zOffset\": " + cuboid.getZOffset() + ",");
			writer.newLine();
		}
		
		if (cuboid.getRenderPass() > -1)
		{
			writer.write(space(indentation) + "\"renderPass\": " + cuboid.getRenderPass() + ",");
			writer.newLine();
		}

		if (cuboid.DisableRandomDrawOffset)
		{
			writer.write(space(indentation) + "\"disableRandomDrawOffset\": " + cuboid.DisableRandomDrawOffset + ",");
			writer.newLine();
		}

		
		
		if (project.EntityTextureMode) {
			if (cuboid.getUnwrapMode() > 0)
			{
				writer.write(space(indentation) + "\"unwrapMode\": " + cuboid.getUnwrapMode() + ",");
				writer.newLine();
			}
			if (cuboid.getUnwrapRotation() > 0)
			{
				writer.write(space(indentation) + "\"unwrapRotation\": " + cuboid.getUnwrapRotation() + ",");
				writer.newLine();
			}
			if (!cuboid.isAutoUnwrapEnabled())
			{
				writer.write(space(indentation) + "\"autoUnwrap\": " + cuboid.isAutoUnwrapEnabled() + ",");
				writer.newLine();
			}
			
			
			writer.write(space(indentation) + "\"uv\": [ " + d2s(cuboid.getTexUStart()) + ", " + d2s(cuboid.getTexVStart()) + " ],");
			writer.newLine();
		}
		
		if (!cuboid.getRenderInEditor()) {
			writer.write(space(indentation) + "\"renderInEditor\": " + cuboid.getRenderInEditor() + ",");
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
		writer.write(space(indentation) + "\"rotationX\": \"" + rndVal(point.getRotationX()) + "\",");
		writer.write("\"rotationY\": \"" + rndVal(point.getRotationY()) + "\",");
		writer.write("\"rotationZ\": \"" + rndVal(point.getRotationZ()) + "\"");
		
		indentation--;
		
		writer.newLine();
		writer.write(space(indentation) + "}");
	}

	private void writeBounds(BufferedWriter writer, Element cuboid, int indentation) throws IOException
	{
		writer.write(space(indentation) + "\"from\": [ " + d2s(cuboid.getStartX()) + ", " + d2s(cuboid.getStartY()) + ", " + d2s(cuboid.getStartZ()) + " ],");
		writer.newLine();
		writer.write(space(indentation) + "\"to\": [ " + d2s(cuboid.getStartX() + cuboid.getWidth()) + ", " + d2s(cuboid.getStartY() + cuboid.getHeight()) + ", " + d2s(cuboid.getStartZ() + cuboid.getDepth()) + " ],");
	}

	private void writeShade(BufferedWriter writer, Element cuboid, int indentation) throws IOException
	{
		writer.write(space(indentation) + "\"shade\": " + cuboid.isShaded() + ",");
		if (cuboid.isGradientShaded()) {
			writer.write(space(indentation) + "\"gradientShade\": " + cuboid.isGradientShaded() + ",");	
		}
	}


	private void writeGradientShade(BufferedWriter writer, Element cuboid, int indentation) throws IOException
	{
		writer.write(space(indentation) + "\"gradientShade\": " + cuboid.isGradientShaded() + ",");
	}

	private void writeRotation(BufferedWriter writer, Element cuboid, int indentation) throws IOException
	{
		writer.write(space(indentation) + "\"rotationOrigin\": [ " + d2s(cuboid.getOriginX()) + ", " + d2s(cuboid.getOriginY()) + ", " + d2s(cuboid.getOriginZ()) + " ],");
		writer.newLine();
		if (cuboid.getRotationX() != 0) { writer.write(space(indentation) + "\"rotationX\": " + rndVal(cuboid.getRotationX()) + ","); writer.newLine(); }
		if (cuboid.getRotationY() != 0) { writer.write(space(indentation) + "\"rotationY\": " + rndVal(cuboid.getRotationY()) + ","); writer.newLine(); }
		if (cuboid.getRotationZ() != 0) { writer.write(space(indentation) + "\"rotationZ\": " + rndVal(cuboid.getRotationZ()) + ","); writer.newLine(); }
	}

	private void writeFaces(BufferedWriter writer, Element cuboid, int indentation) throws IOException
	{
		writer.write(space(indentation) + "\"faces\": {");
		int facesWritten=0;
		writer.newLine();
		for (Face face : cuboid.getAllFaces())
		{
			if (!face.isEnabled()) continue;
			if (facesWritten > 0) {
				writer.write(",");
				writer.newLine();
			}
			
			facesWritten++;	
			writer.write(space(indentation + 1) + "\"" + Face.getFaceName(face.getSide()) + "\": { ");
			writer.write("\"texture\": \"#" + face.getTextureCode() + "\"");
			writer.write(", \"uv\": [ " + d2s(face.getStartU()) + ", " + d2s(face.getStartV()) + ", " + d2s(face.getEndU()) + ", " + d2s(face.getEndV()) + " ]");
			if (face.getRotation() > 0) {
				writer.write(", \"rotation\": " + (int) face.getRotation() * 90);
			}
			if (face.getGlow() > 0) {
				writer.write(", \"glow\": " + face.getGlow());
			}
			if (!face.isAutoUVEnabled()) {
				writer.write(", \"autoUv\": false");
			}
			if (!face.isSnapUvEnabled()) {
				writer.write(", \"snapUv\": false");
			}
			if (face.WindModes != null) {
				writer.write(", \"windMode\": ["+face.WindModes[0]+","+face.WindModes[1]+","+face.WindModes[2]+","+face.WindModes[3]+"]");
			}
			if (face.WindData != null) {
				writer.write(", \"windData\": ["+face.WindData[0]+","+face.WindData[1]+","+face.WindData[2]+","+face.WindData[3]+"]");
			}			
			if (face.reflectiveMode > 0) {
				writer.write(", \"reflectiveMode\": " + face.reflectiveMode);
			}
			
			writer.write(" }");
		}
				
		if (facesWritten > 0) writer.newLine();
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
