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
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
}
