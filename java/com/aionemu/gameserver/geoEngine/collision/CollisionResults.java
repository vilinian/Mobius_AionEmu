/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.geoEngine.collision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * This class holds the results of a collision check performed by the {@code GeoEngine}.<br>
 * It provides an iterable collection of all {@code CollisionResult} objects detected during the process.
 */
public class CollisionResults implements Iterable<CollisionResult>
{
	private final ArrayList<CollisionResult> results = new ArrayList<>();
	private boolean sorted = true;
	private final boolean onlyFirst;
	private final byte intentions;
	private final int instanceId;
	
	/**
	 * Creates a new {@link CollisionResults} object.<br>
	 * This constructor initializes the collision data with specific settings.
	 * @param intentions The movement goals of the entity.
	 * @param searchFirst Set to {@code true} if only the first result is needed.
	 * @param instanceId The unique identifier for the current instance.
	 */
	public CollisionResults(byte intentions, boolean searchFirst, int instanceId)
	{
		this.intentions = intentions;
		onlyFirst = searchFirst;
		this.instanceId = instanceId;
	}
	
	/**
	 * Removes all elements from the results list.<br>
	 * The {@code size()} will become 0 after this call.<br>
	 * This method does not affect other collections.
	 */
	public void clear()
	{
		results.clear();
	}
	
	/**
	 * Returns an {@link Iterator} to loop through all collision results.<br>
	 * This method ensures the internal list is sorted before returning the iterator.
	 * @return An {@code Iterator} of {@link CollisionResult} objects.
	 */
	@Override
	public Iterator<CollisionResult> iterator()
	{
		if (!sorted)
		{
			Collections.sort(results);
			sorted = true;
		}
		
		return results.iterator();
	}
	
	/**
	 * Adds a new {@code CollisionResult} to the collection.<br>
	 * This method ignores results where the distance is {@code NaN}.<br>
	 * It updates the internal sorting state if multiple results are allowed.
	 * @param result The {@code CollisionResult} object to add.
	 */
	public void addCollision(CollisionResult result)
	{
		if (Float.isNaN(result.getDistance()))
		{
			return;
		}
		
		results.add(result);
		if (!onlyFirst)
		{
			sorted = false;
		}
	}
	
	/**
	 * Returns the number of collision results.<br>
	 * This count represents all items currently stored in the internal list.
	 * @return The total number of {@code CollisionResult} objects.
	 */
	public int size()
	{
		return results.size();
	}
	
	/**
	 * Finds the collision result that is nearest to the player.<br>
	 * This method sorts the internal list if it is not already ordered.<br>
	 * It returns the first item in the sorted collection.
	 * @return The closest {@link CollisionResult} or {@code null} if no collisions exist.
	 */
	public CollisionResult getClosestCollision()
	{
		if (size() == 0)
		{
			return null;
		}
		
		if (!sorted)
		{
			Collections.sort(results);
			sorted = true;
		}
		
		return results.get(0);
	}
	
	/**
	 * Retrieves the collision result that is furthest away.<br>
	 * This method sorts the internal list if it is not already sorted.<br>
	 * It returns {@code null} if no collisions exist.
	 * @return The farthest {@link CollisionResult} or {@code null}.
	 */
	public CollisionResult getFarthestCollision()
	{
		if (size() == 0)
		{
			return null;
		}
		
		if (!sorted)
		{
			Collections.sort(results);
			sorted = true;
		}
		
		return results.get(size() - 1);
	}
	
	/**
	 * Retrieves a specific collision result from the list.<br>
	 * This method ensures the results are sorted before returning the item.
	 * @param index The position of the collision to retrieve.
	 * @return The {@code CollisionResult} at the specified index.
	 */
	public CollisionResult getCollision(int index)
	{
		if (!sorted)
		{
			Collections.sort(results);
			sorted = true;
		}
		
		return results.get(index);
	}
	
	/**
	 * Retrieves a specific collision result from the list.<br>
	 * This method uses the provided index to access the internal collection.
	 * @param index The position of the result in the list.
	 * @return The {@code CollisionResult} at the specified position.
	 */
	public CollisionResult getCollisionDirect(int index)
	{
		return results.get(index);
	}
	
	/**
	 * Returns a string representation of the collision results.<br>
	 * This method lists all {@link CollisionResult} objects in the collection.<br>
	 * The output is formatted as a bracketed list.
	 * @return A string describing the current contents of this object.
	 */
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("CollisionResults[");
		for (CollisionResult result : results)
		{
			sb.append(result).append(", ");
		}
		
		if (results.size() > 0)
		{
			sb.setLength(sb.length() - 2);
		}
		
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * Checks if this result set contains only the first collision.<br>
	 * This is determined by the {@code onlyFirst} flag.
	 * @return {@code true} if only the first collision was requested, {@code false} otherwise.
	 */
	public boolean isOnlyFirst()
	{
		return onlyFirst;
	}
	
	/**
	 * Retrieves the intention flags for this collision result.<br>
	 * This value was set during the creation of the {@link CollisionResults} object.
	 * @return The {@code byte} value representing the intentions.
	 */
	public byte getIntentions()
	{
		return intentions;
	}
	
	/**
	 * Retrieves the unique identifier for this instance.<br>
	 * This ID was provided during the construction of {@link CollisionResults}.
	 * @return The {@code int} value representing the instance ID.
	 */
	public int getInstanceId()
	{
		return instanceId;
	}
}
