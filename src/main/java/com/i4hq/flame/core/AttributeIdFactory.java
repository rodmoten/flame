/**
 * 
 */
package com.i4hq.flame.core;

/**
 * @author rmoten
 *
 */
public interface AttributeIdFactory {
	/**
	 * @param attributeName
	 * @return Returns the unique ID for the given attribute.
	 */
	public long getAttributeId(String attributeName);
}
