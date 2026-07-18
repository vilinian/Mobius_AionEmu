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
package com.aionemu.gameserver.model.gameobjects.player.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.gameserver.model.templates.collection.CollectionType;

/**
 * Represents a collection object owned by a player.<br>
 * It manages the data and state for various types of player collections.<br>
 * This class is used to track items and progress within a {@link CollectionType}.
 */
public class PlayerCollection
{
	private Map<CollectionType, PlayerCollectionInfos> collectionInfos;
	private List<PlayerCollectionEntry> completeCollection;
	private Map<Integer, PlayerCollectionEntry> playerCollectionEntry;
	
	/**
	 * Creates a new instance of {@code PlayerCollection}.<br>
	 * This constructor initializes all internal collections.<br>
	 * It sets up the maps and lists to be empty and ready for use.
	 */
	public PlayerCollection()
	{
		collectionInfos = new HashMap<>();
		completeCollection = new ArrayList<>();
		playerCollectionEntry = new HashMap<>();
	}
	
	/**
	 * Retrieves the list of all completed collections for the player.<br>
	 * This method returns the {@code completeCollection} list.
	 * @return A {@code List} of {@link PlayerCollectionEntry} objects.
	 */
	public List<PlayerCollectionEntry> getCompleteCollection()
	{
		return completeCollection;
	}
	
	/**
	 * Updates the list of completed collections for the player.<br>
	 * This method sets the {@code completeCollection} field.
	 * @param completeCollection The new list of {@link PlayerCollectionEntry} objects to store.
	 */
	public void setCompleteCollection(List<PlayerCollectionEntry> completeCollection)
	{
		this.completeCollection = completeCollection;
	}
	
	/**
	 * Retrieves the map of player collection entries.<br>
	 * This method returns all entries associated with a player's collection.
	 * @return A {@code Map} where the key is an {@code Integer} and the value is a {@link PlayerCollectionEntry}.
	 */
	public Map<Integer, PlayerCollectionEntry> getPlayerCollectionEntry()
	{
		return playerCollectionEntry;
	}
	
	/**
	 * Updates the internal map of {@code PlayerCollectionEntry} objects.<br>
	 * This method sets the data for the player's collection entries.
	 * @param playerCollectionEntry The new {@code Map} to associate with this collection.
	 */
	public void setPlayerCollectionEntry(Map<Integer, PlayerCollectionEntry> playerCollectionEntry)
	{
		this.playerCollectionEntry = playerCollectionEntry;
	}
	
	/**
	 * Retrieves the information for all player collections.<br>
	 * It returns a map where each {@code CollectionType} is linked to its specific data.
	 * @return A {@code Map} containing {@code CollectionType} keys and their corresponding {@link PlayerCollectionInfos}.
	 */
	public Map<CollectionType, PlayerCollectionInfos> getCollectionInfos()
	{
		return collectionInfos;
	}
	
	/**
	 * Sets the information for player collections.<br>
	 * This method updates the {@code collectionInfos} map.
	 * @param collectionInfos The map containing {@link CollectionType} and their corresponding {@link PlayerCollectionInfos}.
	 */
	public void setCollectionInfos(Map<CollectionType, PlayerCollectionInfos> collectionInfos)
	{
		this.collectionInfos = collectionInfos;
	}
}
