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
package com.aionemu.gameserver.model.gameobjects.player.motion;

import java.util.HashMap;
import java.util.Map;

import com.aionemu.gameserver.model.IExpirable;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.f2p.F2pAccount;

/**
 * Represents the movement state and physical motion of a {@link Player}.<br>
 * It handles how a character moves through the game world.<br>
 * This class implements {@link IExpirable} to manage temporary motion effects.
 * @author MrPoke
 */
public class Motion implements IExpirable
{
	static final Map<Integer, Integer> motionType = new HashMap<>();
	static
	{
		// Ninja Set
		motionType.put(1, 1);
		motionType.put(2, 2);
		motionType.put(3, 3);
		motionType.put(4, 4);
		
		// Hovering Set
		motionType.put(5, 1);
		motionType.put(6, 2);
		motionType.put(7, 3);
		motionType.put(8, 4);
		
		// Hovering Set 3-Day Pass (test_add_customize_motion_shop_01)
		motionType.put(9, 1);
		
		// Signboard
		motionType.put(10, 1);
		
		// Martial Arts Set
		motionType.put(11, 1);
		motionType.put(12, 2);
		motionType.put(13, 3);
		motionType.put(14, 4);
		
		// Martial Arts Master and Teachings of the Master
		motionType.put(15, 1);
		motionType.put(16, 2);
		motionType.put(17, 3);
		motionType.put(18, 4);
		
		// Private Store Sign
		motionType.put(19, 1);
		
		// Hello?
		motionType.put(20, 1);
		
		// Boxing legend
		motionType.put(21, 1);
		
		// Boxing champion
		motionType.put(22, 1);
		
		// Monkey King
		motionType.put(23, 1);
		motionType.put(24, 2);
		motionType.put(26, 3);
		motionType.put(25, 4);
		
		// Test (cash_add_customize_motion_lyn_01)
		motionType.put(27, 1);
		motionType.put(28, 2);
		motionType.put(29, 3);
		motionType.put(30, 4);
		
		// Illuminated Signboard
		motionType.put(31, 1);
		
		// Energy Concentration
		motionType.put(32, 1);
		
		// Fun Outing
		motionType.put(33, 1);
		
		// Ice Skate
		motionType.put(34, 1);
		motionType.put(35, 2);
		motionType.put(36, 3);
		motionType.put(37, 4);
		
		// Spellbinding Rhythm
		motionType.put(38, 1);
		
		// Leisurely Strol
		motionType.put(39, 1);
		
		// Dream Wedding
		motionType.put(40, 1);
	}
	
	private final int id;
	private int deletionTime = 0;
	private boolean active = false;
	
	/**
	 * Creates a new instance of a {@link Motion}.<br>
	 * This constructor initializes the motion with its unique identifier, expiration time, and status.
	 * @param id The unique identification number for the motion.
	 * @param deletionTime The timestamp when this motion should be removed.
	 * @param isActive The initial active state of the motion. Set to {@code true} or {@code false}.
	 */
	public Motion(int id, int deletionTime, boolean isActive)
	{
		this.id = id;
		this.deletionTime = deletionTime;
		active = isActive;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Calculates the time left before this motion expires.<br>
	 * It subtracts the current system time from the {@code deletionTime}.<br>
	 * If {@code deletionTime} is {@code 0}, it returns {@code 0}.
	 * @return The remaining time in seconds as an {@code int}.
	 */
	public int getRemainingTime()
	{
		if (deletionTime == 0)
		{
			return 0;
		}
		
		return deletionTime - (int) (System.currentTimeMillis() / 1000);
	}
	
	/**
	 * Checks if the motion is currently active.<br>
	 * This method returns the current state of the {@code active} field.
	 * @return {@code true} if the motion is active, {@code false} otherwise.
	 */
	public boolean isActive()
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
	 * Retrieves the expiration time of this object.<br>
	 * This value represents when the object will expire.
	 * @return The expiration time as an {@code int}.
	 */
	@Override
	public int getExpireTime()
	{
		return deletionTime;
	}
	
	/**
	 * Removes the motion from the player's list.<br>
	 * This method deletes the specific motion associated with the current ID.
	 * @param player The {@link Player} who triggered the expiration.
	 */
	@Override
	public void expireEnd(Player player)
	{
		player.getMotions().remove(id);
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
}
