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
package com.aionemu.gameserver.model.templates.challenge;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a reward granted to players for their contributions to a challenge.<br>
 * This class defines the data structure for rewards associated with {@code Challenge} objectives.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContributionReward")
public class ContributionReward
{
	@XmlAttribute(name = "item_count", required = true)
	protected int itemCount;
	@XmlAttribute(name = "reward_id", required = true)
	protected int rewardId;
	@XmlAttribute(required = true)
	protected int number;
	@XmlAttribute(required = true)
	protected int rank;
	
	/**
	 * Retrieves the total number of items for this reward.<br>
	 * This method returns the value stored in the {@code itemCount} field.
	 * @return The integer count of items.
	 */
	public int getItemCount()
	{
		return itemCount;
	}
	
	/**
	 * Retrieves the unique identifier for the reward.<br>
	 * This value corresponds to the {@code reward_id} attribute.
	 * @return The integer ID of the reward.
	 */
	public int getRewardId()
	{
		return rewardId;
	}
	
	/**
	 * Retrieves the current value of the {@code number} attribute.<br>
	 * This method returns the integer assigned to this reward.
	 * @return The current {@code number} value.
	 */
	public int getNumber()
	{
		return number;
	}
	
	/**
	 * Retrieves the current rank of the {@code MCEntry}.<br>
	 * This value is used to determine the item's standing.
	 * @return The integer value representing the rank.
	 */
	public int getRank()
	{
		return rank;
	}
}
