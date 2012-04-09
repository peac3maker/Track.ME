package ch.peac3maker.trackme;

import java.util.EventListener;

public interface DataReceivedEventListener extends EventListener {
	public void onDataReceived(DataReceivedEvent evt);
}
