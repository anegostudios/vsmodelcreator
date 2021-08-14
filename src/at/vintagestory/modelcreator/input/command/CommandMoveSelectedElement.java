package at.vintagestory.modelcreator.input.command;

import at.vintagestory.modelcreator.Camera;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.proxy.ElementCardinalTranslate;

public class CommandMoveSelectedElement implements ProjectCommand
{
	private ModelCreator creator;
	private double[][] matrix;
	
	public CommandMoveSelectedElement(ModelCreator creator, double[][] matrix) {
		this.creator = creator;
		this.matrix = matrix;
	}

	@Override
	public void execute()
	{
		Camera camera = creator.getCamera();
		Element element = ModelCreator.rightTopPanel.getCurrentElement();
		
		if(element != null) {
			ModelCreator.currentProject.selectElement(element);
			
			double alpha = camera.getRY() < 0 ? 360 + camera.getRY() : camera.getRY();
			ElementCardinalTranslate elementCard = new ElementCardinalTranslate(element);
			
			// Apply cardinal translation
			elementCard.cardinalTranslate(alpha, matrix);
		}
		
	}

}
