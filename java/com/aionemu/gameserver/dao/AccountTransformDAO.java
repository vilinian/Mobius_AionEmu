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
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.AccountTransfo;

/**
 * This class provides Data Access Object (DAO) operations for managing {@link AccountTransfo} records.<br>
 * It handles the persistence and retrieval of account transformation data from the database.
 */
public abstract class AccountTransformDAO implements DAO
{
	public abstract Map<Integer, AccountTransfo> loadAccountTransfo(Account account);
	
	public abstract boolean addTransfo(Account account, AccountTransfo trans);
	
	public abstract boolean updateTransfo(Account account, AccountTransfo transfo);
	
	public abstract void deleteTransfo(Account account, int id);
	
	/**
	 * Returns the name of the current class.<br>
	 * This is useful for identifying the {@code DAO} type in logs or configurations.
	 * @return The full name of the class as a {@code String}.
	 */
	@Override
	public String getClassName()
	{
		return AccountTransformDAO.class.getName();
	}
}
