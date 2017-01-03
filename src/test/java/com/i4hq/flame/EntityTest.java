package com.i4hq.flame;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import com.i4hq.flame.core.AttributeIdFactory;
import com.i4hq.flame.core.FlameEntity;
import com.i4hq.flame.core.EntityIdFactory;
import com.i4hq.flame.in_memory.InMemoryAttributeIdFactory;
import com.i4hq.flame.in_memory.InMemoryEntityIdFactory;

public class EntityTest {

	private EntityIdFactory entityIdFactory = new InMemoryEntityIdFactory();
	private AttributeIdFactory attributeIdFactory = new InMemoryAttributeIdFactory();

	@Test
	public void testCreateFromJson() throws IOException {
		/*
Attribute names		 
type (1::1) NUMBER
id (1::2)
name (1::3)
properties.report 1::4:5
properties.style 1::4:6
properties.attrs.type 1::4:7:1
properties.attrs.name  1::4:7:3
properties.geometry.type 1::4:8:1
properties.geometry.angle 1::4:8:9
		 */
		int expectedNumOfAttributes = 9;
		StringBuilder jsonText = readJsonFromFile("src/test/resources/entity-positive-test.json");
		FlameEntity entity = FlameEntity.createFromJson(entityIdFactory, attributeIdFactory, jsonText.toString());
		String expectedEntityId = "262";
		assertEquals("num of attributes", expectedNumOfAttributes, entity.getAttributes().size());
		String expectedType = "1:::NUMBER\n2:::STRING\n3:::STRING\n4:5:::BOOLEAN\n4:6:::STRING\n4:7:1:::STRING\n4:7:3:::STRING\n4:8:1:::STRING\n4:8:9:::NUMBER\n";
		assertEquals("type",expectedType, entity.getType());
		assertEquals("id(2)","9", entity.getAttributes().get(expectedEntityId + "::2").getValue());
		assertEquals("properties.attrs.name(4:7:3)","Sector", entity.getAttributes().get(expectedEntityId + "::4:7:3").getValue());
		
		// attribute not in entity
		assertEquals("properties.attrs.type(4:10:11)",null, entity.getAttributes().get("4:10:11"));		

	}

	@Test(expected=IllegalArgumentException.class)
	public void testCreateFromJson_arrays() throws Exception {
		StringBuilder jsonText = readJsonFromFile("src/test/resources/entity-negative-test1.json");
		FlameEntity.createFromJson(entityIdFactory, attributeIdFactory, jsonText.toString());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreateFromJson_invalidFieldName() throws IOException {
		StringBuilder jsonText = readJsonFromFile("src/test/resources/entity-negative-test2.json");
		FlameEntity.createFromJson(entityIdFactory, attributeIdFactory, jsonText.toString());
	}

	/**
	 * @param filePath 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private StringBuilder readJsonFromFile(String filePath) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader (new FileReader (filePath));
		StringBuilder jsonText = new StringBuilder();
		while (reader.ready()) {
			jsonText.append(reader.readLine());
			jsonText.append('\n');
		}
		reader.close();
		return jsonText;
	}

}
