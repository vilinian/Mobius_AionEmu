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

import java.util.Map;

import com.aionemu.commons.database.dao.DAO;

/**
 * This class provides data access methods for managing player-specific variables.<br>
 * It handles the persistence of custom attributes stored in the database.<br>
 * It extends {@link DAO} to provide a structured way to interact with player data.
 * @author KID
 */
public abstract class PlayerVarsDAO implements DAO
{
	/**
	 * Returns the name of the current class.<br>
	 * This is useful for identifying the {@code DAO} type in logs or configurations.
	 * @return The full name of the class as a {@code String}.
	 */
	@Override
	public String getClassName()
	{
		return PlayerVarsDAO.class.getName();
	}
	
	public abstract Map<String, Object> load(int playerId);
	
	public abstract boolean set(int playerId, String key, Object value);
	
	public abstract boolean remove(int playerId, String key);
}
