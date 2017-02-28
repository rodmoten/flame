package com.i4hq.flame.core;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EntityTypeTest {

	private EntityType type1;
	@Before
	public void setUp() throws Exception {
		type1 = new EntityType(new AttributeDecl("string1", AttributeType.STRING), 
				new AttributeDecl("boolean", AttributeType.BOOLEAN), 
				new AttributeDecl("string2", AttributeType.STRING), 
				new AttributeDecl("number", AttributeType.NUMBER),
				new AttributeDecl("reference", AttributeType.REFERENCE));
	}

	@Test
	public void testHasType_happy() {
		String entityInJson = "{'string1':'abcd', 'boolean':true, 'string2':'xyz', 'number':45}";

		FlameEntity fissure = FlameEntityFactory.createFromJson(GuidEntityIdFactory.getInstance(), entityInJson);
		fissure.addAttribute("reference", "http://some.io", AttributeType.REFERENCE, new MetadataItem("frog", "feet"));
		assertEquals(true, type1.hasType(fissure));
	}

	@Test
	public void testHasType_subsumption() {
		String entityInJson = "{'string1':'abcd', 'extra_attribute': 99.0, 'boolean':true, 'string2':'xyz', 'number':45}";

		FlameEntity fissure = FlameEntityFactory.createFromJson(GuidEntityIdFactory.getInstance(), entityInJson);
		fissure.addAttribute("reference", "http://some.io", AttributeType.REFERENCE, new MetadataItem("frog", "feet"));
		assertEquals(true, type1.hasType(fissure));
	}

	@Test
	public void testHasType_nullAttributeValue() {
		String entityInJson = "{'string1':'abcd', 'boolean':null, 'string2':'xyz', 'number':45}";

		FlameEntity fissure = FlameEntityFactory.createFromJson(GuidEntityIdFactory.getInstance(), entityInJson);
		fissure.addAttribute("reference", "http://some.io", AttributeType.REFERENCE, new MetadataItem("frog", "feet"));
		assertEquals(false, type1.hasType(fissure));
	}
	
	@Test
	public void testHasType_emptyList() {
		String entityInJson = "{'string1':'abcd', 'boolean':[], 'string2':'xyz', 'number':45}";

		FlameEntity fissure = FlameEntityFactory.createFromJson(GuidEntityIdFactory.getInstance(), entityInJson);
		fissure.addAttribute("reference", "http://some.io", AttributeType.REFERENCE, new MetadataItem("frog", "feet"));
		assertEquals(false, type1.hasType(fissure));
	}


	@Test
	public void testHasType_listUniformed() {
		String entityInJson = "{'string1':'abcd', 'boolean':false, 'string2':'xyz', 'number':45}";

		FlameEntity fissure = FlameEntityFactory.createFromJson(GuidEntityIdFactory.getInstance(), entityInJson);
		fissure.addAttribute("reference", "http://some.io", AttributeType.REFERENCE, new MetadataItem("frog", "feet"));

		fissure.addAttribute("string1", "efg", AttributeType.STRING);
		fissure.addAttribute("string1", "hij", AttributeType.STRING);
		fissure.addAttribute("string1", "klmno", AttributeType.STRING);

		assertEquals(true, type1.hasType(fissure));
	}

	@Test
	public void testHasType_listMixedValue() {
		String entityInJson = "{'string1':'abcd', 'boolean':false, 'string2':'xyz', 'number':45}";

		FlameEntity fissure = FlameEntityFactory.createFromJson(GuidEntityIdFactory.getInstance(), entityInJson);
		fissure.addAttribute("reference", "http://some.io", AttributeType.REFERENCE, new MetadataItem("frog", "feet"));

		fissure.addAttribute("string1", "efg", AttributeType.STRING);
		fissure.addAttribute("string1", 45, AttributeType.NUMBER);
		fissure.addAttribute("string1", "klmno", AttributeType.STRING);

		assertEquals(false, type1.hasType(fissure));
	}


	@Test
	public void testHasType_notInType() {
		String entityInJson = "{'string1':12, 'boolean':'45', '2gnirts':'xyz', 'int':45}";

		FlameEntity fissure = FlameEntityFactory.createFromJson(GuidEntityIdFactory.getInstance(), entityInJson);
		fissure.addAttribute("reference", "http://some.io", AttributeType.REFERENCE, new MetadataItem("frog", "feet"));
		assertEquals(false, type1.hasType(fissure));	
	}

}
