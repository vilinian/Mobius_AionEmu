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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages the collection of all macros for a player.<br>
 * It serves as a container to store and retrieve macro data associated with a {@link com.aionemu.gameserver.model.gameobjects.player.Player}.
 * @author Aquanox, nrg
 */
public class MacroList
{
	/**
	 * Class logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(MacroList.class);
	/**
	 * Container of macrosses, position to xml.
	 */
	private final Map<Integer, String> macrosses;
	
	/**
	 * Creates a new instance of {@link MacroList}.<br>
	 * This initializes an empty collection for macrosses.
	 */
	public MacroList()
	{
		macrosses = new HashMap<>(12);
	}
	
	/**
	 * Creates a new {@link MacroList} using the provided map.<br>
	 * This constructor initializes the internal collection of macrosses.
	 * @param arg The map containing macro positions and their XML strings.
	 */
	public MacroList(Map<Integer, String> arg)
	{
		macrosses = arg;
	}
	
	/**
	 * Retrieves the collection of all player macrosses.<br>
	 * The map uses an {@code Integer} key for the position and a {@code String} value for the XML data.<br>
	 * This method returns an unmodifiable view of the internal map.
	 * @return A {@link Map} containing the macro positions and their corresponding XML strings.
	 */
	public Map<Integer, String> getMacrosses()
	{
		return Collections.unmodifiableMap(macrosses);
	}
	
	/**
	 * Adds a new macro to the list at a specific position.<br>
	 * This method updates the entry if it already exists.
	 * @param macroPosition The unique index for the macro.
	 * @param macroXML The XML string content of the macro.
	 * @return {@code true} if a new macro was added, or {@code false} if an existing one was overwritten.
	 */
	public synchronized boolean addMacro(int macroPosition, String macroXML)
	{
		if (macrosses.containsKey(macroPosition))
		{
			macrosses.remove(macroPosition);
			macrosses.put(macroPosition, macroXML);
			return false;
		}
		
		macrosses.put(macroPosition, macroXML);
		return true;
	}
	
	/**
	 * Removes a macro from the list based on its position.<br>
	 * This method is thread-safe because it is {@code synchronized}.<br>
	 * It returns {@code false} if no macro exists at the given position.
	 * @param macroPosition The index of the macro to remove.
	 * @return {@code true} if the removal was successful, or {@code false} otherwise.
	 */
	public synchronized boolean removeMacro(int macroPosition)
	{
		final String m = macrosses.remove(macroPosition);
		if (m == null)//
		{
			logger.warn("Trying to remove non existing macro.");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Returns the total number of macrosses in the list.<br>
	 * This value corresponds to the size of the internal {@code macrosses} map.
	 * @return The count of macrosses as an {@code int}.
	 */
	public int getSize()
	{
		return macrosses.size();
	}
	
	/**
	 * Retrieves a specific subset of macros from the collection.<br>
	 * This method returns either the first or second part of the macro list.
	 * @param secondPart Set to {@code true} to get the second part, or {@code false} for the first part.
	 * @return An unmodifiable {@link Map} containing the requested macros.
	 */
	public Map<Integer, String> getMarcosPart(boolean secondPart)
	{
		final Map<Integer, String> macrosPart = new HashMap<>();
		int currentIndex = secondPart ? 7 : 0;
		final int endIndex = secondPart ? 11 : 6;
		
		for (; currentIndex <= endIndex; currentIndex++)
		{
			macrosPart.put(currentIndex, macrosses.get(currentIndex));
		}
		
		return Collections.unmodifiableMap(macrosPart);
	}
}
