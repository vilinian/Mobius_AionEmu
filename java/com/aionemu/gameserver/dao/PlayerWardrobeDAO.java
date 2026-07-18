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
import com.aionemu.gameserver.model.dorinerk_wardrobe.PlayerWardrobeList;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class provides Data Access Object (DAO) operations for managing player wardrobes.<br>
 * It handles the persistence and retrieval of {@link PlayerWardrobeList} data associated with a {@link Player}.
 */
public abstract class PlayerWardrobeDAO implements DAO
{
	/**
	 * Returns the name of the current class.<br>
	 * This is useful for identifying the {@code DAO} type in logs or configurations.
	 * @return The full name of the class as a {@code String}.
	 */
	@Override
	public String getClassName()
	{
		return PlayerWardrobeDAO.class.getName();
	}
	
	public abstract PlayerWardrobeList load(Player paramPlayer);
	
	public abstract boolean store(int paramInt1, int paramInt2, int paramInt3, int reskin);
	
	public abstract boolean delete(int paramInt, int paramInt2);
	
	public abstract int getItemSize(int playerId);
	
	public abstract int getWardrobeItemBySlot(int playerObjId, int slot);
	
	public abstract int getReskinCountBySlot(int playerObjId, int slot);
	
	public abstract boolean setReskinCountBySlot(int playerObjId, int slot, int reskin_count);
}
