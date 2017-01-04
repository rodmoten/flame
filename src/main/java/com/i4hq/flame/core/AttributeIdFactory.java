/**
 * 
 */
package com.i4hq.flame.core;

/**
 * An Attribute ID factory is responsible for converting an attribute name to an integer.
 * The attribute ID is used to compress an attribute path in Flame. 
 * The attribute path will consist of the unique IDs of the attributes instead of their actual names.
 * This should keep the attribute paths short.
 * @author rmoten
 *
 */
public interface AttributeIdFactory {
	/**
	 * @param attributeName
	 * @return Returns the unique compressed version of the given attribute name. Each attribute path has a unique ID.
	 */
	public long getAttributeId(String attributeName);
}
