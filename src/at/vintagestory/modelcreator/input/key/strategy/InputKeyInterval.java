package at.vintagestory.modelcreator.input.key.strategy;

import at.vintagestory.modelcreator.input.key.InputKeyEvent;

public class InputKeyInterval implements StrategyInputKey
{
	private int keyCode;
	private long nanoseconds;
	private StrategyInputKey strategy;
	
	private boolean hasPreviousNanoseconds;
	private long previousNanoseconds;
	
	public InputKeyInterval(int keyCode, int milliseconds, StrategyInputKey strategy) {
		this.keyCode = keyCode;
		this.nanoseconds = milliseconds * 1000000;
		this.strategy = strategy;
		this.previousNanoseconds = 0L;
		this.hasPreviousNanoseconds = false;
	}
	
	@Override
	public void execute(InputKeyEvent event)
	{
		long currentNanoseconds = event.getNanoSeconds();
		if(event.keyCode() == this.keyCode && ((currentNanoseconds - previousNanoseconds) > nanoseconds) || !hasPreviousNanoseconds) {
			this.previousNanoseconds = currentNanoseconds;
			this.hasPreviousNanoseconds = true;
			strategy.execute(event);
		}
	}
}
