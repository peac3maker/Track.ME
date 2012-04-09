package ch.peac3maker.trackme;

import java.util.Date;

//Datacontract for Tracks
public class Track {
	Date datum;
	long id;	
	int totalDistance;
	
	public Date getDatum() {
		return datum;
	}
	public void setDatum(Date datum) {
		this.datum = datum;
	}
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	@Override
    public String toString() {
        return this.datum.toLocaleString();
    }
	public int getTotalDistance() {
		return totalDistance;
	}
	public void setTotalDistance(int totalDistance) {
		this.totalDistance = totalDistance;
	}
}
