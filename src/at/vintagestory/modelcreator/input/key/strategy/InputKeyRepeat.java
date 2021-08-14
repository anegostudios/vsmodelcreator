package at.vintagestory.modelcreator.input.key.strategy;

import at.vintagestory.modelcreator.input.command.ProjectCommand;
import at.vintagestory.modelcreator.input.key.InputKeyEvent;

public class InputKeyRepeat implements StrategyInputKey
{
	private int keyCode;
	private ProjectCommand command;
	
	public InputKeyRepeat(int keyCode, ProjectCommand command) {
		this.keyCode = keyCode;
		this.command = command;
	}
	
	@Override
	public void execute(InputKeyEvent event) {
		
		if(event.keyCode() == keyCode && event.pressed())
			command.execute();
		
	}
}
