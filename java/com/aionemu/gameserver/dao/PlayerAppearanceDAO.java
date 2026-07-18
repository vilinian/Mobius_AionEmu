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

// ~--- non-JDK imports --------------------------------------------------------

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerAppearance;

/**
 * This class handles the persistence of {@link PlayerAppearance} data.<br>
 * It provides methods to load and store appearance information for a {@link Player}.
 * @author SoulKeeper
 */
public abstract class PlayerAppearanceDAO implements DAO
{
	/**
	 * Returns the name of the current class.<br>
	 * This is useful for identifying the {@code DAO} type in logs or configurations.
	 * @return The full name of the class as a {@code String}.
	 */
	@Override
	public String getClassName()
	{
		return PlayerAppearanceDAO.class.getName();
	}
	
	/**
	 * Loads player apperance DAO by player ID.<br>
	 * Returns null if not found in database
	 * @param playerId player id
	 * @return player appearance or null
	 */
	public abstract PlayerAppearance load(int playerId);
	
	/**
	 * Saves the appearance data of a {@link Player} to the database.<br>
	 * This method persists the current appearance settings for the given player object.
	 * @param player The {@code Player} object whose appearance needs to be saved.
	 * @return {@code true} if the save operation was successful, or {@code false} otherwise.
	 */
	public boolean store(Player player)
	{
		return store(player.getObjectId(), player.getPlayerAppearance());
	}
	
	/**
	 * Stores appearance in database
	 * @param id player id
	 * @param playerAppearance player appearance
	 * @return true, if sql query was successful, false overwise
	 */
	public abstract boolean store(int id, PlayerAppearance playerAppearance);
}
