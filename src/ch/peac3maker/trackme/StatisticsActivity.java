package ch.peac3maker.trackme;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TableLayout;
import android.widget.TextView;

public class StatisticsActivity extends Activity {
	
	private TrackBroadCastReceiver dataUpdateReceiver;
	private long lastPaint = -1;
	private int paintDifference = 20000;
	private long trackID;

	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.rowlayout);
    	TableLayout table = (TableLayout) findViewById(R.id.table2);  
    	long trackID = getIntent().getLongExtra("Selected_Track", -1);
    	if(trackID == -1){
    		setResult(RESULT_OK, new Intent());
			finish();
    	}
    	getNewValues(trackID);
    }
	
	private void getNewValues(long trackid){
		GPointDataSource source = new GPointDataSource(getApplicationContext());
		source.open();
		Track t = source.getTrackByID(trackid);
		int count = source.GetGPointCount(trackid);
		source.close();
		TextView tracklength = (TextView) findViewById(R.id.tracklength);
		TextView avgspeed = (TextView) findViewById(R.id.avgspeed);
		TextView curavgspeed = (TextView) findViewById(R.id.curavgspeed);
		TextView diffheight = (TextView) findViewById(R.id.diffheight);	
		TextView lowalt = (TextView) findViewById(R.id.lowalt);	
		TextView highalt = (TextView) findViewById(R.id.highalt);
		TextView pointsindb = (TextView) findViewById(R.id.pointsindb);
		tracklength.setText(String.valueOf(t.getTotalDistance()));		
		avgspeed.setText(String.valueOf(t.getAvgSpeed()));
		curavgspeed.setText(String.valueOf(t.getCurSpeed()));
		diffheight.setText(String.valueOf(t.getTotalHeightDistance()));
		lowalt.setText(String.valueOf(t.getLowestAlt()));
		highalt.setText(String.valueOf(t.getHighestAlt()));
		pointsindb.setText(String.valueOf(count));
	}
	
    @Override
   protected void onResume() {
    	super.onResume();
    	if (dataUpdateReceiver == null) dataUpdateReceiver = new TrackBroadCastReceiver();
    	dataUpdateReceiver.addDataReceivedListener(new DataReceivedEventListener() {
			
			@Override
			public void onDataReceived(DataReceivedEvent evt) {
				// TODO Auto-generated method stub
				GPointDataSource datasource = new GPointDataSource(getApplicationContext());
		  		datasource.open();		  		
		  		long trackid = datasource.getNewestTrackID();
		  		datasource.close();
		  		if(trackid != -1 && (lastPaint == -1 || new Date().getTime() - lastPaint > paintDifference)){
		  			lastPaint = new Date().getTime();		  			
		  			getNewValues(trackid);
		  		}
			}
		});    	    
    	
    	IntentFilter intentFilter = new IntentFilter(TrackBroadCastReceiver.TRACK_ID_RECEIVER);    	
    	registerReceiver(dataUpdateReceiver, intentFilter); 
    	
    };
    
  //Unregister broadcastreceiver, so we won't repaint the maps activity if it is paused.
    @Override
    protected void onPause() {
    	super.onPause();
    	if (dataUpdateReceiver != null) unregisterReceiver(dataUpdateReceiver);
    }; 
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent();
		intent.putExtra("Selected_Track", trackID);
		setResult(RESULT_OK, intent);
		finish();
	}	
}
