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
import com.aionemu.gameserver.model.gameobjects.player.FriendList;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class provides data access operations for managing player friend lists.<br>
 * It handles the persistence of {@link FriendList} objects associated with a {@link Player}.
 * @author Ben
 */
public abstract class FriendListDAO implements DAO
{
	/**
	 * Returns the name of the current class.<br>
	 * This is useful for identifying the {@code DAO} type in logs or configurations.
	 * @return The full name of the class as a {@code String}.
	 */
	@Override
	public String getClassName()
	{
		return FriendListDAO.class.getName();
	}
	
	/**
	 * Loads the friend list for the given player
	 * @param player Player to get friend list of
	 * @return FriendList for player
	 */
	public abstract FriendList load(Player player);
	
	/**
	 * Makes the given players friends
	 * <ul>
	 * <li>Note: Adds for both players</li>
	 * </ul>
	 * @param player Player who is adding
	 * @param friend Friend to add to the friend list
	 * @return Success
	 */
	public abstract boolean addFriends(Player player, Player friend);
	
	/**
	 * Deletes the friends from eachothers lists
	 * @param playerOid
	 * @param friendOid
	 * @return Success
	 */
	public abstract boolean delFriends(int playerOid, int friendOid);
	
	/**
	 * Set Note for friends
	 * @param playerId
	 * @param friendId
	 * @param notice
	 */
	public abstract void setFriendNote(int playerId, int friendId, String notice);
}
