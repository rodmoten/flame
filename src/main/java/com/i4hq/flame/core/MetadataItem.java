/**
 * 
 */
package com.i4hq.flame.core;

/**
 * @author rmoten
 *
 */
public class MetadataItem {
	final private String name;
	final private String value;
	
	public MetadataItem(String name, String value) {
		super();
		if (name == null){
			throw new IllegalArgumentException("name of metadata item cannot be null");
			
		}
		
		if (value == null){
			throw new IllegalArgumentException("value of metadata item cannot be null");
			
		}
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MetadataItem [name=");
		builder.append(name);
		builder.append(", value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}
}
