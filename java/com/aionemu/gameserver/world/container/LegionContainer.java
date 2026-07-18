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
package com.aionemu.gameserver.world.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.world.exceptions.DuplicateAionObjectException;

/**
 * This class serves as a container for managing {@link Legion} objects.<br>
 * It stores legions indexed by their unique {@code legionId} and name.
 * @author Simple
 */
public class LegionContainer implements Iterable<Legion>
{
	/**
	 * Map<LegionId, Legion>
	 */
	private final Map<Integer, Legion> legionsById = new ConcurrentHashMap<>();
	/**
	 * Map<LegionName, Legion>
	 */
	private final Map<String, Legion> legionsByName = new ConcurrentHashMap<>();
	
	/**
	 * Adds a {@link Legion} to the container.<br>
	 * This method stores the object by its ID and name.<br>
	 * It will throw a {@code DuplicateAionObjectException} if the data already exists.
	 * @param legion The {@code Legion} object to add.
	 */
	public void add(Legion legion)
	{
		if ((legion == null) || (legion.getLegionName() == null))
		{
			return;
		}
		
		if ((legionsById.put(legion.getLegionId(), legion) != null) || (legionsByName.put(legion.getLegionName().toLowerCase(), legion) != null))
		{
			throw new DuplicateAionObjectException();
		}
	}
	
	/**
	 * Removes a specific {@link Legion} from the container.<br>
	 * This method deletes the entry using both the legion ID and name.
	 * @param legion The {@code Legion} object to be removed.
	 */
	public void remove(Legion legion)
	{
		legionsById.remove(legion.getLegionId());
		legionsByName.remove(legion.getLegionName().toLowerCase());
	}
	
	/**
	 * Retrieves a {@link Legion} object based on its unique ID.<br>
	 * This method looks up the data in the internal map.
	 * @param legionId The unique integer identifier for the legion.
	 * @return The {@code Legion} associated with the provided ID, or {@code null} if not found.
	 */
	public Legion get(int legionId)
	{
		return legionsById.get(legionId);
	}
	
	/**
	 * Retrieves a {@link Legion} object based on its name.<br>
	 * The search is case-insensitive.
	 * @param name The name of the legion to find.
	 * @return The matching {@code Legion} object, or {@code null} if not found.
	 */
	public Legion get(String name)
	{
		return legionsByName.get(name.toLowerCase());
	}
	
	/**
	 * Retrieves all legions currently stored in the container.<br>
	 * This method returns a {@code List} containing every {@link Legion} object.
	 * @return A {@code List} of all {@link Legion} objects.
	 */
	public List<Legion> getAllLegions()
	{
		final List<Legion> list = new ArrayList<>();
		list.addAll(legionsByName.values());
		return list;
	}
	
	/**
	 * Checks if a specific legion exists in the container.<br>
	 * This method uses the {@code legionId} to perform the search.
	 * @param legionId The unique identifier of the legion to find.
	 * @return {@code true} if the legion is found, otherwise {@code false}.
	 */
	public boolean contains(int legionId)
	{
		return legionsById.containsKey(legionId);
	}
	
	/**
	 * Checks if a legion exists in the container by its name.<br>
	 * The search is case-insensitive.
	 * @param name The name of the legion to look for.
	 * @return {@code true} if the name exists, otherwise {@code false}.
	 */
	public boolean contains(String name)
	{
		return legionsByName.containsKey(name.toLowerCase());
	}
	
	/**
	 * Returns an {@link Iterator} to loop through all the legions.<br>
	 * This allows you to visit every {@code Legion} in this container.
	 * @return An {@code Iterator} containing all {@code Legion} objects.
	 */
	@Override
	public Iterator<Legion> iterator()
	{
		return legionsById.values().iterator();
	}
	
	/**
	 * Removes all legions from this container.<br>
	 * This clears both the ID and name maps.<br>
	 * The container will be empty after this call.
	 */
	public void clear()
	{
		legionsById.clear();
		legionsByName.clear();
	}
}
