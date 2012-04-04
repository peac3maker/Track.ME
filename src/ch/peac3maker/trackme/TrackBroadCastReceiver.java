package ch.peac3maker.trackme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TrackBroadCastReceiver extends BroadcastReceiver {
	public long trackid = -1;
	public static final String TRACK_ID_RECEIVER = "Track_ID";
	@Override	
	   public void onReceive(Context context, Intent intent) 
	   {    
	    String action = intent.getAction();
	    	if(action.equals(R.string.point_received)){
	    		trackid = intent.getLongExtra(TRACK_ID_RECEIVER, -1);
	    	}
	       /*if(action.equalsIgnoreCase(IMService.NEW_MESSAGE)){    
	          Bundle extra = intent.getExtras();
	          String username = extra.getString(FriendInfo.USERNAME);   
	          String message = extra.getString(FriendInfo.MESSAGE);
	          String who = extra.getString("who");
	         }*/
	   }

	
}
