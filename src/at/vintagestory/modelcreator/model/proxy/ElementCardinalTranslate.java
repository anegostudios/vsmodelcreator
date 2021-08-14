package at.vintagestory.modelcreator.model.proxy;

import at.vintagestory.modelcreator.model.Element;

public class ElementCardinalTranslate
{
	private Element element;
	
	public ElementCardinalTranslate(Element element) {
		this.element = element;
	}
	
	public void cardinalTranslate(double alpha, double[][] cardinals)
	{
		double[] translation;
		
		if(alpha < 45 || alpha > 315) // South
			translation = cardinals[2];
		else if(alpha > 45 && alpha < 135) // West
			translation = cardinals[3];
		else if(alpha > 135 && alpha < 225) // North
			translation = cardinals[0];
		else // East
			translation = cardinals[1];
		
		// Apply translations
		this.element.setStartX(this.element.getStartX() + translation[0]);
		this.element.setOriginX(this.element.getOriginX() + translation[0]);
		
		this.element.setStartY(this.element.getStartY() + translation[1]);
		this.element.setOriginY(this.element.getOriginY() + translation[1]);
		
		this.element.setStartZ(this.element.getStartZ() + translation[2]);
		this.element.setOriginZ(this.element.getOriginZ() + translation[2]);
	}
}
