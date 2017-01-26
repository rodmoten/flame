/**
 * 
 */
package com.i4hq.flame.core;

import java.util.Collection;
import java.util.List;

/**
 * Implementations of this interface implement CRUD operations of Flame entities.
 * @author rmoten
 *
 */
public interface FlameEntityDAO {
	/**
	 * Write the given entity to persistent storage.
	 * @param entity
	 * @return Returns true if and only if the entity was persisted.
	 */
	public boolean save (FlameEntity entity);
	
	/**
	 * Write the given entities to persistent storage.
	 * The implementation may write all or some of the entities. 
	 * @param entities
	 * @return Returns the number of entities that were persisted.
	 */
	public int save (List<FlameEntity> entities);
	
	/**
	 * @param id
	 * @return Returns the entity with the given entity ID.
	 */
	public FlameEntity getEntitiesById (String id);
	
	/**
	 * @param ids
	 * @return Returns the entities with the given entity IDs. The list will contain at most ids.size() entities. Null is never returned.
	 */
	public Collection<FlameEntity> getEntitiesByIds (List<String> ids);
	
	/**
	 * @param attributePath
	 * @param value
	 * @return Returns the entities that contains the given attribute with the given value. 
	 */
	public Collection<FlameEntity> getEntitiesWithAttributeValue (String attributePath, String value);
	
	/**
	 * @param expr
	 * @return Returns the entities that satisfy the attribute expression.
	 */
	public Collection<FlameEntity> getEntitiesByAttributeExpression (AttributeExpression expr);
	
	/**
	 *  A DAO may buffer its contents for performance. This method forces it to persist any buffered contents it may have.
	 */
	public void flush();
	
	/**
	 * Determine if any attributes are geo locations. If so, make sure that the given entities are indexed by those locations.
	 * @param entityIds
	 */
	public void updateEntitiesWithGeoLoctions(Collection<String> entityIds);
	
}
