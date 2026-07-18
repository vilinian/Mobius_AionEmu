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

import com.aionemu.gameserver.model.IExpirable;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Represents a Free-to-Play (F2P) account within the game server.<br>
 * This class manages account data and handles expiration logic via {@link IExpirable}.<br>
 * It serves as a specialized model for players who do not have premium status.
 */
public class F2pAccount implements IExpirable
{
	private int deleteTime = 0;
	private boolean active = false;
	
	/**
	 * Creates a new {@link F2pAccount} instance.<br>
	 * Sets the initial expiration time for the account.
	 * @param deletionTime The timestamp when the account should be deleted.
	 */
	public F2pAccount(int deletionTime)
	{
		deleteTime = deletionTime;
	}
	
	/**
	 * Calculates the time left before the account expires.<br>
	 * This value is based on the {@code deleteTime}.<br>
	 * The result is returned in seconds as an {@code int}.
	 * @return The remaining time in seconds.
	 */
	public int getRemainingTime()
	{
		if (deleteTime == 0)
		{
			return 0;
		}
		
		return deleteTime - (int) (System.currentTimeMillis() / 1000L);
	}
	
	/**
	 * Retrieves the expiration time of this object.<br>
	 * This value represents when the object will expire.
	 * @return The expiration time as an {@code int}.
	 */
	@Override
	public int getExpireTime()
	{
		return deleteTime;
	}
	
	/**
	 * Checks if the {@code F2pAccount} is currently active.<br>
	 * This method returns the current status of the account.
	 * @return {@code true} if the account is active, {@code false} otherwise.
	 */
	public boolean getActive()
	{
		return active;
	}
	
	/**
	 * Updates the active status of this {@link F2pAccount}.<br>
	 * Sets the internal state to {@code true} or {@code false}.
	 * @param active The new status to set for the account.
	 */
	public void setActive(boolean active)
	{
		this.active = active;
	}
	
	/**
	 * Deactivates the {@code F2pAccount} and removes it from the player.<br>
	 * This method sends an expiration message to the {@link Player}.
	 * @param player The {@link Player} who is losing their {@code F2pAccount}.
	 */
	@Override
	public void expireEnd(Player player)
	{
		setActive(false);
		player.getF2p().remove();
		PacketSendUtility.sendBrightYellowMessageOnCenter(player, "<F2p Pack> is expired!!!");
	}
	
	/**
	 * Checks if the chair object is allowed to expire at this moment.<br>
	 * This method currently always returns {@code true}.
	 * @return {@code true} if the object can expire now, otherwise {@code false}.
	 */
	@Override
	public boolean canExpireNow()
	{
		return true;
	}
	
	/**
	 * Sends an expiration message to a specific player.<br>
	 * This method notifies the {@code Player} that an object is expiring.<br>
	 * It uses the provided {@code time} value to format the remaining duration.
	 * @param player The {@code Player} who will receive the notification.
	 * @param time The amount of time left before expiration.
	 */
	@Override
	public void expireMessage(Player player, int time)
	{
		PacketSendUtility.sendBrightYellowMessageOnCenter(player, "<F2p Pack> end!!!");
	}
}
