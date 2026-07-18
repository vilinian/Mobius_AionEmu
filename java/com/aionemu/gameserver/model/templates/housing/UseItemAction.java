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
package com.aionemu.gameserver.model.templates.housing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents an action triggered when a player uses an item within the housing system.<br>
 * This class defines the specific behavior or effect associated with that item usage.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UseItemAction")
public class UseItemAction
{
	@XmlAttribute(name = "final_reward_id")
	protected Integer finalRewardId;
	@XmlAttribute(name = "reward_id")
	protected Integer rewardId;
	@XmlAttribute(name = "remove_count")
	protected Integer removeCount;
	@XmlAttribute(name = "check_type")
	protected Integer checkType;
	
	/**
	 * Retrieves the unique identifier for the final reward.<br>
	 * This value is used to determine which item is given at the end of an action.
	 * @return the {@code Integer} ID of the final reward, or {@code null} if not set.
	 */
	public Integer getFinalRewardId()
	{
		return finalRewardId;
	}
	
	/**
	 * Retrieves the unique identifier for the reward.<br>
	 * This value is used to determine which item or prize is granted.
	 * @return the {@code rewardId} as an {@code Integer}, or {@code null} if not set.
	 */
	public Integer getRewardId()
	{
		return rewardId;
	}
	
	/**
	 * Retrieves the number of items to be removed.<br>
	 * This value is stored in the {@code removeCount} field.
	 * @return The count of items to remove as an {@code Integer}.
	 */
	public Integer getRemoveCount()
	{
		return removeCount;
	}
	
	/**
	 * Retrieves the type of check performed for this action.<br>
	 * This value is mapped from the {@code check_type} attribute.
	 * @return the {@code Integer} value representing the check type.
	 */
	public Integer getCheckType()
	{
		return checkType;
	}
}
