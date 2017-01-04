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
		FlameEntity entity = FlameEntity.createFromJson(entityIdFactory, attributeIdFactory, jsonText.toString());
		String expectedEntityId = "262";
		assertEquals("num of attributes", expectedNumOfAttributes, entity.size());
		String expectedType = "1:::NUMBER\n2:::STRING\n3:::STRING\n4:5:::BOOLEAN\n4:6:::STRING\n4:7:1:::STRING\n4:7:3:::STRING\n4:8:1:::STRING\n4:8:9:::NUMBER\n";
		assertEquals("type",expectedType, entity.getType());
		assertEquals("id(2)","9", entity.getAttribute(expectedEntityId + "::2").getValue());
		assertEquals("properties.attrs.name(4:7:3)","Sector", entity.getAttribute(expectedEntityId + "::4:7:3").getValue());
		
		// attribute not in entity
		assertEquals("properties.attrs.type(4:10:11)",null, entity.getAttribute("4:10:11"));		

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

	@Test
	public void testCreateFromCsv() throws Exception {
		File csvFile = new File("src/test/resources/few-dummy-entities-with-dups.csv");
		
		List<FlameEntity> actualEntities = FlameEntity.createFromCsv("entity_id", attributeIdFactory, csvFile);
		int expectedNumOfEntities = 5;
		assertEquals("number of entities", expectedNumOfEntities, actualEntities.size());
		// Assert the first entity
		String[][] firstEntity = {{"type", "0AFFD7D2-6366-47F8-945C-500FAC21C67E::2", "Equipment"}, {"name", "0AFFD7D2-6366-47F8-945C-500FAC21C67E::3","Trailer, Generator"}};
		assertAttribute(0,0,firstEntity, actualEntities);
		assertAttribute(1,0,firstEntity, actualEntities);

		// Assert the 3rd entity
		String[][] thirdEntity = {{"ANALYST_COMMENTS", "00914273-E995-42F0-A428-77170495311A::8", "NULL"}, {"name", "00914273-E995-42F0-A428-77170495311A::3","LJUBAN KONSTANTIN STANKIC"}};
		assertAttribute(0,2,thirdEntity, actualEntities);
		assertAttribute(1,2,thirdEntity, actualEntities);
		
		// Assert the last entity
		String[][] lastEntity = {{"LATITUDE", "11F7723C-6F4A-46B2-93A0-3F3E18D95910::4", "40.12242653"}, {"name", "11F7723C-6F4A-46B2-93A0-3F3E18D95910::3","352 AD BN"}};
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
