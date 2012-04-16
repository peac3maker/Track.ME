package ch.peac3maker.trackme;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

public class GeoDataTestActivity extends MapActivity {		
	private MapView mapView;
	private MapController mc;
	private TrackBroadCastReceiver dataUpdateReceiver;
	private long lastPaint = -1;
	private long trackid = -1;
	private int paintDifference = 20000;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);    	    	
    }
    
    //Register our broadcastreceiver and add our DataReceivedListener to it.
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
		  		trackid = datasource.getNewestTrackID();
		  		datasource.close();
		  		if(trackid != -1 && (lastPaint == -1 || new Date().getTime() - lastPaint > paintDifference)){
		  			lastPaint = new Date().getTime();
		  			rePaintPointsOfTrack(trackid);		  			
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
    
    //Create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
    
    //Start Logging by starting TrackingService.
    private void startLogging(){    	
    	startService(new Intent(getApplicationContext(), TrackingService.class));
    }
    
    //Stop logging by stopping the TrackingService
    private void stopLogging(){    	
    	stopService(new Intent(getApplicationContext(), TrackingService.class));
    }
    
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return true;
	}
	
	//Extended MapOverlay that paints all the GeoPoints when draw is called.
	public class MapOverlay extends com.google.android.maps.Overlay {
		public List<GeoPoint> points = new ArrayList<GeoPoint>();  
		
	      protected MapOverlay(long id){
	    	  GPointDataSource datasource = new GPointDataSource(getApplicationContext());
		   		datasource.open();
		   		points = datasource.getAllGeoPointsTrackID(id);
		   		datasource.close();
	      }
	      @Override
	      public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
	            long when) {
	         super.draw(canvas, mapView, shadow);
	         
	        
	   		
	         Paint paint;
	         paint = new Paint();
	         paint.setColor(Color.RED);
	         paint.setAntiAlias(true);
	         paint.setStyle(Style.STROKE);
	         paint.setStrokeWidth(2);
	         
	         Projection projection = mapView.getProjection();	         
	         GeoPoint first = null;	         
	         for(GeoPoint pt : points)
	         {
	        	 if(first != null){
	        		 Point pt1 = new Point();
	    	         Point pt2 = new Point();
	        		 projection.toPixels(first, pt1);
	        		 projection.toPixels(pt, pt2);
	        		 canvas.drawLine(pt1.x, pt1.y, pt2.x, pt2.y, paint);
	        		 
	        	 }
	        	 first =pt;
	         }	         
	         
	         return true;
	      }
	   }
	
	//Handles optionsitem selected event
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.start:	        	
	            startLogging();	            	        	
	            return true;
	        case R.id.stop:	        	
	            stopLogging();
	            return true;
	        case R.id.load:
	        	Intent myIntent = new Intent(getApplicationContext(), TrackListActivity.class);	        	
                startActivityForResult(myIntent, 0);
                return true;
	        case R.id.stats:
	        	Intent statsIntend = new Intent(getApplicationContext(), StatisticsActivity.class);	
	        	statsIntend.putExtra("Selected_Track", trackid);
                startActivityForResult(statsIntend, 0);
                return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	//Called when this activity gets a result from another activity
	//In this case it will create an overlay for the selected track in the TrackListActivity
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
	  super.onActivityResult(requestCode, resultCode, data); 	  
	  if(data!= null && data.hasExtra("Selected_Track")){
	  long selected = data.getLongExtra("Selected_Track", -1);
	  if(selected != -1){
		  trackid = selected;
		  rePaintPointsOfTrack(selected);
	  }
	  }
	}
	
	//Recreates the custom map overlay with the points of a trackid
	private void rePaintPointsOfTrack(long id){		
  		  		
  		mapView = (MapView) findViewById(R.id.mapview1);
  		mc = mapView.getController();  	   
  	    mc.setZoom(16);
  		MapOverlay mapOvlay = new MapOverlay(id);  		
  	    mapView.getOverlays().add(mapOvlay);
  	    if(mapOvlay.points.size() > 0){
  	    mc.animateTo(mapOvlay.points.get(0));
  	    }
	}
		
}