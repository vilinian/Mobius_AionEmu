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
package com.aionemu.loginserver.dao;

import java.util.List;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.loginserver.service.ptransfer.PlayerTransferTask;

/**
 * This class provides data access operations for handling player transfers.<br>
 * It serves as a base {@link DAO} for managing transfer-related database records.
 * @author KID
 */
public abstract class PlayerTransferDAO implements DAO
{
	public abstract List<PlayerTransferTask> getNew();
	
	public abstract boolean update(PlayerTransferTask task);
	
	/**
	 * Returns the name of the current class.<br>
	 * This is useful for identifying the {@code DAO} type in logs or configurations.
	 * @return The full name of the class as a {@code String}.
	 */
	@Override
	public String getClassName()
	{
		return PlayerTransferDAO.class.getName();
	}
}
