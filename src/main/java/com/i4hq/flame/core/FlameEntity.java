package com.i4hq.flame.core;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.opencsv.CSVReader;

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
	 * Create an new Flame Entity object.  
	 * @param entityId
	 * @param entityType
	 * @param attributes - the attributes of the entity. The m
	 * @return A new Flame Entity without any attributes.
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
	 * Create an entity from each line of a CSV file. One of the columns contains the entity IDs. 
	 * If two rows contains the same entity ID, then only the first row is read.
	 * The method assumes the first row contains the column names.
	 * 
	 * 
	 * @param entityIdColumn - name of the column that has the entity ID.
	 * @param attributeIdFactory
	 * @param csvFile
	 * @param skipDuplicates
	 * @return Returns a list of the entities created. The order of the entities in the list is the same as the order of the file.
	 * @throws IOException 
	 * @exception RuntimeException is thrown if no column matches the entityIdColumn.
	 */
	public static List<FlameEntity> createFromCsv (String entityIdColumn, AttributeIdFactory attributeIdFactory, File csvFile) throws IOException{
		// Create a reader to read each line.
		CSVReader reader = new CSVReader(new FileReader(csvFile));
		List<FlameEntity> entities = new LinkedList<>();

		// We use this variable to ensure we only get one entity for each entity ID. 
		Set<String> readEntities = new HashSet<>();

		try {
			int lineNumber = 1;
			// Get the columns.
			String [] columns;
			long[] attributeIds;
			columns = reader.readNext();
			if (columns == null){
				logger.error("No columns in CSV: {}", csvFile);
				throw new RuntimeException("No columns in CSV");
			}

			// Throw an exception if the entity ID column is not present.
			// In addition, get the attribute IDs for each of the column names. We will use them later when we read each row.
			attributeIds = new long[columns.length];
			int entityIdColumnIndex = -1;
			for (int i = 0; i < columns.length; i++){
				String columnName = columns[i];
				if (entityIdColumn.equalsIgnoreCase(columnName)) {
					entityIdColumnIndex = i;
				}
				attributeIds[i] = attributeIdFactory.getAttributeId(columnName);
			}
			if (entityIdColumnIndex < 0) {
				throw new RuntimeException(String.format("Entity ID column '%s' not present in '%s'", entityIdColumn, csvFile.getAbsolutePath()));
			}

			// Create an entity for each line.
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				lineNumber++;
				int numOfColumns = Math.min(columns.length, nextLine.length);
				// Skip blank lines.
				if (numOfColumns == 0) {
					continue;
				}
				if (numOfColumns <= entityIdColumnIndex) {
					logger.warn("Skipping entity at line {} in file '{}' because it doesn't have an entity ID.", lineNumber, csvFile.getName());
				}
				String entityId = nextLine[entityIdColumnIndex];
				FlameEntity entity = createEntity(entityId);
				// skip the entity if one with the same ID was previously read.
				if (readEntities.contains(entity.getId())) {
					continue;
				}
				
				for (int i = 0; i < numOfColumns; i++){
					// Don't make the entity ID an attribute.
					if (i == entityIdColumnIndex) {
						continue;
					}
					long attributeId = attributeIds[i];

					String attributeName = createAttributePathName(entity, null, attributeId);
					Object value = nextLine[i];
					entity.addAttribute(attributeName, value, AttributeType.STRING);
				}
				readEntities.add(entity.getId());
				entities.add(entity);

			}
		} catch (IOException ex) {
			logger.error("Error reading CSV file: {}", csvFile);
			throw ex;
		}

		finally {
			reader.close();
		}


		return entities;

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
				String attributePathName = createAttributePathName(entity, parentPath, attributeId);
				if (attributeValue.isJsonNull()) {
					entity.attributes.put(attributePathName, new AttributeValue(null, getAttributeTypeOfScalarValue(null)));
				} else {
					entity.attributes.put(attributePathName, new AttributeValue(attributeValue.getAsString(), getAttributeTypeOfScalarValue(attributeValue.getAsJsonPrimitive())));
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
	 * @param entity 
	 * @param parentPath
	 * @param attributeId
	 * @return Returns the attribute path name 
	 */
	private static String createAttributePathName(FlameEntity entity, Stack<Long> parentPath, long attributeId) {
		StringBuilder attributePathName = new StringBuilder(entity.getEntityIdPrefix());
		int count = 0;
		if (parentPath != null){
			for (long ancestorAttributeId : parentPath) {
				if (count > 0) {
					attributePathName.append(ATTIRBUTE_PATH_SEPARATOR);
				}
				count++;
				attributePathName.append(ancestorAttributeId);
			}
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
	 * @param attributePath
	 * @return Returns the value for this attribute. null will be return if the value of the attribute is null or if the entity doesn't contain the attribute.
	 */
	public AttributeValue getAttribute(String attributePath) {
		return attributes.get(attributePath);
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
			md.update(this.id.getBytes());
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



	/**
	 * @return Returns the attributes of this entity
	 */
	public Set<Entry<String, AttributeValue>> getAttributes() {
		return attributes.entrySet();
	}


}
