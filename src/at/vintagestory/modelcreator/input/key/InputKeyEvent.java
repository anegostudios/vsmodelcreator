package at.vintagestory.modelcreator.input.key;

public class InputKeyEvent
{
	
	private int keyCode;
	private char keyChar;
	boolean keyState;
	boolean repeatEvent;
	long nanoseconds;
	
	public InputKeyEvent(int keyCode, char keyChar, boolean keyState, boolean repeatEvent, long nanoseconds)
	{
		this.keyCode = keyCode;
		this.keyChar = keyChar;
		this.keyState = keyState;
		this.repeatEvent = repeatEvent;
		this.nanoseconds = nanoseconds;
	}
	
	public int keyCode() {
		return this.keyCode;
	}
	
	public char keyChar() {
		return this.keyChar;
	}
	
	public boolean pressed() {
		return this.keyState;
	}
	
	public boolean released() {
		return !this.keyState;
	}
	
	public boolean repeatEvent() {
		return repeatEvent;
	}
	
	public long getNanoSeconds() {
		return this.nanoseconds;
	}
}
