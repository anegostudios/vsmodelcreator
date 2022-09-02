package at.vintagestory.modelcreator.input.command;

import org.lwjgl.input.Keyboard;

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
			
			double[][] mat = matrix;
			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) { 
				mat = new double[mat.length][];
				for (int i = 0; i < mat.length; i++) {
					mat[i] = new double[matrix[i].length];
					for (int j = 0; j < matrix[i].length; j++) {
						mat[i][j] = matrix[i][j] * 0.1;	
					}
				}
			}
			
			// Apply cardinal translation
			elementCard.cardinalTranslate(alpha, mat);
		}
	}

}
