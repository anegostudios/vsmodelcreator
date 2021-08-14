package at.vintagestory.modelcreator.input.listener;

import at.vintagestory.modelcreator.input.InputListener;
import at.vintagestory.modelcreator.input.command.ProjectCommand;
import at.vintagestory.modelcreator.input.key.InputKeyEvent;
import at.vintagestory.modelcreator.input.key.strategy.StrategyInputKey;
import at.vintagestory.modelcreator.input.key.strategy.FactoryInputKeyStrategy;

public class ListenerKeyPressOnce implements InputListener
{
	private StrategyInputKey strategy;
	
	public ListenerKeyPressOnce(ProjectCommand command, int key) {
		this(command, null, key);
	}
	
	public ListenerKeyPressOnce(ProjectCommand command, int controlKey, int key) {
		this(command, Integer.valueOf(controlKey), key);
	}
	
	private ListenerKeyPressOnce(ProjectCommand command, Integer controlKey, int key) {
		this.strategy = FactoryInputKeyStrategy.CreateKeyOnce(controlKey, key, command);
	}
	
	@Override
	public void update(InputKeyEvent event)
	{
		this.strategy.execute(event);
	}
}
