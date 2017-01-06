/**
 * 
 */
package com.i4hq.flame.core;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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

/**
 * @author rmoten
 *
 */
public class FlameEntityFactory {
	private static Logger logger = LoggerFactory.getLogger(FlameEntity.class);
	private final static GsonBuilder gsonBuilder = new GsonBuilder();

	/**
	 * @param attributeNames
	 * @return Returns an attribute path using the given attribute names. 
	 */
	public static String createAttributePath(AttributeIdFactory attributeIdFactory, String ... attributeNames){
		StringBuilder b = new StringBuilder();
		int count = 0;
		for (String attributeName : attributeNames){
			if (count > 0) {
				b.append(FlameEntity.ATTIRBUTE_PATH_SEPARATOR);
			}
			b.append(attributeIdFactory.getAttributeId(attributeName));
			count++;
		}
		return b.toString();
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

	/**
	 * Create an entity from each line of a CSV file. One of the columns contains the entity IDs. 
	 * If two rows contains the same entity ID, then only the first row is read.
	 * The method assumes the first row contains the column names.
	 * 
	 * 
	 * @param entityIdColumn - name of the column that has the entity ID.
	 * @param attributeIdFactory
	 * @param csvFile
	 * @param longitudeColumn - name of the column that has an entity's longitude.
	 * @param latitudeColumn - name of the column that has an entity's latitude.
	 * @param nullWordMeansNull - when true, this mean will interpret the column value matching "NULL" (ignoring case) means the value is null.
	 * @return Returns a list of the entities created. The order of the entities in the list is the same as the order of the file.
	 * @throws IOException 
	 * @exception RuntimeException is thrown if no column matches the entityIdColumn.
	 */
	public static List<FlameEntity> createFromCsv (String entityIdColumn, AttributeIdFactory attributeIdFactory, File csvFile, String longitudeColumn, String latitudeColumn, boolean nullWordMeansNull) throws IOException{
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
			if (columns == null || columns.length == 0){
				logger.error("The first line doesn't contain the column headers in  {}", csvFile);
				throw new RuntimeException("No columns in CSV");
			}
	
			// For some reason, the first character in the CSV is 65279. As a result, we remove it from the first column header.
			if (columns[0].charAt(0) == 65279){
				columns[0] = columns[0].substring(1);
			}
			// Throw an exception if the entity ID column is not present.
			// In addition, get the attribute IDs for each of the column names. We will use them later when we read each row.
			attributeIds = new long[columns.length];
			int entityIdColumnIndex = -1;
			int longitudeColumnIndex = -1;
			int latitudeColumnIndex = -1;
			for (int i = 0; i < columns.length; i++){
				String columnName = columns[i];
				if (entityIdColumn.equalsIgnoreCase(columnName)) {
					entityIdColumnIndex = i;
				} else if (columnName.equalsIgnoreCase(longitudeColumn)) {
					longitudeColumnIndex = i;
				} else if (columnName.equalsIgnoreCase(latitudeColumn)) {
					latitudeColumnIndex = i;
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
				Double longitude = null;
				Double latitude = null;
				
				// skip the entity if another entity with the same ID was previously read.
				if (readEntities.contains(entity.getId())) {
					continue;
				}
				
				for (int i = 0; i < numOfColumns; i++){
					// Don't make the entity ID an attribute
					if (i == entityIdColumnIndex) {
						continue;
					}
					long attributeId = attributeIds[i];
	
					String attributeName = createAttributePathName(null, attributeId);
					String value = nextLine[i];
					// In some CSV files, the word NULL means the value should be NULL.
					if (nullWordMeansNull && "NULL".equalsIgnoreCase(value)) {
						value = null;
					}
					entity.addAttribute(attributeName, value, AttributeType.STRING);
					
					// Save the value of the attribute, if it is attribute  the longitude or latitude attribute.
					if (longitudeColumnIndex == i) {
						longitude = parseDouble(value);
					} else if (latitudeColumnIndex == i){
						latitude = parseDouble(value);
					}
					
				}
				// If row has a lat and long for the entity, then use them as the entity's location.
				if(longitude != null && latitude !=null) {
					entity.setLocation(longitude, latitude);
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
	 * @param value
	 * @return
	 */
	private static Double parseDouble(String value) {
		try {
			return value == null ? null : Double.parseDouble(value);
		} catch (NumberFormatException ex) {
			logger.warn("Failed to parse value '{}' as a double. It probably is supposed to be used as a latitude or longitude.", value);
			return null;
		}
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
		Gson gson = gsonBuilder.create();
	
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
					entity.addAttribute(attributePathName, null, getAttributeTypeOfScalarValue(null));
				} else {
					entity.addAttribute(attributePathName,attributeValue.getAsString(), getAttributeTypeOfScalarValue(attributeValue.getAsJsonPrimitive()));
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

	/**
	 * @param attributeValue
	 * @return
	 */
	static AttributeType getAttributeTypeOfScalarValue(JsonPrimitive attributeValue) {
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
	static boolean validAttributeName(String attributeName) {
		if (attributeName == null) {
			return false;
		}
		int len = attributeName.length();
		for (int i = 0; i < len; i++){
			if (attributeName.charAt(i) == FlameEntity.ATTIRBUTE_PATH_SEPARATOR) {
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
		if (parentPath != null){
			for (long ancestorAttributeId : parentPath) {
				if (count > 0) {
					attributePathName.append(FlameEntity.ATTIRBUTE_PATH_SEPARATOR);
				}
				count++;
				attributePathName.append(ancestorAttributeId);
			}
		}
		if (count > 0) {
			attributePathName.append(FlameEntity.ATTIRBUTE_PATH_SEPARATOR);
		}
		attributePathName.append(attributeId);
	
		return attributePathName.toString();
	}

}
