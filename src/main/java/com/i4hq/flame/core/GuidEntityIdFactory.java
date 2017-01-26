/**
 * 
 */
package com.i4hq.flame.core;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This entity ID factory uses the Java GUID as the entity ID.
 * @author rmoten
 *
 */
public class GuidEntityIdFactory implements EntityIdFactory {
	private static Logger logger = LoggerFactory.getLogger(GuidEntityIdFactory.class);

	private static GuidEntityIdFactory instance = new GuidEntityIdFactory();

	public static GuidEntityIdFactory getInstance() {
		return instance;
	}
	private GuidEntityIdFactory () {

	}

	/* (non-Javadoc)
	 * @see com.i4hq.flame.EntityIdFactory#createId()
	 */
	@Override
	public String createId() {
		return UUID.randomUUID().toString();
	}
	/**
	 * @param seed
	 * @return Returns the MD5 hash of <tt>seed</tt>.
	 */
	@Override
	public String createId(String seed) {
		if (seed == null){
			seed = "";
		}
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(seed.getBytes());

			return new BigInteger(1,md.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			logger.error("MD5", e);
			throw new RuntimeException(e);
		}
	}
}
