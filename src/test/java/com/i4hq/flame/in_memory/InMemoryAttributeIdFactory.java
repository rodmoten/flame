/**
 * 
 */
package com.i4hq.flame.in_memory;

import java.util.HashMap;
import java.util.Map;

import com.i4hq.flame.core.AttributeIdFactory;

/**
 * @author rmoten
 *
 */
public class InMemoryAttributeIdFactory implements AttributeIdFactory {

	private long nextId = 1;
	private Map<String, Long> ids = new HashMap<>();
	
	public InMemoryAttributeIdFactory () {
		
	}
	
	
	/* (non-Javadoc)
	 * @see com.i4hq.flame.AttributeIdFactory#getAttributeId(java.lang.String)
	 */
	@Override
	public long getAttributeId(String attributeName) {
		Long id = ids.get(attributeName);
		if (id != null) {
			return id;
		}
		
		id = nextId++;
		ids.put(attributeName, id);
		return id;
	}

}
