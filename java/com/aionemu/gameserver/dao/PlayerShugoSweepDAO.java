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

/**
 * This class provides Data Access Object (DAO) operations for managing player Shugo Sweep data.<br>
 * It handles the persistence and retrieval of sweep-related information for {@link Player} objects.
 * @author Ghostfur
 */
public abstract class PlayerShugoSweepDAO implements DAO
{
	
	/**
	 * Returns the name of the current class.<br>
	 * This is useful for identifying the {@code DAO} type in logs or configurations.
	 * @return The full name of the class as a {@code String}.
	 */
	@Override
	public String getClassName()
	{
		return PlayerShugoSweepDAO.class.getName();
	}
	
	public abstract void load(Player player);
	
	public abstract boolean add(int playerId, int freeDice, int step, int boardId);
	
	public abstract boolean delete();
	
	public abstract boolean store(Player player);
	
	public abstract boolean setShugoSweepByObjId(int obj, int freeDice, int step, int boardId);
}
