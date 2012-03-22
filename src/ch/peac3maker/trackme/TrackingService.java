package ch.peac3maker.trackme;

import java.util.ArrayList;
import java.util.Date;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class TrackingService extends Service {
	private LocationManager lm;
	private LocationListener locationListener;
	private Location lastLoc = null;
	private int totalLength;
	private long trackid;
	private Date start = null;
	GPointDataSource datasource;

	public class LocalBinder extends Binder {
		TrackingService getService() {
            return TrackingService.this;
        }
    }

	@Override
    public void onCreate() {		
		
    }
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        start = new Date();
        datasource = new GPointDataSource(this);
    	datasource.open();
    	trackid = datasource.createTrack(start,totalLength);
    	datasource.close();    
        return START_STICKY;
    }
	
	private void stopLogging(){
    	lm.removeUpdates(locationListener); 
    	locationListener = null;    	    
    }
	
	private void startLogging(){
    	lm = (LocationManager)
    	    	getSystemService(Context.LOCATION_SERVICE);
    	    	locationListener = new MyLocationListener();
    	    	lm.requestLocationUpdates(
    	    	LocationManager.GPS_PROVIDER,
    	    	0,
    	    	0,
    	    	locationListener); 
    	    	
    }
	
	@Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
	
	// This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    
    private class MyLocationListener implements LocationListener
	{ 
    	
    	public MyLocationListener(){
    		Handler mHandler = new Handler();
    	}
	@Override
	public void onLocationChanged(Location loc) {
	if (loc != null) {
		float distance = 0;
		if(lastLoc!= null){
			distance = lastLoc.distanceTo(loc);
			totalLength += distance;
		}
		lastLoc = loc;

	GeoPoint p = new GeoPoint(
	(int) (loc.getLatitude() * 1E6),
	(int) (loc.getLongitude() * 1E6));	 
 	datasource.open();
 	datasource.createGeoPoint(trackid, p);
 	datasource.close(); 		
	}
	}
	@Override
	public void onProviderDisabled(String provider) {
	// TODO Auto-generated method stub
	}
	@Override
	public void onProviderEnabled(String provider) {		
	}
	@Override
	public void onStatusChanged(String provider, int status,
	Bundle extras) {
	// TODO Auto-generated method stub
	}


	}

}
