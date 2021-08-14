package at.vintagestory.modelcreator.input.key.strategy;

import at.vintagestory.modelcreator.input.command.ProjectCommand;

public class FactoryInputKeyStrategy
{
	
	public static StrategyInputKey CreateKeyInterval25(int keyCode, ProjectCommand command)
	{
		return CreateKeyInterval25(null, keyCode, command);
	}
	
	public static StrategyInputKey CreateKeyInterval25(Integer controlKeyCode, int keyCode, ProjectCommand command) {
		if (controlKeyCode == null)
			return new InputKeyInterval(keyCode, 25, CreateKeyRepeat(keyCode, command));
		return new InputKeyControl(controlKeyCode.intValue(), new InputKeyInterval(keyCode, 100, CreateKeyRepeat(keyCode, command)));
	}
	
	public static StrategyInputKey CreateKeyOnce(int keyCode, ProjectCommand command) {
		return CreateKeyOnce(null, keyCode, command);
	}
	
	public static StrategyInputKey CreateKeyOnce(Integer controlKeyCode, int keyCode, ProjectCommand command) {
		if (controlKeyCode == null)
			return new InputKeyOnce(CreateKeyRepeat(keyCode, command));
		return new InputKeyControl(controlKeyCode.intValue(), new InputKeyOnce(CreateKeyRepeat(keyCode, command)));
	}
	
	public static StrategyInputKey CreateKeyRepeat(int keyCode, ProjectCommand command) {
		return CreateKeyRepeat(null, keyCode, command);
	}
	
	public static StrategyInputKey CreateKeyRepeat(Integer controlKeyCode, int keyCode, ProjectCommand command) {
		if(controlKeyCode == null)
			return new InputKeyRepeat(keyCode, command);
		return new InputKeyControl(controlKeyCode, CreateKeyRepeat(keyCode, command));
	}
}
