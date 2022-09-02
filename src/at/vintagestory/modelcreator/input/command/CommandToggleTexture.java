package at.vintagestory.modelcreator.input.command;

import at.vintagestory.modelcreator.ModelCreator;

public class CommandToggleTexture implements ProjectCommand
{

	@Override
	public void execute()
	{
		ModelCreator.renderTexture = !ModelCreator.renderTexture;
		ModelCreator.updateValues(null);
	}

}
