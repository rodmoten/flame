/**
 * 
 */
package com.i4hq.flame.core;

/**
 * @author rmoten
 *
 */
public class DuplicateMetadataException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5174700064525860793L;
	
	private final String duplicateName;
	private final String message;

	public DuplicateMetadataException(String name) {
		this.duplicateName = name;
		this.message = "Multiple metadata items with the name " + duplicateName;

	}

	public String getDuplicateName() {
		return duplicateName;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getLocalizedMessage() {
		return getMessage();
	}

}
