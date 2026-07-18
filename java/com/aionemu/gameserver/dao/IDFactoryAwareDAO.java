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
 * This interface is a generic base for all DAO classes that generate unique identifiers.<br>
 * It ensures that the DAO uses {@link com.aionemu.gameserver.utils.idfactory.IDFactory} to create IDs.
 * @author SoulKeeper
 */
public interface IDFactoryAwareDAO extends DAO
{
	/**
	 * Returns array of all id's that are used by this DAO
	 * @return array of used id's
	 */
	public int[] getUsedIDs();
}
