package ch.peac3maker.trackme;

import java.util.Date;

import com.google.android.maps.GeoPoint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

//Service that is used to do the tracking
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

	//On Start a new Track is created and the startlogging method is called
	//Service returns start_sticky so it will keep on running until stopped
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		start = new Date();
		datasource = new GPointDataSource(this);
		datasource.open();
		trackid = datasource.createTrack(start, totalLength);
		datasource.close();
		startLogging();
		return START_STICKY;		
	}
	
	//OnDestroy event, logging is stopped and service destroyed
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		stopLogging();
		super.onDestroy();
		
	}

	//removes the locationlistener
	private void stopLogging() {
		lm.removeUpdates(locationListener);
		locationListener = null;
		datasource = new GPointDataSource(getApplicationContext());
		datasource.open();
		datasource.UpdateTrack(trackid, totalLength);
		datasource.close();
	}

	//starts a custom locationlistener that sends a broadcast as soon as it receives a new location.
	private void startLogging() {
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		MyLocationListener listen = new MyLocationListener();
		
		listen.addMyEventListener(new DataReceivedEventListener() {
			
			@Override
			public void onDataReceived(DataReceivedEvent evt) {
 				Intent intent = new Intent(TrackBroadCastReceiver.TRACK_ID_RECEIVER);				
				sendBroadcast(intent);
			}
		});
		locationListener = listen;
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				locationListener);

	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	//custom locationlistener saves each geopoint receivedd to the database
	private class MyLocationListener implements LocationListener {
		
		protected DataReceivedEventListener listener;
		
		// This methods allows classes to register for DataReceived Events
	    public void addMyEventListener(DataReceivedEventListener listener) {
	        this.listener = listener;
	    }

		@Override
		public void onLocationChanged(Location loc) {
			if (loc != null) {
				float distance = 0;
				if (lastLoc != null) {
					distance = lastLoc.distanceTo(loc);
					totalLength += distance;
				}
				lastLoc = loc;
				GeoPoint p = new GeoPoint((int) (loc.getLatitude() * 1E6),
						(int) (loc.getLongitude() * 1E6));
				datasource.open();
				datasource.createGeoPoint(trackid, p);
				datasource.close();
				this.listener.onDataReceived(new DataReceivedEvent(this));
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
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}

	}

}
