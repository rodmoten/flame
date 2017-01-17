package com.i4hq.flame;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.i4hq.flame.core.AttributeIdFactory;
import com.i4hq.flame.core.EntityIdFactory;
import com.i4hq.flame.core.FlameEntity;
import com.i4hq.flame.core.FlameEntityFactory;
import com.i4hq.flame.in_memory.InMemoryAttributeIdFactory;
import com.i4hq.flame.in_memory.InMemoryEntityIdFactory;

/**
 * @author rmoten
 *
 */
public class FlameEntityTest {

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
		FlameEntity entity = FlameEntityFactory.createFromJson(entityIdFactory, attributeIdFactory, jsonText.toString());
		assertEquals("num of attributes", expectedNumOfAttributes, entity.size());
		String expectedType = "id:::STRING\nname:::STRING\nproperties:attrs:name:::STRING\nproperties:attrs:type:::STRING\nproperties:geometry:angle:::NUMBER\n"
				+ "properties:geometry:type:::STRING\nproperties:report:::BOOLEAN\nproperties:style:::STRING\n"
				+ "type:::NUMBER\n";
		System.out.println(entity.getType());
		assertEquals("type",expectedType, entity.getType());
		assertEquals("id","9", entity.getAttribute("id").getValue());
		assertEquals("properties.attrs.name(properties.attrs.name)","Sector", entity.getAttribute("properties:attrs:name").getValue());
		
		// attribute not in entity
		assertEquals("properties.attrs.type(4:10:11)",null, entity.getAttribute("4:10:11"));		

	}

	@Test(expected=IllegalArgumentException.class)
	public void testCreateFromJson_arrays() throws Exception {
		StringBuilder jsonText = readJsonFromFile("src/test/resources/entity-negative-test1.json");
		FlameEntityFactory.createFromJson(entityIdFactory, attributeIdFactory, jsonText.toString());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreateFromJson_invalidFieldName() throws IOException {
		StringBuilder jsonText = readJsonFromFile("src/test/resources/entity-negative-test2.json");
		FlameEntityFactory.createFromJson(entityIdFactory, attributeIdFactory, jsonText.toString());
	}

	@Test
	public void testCreateFromCsv() throws Exception {
		File csvFile = new File("src/test/resources/few-dummy-entities-with-dups.csv");
		
		List<FlameEntity> actualEntities = FlameEntityFactory.createFromCsv("entity_id", attributeIdFactory, csvFile, "LONGITUDE", "LATITUDE", false);
		int expectedNumOfEntities = 5;
		assertEquals("number of entities", expectedNumOfEntities, actualEntities.size());
		// Assert the first entity
		String[][] firstEntity = {{"type", "type", "Equipment"}, {"name", "name","Trailer, Generator"}};
		assertAttribute(0,0,firstEntity, actualEntities);
		assertAttribute(1,0,firstEntity, actualEntities);

		// Assert the 3rd entity
		String[][] thirdEntity = {{"ANALYST_COMMENTS", "ANALYST_COMMENTS", "NULL"}, {"name", "name","LJUBAN KONSTANTIN STANKIC"}};
		assertAttribute(0,2,thirdEntity, actualEntities);
		assertAttribute(1,2,thirdEntity, actualEntities);
		
		// Assert the last entity
		String[][] lastEntity = {{"LATITUDE", "LATITUDE", "40.12242653"}, {"name", "name","352 AD BN"}};
		assertAttribute(0,4,lastEntity, actualEntities);
		assertAttribute(1,4,lastEntity, actualEntities);
	}

	
	/**
	 * @param indexOfExpectedAttribute
	 * @param indexOfActualEntity
	 * @param expectedEntity
	 * @param actualEntities
	 */
	private void assertAttribute(int indexOfExpectedAttribute, int indexOfActualEntity, String[][] expectedEntity, List<FlameEntity> actualEntities) {
		assertEquals(expectedEntity[indexOfExpectedAttribute][0], 
					actualEntities.get(indexOfActualEntity).getAttribute(expectedEntity[indexOfExpectedAttribute][1]).getValue(), 
					expectedEntity[indexOfExpectedAttribute][2]);
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
