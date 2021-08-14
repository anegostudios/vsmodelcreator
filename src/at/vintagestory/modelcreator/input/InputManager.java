package at.vintagestory.modelcreator.input;

import java.util.ArrayList;
import java.util.List;

import at.vintagestory.modelcreator.input.key.InputKeyEvent;

public class InputManager
{
	
	private List<InputListener> listeners = new ArrayList<InputListener>();
	
	public InputManager() {
		
	}
	
	public void subscribe(InputListener listener) {
		listeners.add(listener);
	}
	
	public void unsubscribe(InputListener listener) {
		listeners.remove(listener);
	}
	
	public void notifyListeners(InputKeyEvent event) {
		for(InputListener listener : listeners) {
			listener.update(event);
		}
	}

}
