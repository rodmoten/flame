/**
 * 
 */
package com.i4hq.flame.core;

/**
 * @author rmoten
 *
 */
public enum AttributeType {

	STRING(new ConvertValueToObject(){ public Object toJava(String s){ return s;}}),
	NUMBER(new ConvertValueToObject(){ public Object toJava(String s){
		try {
			return Long.parseLong(s);
		} catch(NumberFormatException ex){
			return Double.parseDouble(s);
		}
	}}),
	DATE(new ConvertValueToObject(){ public Object toJava(String s){ return Long.parseLong(s);}}),
	BOOLEAN(new ConvertValueToObject(){ public Object toJava(String s){ return Boolean.parseBoolean(s);}}),
	;

	/**
	 * The physical models need the Java type to determine how to map a literal to a literal in the physical model.
	 */
	private final ConvertValueToObject converter;
	private final byte[] bytes; // Used when creating hashes.
	/**
	 * @return the bytes
	 */
	public byte[] getBytes() {
		return bytes;
	}

	/**
	 * @param javaClass
	 */
	private AttributeType(ConvertValueToObject converter) {
		this.converter = converter;
		this.bytes = this.toString().getBytes();
	}

	public Object convertToJava(String value) {
		return converter.toJava(value);
	}

	public static AttributeType inferType(String value) {
		if (value == null || value.length() == 0) {
			return STRING;
		}
		char firstChar = value.charAt(0);
		
		if (Character.isDigit(firstChar)) {
			try {
				Double.parseDouble(value);
				return NUMBER;
			} catch (NumberFormatException ex) {
				return STRING;
			}
		}
		switch (firstChar) {
		case 't' : case 'T': case 'f' : case 'F': 
			try {
				Boolean.parseBoolean(value);
				return BOOLEAN;
			} catch (Exception ex) {
				return STRING;
			}
		case '+': case '.' : case '-':
			try {
				Double.parseDouble(value);
				return NUMBER;
			} catch (NumberFormatException ex) {
				return STRING;
			}
		default: return STRING;
		}
	}

	
}
