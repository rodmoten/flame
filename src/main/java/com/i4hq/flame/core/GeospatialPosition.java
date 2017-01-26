package com.i4hq.flame.core;

/**
 * @author rmoten
 *
 */
public class GeospatialPosition {
	public static final double LATITUDE_LENGTH_PER_DEGREE_IN_METERS = 110574.61087757687;
	public static final double LONGITUDE_LENGTH_PER_DEGREE_IN_METERS = 111302.61697430261;

	private double longitude;
	private double latitude;
	/**
	 * @param longitude
	 * @param latitude
	 */
	public GeospatialPosition(double longitude, double latitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}
	public GeospatialPosition(GeospatialPosition other) {
		this.longitude = other.longitude;
		this.latitude = other.latitude;
	}


	/**
	 * Create a Geospatial Position relative to a given point. 
	 * @param center
	 * @param longitudeDistance - distance from center in meters  
	 * @param latidudeDistance - distance from center in meters
	 */
	public GeospatialPosition(GeospatialPosition center, double longitudeDistance, double latidudeDistance) {
		double earthRadius = 6378137.0;
		this.latitude = center.latitude + latidudeDistance/LATITUDE_LENGTH_PER_DEGREE_IN_METERS;
		this.longitude = center.longitude + (longitudeDistance/earthRadius) * (180/Math.PI) / Math.cos(this.latitude * Math.PI/180);
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
	public GeospatialPosition addLatitude(double d) {
		latitude += d;
		return this;
	}
	public GeospatialPosition addLongitude(double d) {
		longitude += d;
		return this;
	}

	@Override
	public String toString() {
		return String.format("(%f, %f)", longitude, latitude);
	}
}