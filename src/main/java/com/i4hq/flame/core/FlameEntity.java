package com.i4hq.flame.core;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class FlameEntity {	

	private static Logger logger = LoggerFactory.getLogger(FlameEntity.class);

	private final Map<String, List<AttributeValue>> attributes = new HashMap<>();
	public static final char ATTIRBUTE_PATH_SEPARATOR = ':';
	public static final String ENITY_ID_ATTIRBUTE_PATH_SEPARATOR = "" + ATTIRBUTE_PATH_SEPARATOR + ATTIRBUTE_PATH_SEPARATOR;
	private final String id;
	private final String entityIdPrefix;
	public static final String ATTRIBUTE_TYPE_EXPR_SEPARATOR = ":::";
	private GeospatialPosition geospatialPosition;
	private String hash = "";


	protected FlameEntity(String id) {
		this.id = id;
		entityIdPrefix = id + ENITY_ID_ATTIRBUTE_PATH_SEPARATOR;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (hash == null) {
			setHash();
		}
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		return result;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (hash == null) {
			setHash();
		}
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FlameEntity other = (FlameEntity) obj;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		return true;
	}





	//	/**
	//	 * @param entityType
	//	 */
	//	public void addEntityType (String entityType) {
	//		if (entityType == null){
	//			return;
	//		}
	//		String[] attributeDecls = entityType.trim().split("\\n");
	//		for (String attributeDecl : attributeDecls){
	//			String[] attributeDeclParts = attributeDecl.split(ATTRIBUTE_TYPE_EXPR_SEPARATOR,2);
	//			AttributeType attributeType = AttributeType.valueOf(attributeDeclParts[1]);
	//			String attributeName = attributeDeclParts[0];
	//			this.attributeTypes.put(attributeName, attributeType);
	//		}
	//	}

	/**
	 * Set the location of this entity.
	 * @param longitude
	 * @param latitude
	 */
	public void setLocation(double longitude, double latitude) {
		this.geospatialPosition = new GeospatialPosition(longitude, latitude);
	}

	/**
	 * @return Returns the location of this entity.
	 */
	public GeospatialPosition getGeospatialPosition(){
		return this.geospatialPosition;
	}

	/**
	 * @param attributes
	 */
	public void addAttribute(String name, Object value, AttributeType type, MetadataItem ...metadata) {
		if (name == null) {
			return;
		}

		// Hopefully, we aren't adding attributes and trying to use the hash at the same time.
		hash = null;
		List<AttributeValue> values = this.attributes.get(name);
		if (values == null) {
			values = new LinkedList<>();
		}
		values.add(new AttributeValue (value == null ? null : value.toString(), type, metadata));
		this.attributes.put(name, values);

		// Set the geolocation of the entity if the attribute has a geospatial attribute.
		if (type == AttributeType.LATITUDE){
			this.setLatitude(((Number) value).doubleValue());
		} else if (type == AttributeType.LONGITUDE) {
			this.setLongitude(((Number) value).doubleValue()); 
		}
	}

	public String getEntityIdPrefix() {
		return entityIdPrefix ;
	}

	/**
	 * @return the global unique ID of the entity
	 */
	public String getId() {
		return id;
	}


	/**
	 * @param attributePath - 
	 * @return Returns the 1st value  of the attribute. null will be return if the value of the attribute is null or if the entity doesn't contain the attribute.
	 */
	public AttributeValue getAttribute(String attributePath) {

		final List<AttributeValue> list = attributes.get(attributePath);
		return list == null ? null : list.get(0);
	}

	
	/**
	 * @param attributePath - 
	 * @return Returns all of the values for a given attribute.
	 */
	public List<AttributeValue> getAttributes(String attributePath) {
		return attributes.get(attributePath);
	}
	

	public List<AttributeValue> getAttributesOfSpecificType(final AttributeType targetType) {
		List<AttributeValue> values = new LinkedList<>();
		for (List<AttributeValue> vs : attributes.values()){
			for (AttributeValue v : vs) {
				if (v.getType() == targetType) {
				}
				values.add(v);
			}
		}
		return values;
	}


	/**
	 * @param attributePath
	 * @return Returns true if and only if the entity contains this attribute.
	 */
	public boolean containAttribute(String attributePath) {
		return attributes.containsKey(attributePath);
	}

	/**
	 * @return Returns the number of attributes in the entity.
	 */
	public int size() {
		return attributes.size();
	}

	/**
	 * @return Returns the type of the entity. The type is a sorted list of the attributes.
	 */
	public String getType() {
		StringBuilder b = new StringBuilder();
		List<String> attributeTypExpression = new LinkedList<>();

		for (Entry<String, List<AttributeValue>> entry : this.attributes.entrySet()) {
			if (entry.getValue() != null) {
				attributeTypExpression.add(entry.getKey() + ATTRIBUTE_TYPE_EXPR_SEPARATOR + entry.getValue().get(0).getType());
			}
		}
		Collections.sort(attributeTypExpression);
		for (String attributeTypeExpr : attributeTypExpression){
			b.append(attributeTypeExpr);
			b.append('\n');
		}
		return b.toString();
	}

	/**
	 * @param attributeName
	 * @return Returns the type of the given attribute.
	 */
	public AttributeType getTypeOfAttribute(String attributeName) {
		if (attributeName == null) {
			return null;
		}
		List<AttributeValue> value = this.attributes.get(attributeName);
		if (value == null){
			return null;
		}
		return value.get(0).getType();
	}

	/**
	 * @return
	 */
	private synchronized void setHash() {
		try {
			// If the hash is already set, then don't create it again.
			if (hash != null) {
				return;
			}
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(this.id.getBytes());
			for (Entry<String, List<AttributeValue>> attribute : attributes.entrySet()) {
				md.update(attribute.getKey().getBytes());
				List<AttributeValue> values = attribute.getValue();
				if (values == null){
					continue;
				}
				for (AttributeValue av : values){
					if (av == null) {
						md.update(new byte[]{'n','u','l','l'});
					} else {
						String v = av.getValue();
						md.update(v == null ? new byte[]{'n','u','l','l'} :  v.getBytes());
						md.update(av.getType().getBytes());
					}
				}
			}
			hash = new BigInteger(1,md.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			logger.error("MD5", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create an MD 5 hash of all of the attributes in this entity, i.e. a hash of all of the attribute names, values, and types in this entity.
	 * @return Returns the MD5 hash of this entities.
	 */
	public String getHash() {
		if (hash == null) {
			setHash();
		}
		return hash;
	}

	/**
	 * @return Returns the JSON version of this Flame entity.
	 */
	public JsonObject toJson() {
		return null;
	}



	/**
	 * @return Returns the attributes of this entity
	 */
	public Set<Entry<String, List<AttributeValue>>> getAttributes() {
		return attributes.entrySet();
	}


	/**
	 * @param d
	 */
	public void setLatitude(Double d) {
		if (this.geospatialPosition == null){
			this.geospatialPosition = new GeospatialPosition(0, 0);
		}
		this.geospatialPosition.addLatitude(d);

	}

	/**
	 * @param d
	 */
	public void setLongitude(Double d) {
		if (this.geospatialPosition == null){
			this.geospatialPosition = new GeospatialPosition(0, 0);
		}
		this.geospatialPosition.addLongitude(d);
	}

	public String getReference(String referenceName) {
		return null;
	}
}
