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
 * Represents the rewards granted to players within the {@code ArgentManor} instance.<br>
 * This class handles specific loot and items associated with this location.
 * @author Falke_34
 */
public class ArgentManorPlayerReward extends InstancePlayerReward
{
	/**
	 * Creates a new {@link ArgentManorPlayerReward} instance.<br>
	 * This constructor initializes the reward using an {@code Integer} value.<br>
	 * It passes the provided object to the parent class.
	 * @param object The unique identifier for the reward.
	 */
	public ArgentManorPlayerReward(Integer object)
	{
		super(object);
	}
}
