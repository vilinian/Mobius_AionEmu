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

import com.aionemu.gameserver.model.house.HouseRegistry;

/**
 * This class provides Data Access Object (DAO) operations for managing items registered to players.<br>
 * It handles the persistence and retrieval of player-specific item data within the database.
 * @author Rolandas
 */
public abstract class PlayerRegisteredItemsDAO implements IDFactoryAwareDAO
{
	/**
	 * Returns the name of the current class.<br>
	 * This is useful for identifying the {@code DAO} type in logs or configurations.
	 * @return The full name of the class as a {@code String}.
	 */
	@Override
	public String getClassName()
	{
		return PlayerRegisteredItemsDAO.class.getName();
	}
	
	public abstract void loadRegistry(int playerId);
	
	public abstract boolean store(HouseRegistry registry, int playerId);
	
	public abstract boolean deletePlayerItems(int playerId);
	
	public abstract void resetRegistry(int playerId);
}
