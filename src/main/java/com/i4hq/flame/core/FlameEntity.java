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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Stack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class FlameEntity {	

	private static Logger logger = LoggerFactory.getLogger(FlameEntity.class);
	
	private final Map<String, AttributeValue> attributes = new HashMap<>();
	private final static GsonBuilder gsonBuilder = new GsonBuilder();
	private static final char ATTIRBUTE_PATH_SEPARATOR = ':';
	public static final String ENITY_ID_ATTIRBUTE_PATH_SEPARATOR = "" + ATTIRBUTE_PATH_SEPARATOR + ATTIRBUTE_PATH_SEPARATOR;
	private final String id;
	private final String entityIdPrefix;
	private static final String ATTRIBUTE_TYPE_EXPR_SEPARATOR = ":::";
	private GeospatialPosition geospatialPosition;
	private String hash = "";

	
	private FlameEntity(String id) {
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



	/**
	 * Create a Flame Entity object from the parts of a Flame Entity.  
	 * @param entityId
	 * @param entityType
	 * @param attributes - the attributes of the entity. The m
	 * @return
	 */
	public static FlameEntity createEntity(String entityId) {
		FlameEntity entity = new FlameEntity(entityId);
		return entity;
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
	public void setLocation(long longitude, long latitude) {
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
	public void addAttribute(String name, Object value, AttributeType type) {
		if (name == null) {
			return;
		}
		// Hopefully, we aren't adding attributes and trying to use the hash at the same time.
		hash = null;
		this.attributes.put(name, new AttributeValue (value == null ? null : value.toString(), type));
		
	}
	
	/**
	 * @param entityIdFactory
	 * @param attributeIdFactory
	 * @param jsonText
	 * @return Returns a Flame entity created from JSON contained in a string.
	 */
	public static FlameEntity createFromJson (EntityIdFactory entityIdFactory, AttributeIdFactory attributeIdFactory, String jsonText){
		Stack<Long> parentPath = new Stack<>();
		FlameEntity entity = new FlameEntity(entityIdFactory.createId(jsonText));
		Gson gson =gsonBuilder.create();
		
		JsonObject jsonObj = gson.fromJson(jsonText, JsonObject.class);
		createFromJson(entity, entityIdFactory, attributeIdFactory, parentPath, jsonObj);
		
		return entity;
	}
	
	/**
	 * @param entity
	 * @param entityIdFactory
	 * @param attributeIdFactory
	 * @param parentPath
	 * @param jsonObj
	 */
	private static void createFromJson(FlameEntity entity, EntityIdFactory entityIdFactory, AttributeIdFactory attributeIdFactory,
			Stack<Long> parentPath, JsonObject jsonObj) {
		
		
		for (Entry<String, JsonElement> jsonElement : jsonObj.entrySet()) {
			// Get the attribute name 
			String attributeName = jsonElement.getKey();
			if (!validAttributeName(attributeName)) {
				throw new IllegalArgumentException (String.format("Attribute name is not valid: '{}'", attributeName));
			}
			
			// Get the attribute value. 
			JsonElement attributeValue = jsonElement.getValue();
			//TODO handle arrays
			if (attributeValue.isJsonArray()) {
				throw new IllegalArgumentException ("JSON must not contain any arrays");
			}
			boolean isScalar = attributeValue.isJsonPrimitive() || attributeValue.isJsonNull();
			long attributeId = attributeIdFactory.getAttributeId(attributeName);
			
			// If this is a primitive JSON element, then add it to the entity.
			if (isScalar) {
				String attributePathName = createAttributePathName(parentPath, attributeId);
				if (attributeValue.isJsonNull()) {
					entity.attributes.put(entity.getEntityIdPrefix() + attributePathName, new AttributeValue(null, getAttributeTypeOfScalarValue(null)));
				} else {
					entity.attributes.put(entity.getEntityIdPrefix() + attributePathName, new AttributeValue(attributeValue.getAsString(), getAttributeTypeOfScalarValue(attributeValue.getAsJsonPrimitive())));
				}
			} else {
				// Add the JSON element's attribute ID to the stack and continue with its children.
				parentPath.push(attributeId);
				createFromJson (entity, entityIdFactory, attributeIdFactory, parentPath, attributeValue.getAsJsonObject());
			}
		}
		if (!parentPath.empty()) {
			parentPath.pop();
		}
		
	}
	

	public String getEntityIdPrefix() {
		return entityIdPrefix ;
	}

	/**
	 * @param attributeValue
	 * @return
	 */
	private static AttributeType getAttributeTypeOfScalarValue(JsonPrimitive attributeValue) {
		if (attributeValue == null){
			return AttributeType.STRING;
		}
		if (attributeValue.isBoolean()) {
			return AttributeType.BOOLEAN;
		}
		if(attributeValue.isNumber()) {
			return AttributeType.NUMBER;
		}
		// Should be a JSON string or a JSON null.
		return AttributeType.STRING;
		
	}
	/**
	 * @param attributeName
	 * @return Returns true if and only if the attribute name is valid.
	 */
	private static boolean validAttributeName(String attributeName) {
		if (attributeName == null) {
			return false;
		}
		int len = attributeName.length();
		for (int i = 0; i < len; i++){
			if (attributeName.charAt(i) == ATTIRBUTE_PATH_SEPARATOR) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param parentPath
	 * @param attributeId
	 * @return Returns the attribute path name 
	 */
	private static String createAttributePathName(Stack<Long> parentPath, long attributeId) {
		StringBuilder attributePathName = new StringBuilder();
		int count = 0;
		for (long ancestorAttributeId : parentPath) {
			if (count > 0) {
				attributePathName.append(ATTIRBUTE_PATH_SEPARATOR);
			}
			count++;
			attributePathName.append(ancestorAttributeId);
		}
		if (count > 0) {
			attributePathName.append(ATTIRBUTE_PATH_SEPARATOR);
		}
		attributePathName.append(attributeId);

		return attributePathName.toString();
	}

	/**
	 * @return the global unique ID of the entity
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, AttributeValue> getAttributes() {
		return attributes;
	}
	
	/**
	 * @return Returns the type of the entity.
	 */
	public String getType() {
		StringBuilder b = new StringBuilder();
		List<String> attributeTypExpression = new LinkedList<>();
		
		for (Entry<String, AttributeValue> entry : this.attributes.entrySet()) {
			attributeTypExpression.add(entry.getKey().substring(getEntityIdPrefix().length()) + ATTRIBUTE_TYPE_EXPR_SEPARATOR + entry.getValue().getType());
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
		AttributeValue value = this.attributes.get(attributeName);
		if (value == null){
			return null;
		}
		return value.getType();
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
			for (Entry<String, AttributeValue> attribute : attributes.entrySet()) {
				md.update(attribute.getKey().getBytes());
				AttributeValue av = attribute.getValue();
				if (av == null) {
					md.update(new byte[]{'n','u','l','l'});
				} else {
					String v = av.getValue();
					md.update(v == null ? new byte[]{'n','u','l','l'} :  v.getBytes());
					md.update(av.getType().getBytes());
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


}
