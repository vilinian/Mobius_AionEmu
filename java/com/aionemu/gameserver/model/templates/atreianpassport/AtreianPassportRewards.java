/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 * Aion-Lightning is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Aion-Lightning is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. * You should have received a copy of the GNU General Public License
 * along with Aion-Lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.templates.atreianpassport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.AtreianPassport;

/**
 * This class defines the rewards associated with the {@link AtreianPassport} system.<br>
 * It maps specific items or benefits granted to players upon completing passport requirements.
 * @author Falke_34
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AtreianPassportRewards")
public class AtreianPassportRewards
{
	@XmlAttribute(name = "reward_item_num", required = true)
	protected int rewardItemNum;
	
	@XmlAttribute(name = "reward_item_count", required = true)
	protected int rewardItemCount;
	
	@XmlAttribute(name = "reward_item", required = true)
	protected int rewardItemId;
	
	@XmlAttribute(name = "name")
	protected String name;
	
	/**
	 * Retrieves the total number of items awarded.<br>
	 * This value corresponds to the {@code reward_item_num} attribute.
	 * @return The integer count of the reward item.
	 */
	public int getRewardItemNum()
	{
		return rewardItemNum;
	}
	
	/**
	 * Retrieves the total number of items for a specific reward.<br>
	 * This value corresponds to the {@code reward_item_count} attribute.
	 * @return The count of reward items as an {@code int}.
	 */
	public int getRewardItemCount()
	{
		return rewardItemCount;
	}
	
	/**
	 * Retrieves the unique identifier for the reward item.<br>
	 * This value corresponds to the {@code reward_item} attribute in the data.
	 * @return The {@code int} ID of the reward item.
	 */
	public int getRewardItemId()
	{
		return rewardItemId;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
}
