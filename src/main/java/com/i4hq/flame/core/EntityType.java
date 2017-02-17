/**
 * 
 */
package com.i4hq.flame.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rmoten
 *
 */
public class EntityType {
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
}
