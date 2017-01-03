/**
 * 
 */
package com.i4hq.flame.in_memory;

import com.i4hq.flame.core.EntityIdFactory;

/**
 * @author rmoten
 *
 */
public class InMemoryEntityIdFactory implements EntityIdFactory {

	private int nextId = 1;
	public InMemoryEntityIdFactory () {
		
	}
	/* (non-Javadoc)
	 * @see com.i4hq.flame.EntityIdFactory#createId()
	 */
	@Override
	public synchronized String createId() {
		// TODO Auto-generated method stub
		return (nextId++)+"";
	}
	@Override
	public String createId(String seed) {
		return seed == null ? "-1" : seed.length() + "";
	}

}
