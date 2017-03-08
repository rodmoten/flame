package com.i4hq.flame.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Stack;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;



/**
 * This class reads an XML file and converts it into a Flame Entity.
 * @author Rod Moten
 *
 */
public class XmlToFlameEntityTransformer {
	private XMLInputFactory factory = null;

	private static XmlToFlameEntityTransformer instance = new XmlToFlameEntityTransformer();

	public static XmlToFlameEntityTransformer getInstance() {
		return instance;
	}
	private XmlToFlameEntityTransformer () {
		init();
	}
	/**
	 * Initialize the extractor.
	 */
	private void init () {
		factory = XMLInputFactory.newInstance();
	}


	public FlameEntity process(String inputFile, String attributePathOfId) throws XMLStreamException, IOException {
		XMLEventReader reader = createReader(inputFile);
		try {
			return extractElements(reader, attributePathOfId);
		} finally{
			reader.close();
		}
	}

	protected XMLEventReader createReader (String filename) throws XMLStreamException, IOException {
		//		BufferedReader reader = new BufferedReader (new FileReader (filename));
		//		StringBuilder xml = new StringBuilder();
		//		while (reader.ready()){
		//			String readLine = reader.readLine().replace("\n", "");
		//			xml.append(readLine);
		//		}
		//		reader.close();
		//		StringReader sr = new StringReader(xml.toString());
		//		factory.createXMLEventReader(sr);
		XMLEventReader xmlr = factory.createXMLEventReader(filename,
				new FileInputStream(filename));

		// when XMLStreamReader is created, 
		// it is positioned at START_DOCUMENT event.
		// Therefore, read pass it.		
		return xmlr;
	}

	private class Accumulator{
		int numOfChildren;
	}
	protected FlameEntity extractElements (XMLEventReader xmlr, String attributePathOfId) throws XMLStreamException {
		// check if there are more events 
		// in the input stream
		String entityId = null;
		Stack<String> pathStack = new Stack<>();
		Stack<Accumulator> numOfChildrenStack = new Stack<>();
		numOfChildrenStack.push(new Accumulator());
		AttributeValue value = null;

		FlameEntity fe = new FlameEntity("");
		while(xmlr.hasNext()) {
			XMLEvent event = xmlr.nextEvent();
			switch (event.getEventType()) {
			case XMLEvent.START_ELEMENT:
				numOfChildrenStack.peek().numOfChildren++;
				numOfChildrenStack.push(new Accumulator());
				StartElement startElement = event.asStartElement();
				String elementName = startElement.getName().getLocalPart();
				if (pathStack.isEmpty()){
					pathStack.push(elementName);
				} else {
					pathStack.push(pathStack.peek() + FlameEntity.ATTIRBUTE_PATH_SEPARATOR + elementName);
				}
				break;

			case XMLEvent.END_ELEMENT:
				if (!pathStack.isEmpty()) {
					String currentAttributePath = pathStack.pop();
					if (numOfChildrenStack.pop().numOfChildren == 0){
						if (value != null) {
							if (attributePathOfId != null && attributePathOfId.equals(currentAttributePath)) {
								entityId = value.getValue();
							} else{
								fe.addAttribute(currentAttributePath, value.getValue(), value.getType());
							}
							value = null;
						}
					}
				} 
				break;
			case XMLEvent.CHARACTERS:
				value = processValue(event.asCharacters());
			default: 
				;
			}
		}

		if (entityId == null){
			entityId = GuidEntityIdFactory.getInstance().createId();
		}
		fe = new FlameEntity(entityId, fe);
		return fe;
	}

	private AttributeValue processValue(Characters asCharacters) {
		String data = asCharacters.getData();
		try {
			Double.parseDouble(data);
			return new AttributeValue(data, AttributeType.NUMBER);
		} catch (Exception ex) {
		}
		return new AttributeValue(data, AttributeType.STRING);
	}

}