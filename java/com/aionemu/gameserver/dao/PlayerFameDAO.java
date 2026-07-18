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

import java.util.List;
import java.util.Map;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.fame.PlayerFame;

/**
 * This class provides data access operations for {@link PlayerFame} objects.<br>
 * It handles the persistence and retrieval of player fame records from the database.
 */
public abstract class PlayerFameDAO implements DAO
{
	public abstract Map<Integer, PlayerFame> loadPlayerFame(Player player);
	
	public abstract boolean addPlayerFame(Player player, PlayerFame fame);
	
	public abstract boolean updatePlayerFame(Player player, PlayerFame fame);
	
	public abstract List<PlayerFame> weeklyFame();
	
	public abstract boolean reduceWeekly(PlayerFame fame);
	
	/**
	 * Returns the name of the current class.<br>
	 * This is useful for identifying the {@code DAO} type in logs or configurations.
	 * @return The full name of the class as a {@code String}.
	 */
	@Override
	public String getClassName()
	{
		return PlayerFameDAO.class.getName();
	}
}
