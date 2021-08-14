package at.vintagestory.modelcreator.input.listener;

import at.vintagestory.modelcreator.input.InputListener;
import at.vintagestory.modelcreator.input.command.ProjectCommand;
import at.vintagestory.modelcreator.input.key.InputKeyEvent;
import at.vintagestory.modelcreator.input.key.strategy.FactoryInputKeyStrategy;
import at.vintagestory.modelcreator.input.key.strategy.StrategyInputKey;

public class ListenerKeyPressInterval implements InputListener
{
	private StrategyInputKey strategy;
	
	public ListenerKeyPressInterval(ProjectCommand command, int key) {
		this(command, null, key);
	}
	
	public ListenerKeyPressInterval(ProjectCommand command, int controlKey, int key) {
		this(command, Integer.valueOf(controlKey), key);
	}
	
	private ListenerKeyPressInterval(ProjectCommand command, Integer controlKey, int key) {
		this.strategy = FactoryInputKeyStrategy.CreateKeyInterval50(controlKey, key, command);
	}
	
	@Override
	public void update(InputKeyEvent event)
	{
		this.strategy.execute(event);
	}
}
