package at.vintagestory.modelcreator.input.key.strategy;

import at.vintagestory.modelcreator.input.key.InputKeyEvent;

public class InputKeyOnce implements StrategyInputKey
{
	private StrategyInputKey strategy;
	
	public InputKeyOnce(StrategyInputKey strategy) {
		this.strategy = strategy;
	}
	
	@Override
	public void execute(InputKeyEvent event) {
		if(!event.repeatEvent())
			strategy.execute(event);
	}
}
