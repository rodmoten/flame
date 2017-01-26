/**
 * 
 */
package com.i4hq.flame.core;

/**
 * Implementations of this interface create global unique IDs for entities.
 *  @author rmoten
 *
 */
public interface EntityIdFactory {
	public String createId();
	
	/**
	 * @param seed
	 * @return Create a unique ID using the given input value. Returns the same ID for the same seed.
	 */
	public String createId(String seed);
	
}
