package ch.peacemaker.geodata;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.text.format.DateUtils;

import com.google.android.maps.GeoPoint;

public class Calculator {
	private static final int R = 6371;
	
	public static double CalculateDistanceBetweenPointsM(GeoPoint pt1, GeoPoint pt2){
		// Approximate Equirectangular -- works if (lat1,lon1) ~ (lat2,lon2)
		double x = (pt2.getLongitudeE6()/1E6- pt1.getLongitudeE6()/1E6)*Math.cos((pt1.getLatitudeE6()/1E6+ pt2.getLatitudeE6()/1E6) / 2);
		double y = (pt2.getLatitudeE6()/1E6 - pt1.getLatitudeE6()/1E6);
		return Math.sqrt(x * x + y * y) * R;
	}
	
	public static double GetCurrentAvgSpeedKMH(List<GeoPoint> points, Date start, Date end){			
		double metersec =GetCurrentAvgSpeedMPS(points, start, end);
		double kmh = metersec*3.6;
		return kmh;
	}
	
	public static double GetCurrentAvgSpeedKMH(List<GeoPoint> points, Date start, Date end,int distance){			
		double metersec =GetCurrentAvgSpeedMPS(points, start, end, distance);
		double kmh = metersec*3.6;
		return kmh;
	}
	
	public static double GetCurrentAvgSpeedMPS(List<GeoPoint> points, Date start, Date end){	
		double meters = getTotalDistancePassedM(points);
		long sec = end.getTime()/1000 - start.getTime()/1000;
		double metersec = meters / sec;
		return metersec;
	}
	
	public static double GetCurrentAvgSpeedMPS(List<GeoPoint> points, Date start, Date end, int distance){			
		long sec = end.getTime()/1000 - start.getTime()/1000;
		double metersec = distance / sec;
		return metersec;
	}
	
	public static double getTotalDistancePassedM(List<GeoPoint> points){
		double total = 0;
		for(int index = 0; index<points.size(); index++){
			if(index != 0){
				total += CalculateDistanceBetweenPointsM(points.get(index-1), points.get(index));
			}
		}
		return total;
	}
}
