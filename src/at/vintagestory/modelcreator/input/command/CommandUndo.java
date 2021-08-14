package at.vintagestory.modelcreator.input.command;

import at.vintagestory.modelcreator.ModelCreator;

public class CommandUndo implements ProjectCommand
{
	@Override
	public void execute()
	{
		ModelCreator.changeHistory.Undo();
	}
}
