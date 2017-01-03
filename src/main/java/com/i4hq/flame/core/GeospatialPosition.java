package com.i4hq.flame.core;

/**
 * @author rmoten
 *
 */
public class GeospatialPosition {
		private final double longitude;
		private final double latitude;
		/**
		 * @param longitude
		 * @param latitude
		 */
		public GeospatialPosition(double longitude, double latitude) {
			super();
			this.longitude = longitude;
			this.latitude = latitude;
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
		
		
		
	}