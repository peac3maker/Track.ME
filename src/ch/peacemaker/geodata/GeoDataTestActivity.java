package ch.peacemaker.geodata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class GeoDataTestActivity extends MapActivity {
	private LocationManager lm;
	private LocationListener locationListener;
	private MapView mapView;
	private MapController mc;
	private List<GeoPoint> points = new ArrayList<GeoPoint>();
	private Location lastLoc = null;
	private int totalLength;
	private Date start = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);    	
    	//---use the LocationManager class to obtain GPS locations---
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
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
    	    	mapView = (MapView) findViewById(R.id.mapview1);
    	    	points = new ArrayList<GeoPoint>();
    	    	lastLoc = null;
    	    	mc = mapView.getController();
    }
    
    private void stopLogging(){
    	lm.removeUpdates(locationListener); 
    	locationListener = null;
    	double speed = Calculator.GetCurrentAvgSpeedKMH(points, start, new Date(),totalLength);
    	Toast.makeText(getBaseContext(),
    			"Total Distance Taken: "+ totalLength+" current avg speed:"+ speed,
    			Toast.LENGTH_LONG).show();
    	GPointDataSource datasource = new GPointDataSource(this);
    	datasource.open();
    	datasource.createTrack(points,start,totalLength);
    	datasource.close();
    }
    
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private class MyLocationListener implements LocationListener
	{
	@Override
	public void onLocationChanged(Location loc) {
	if (loc != null) {
		float distance = 0;
		if(lastLoc!= null){
			distance = lastLoc.distanceTo(loc);
			totalLength += distance;
		}
		lastLoc = loc;
	/*Toast.makeText(getBaseContext(),
	"Location changed : Lat: " + loc.getLatitude() +
	" Lng: " + loc.getLongitude()+ "Distance To Last: "+ distance,
	Toast.LENGTH_SHORT).show();*/
	GeoPoint p = new GeoPoint(
	(int) (loc.getLatitude() * 1E6),
	(int) (loc.getLongitude() * 1E6));
	/*mc.animateTo(p);
	mc.setZoom(16);*/
	if(points.size() == 0){
		mc.animateTo(p);
	}
	MapOverlay mapOvlay = new MapOverlay(p);
    mapView.getOverlays().add(mapOvlay);    

	
	mapView.invalidate();
	}
	}
	@Override
	public void onProviderDisabled(String provider) {
	// TODO Auto-generated method stub
	}
	@Override
	public void onProviderEnabled(String provider) {
	// TODO Auto-generated method stub
	}
	@Override
	public void onStatusChanged(String provider, int status,
	Bundle extras) {
	// TODO Auto-generated method stub
	}
	}
	
	public class MapOverlay extends com.google.android.maps.Overlay {
		
	      //private GeoPoint mGpt1;
	      //private GeoPoint mGpt2;

	      protected MapOverlay(GeoPoint gp1) {
	         points.add(gp1);
	      }
	      
	      protected MapOverlay(){
	      
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
	         /*if(points.size() > 1){
	         first =  points.get(points.size()-2);
	         GeoPoint second = points.get(points.size()-1);
	         Point pt1 = new Point();
	         Point pt2 = new Point();
    		 projection.toPixels(first, pt1);
    		 projection.toPixels(second, pt2);
    		 canvas.drawLine(pt1.x, pt1.y, pt2.x, pt2.y, paint);    		 
	         }*/
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.start:
	        	if(locationListener == null){
	            startLogging();
	            start = new Date();
	        	}
	            return true;
	        case R.id.stop:
	        	if(locationListener != null)
	            stopLogging();
	            return true;
	        case R.id.load:
	        	Intent myIntent = new Intent(this, TrackListActivity.class);
                startActivityForResult(myIntent, 0);
                return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
	  super.onActivityResult(requestCode, resultCode, data); 
	  long selected = data.getLongExtra("Selected_Track", -1);
	  if(selected != -1){
	  GPointDataSource datasource = new GPointDataSource(this);
  		datasource.open();
  		points = datasource.getAllGeoPointsTrackID(selected);
  		datasource.close();
  		MapOverlay mapOvlay = new MapOverlay();
  		mapView = (MapView) findViewById(R.id.mapview1);
  	    mapView.getOverlays().add(mapOvlay);
  	    mc.animateTo(points.get(0));
	  }
	}	

	
	private void PreloadTracksAndCreateSubMenu(MenuItem item){
		GPointDataSource datasource = new GPointDataSource(this);
    	datasource.open();
    	Menu menu = item.getSubMenu();
    	List<Track> track = datasource.GetListOfTracks();
    	ArrayAdapter<Track> adapter = new ArrayAdapter<Track>(this,
				android.R.layout.simple_list_item_1, track);		
    	datasource.close();	
	}

}