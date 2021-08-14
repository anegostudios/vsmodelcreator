package at.vintagestory.modelcreator.input.key.strategy;

import at.vintagestory.modelcreator.input.key.InputKeyEvent;

public class InputKeyControl implements StrategyInputKey
{
	private StrategyInputKey strategy;
	private int keyCode;
	private boolean keyPressed;
	
	public InputKeyControl(int keyCode, StrategyInputKey strategy) {
		this.strategy = strategy;
		this.keyCode = keyCode;
		this.keyPressed = false;
	}

	@Override
	public void execute(InputKeyEvent event)
	{
		// Key is being pressed down
		if(event.keyCode() == this.keyCode)
			keyPressed = event.pressed();
		
		// Execute related event
		if(keyPressed)
			strategy.execute(event);
			
	}

}
