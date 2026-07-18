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

/**
 * This class provides the Data Access Object (DAO) interface for managing server-wide variables.<br>
 * It handles the persistence and retrieval of global configuration settings from the database.
 * @author Ben
 */
public abstract class ServerVariablesDAO implements DAO
{
	/**
	 * Returns the name of the current class.<br>
	 * This is useful for identifying the {@code DAO} type in logs or configurations.
	 * @return The full name of the class as a {@code String}.
	 */
	@Override
	public String getClassName()
	{
		return ServerVariablesDAO.class.getName();
	}
	
	/**
	 * Loads the server variables stored in the database
	 * @param var
	 * @return
	 * @returns variable stored in database
	 */
	public abstract int load(String var);
	
	/**
	 * Stores the server variables
	 * @param var
	 * @param value
	 * @return
	 */
	public abstract boolean store(String var, int value);
}
