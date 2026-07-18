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
package com.aionemu.gameserver.model.gameobjects.player;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import com.aionemu.gameserver.model.templates.atreianpassport.AtreianPassportTemplate;

/**
 * Represents an {@link AtreianPassport} object for a player character.<br>
 * This class manages the data and properties associated with the passport item.
 * @author Falke_34
 */
public class AtreianPassport
{
	private final SortedMap<Integer, AtreianPassportTemplate> passports = new TreeMap<>();
	
	/**
	 * Adds a new passport to the collection.<br>
	 * This method checks if the {@code id} already exists.<br>
	 * It returns {@code false} if the ID is taken.<br>
	 * Otherwise, it saves the template and returns {@code true}.
	 * @param id The unique identifier for the passport.
	 * @param ap The {@link AtreianPassportTemplate} to be added.
	 * @return {@code true} if the passport was added successfully, or {@code false} otherwise.
	 */
	public synchronized boolean addPassport(int id, AtreianPassportTemplate ap)
	{
		if (passports.containsKey(id))
		{
			return false;
		}
		
		passports.put(id, ap);
		return true;
	}
	
	/**
	 * Removes a passport from the collection based on its unique identifier.<br>
	 * This method is thread-safe because it is {@code synchronized}.
	 * @param id The unique {@code int} ID of the passport to remove.
	 * @return {@code true} if the passport was successfully removed, or {@code false} if the ID was not found.
	 */
	public synchronized boolean removePassport(int id)
	{
		if (passports.containsKey(id))
		{
			passports.remove(id);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retrieves all available passport templates.<br>
	 * This method returns the values stored in the internal map.
	 * @return a {@code Collection} of {@link AtreianPassportTemplate} objects.
	 */
	public Collection<AtreianPassportTemplate> getAllPassports()
	{
		return passports.values();
	}
}
