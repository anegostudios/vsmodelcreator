package at.vintagestory.modelcreator.input.command;

import at.vintagestory.modelcreator.ModelCreator;

public class CommandReloadTexture implements ProjectCommand
{
	@Override
	public void execute()
	{
		ModelCreator.currentProject.reloadTextures(ModelCreator.Instance);
	}
}
