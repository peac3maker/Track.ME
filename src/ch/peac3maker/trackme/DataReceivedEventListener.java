package ch.peac3maker.trackme;

import java.util.EventListener;

//Declare the listener class. It must extend EventListener.
//A class must implement this interface to get MyEvents.
public interface DataReceivedEventListener extends EventListener {
	public void onDataReceived(DataReceivedEvent evt);
}
