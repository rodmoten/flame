package com.i4hq.flame.core;

/**
 * @author rmoten
 *
 */
public class Geo2DPoint {
	public static final double LATITUDE_LENGTH_PER_DEGREE_IN_METERS = 110574.61087757687;
	public static final double LONGITUDE_LENGTH_PER_DEGREE_IN_METERS = 111302.61697430261;
	public static final double EARTH_RADIUS_IN_METERS = 6378137.0;

	private double longitude;
	private double latitude;
	/**
	 * @param longitude
	 * @param latitude
	 */
	public Geo2DPoint(double longitude, double latitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}
	public Geo2DPoint(Geo2DPoint other) {
		this.longitude = other.longitude;
		this.latitude = other.latitude;
	}

	
	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}
	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}
	public Geo2DPoint setLatitude(double d) {
		latitude = d;
		return this;
	}
	public Geo2DPoint setLongitude(double d) {
		longitude = d;
		return this;
	}

	@Override
	public String toString() {
		return String.format("(%f, %f)", longitude, latitude);
	}
	
	/**
	 * Uses the Haversine formula to compute the distance.
	 * @param otherLongitude
	 * @param otherLatitude
	 * @return Returns the distance in meters between this position and the position specified by the given coordinates
	 */
	public double distance(double otherLongitude, double otherLatitude){  
		double lat1 = this.latitude;
		double lon1 = this.longitude;
		double lat2 = otherLatitude;
		double lon2 = otherLongitude;
		
	    double  dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
	    double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon/2) * Math.sin(dLon/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double d = EARTH_RADIUS_IN_METERS * c;
	    return d;
	}
	
	/**
	* Uses the Haversine formula to compute the distance.
	* @param other
	 * @return Returns the distance in meters between this geo point and the other geo point.
	 *
	 */
	public double distance(Geo2DPoint other) {
		return distance(other.longitude, other.latitude);
	}
	/**
	 * Change the latitude of this geo-position
	 * @param longitudeDistance 
	 * @param latidudeDistance 
	 * @return Returns this object
	 */
	public Geo2DPoint add(double longitudeDistance, double latidudeDistance) {
		this.latitude = this.latitude + latidudeDistance/LATITUDE_LENGTH_PER_DEGREE_IN_METERS;
		this.longitude = this.longitude + (longitudeDistance/EARTH_RADIUS_IN_METERS) * (180/Math.PI) / Math.cos(this.latitude * Math.PI/180);
		return this;
	}

	
	
}