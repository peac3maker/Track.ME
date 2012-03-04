package ch.peac3maker.trackme;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.maps.GeoPoint;

public class GPointDataSource {


		// Database fields
		private SQLiteDatabase database;
		private MySQLiteHelper dbHelper;
		private String[] allColumns = { MySQLiteHelper.GPOINT_ID,
				MySQLiteHelper.GPOINT_lATITUTE, MySQLiteHelper.GPOINT_LONGITUDE };

		public GPointDataSource(Context context) {
			dbHelper = new MySQLiteHelper(context);
		}

		public void open() throws SQLException {
			database = dbHelper.getWritableDatabase();
		}

		public void close() {
			dbHelper.close();
		}

		public GPoint createGPoint(GeoPoint point) {
			ContentValues values = new ContentValues();
			values.put(MySQLiteHelper.GPOINT_lATITUTE, point.getLatitudeE6());
			values.put(MySQLiteHelper.GPOINT_LONGITUDE, point.getLongitudeE6());									
			long trackId = database.insert(MySQLiteHelper.TABLE_TRACK, null,
					null);
			values.put(MySQLiteHelper.GPOINT_TRACK, trackId);
			long insertId = database.insert(MySQLiteHelper.TABLE_GPOINTS, null,
					values);
			// To show how to query
			Cursor cursor = database.query(MySQLiteHelper.TABLE_GPOINTS,
					allColumns, MySQLiteHelper.GPOINT_ID + " = " + insertId, null,
					null, null, null);
			cursor.moveToFirst();
			return cursorToGPoint(cursor);
		}
		
		public void createTrack(List<GeoPoint> points, java.util.Date startDate,int totaldistance) {	
			ContentValues trackvalues = new ContentValues();			
			trackvalues.put(MySQLiteHelper.TRACK_DATE, new java.util.Date().getTime());
			trackvalues.put(MySQLiteHelper.TRACK_START_DATE, startDate.getTime());
			trackvalues.put(MySQLiteHelper.TRACK_TOTAL_DISTANCE, totaldistance);
			long trackId = database.insert(MySQLiteHelper.TABLE_TRACK, MySQLiteHelper.TRACK_ID,
					trackvalues);
			for(GeoPoint point: points){
			ContentValues values = new ContentValues();
			values.put(MySQLiteHelper.GPOINT_lATITUTE, point.getLatitudeE6());
			values.put(MySQLiteHelper.GPOINT_LONGITUDE, point.getLongitudeE6());			
			values.put(MySQLiteHelper.GPOINT_TRACK, trackId);
			long insertId = database.insert(MySQLiteHelper.TABLE_GPOINTS, null,
					values);			
			}			
		}

		public void deletePoint(GPoint point) {
			long id = point.getId();
			System.out.println("Comment deleted with id: " + id);
			database.delete(MySQLiteHelper.TABLE_GPOINTS,MySQLiteHelper.GPOINT_ID
					+ " = " + id, null);
		}

		/*public List<GPoint> getAllGPoints() {
			List<GPoint> points = new ArrayList<GPoint>();
			Cursor cursor = database.query(MySQLiteHelper.TABLE_GPOINTS,
					allColumns, MySQLiteHelper.TRACK_ID+"="+id, null, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				GPoint point = cursorToGPoint(cursor);
				points.add(point);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			return points;
		}*/
		
		public List<GeoPoint> getAllGeoPointsTrackID(long id) {
			List<GeoPoint> points = new ArrayList<GeoPoint>();
			Cursor cursor = database.query(MySQLiteHelper.TABLE_GPOINTS,allColumns
				,MySQLiteHelper.GPOINT_TRACK+"="+id, null, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				GeoPoint point = cursorToGeoPoint(cursor);
				points.add(point);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			return points;
		}			

		private GPoint cursorToGPoint(Cursor cursor) {
			GPoint point = new GPoint();
			point.setId(cursor.getLong(0));
			point.setLat(cursor.getDouble(1));
			point.setLon(cursor.getDouble(2));
			point.setTrackid(cursor.getLong(3));
			return point;
		}	
		
		private GeoPoint cursorToGeoPoint(Cursor cursor) {			
			GeoPoint point = new GeoPoint(cursor.getInt(1), cursor.getInt(2));			
			return point;
		}
		
		public List<Track> GetListOfTracks(){
			List<Track> tracks = new ArrayList<Track>();
			
			Cursor cursor = database.query(MySQLiteHelper.TABLE_TRACK, new String[]{MySQLiteHelper.TRACK_ID,MySQLiteHelper.TRACK_DATE,MySQLiteHelper.TRACK_START_DATE}, null, null, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Track t = new Track();
				t.setId(cursor.getLong(0));						
				t.setDatum(new Date(cursor.getLong(1)));
				t.setDateStarted(new Date(cursor.getLong(2)));
				t.setTotalDistance(cursor.getInt(3));
				tracks.add(t);
				cursor.moveToNext();
			}
			return tracks;
		}
}
