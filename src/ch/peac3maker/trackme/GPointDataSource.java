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

//Datasource object
public class GPointDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.GPOINT_ID,
			MySQLiteHelper.GPOINT_lATITUTE, MySQLiteHelper.GPOINT_LONGITUDE, MySQLiteHelper.GPOINT_ALTITUDE, MySQLiteHelper.GPOINT_TRACK };

	public GPointDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	// Creates a new GPoint on a new track
	public GPoint createGPoint(GeoPoint point) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.GPOINT_lATITUTE, point.getLatitudeE6());
		values.put(MySQLiteHelper.GPOINT_LONGITUDE, point.getLongitudeE6());
		long trackId = database.insert(MySQLiteHelper.TABLE_TRACK, null, null);
		values.put(MySQLiteHelper.GPOINT_TRACK, trackId);
		long insertId = database.insert(MySQLiteHelper.TABLE_GPOINTS, null,
				values);
		// To show how to query
		Cursor cursor = database.query(MySQLiteHelper.TABLE_GPOINTS,
				allColumns, MySQLiteHelper.GPOINT_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		GPoint gpoint = cursorToGPoint(cursor);
		cursor.close();
		return gpoint;
	}

	// Creates a GeoPoint for a given trackid
	public long createGeoPoint(long trackid, GPoint point) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.GPOINT_lATITUTE, point.getLat());
		values.put(MySQLiteHelper.GPOINT_LONGITUDE, point.getLon());
		values.put(MySQLiteHelper.GPOINT_ALTITUDE, point.getAlt());
		values.put(MySQLiteHelper.GPOINT_TRACK, trackid);
		return database.insert(MySQLiteHelper.TABLE_GPOINTS, null, values);
	}

	// Creates a new track
	public long createTrack(java.util.Date startDate, double totaldistance) {
		ContentValues trackvalues = new ContentValues();
		trackvalues.put(MySQLiteHelper.TRACK_DATE,
				new java.util.Date().getTime());
		trackvalues.put(MySQLiteHelper.TRACK_TOTAL_DISTANCE, totaldistance);
		return database.insert(MySQLiteHelper.TABLE_TRACK,
				MySQLiteHelper.TRACK_ID, trackvalues);
	}

	// Deletes a GPoint by id
	public void deletePoint(GPoint point) {
		long id = point.getId();
		System.out.println("Comment deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_GPOINTS, MySQLiteHelper.GPOINT_ID
				+ " = " + id, null);
	}

	// Gets all GeoPoints belonging to a certain trackid
	public List<GeoPoint> getAllGeoPointsTrackID(long id) {
		List<GeoPoint> points = new ArrayList<GeoPoint>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_GPOINTS,
				allColumns, MySQLiteHelper.GPOINT_TRACK + "=" + id, null, null,
				null, null);
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

	// Gets all GPoints belonging to a certain trackid
	public List<GPoint> getAllGPointsTrackID(long id) {
		List<GPoint> points = new ArrayList<GPoint>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_GPOINTS,
				allColumns, MySQLiteHelper.GPOINT_TRACK + "=" + id, null, null,
				null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			GPoint point = cursorToGPoint(cursor);
			points.add(point);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return points;
	}

	// Wraps a cursor to a Gpoint
	private GPoint cursorToGPoint(Cursor cursor) {
		GPoint point = new GPoint();
		point.setId(cursor.getLong(0));
		point.setLat(cursor.getDouble(1));
		point.setLon(cursor.getDouble(2));
		point.setAlt(cursor.getDouble(3));
		point.setTrackid(cursor.getLong(4));
		return point;
	}
	
	// Wraps a cursor to a Gpoint
		private Track cursorToTrack(Cursor cursor) {
			Track track = new Track();
			track.setId(cursor.getLong(0));
			track.setDatum(new Date(cursor.getLong(1)));
			if (!cursor.isNull(2)) {
				track.setTotalDistance(cursor.getDouble(2));
			}
			if (!cursor.isNull(3)) {
				track.setTotalHeightDistance(cursor.getDouble(3));
			}
			if (!cursor.isNull(4)) {
				track.setAvgSpeed(cursor.getDouble(4));
			}
			if (!cursor.isNull(5)) {
				track.setCurSpeed(cursor.getDouble(5));
			}
			return track;
		}

	// Creates a GeoPoint object from a cursor
	private GeoPoint cursorToGeoPoint(Cursor cursor) {
		GeoPoint point = new GeoPoint(cursor.getInt(1), cursor.getInt(2));
		return point;
	}

	// Gets a list of all Tracks saved to the database.
	public List<Track> GetListOfTracks() {
		List<Track> tracks = new ArrayList<Track>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_TRACK,
				new String[] { MySQLiteHelper.TRACK_ID,
						MySQLiteHelper.TRACK_DATE }, null, null, null, null,
				null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Track t = new Track();
			t.setId(cursor.getLong(0));
			t.setDatum(new Date(cursor.getLong(1)));		
			tracks.add(t);
			cursor.moveToNext();
		}
		cursor.close();
		return tracks;
	}

	// Gets the id of the newest track
	public long getNewestTrackID() {
		Cursor cursor = database.query(MySQLiteHelper.TABLE_TRACK,
				new String[] { MySQLiteHelper.TRACK_ID }, null, null, null,
				null, MySQLiteHelper.TRACK_DATE + " DESC", "1");
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			long id = cursor.getLong(0);
			cursor.close();
			return id;
		}
		cursor.close();
		return -1;
	}
	
	public Track getTrackByID(long id){
		Cursor cursor = database.query(MySQLiteHelper.TABLE_TRACK,
				new String[] { MySQLiteHelper.TRACK_ID, MySQLiteHelper.TRACK_DATE, MySQLiteHelper.TRACK_TOTAL_DISTANCE, MySQLiteHelper.TRACK_TOTAL_DISTANCE_HEIGHT, MySQLiteHelper.TRACK_TOTAL_AVG_SPEED,MySQLiteHelper.TRACK_TOTAL_CUR_SPEED }, MySQLiteHelper.TRACK_ID+" = "+id, null, null,null,null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			Track t = cursorToTrack(cursor);
			cursor.close();
			return t;
		}
		cursor.close();
		return null;
	}

	public int UpdateTrack(long id, double totalLength,double totalheight, double avgspeed) {
		ContentValues val = new ContentValues();
		val.put(MySQLiteHelper.TRACK_TOTAL_DISTANCE, totalLength);
		val.put(MySQLiteHelper.TRACK_TOTAL_DISTANCE_HEIGHT, totalheight);
		val.put(MySQLiteHelper.TRACK_TOTAL_AVG_SPEED, avgspeed);
		return database.update(MySQLiteHelper.TABLE_TRACK, val,
				MySQLiteHelper.TRACK_ID + " = " + id, null);
	}
	
	public int UpdateTrackCurrentSpeed(long id,double totalLength,double totalheight, double avgspeed, double currentspeed) {
		ContentValues val = new ContentValues();		
		val.put(MySQLiteHelper.TRACK_TOTAL_DISTANCE, totalLength);
		val.put(MySQLiteHelper.TRACK_TOTAL_DISTANCE_HEIGHT, totalheight);
		val.put(MySQLiteHelper.TRACK_TOTAL_AVG_SPEED, avgspeed);
		val.put(MySQLiteHelper.TRACK_TOTAL_CUR_SPEED, currentspeed);
		return database.update(MySQLiteHelper.TABLE_TRACK, val,
				MySQLiteHelper.TRACK_ID + " = " + id, null);
	}
}
