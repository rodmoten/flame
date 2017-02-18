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
	private final MetadataItem[] metadata;
	/**
	 * @param name
	 * @param type
	 */
	public AttributeDecl(String name, AttributeType type, MetadataItem ...metadata) {
		super();
		this.name = name;
		this.type = type;
		this.metadata = metadata;
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
	public MetadataItem[] getMetadata() {
		return metadata;
	}
	
	
}
