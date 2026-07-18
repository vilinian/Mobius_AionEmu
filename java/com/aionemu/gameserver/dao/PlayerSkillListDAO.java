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

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillList;

/**
 * This class provides Data Access Object (DAO) operations for managing {@link com.aionemu.gameserver.model.skill.PlayerSkillList} objects.<br>
 * It handles the persistence and retrieval of player skill data from the database.
 * @author IceReaper, orfeo087, Avol, AEJTester
 */
public abstract class PlayerSkillListDAO implements DAO
{
	/**
	 * Returns the name of the current class.<br>
	 * This is useful for identifying the {@code DAO} type in logs or configurations.
	 * @return The full name of the class as a {@code String}.
	 */
	@Override
	public String getClassName()
	{
		return PlayerSkillListDAO.class.getName();
	}
	
	/**
	 * Returns a list of skilllist for player
	 * @param playerId Player object id.
	 * @return a list of skilllist for player
	 */
	public abstract PlayerSkillList loadSkillList(int playerId);
	
	/**
	 * Updates skill with new information
	 * @param player
	 * @return
	 */
	public abstract boolean storeSkills(Player player);
}
