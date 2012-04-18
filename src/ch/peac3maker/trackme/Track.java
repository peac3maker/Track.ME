package ch.peac3maker.trackme;

import java.util.Date;

//Datacontract for Tracks
public class Track {
	Date datum;
	long id;	
	double totalDistance;
	double totalHeightDistance;
	double avgSpeed;
	double curSpeed;
	private double lowestAlt;
	private double highestAlt;
	
	
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
	public double getTotalDistance() {
		return totalDistance;
	}
	public void setTotalDistance(double totalDistance) {
		this.totalDistance = totalDistance;
	}
	public double getTotalHeightDistance() {
		return totalHeightDistance;
	}
	public void setTotalHeightDistance(double totalHeightDistance) {
		this.totalHeightDistance = totalHeightDistance;
	}
	public double getAvgSpeed() {
		return avgSpeed;
	}
	public void setAvgSpeed(double avgSpeed) {
		this.avgSpeed = avgSpeed;
	}
	public double getCurSpeed() {
		return curSpeed;
	}
	public void setCurSpeed(double curSpeed) {
		this.curSpeed = curSpeed;
	}
	public double getLowestAlt() {
		return lowestAlt;
	}
	public void setLowestAlt(double lowestHeight) {
		this.lowestAlt = lowestHeight;
	}
	public double getHighestAlt() {
		return highestAlt;
	}
	public void setHighestAlt(double highestHeight) {
		this.highestAlt = highestHeight;
	}
}
