package com.i4hq.flame.core;

import static org.junit.Assert.assertEquals;

import java.io.FileReader;
import java.io.Reader;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class FlameEntitySerializerTest {

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private String test1;
	private String test2;
	private String test3;
	private String test4;
	private FlameEntitySerializer serializer = new FlameEntitySerializer();

	@Before
	public void setUp() throws Exception {
		Reader reader = new FileReader("src/test/resources/serializer-tests.json");
		JsonObject tests = gson.fromJson(reader, JsonObject.class);
		test1 = tests.getAsJsonObject("happyPath").toString();
		test2 = tests.getAsJsonObject("metadata").toString();
		test3 = tests.getAsJsonObject("no_attributes").toString();
		test4 = tests.getAsJsonObject("no_geo_blank_metadata").toString();
	}

	@Test
	public void testSerialize() {
		FlameEntity fe = FlameEntityFactory.createEntity("abcde");
		fe.setLatitude(7.77);
		fe.addAttribute("attr1", 8.99, AttributeType.NUMBER);
		fe.addAttribute("attr2", "s1", AttributeType.STRING);
		fe.addAttribute("attr2", "s2", AttributeType.STRING);
		fe.addAttribute("attr3", 3.5, AttributeType.NUMBER, new MetadataItem("marking", "U"));
		String expected = "{\"id\":\"abcde\",\"longitude\":0.0,\"latitude\":7.77,\"attributes\":[{\"name\":\"attr2\",\"values\":[{\"value\":\"s2\",\"type\":\"STRING\"},"
				+ "{\"value\":\"s1\",\"type\":\"STRING\"}]},"
				+ "{\"name\":\"attr1\",\"values\":[{\"value\":\"8.99\",\"type\":\"NUMBER\"}]},"
				+ "{\"name\":\"attr3\",\"values\":[{\"value\":\"3.5\",\"type\":\"NUMBER\",\"metadata\":[{\"value\":\"U\",\"name\":\"marking\"}]}]}]}";
		String actual = serializer.serialize(fe);
		assertEquals(expected, actual);
	}

	@Test
	public void testDeserialize_happyPath() {
		FlameEntity actual = serializer.deserialize(test1);
		assertEquals("id", "test1", actual.getId());
		assertEquals("latitude", "45.6", actual.getGeospatialPosition().getLatitude() + "");
		assertEquals("longitude", "-9.5455", actual.getGeospatialPosition().getLongitude() + "");

		assertAttribute("attr1", 0, "NUMBER", "777", 0, actual); 
		assertAttribute("attr2", 1, "NUMBER", "777", 0, actual); 
		assertAttribute("attr2", 0, "STRING", "believe", 0, actual); 
		
	}
	
	@Test
	public void testDeserialize_no_geo_blank_metadata() {
		FlameEntity actual = serializer.deserialize(test4);
		assertEquals("id", "test4", actual.getId());
		assertEquals("geo", null, actual.getGeospatialPosition());

		assertAttribute("attr1", 0, "NUMBER", "777", 0, actual); 
		assertAttribute("attr2", 1, "NUMBER", "777", 0, actual); 
		assertAttribute("attr2", 0, "STRING", "believe", 0, actual); 
		
	}
	
	@Test
	public void testDeserialize_no_attributes() {
		FlameEntity actual = serializer.deserialize(test3);
		assertEquals("id", "test3", actual.getId());
		assertEquals(0, actual.getAttributes().size());
		assertEquals("latitude", "45.6", actual.getGeospatialPosition().getLatitude() + "");
		assertEquals("longitude", "0.0", actual.getGeospatialPosition().getLongitude() + "");

	}
	
	@Test
	public void testDeserialize_metadata() {
		FlameEntity actual = serializer.deserialize(test2);
		assertEquals("id", "test2", actual.getId());
		assertEquals("latitude", "45.6", actual.getGeospatialPosition().getLatitude() + "");
		assertEquals("longitude", "-9.5455", actual.getGeospatialPosition().getLongitude() + "");

		assertAttribute("attr1", 0, "NUMBER", "777", 1, actual); 
		assertAttribute("attr2", 1, "NUMBER", "777", 2, actual); 
		assertAttribute("attr2", 0, "STRING", "believe", 0, actual); 
		
	}
	
	private void assertAttribute(String attributeName, int valueIndex, String expectedType, String expectedValue, int expectedMetadataSize, FlameEntity actualEntity){
		assertEquals(attributeName + " value " + valueIndex + " value",expectedValue , actualEntity.getAttributes(attributeName).get(valueIndex).getValue());
		assertEquals(attributeName + " value " + valueIndex + " type", expectedType, actualEntity.getAttributes(attributeName).get(valueIndex).getType().toString());
		assertEquals(attributeName + " value " + valueIndex + " metadata size", expectedMetadataSize, actualEntity.getAttributes(attributeName).get(valueIndex).getMetadata().size());
	}

}
