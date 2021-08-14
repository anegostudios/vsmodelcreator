package at.vintagestory.modelcreator.input.command;

import java.io.File;

import at.vintagestory.modelcreator.ModelCreator;

public class CommandSave implements ProjectCommand
{

	private ModelCreator creator;
	
	public CommandSave(ModelCreator creator) {
		this.creator = creator;
	}
	
	@Override
	public void execute()
	{
		if (ModelCreator.currentProject.filePath == null)
			this.creator.SaveProjectAs();
		else
			this.creator.SaveProject(new File(ModelCreator.currentProject.filePath));
	}

}
