package ch.peac3maker.trackme;

//DataContract Class for a GPoint
public class GPoint {	
		private long id;
		private long trackid;
		private double lon;
		private double lat;			

		public long getId() {
			return id;
		}		

		public void setId(long id) {
			this.id = id;
		}

		public double getLon() {
			return lon;
		}

		public void setLon(double lon) {
			this.lon = lon;
		}

		public double getLat() {
			return lat;
		}

		public void setLat(double lat) {
			this.lat = lat;
		}

		public long getTrackid() {
			return trackid;
		}

		public void setTrackid(long trackid) {
			this.trackid = trackid;
		}

		
}
