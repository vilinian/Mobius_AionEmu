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
package com.aionemu.gameserver.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.items.storage.StorageType;

/**
 * This class provides Data Access Object (DAO) operations for managing player inventories.<br>
 * It handles the persistence and retrieval of {@link Item} objects for {@link Player} accounts.<br>
 * It also manages interactions with various {@link Storage} types.
 * @author ATracer
 */
public abstract class InventoryDAO implements IDFactoryAwareDAO
{
	/**
	 * @param playerId
	 * @param storageType
	 * @return IStorage
	 */
	public abstract Storage loadStorage(int playerId, StorageType storageType);
	
	public abstract List<Item> loadStorageDirect(int playerId, StorageType storageType);
	
	/**
	 * @param player
	 * @return Equipment
	 */
	public abstract Equipment loadEquipment(Player player);
	
	/**
	 * @param playerId
	 * @return
	 */
	public abstract List<Item> loadEquipment(int playerId);
	
	public abstract boolean store(Player player);
	
	public abstract boolean store(Item item, Player player);
	
	/**
	 * Saves a specific {@code Item} to the storage of a player.<br>
	 * This method uses the {@code playerId} to locate the correct inventory.<br>
	 * It returns {@code true} if the operation succeeds.<br>
	 * It returns {@code false} if there is an error or insufficient space.
	 * @param item The {@code Item} object to be stored.
	 * @param playerId The unique ID of the player owning the storage.
	 * @return A boolean indicating whether the storage was successful.
	 */
	public boolean store(Item item, int playerId)
	{
		return store(Collections.singletonList(item), playerId);
	}
	
	public abstract boolean store(List<Item> items, int playerId);
	
	/**
	 * Saves an {@code Item} to the database for a specific player.<br>
	 * This method links the item to the correct account and legion.
	 * @param item The {@code Item} object to be saved.
	 * @param playerId The unique ID of the player owning the item.
	 * @param accountId The unique ID of the player's account.
	 * @param legionId The unique ID of the player's legion.
	 * @return {@code true} if the save was successful, otherwise {@code false}.
	 */
	public boolean store(Item item, Integer playerId, Integer accountId, Integer legionId)
	{
		final List<Item> temp = new ArrayList<>();
		temp.add(item);
		return store(temp, playerId, accountId, legionId);
	}
	
	public abstract boolean store(List<Item> items, Integer playerId, Integer accountId, Integer legionId);
	
	/**
	 * @param playerId
	 * @return
	 */
	public abstract boolean deletePlayerItems(int playerId);
	
	public abstract void deleteAccountWH(int accountId);
	
	/**
	 * Returns the name of the current class.<br>
	 * This is useful for identifying the {@code DAO} type in logs or configurations.
	 * @return The full name of the class as a {@code String}.
	 */
	@Override
	public String getClassName()
	{
		return InventoryDAO.class.getName();
	}
}
