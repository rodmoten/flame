/**
 * 
 */
package com.i4hq.flame.core;

import java.util.LinkedList;
import java.util.List;

/**
 * @author rmoten
 *
 */
public class AttributeValue {
	private final String value;
	private final AttributeType type;
	private final List<MetadataItem> metadata = new LinkedList<MetadataItem>();
	/**
	 * @param value
	 * @param type
	 */
	AttributeValue(String value, AttributeType type, MetadataItem ...metadata) {
		super();
		this.value = value;
		this.type = type;
		for (MetadataItem md : metadata){
			this.metadata.add(md);
		}
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @return the type
	 */
	public AttributeType getType() {
		return type;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttributeValue other = (AttributeValue) obj;
		if (type != other.type)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("AttributeValue [value=%s, type=%s]", value, type);
	}
	
	public void addMetadata (String name , String value){
		this.metadata.add(new MetadataItem(name, value));
	}
	public List<MetadataItem> getMetadata() {
		return metadata;
	}
	
	
	
	
	
}
