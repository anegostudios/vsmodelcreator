package at.vintagestory.modelcreator.input.command;

import javax.swing.SwingUtilities;

import at.vintagestory.modelcreator.ModelCreator;

public class CommandInvokeLater implements ProjectCommand
{
	private ProjectCommand command;
	
	public CommandInvokeLater(ProjectCommand command) {
		this.command = command;
	}

	@Override
	public void execute()
	{
		SwingUtilities.invokeLater(() -> {
			command.execute();
		});
	}
}
