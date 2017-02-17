/**
 * 
 */
package com.i4hq.flame.core;

/**
 * @author rmoten
 *
 */
public class AttributeDecl {
	private final String name;
	private final AttributeType type;
	/**
	 * @param name
	 * @param type
	 */
	public AttributeDecl(String name, AttributeType type) {
		super();
		this.name = name;
		this.type = type;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the type
	 */
	public AttributeType getType() {
		return type;
	}
	
	
}
