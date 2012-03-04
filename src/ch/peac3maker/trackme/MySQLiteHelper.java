package ch.peac3maker.trackme;

	import android.content.Context;
	import android.database.sqlite.SQLiteDatabase;
	import android.database.sqlite.SQLiteOpenHelper;
	import android.util.Log;

	public class MySQLiteHelper extends SQLiteOpenHelper {

		public static final String TABLE_GPOINTS = "GeoPoints";
		public static final String GPOINT_ID = "_id";
		public static final String TRACK_ID = "_id";
		public static final String TABLE_TRACK = "Track";
		public static final String GPOINT_lATITUTE = "lat";
		public static final String GPOINT_LONGITUDE = "lon";
		public static final String GPOINT_TRACK = "t_id";
		public static final String TRACK_DATE = "dateSaved";
		public static final String TRACK_START_DATE = "dateStarted";
		public static final String TRACK_TOTAL_DISTANCE = "distance";

		private static final String DATABASE_NAME = "GeoData.db";
		private static final int DATABASE_VERSION = 6;

		private static final String DATABASE_CREATE_TRACK = "create table Track ( " + TRACK_ID
				+ " integer primary key autoincrement, " +TRACK_DATE
				+ " long, " +TRACK_START_DATE
				+ " long, "+ TRACK_TOTAL_DISTANCE
				+" integer );";
		// Database creation sql statement
		private static final String DATABASE_CREATE = "create table "
				+ TABLE_GPOINTS + "( " + GPOINT_ID
				+ " integer primary key autoincrement, " + GPOINT_lATITUTE
				+ " real not null, "+ GPOINT_LONGITUDE
				+ " real not null, "+  GPOINT_TRACK
				+ " integer not null references Track("+TRACK_ID+") );";

		public MySQLiteHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL(DATABASE_CREATE_TRACK);
			database.execSQL(DATABASE_CREATE);
		}

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
