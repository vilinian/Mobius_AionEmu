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
package com.aionemu.gameserver.model.templates.shugosweep;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a reward granted to players during the {@code ShugoSweep} event.<br>
 * This class holds the data for items or benefits received upon completion of the sweep.
 * @author Ghostfur
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShugoSweepReward")
public class ShugoSweepReward
{
	@XmlAttribute(name = "board_id")
	protected int boardId;
	
	@XmlAttribute(name = "reward_num")
	protected int rewardNum;
	
	@XmlAttribute(name = "item_id")
	protected int itemId;
	
	@XmlAttribute(name = "count")
	protected int count;
	
	/**
	 * Retrieves the unique identifier for the game board.<br>
	 * This value is stored in the {@code boardId} field.
	 * @return The current {@code int} board ID.
	 */
	public int getBoardId()
	{
		return boardId;
	}
	
	/**
	 * Retrieves the total number of rewards.<br>
	 * This value is stored in the {@code rewardNum} field.
	 * @return The integer count of rewards.
	 */
	public int getRewardNum()
	{
		return rewardNum;
	}
	
	/**
	 * Retrieves the unique identifier for this wardrobe item.<br>
	 * This value corresponds to the {@code itemId} assigned during object creation.
	 * @return The unique {@code int} ID of the item.
	 */
	public int getItemId()
	{
		return itemId;
	}
	
	/**
	 * Retrieves the current count value.<br>
	 * This method returns the integer stored in the {@code count} field.
	 * @return The current count as an {@code int}.
	 */
	public int getCount()
	{
		return count;
	}
}
