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
package com.aionemu.gameserver.model.instance.playerreward;

/**
 * Represents the rewards granted to a player for participating in the Dredgion instance.<br>
 * This class extends {@link InstancePlayerReward} to handle specific loot logic for this event.
 * @author xTz
 */
public class DredgionPlayerReward extends InstancePlayerReward
{
	private int zoneCaptured;
	
	/**
	 * Creates a new {@link DredgionPlayerReward} instance.<br>
	 * This constructor initializes the reward using an object from the parent class.
	 * @param object The underlying object used to initialize the reward.
	 */
	public DredgionPlayerReward(Integer object)
	{
		super(object);
	}
	
	/**
	 * Updates the count of captured zones.<br>
	 * This method increments the {@code zoneCaptured} field by 1.
	 */
	public void captureZone()
	{
		zoneCaptured++;
	}
	
	/**
	 * Retrieves the number of zones captured.<br>
	 * This value is updated when {@code captureZone} is called.
	 * @return The total count of captured zones as an {@code int}.
	 */
	public int getZoneCaptured()
	{
		return zoneCaptured;
	}
}
