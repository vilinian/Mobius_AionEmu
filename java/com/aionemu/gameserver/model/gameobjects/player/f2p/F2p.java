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
package com.aionemu.gameserver.model.gameobjects.player.f2p;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.Free2PlayDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;

/**
 * Represents a Free-to-Play (F2P) player account within the game server.<br>
 * This class manages specific data and behaviors unique to {@link Player} types with F2P status.
 * @author teenwolf
 */
public class F2p
{
	private final Player owner;
	private F2pAccount f2pAccount;
	
	/**
	 * Creates a new {@code F2p} instance for a specific player.<br>
	 * This object links the {@link Player} to their free-to-play account data.
	 * @param owner The {@code Player} who owns this {@code F2p} object.
	 */
	public F2p(Player owner)
	{
		this.owner = owner;
	}
	
	/**
	 * Adds a {@code F2pAccount} to the player.<br>
	 * Sets the account as active.<br>
	 * If {@code isNew} is {@code true}, it schedules an expiration task and saves the data to the database.
	 * @param f2pacc The {@code F2pAccount} object to add.
	 * @param isNew A boolean flag indicating if this is a new account registration.
	 */
	public void add(F2pAccount f2pacc, boolean isNew)
	{
		f2pAccount = f2pacc;
		f2pacc.setActive(true);
		if (isNew)
		{
			if (f2pacc.getExpireTime() != 0)
			{
				ExpireTimerTask.getInstance().addTask(f2pacc, owner);
			}
			
			DAOManager.getDAO(Free2PlayDAO.class).storeF2p(owner.getPlayerAccount().getId(), f2pacc.getExpireTime());
		}
	}
	
	/**
	 * Updates the {@code F2pAccount} associated with this object.<br>
	 * Sets the account as active and handles initialization for new accounts.<br>
	 * If {@code isNew} is {@code true}, it schedules expiration tasks and updates the database.
	 * @param f2pacc The {@code F2pAccount} object to update.
	 * @param isNew A boolean flag indicating if this is a new account registration.
	 */
	public void update(F2pAccount f2pacc, boolean isNew)
	{
		f2pAccount = f2pacc;
		f2pacc.setActive(true);
		if (isNew)
		{
			if (f2pacc.getExpireTime() != 0)
			{
				ExpireTimerTask.getInstance().addTask(f2pacc, owner);
			}
			
			DAOManager.getDAO(Free2PlayDAO.class).updateF2p(owner.getPlayerAccount().getId(), f2pacc.getExpireTime());
		}
	}
	
	/**
	 * Retrieves the {@code F2pAccount} associated with this object.<br>
	 * This method returns the account data for the player.
	 * @return The {@link F2pAccount} instance or {@code null} if no account exists.
	 */
	public F2pAccount getF2pAccount()
	{
		return f2pAccount;
	}
	
	/**
	 * Removes the {@link F2pAccount} associated with this player.<br>
	 * It deactivates the account and deletes it from the database.<br>
	 * This method also triggers a rank limit check on the owner's equipment.
	 * @return {@code true} if an account was successfully removed, or {@code false} if no account existed.
	 */
	public boolean remove()
	{
		if (f2pAccount != null)
		{
			f2pAccount.setActive(false);
			DAOManager.getDAO(Free2PlayDAO.class).deleteF2p(owner.getPlayerAccount().getId());
			owner.getEquipment().checkRankLimitItems();
			return true;
		}
		
		return false;
	}
}
