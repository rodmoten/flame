package com.i4hq.flame.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class XmlToFlameEntityTransformerTest {

	private XmlToFlameEntityTransformer transformer = XmlToFlameEntityTransformer.getInstance();
	private int guidLength = 36;
	
	@Test
	public void testProcess_flat_has_ID() throws Exception {
		FlameEntity fe = transformer.process("src/test/resources/flat.xml", "containsAnElement:theID");
		assertEquals("I contain text", fe.getAttribute("containsAnElement:containsOnlyText").getValue());
		assertEquals("blah", fe.getId());
		assertEquals("emptyContent", null, fe.getAttribute("containsAnElement:emptyContent"));
		assertEquals("theId", null, fe.getAttribute("containsAnElement:theID"));
	}
	
	@Test
	public void testProcess_flat_has_guid_ID() throws Exception {
		FlameEntity fe = transformer.process("src/test/resources/flat.xml", null);
		assertEquals("I contain text", fe.getAttribute("containsAnElement:containsOnlyText").getValue());
		assertEquals("length of ID", guidLength, fe.getId().length());
		assertEquals("emptyContent", null, fe.getAttribute("containsAnElement:emptyContent"));
		assertEquals("theId", "blah", fe.getAttribute("containsAnElement:theID").getValue());
	}

	@Test
	public void testProcess_multiValueAttributes_noID() throws Exception {
		FlameEntity fe = transformer.process("src/test/resources/multiple-values.xml", null);
		assertEquals("length of ID", guidLength, fe.getId().length());

		// There should be 12 values of each attribute except     
		String[] attributeNames = {"catalog:book:author", "catalog:book:title", "catalog:book:genre", "catalog:book:price", "catalog:book:publish_date", "catalog:book:description"};
		String controlVariableName = "catalog:book:control_variable";
		for (String attributeName : attributeNames){
			assertEquals("size of " + attributeName, 12, fe.getAttributes(attributeName).size());
		}
		
		assertAttributeValue(controlVariableName, "56.0cool", AttributeType.STRING, fe);
		assertAttributeValue("catalog:book:author", "Galos, Mike", AttributeType.STRING, fe);
		assertAttributeValue("catalog:book:price", "49.95", AttributeType.NUMBER, fe);
	}
	
	@Test
	public void testProcess_structuredXml_hasID() throws Exception {
		FlameEntity fe = transformer.process("src/test/resources/structured.xml", "shiporder:orderperson");
		assertEquals("ID", "John Smith", fe.getId());
		assertAttributeValue("shiporder:shipto:address:country:item:note:item:title", "Empire Burlesque", AttributeType.STRING, fe);
	}


	private void assertAttributeValue(String attributePath, String expectedValue, AttributeType expectedType, FlameEntity actualEntity) {
		AttributeValue actualValue = actualEntity.getAttribute(attributePath);
		assertEquals(attributePath + ": value", expectedValue, actualValue.getValue());
		assertEquals(attributePath + ": type", expectedType, actualValue.getType());

	}
}
