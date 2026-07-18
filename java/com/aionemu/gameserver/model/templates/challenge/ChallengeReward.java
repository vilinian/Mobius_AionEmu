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
 * Represents a reward granted to a player upon completing a specific challenge.<br>
 * This class defines the data structure for items or benefits associated with {@code Challenge}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChallengeReward")
public class ChallengeReward
{
	@XmlAttribute(name = "msg_id")
	protected Integer msgId;
	@XmlAttribute
	protected Integer value;
	@XmlAttribute(required = true)
	protected RewardType type;
	
	/**
	 * Retrieves the unique message identifier for this reward.<br>
	 * This value is used to display specific text in the game UI.
	 * @return the {@code Integer} ID of the message, or {@code null} if not set.
	 */
	public Integer getMsgId()
	{
		return msgId;
	}
	
	/**
	 * Retrieves the numerical value of this reward.<br>
	 * This method returns the {@code value} field stored in the object.
	 * @return The integer value associated with the reward, or {@code null} if not set.
	 */
	public Integer getValue()
	{
		return value;
	}
	
	/**
	 * Retrieves the reward category for this challenge.<br>
	 * This method returns the {@code RewardType} associated with the reward.
	 * @return the {@code RewardType} of the reward.
	 */
	public RewardType getType()
	{
		return type;
	}
}
