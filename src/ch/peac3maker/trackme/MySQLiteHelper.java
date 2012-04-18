package ch.peac3maker.trackme;

	import android.content.Context;
	import android.database.sqlite.SQLiteDatabase;
	import android.database.sqlite.SQLiteOpenHelper;
	import android.util.Log;

	//Helper that creates and maintains the database.
	public class MySQLiteHelper extends SQLiteOpenHelper {

		public static final String TABLE_GPOINTS = "GeoPoints";
		public static final String GPOINT_ID = "_id";
		public static final String TRACK_ID = "_id";
		public static final String TABLE_TRACK = "Track";
		public static final String GPOINT_lATITUTE = "lat";
		public static final String GPOINT_LONGITUDE = "lon";
		public static final String GPOINT_ALTITUDE = "alt";
		public static final String GPOINT_TRACK = "t_id";
		public static final String TRACK_DATE = "dateSaved";		
		public static final String TRACK_TOTAL_DISTANCE = "distance";
		public static final String TRACK_TOTAL_DISTANCE_HEIGHT = "distance_height";
		public static final String TRACK_TOTAL_AVG_SPEED = "avg_speed";
		public static final String TRACK_TOTAL_CUR_SPEED = "cur_speed";
		public static final String TRACK_LOWEST_ALT = "high_alt";
		public static final String TRACK_HIGHEST_ALT = "low_alt";

		private static final String DATABASE_NAME = "GeoData.db";
		private static final int DATABASE_VERSION = 12;

		private static final String DATABASE_CREATE_TRACK = "create table Track ( " + TRACK_ID
				+ " integer primary key autoincrement, " +TRACK_DATE				
				+ " long, "+ TRACK_TOTAL_DISTANCE
				+ " real, "+ TRACK_TOTAL_DISTANCE_HEIGHT
				+ " real, "+ TRACK_TOTAL_AVG_SPEED
				+ " real, "+ TRACK_TOTAL_CUR_SPEED
				+ " real, "+ TRACK_LOWEST_ALT
				+ " real, "+ TRACK_HIGHEST_ALT
				+" real );";
		// Database creation sql statement
		private static final String DATABASE_CREATE = "create table "
				+ TABLE_GPOINTS + "( " + GPOINT_ID
				+ " integer primary key autoincrement, " + GPOINT_lATITUTE
				+ " real not null, "+ GPOINT_LONGITUDE
				+ " real not null, "+ GPOINT_ALTITUDE
				+ " real, "+  GPOINT_TRACK
				+ " integer not null references Track("+TRACK_ID+") );";

		//Constructor tells the system which database to use
		public MySQLiteHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		//Creates/recreates the database
		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL(DATABASE_CREATE_TRACK);
			database.execSQL(DATABASE_CREATE);
		}

		//Handles onUpgrade when Version has changed.
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(MySQLiteHelper.class.getName(),
					"Upgrading database from version " + oldVersion + " to "
							+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACK);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPOINTS);
			onCreate(db);
		}

	}
