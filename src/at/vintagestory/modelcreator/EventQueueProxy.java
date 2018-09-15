package at.vintagestory.modelcreator;

import java.awt.AWTEvent;
import java.awt.EventQueue;

import javax.swing.JOptionPane;

public class EventQueueProxy extends EventQueue {
	 
    protected void dispatchEvent(AWTEvent newEvent) {
        try {
            super.dispatchEvent(newEvent);
        } catch (Throwable t) {
            t.printStackTrace();
			JOptionPane.showMessageDialog(
				null, 
				"UI thread crashed, please make a screenshot of this message and report it, save if you can, then restart the Editor. Sorry about that :(\nException: " + t + "\n" + ModelCreator.stackTraceToString(t), 
				"Crash!", 
				JOptionPane.ERROR_MESSAGE, 
				null
			);
            
        }
    }
}