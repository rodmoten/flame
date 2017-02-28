/**
 * 
 */
package com.i4hq.flame.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author rmoten
 *
 */
public class EntityType {
	private static Logger logger = LoggerFactory.getLogger(EntityType.class);
	
	private final Map<String, AttributeType> attributeDecls = new HashMap<>();

	private final long age;

	private final AttributeDecl firstDecl;
	
	/**
	 * @param attributeDecls
	 * @param age
	 */
	public EntityType(long age, AttributeDecl ... attributeDecls) {
		super();
		this.age = 0;
		if (attributeDecls == null) {
			firstDecl = null;
			return;
		}
		firstDecl = attributeDecls.length == 0 ? null : attributeDecls[0];
		for (AttributeDecl d : attributeDecls){
			this.attributeDecls.put(d.getName(), d.getType());
		}
	}
	
	

	/**
	 * @param attributeDecls
	 */
	public EntityType(AttributeDecl ... attributeDecls) {
		this(0, attributeDecls);
	}

	/**
	 * @return the age
	 */
	public long getAge() {
		return age;
	}


	public int numOfAttributes() {
		return attributeDecls.size();
	}

	public boolean contains(AttributeDecl attributeDecl) {
		AttributeType t = attributeDecls.get(attributeDecl.getName());
		return t == null ? false : t == attributeDecl.getType();
	}



	public AttributeDecl getFirstDecl() {
		return firstDecl;
	}	
	
	public boolean hasType(FlameEntity entity){
		Set<Entry<String, List<AttributeValue>>> attributes = entity.getAttributes();
		// The entity's attributes must be a superset of the entity type's attributes.
		if (attributes.size() < attributeDecls.size()) {
			return false;
		}
		
		for (Entry<String, AttributeType> decl : attributeDecls.entrySet()){
			final String attributeName = decl.getKey();
			List<AttributeValue> values = entity.getAttributes(attributeName);
			
			if (values == null || values.isEmpty()){
				logger.debug("Not in type because null or empty list for attribute {}", attributeName);
				return false;
			}
			
			AttributeType declaredTyped = decl.getValue();
			for (AttributeValue value : values){
				if (value == null){
					continue;
				}
				if (value.getType() != declaredTyped){
					return false;
				}
			}
		}
		return true;
		
	}
}
