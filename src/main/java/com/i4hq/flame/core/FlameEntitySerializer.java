/**
 * 
 */
package com.i4hq.flame.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 *Transforms an FlameEntity to and from JSON
 * @author rmoten
 *
 */
public class FlameEntitySerializer {
	private final static GsonBuilder gsonBuilder = new GsonBuilder();

	/**
	 * Converts a FlameEntity to a JSON with the following format.
	 * {
	 *    "id": string,
	 *    "longitude": number,
	 *    "latitude":number,
	 *    "attributes": [
	 *    	{"name": string, "values" : [{"value": string, "type": string "metadata":[{"name": string, "value":string}]}
	 *    ]
	 * }
	 * @param entity
	 * @return Returns a serialized JSON object.
	 */
	public String serialize (FlameEntity entity){
		JsonObject serializedEntity = toJsonObject(entity);
		
		return serializedEntity.toString();
	}
	
	/**
	 * @param entities
	 * @return
	 */
	public String serialize(Collection<FlameEntity> entities){
		JsonObject serializedEntities = new JsonObject();
		JsonArray serializedEntityArray = new JsonArray();
		serializedEntities.add("entities", serializedEntityArray);
		for (FlameEntity entity : entities) {
			serializedEntityArray.add(serialize(entity));
		}
		return serializedEntities.toString();
	}


	/**
	 * @param entity
	 * @return
	 */
	private JsonObject toJsonObject(FlameEntity entity) {
		JsonObject serializedEntity = new JsonObject();
		serializedEntity.addProperty("id", entity.getId());
		Geo2DPoint geoPosition = entity.getGeospatialPosition();
		if (geoPosition != null){
			serializedEntity.addProperty("longitude", geoPosition.getLongitude());
			serializedEntity.addProperty("latitude", geoPosition.getLatitude());
		}

		// Create "attributes" field
		JsonArray attributesArray = new JsonArray();
		serializedEntity.add("attributes", attributesArray);

		// Add attribute objects to the array in the "attributes" field.
		for (Entry<String, List<Attribute>> attributeEntry : entity.getAttributes()){
			JsonObject attributeObj = new JsonObject();
			attributesArray.add(attributeObj);
			
			String attributeName = attributeEntry.getKey();
			attributeObj.addProperty("name", attributeName);

			JsonArray valueArray = new JsonArray();
			attributeObj.add("values", valueArray);
			
			// Convert list of attribute values to a JSON array of attribute values
			List<Attribute> attributeValues = attributeEntry.getValue();
			for (Attribute av : attributeValues){
				JsonObject valueObj = new JsonObject();
				valueObj.addProperty("value", av.getValue());
				valueObj.addProperty("type", av.getType().toString());
				
				// Convert list of metadata to a JSON array of metadata
				List<MetadataItem> metadata = av.getMetadata();
				JsonArray metadatArray = new JsonArray();
				for(MetadataItem mi : metadata){
					JsonObject obj = new JsonObject();
					obj.addProperty("value", mi.getValue());
					obj.addProperty("name", mi.getName());
					metadatArray.add(obj);
				}
				if (metadatArray.size() > 0){
					valueObj.add("metadata", metadatArray);
				}
				valueArray.add(valueObj);
			}
			
		}
		return serializedEntity;
	}
	
	@SuppressWarnings("unused")
	private class SerializedMetdata{
		private String name;
		private String value;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		
		public MetadataItem toMetadataItem(){
			return new MetadataItem(name, value);
		}
	}
	
	@SuppressWarnings("unused")
	private class SerializedAttributeValue {
		private String value;
		private String type;
		private List<SerializedMetdata> metadata = new LinkedList<>();
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public List<SerializedMetdata> getMetadata() {
			if (metadata == null) {
				metadata = new LinkedList<>();
			}
			return metadata;
		}
		public void setMetadata(List<SerializedMetdata> metadata) {
			this.metadata = metadata;
		}		
	}

	@SuppressWarnings("unused")
	private class SerializedAttribute{
		private String name;
		private List<SerializedAttributeValue> values;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<SerializedAttributeValue> getValues() {
			return values;
		}
		public void setValues(List<SerializedAttributeValue> values) {
			this.values = values;
		}
		
		
	}
	@SuppressWarnings("unused")
	private class SerializedEntity{
		private String id;
		private Double longitude;
		private Double latitude;
		private List<SerializedAttribute> attributes;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public Double getLongitude() {
			return longitude;
		}
		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}
		public Double getLatitude() {
			return latitude;
		}
		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}
		public List<SerializedAttribute> getAttributes() {
			return attributes;
		}
		public void setAttributes(List<SerializedAttribute> attributes) {
			this.attributes = attributes;
		}
	}
		

	public FlameEntity deserialize(String json){
		Gson gson = gsonBuilder.create();
		SerializedEntity se = gson.fromJson(json, SerializedEntity.class);
		
		FlameEntity fe = FlameEntityFactory.createEntity(se.getId());
		if (se.getLongitude() != null){
			fe.setLongitude(se.getLongitude());
		}
		if (se.getLatitude() != null){
			fe.setLatitude(se.getLatitude());
		}
		
		for (SerializedAttribute attr : se.getAttributes()){
			for (SerializedAttributeValue value : attr.getValues()) {
				MetadataItem[] metadata= new MetadataItem[value.getMetadata().size()];
				for (int i = 0; i < metadata.length; i++){
					metadata[i] = value.getMetadata().get(i).toMetadataItem();
				}
				fe.addAttribute(attr.getName(), value.getValue(), AttributeType.valueOf(value.getType()), metadata);
			}
		}
		
		return fe;
	}

}
