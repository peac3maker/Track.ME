package ch.peac3maker.trackme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TrackBroadCastReceiver extends BroadcastReceiver {
	@Override
	   public void onReceive(Context context, Intent intent) 
	   {    
	    String action = intent.getAction();
	       /*if(action.equalsIgnoreCase(IMService.NEW_MESSAGE)){    
	          Bundle extra = intent.getExtras();
	          String username = extra.getString(FriendInfo.USERNAME);   
	          String message = extra.getString(FriendInfo.MESSAGE);
	          String who = extra.getString("who");
	         }*/
	   }

	
}
