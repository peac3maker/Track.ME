package ch.peac3maker.trackme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//Custom BroadCastReceiver that contains a DataReceived Event Listener
public class TrackBroadCastReceiver extends BroadcastReceiver {	
	public static final String TRACK_ID_RECEIVER = "Track_ID";
	
	protected DataReceivedEventListener listener;
	
	// This methods allows classes to register for MyEvents
    public void addDataReceivedListener(DataReceivedEventListener listener) {
        this.listener = listener;
    }
    
	@Override	
	   public void onReceive(Context context, Intent intent) 
	   {    
	    String action = intent.getAction();
	    	if(action.equals(TRACK_ID_RECEIVER)){	    		
	    		this.listener.onDataReceived(new DataReceivedEvent(this));
	    	}
	   }

	
}
