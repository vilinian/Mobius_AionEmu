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
package com.aionemu.gameserver.model.templates.luna;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * This class defines the template for rewards granted when a {@code luna} item is consumed.<br>
 * It maps the data structure used to process these specific rewards in the game server.
 */
@XmlType(name = "luna_consume_reward")
@XmlAccessorType(XmlAccessType.NONE)
public class LunaConsumeRewardsTemplate
{
	@XmlAttribute(name = "id", required = true)
	protected int id;
	
	@XmlAttribute(name = "name")
	protected String name;
	
	@XmlAttribute(name = "luna_sum_count", required = true)
	protected int luna_sum_count;
	
	@XmlAttribute(name = "gacha_cost")
	protected int gacha_cost;
	
	@XmlAttribute(name = "create_1")
	protected int create_1;
	
	@XmlAttribute(name = "num_1")
	protected int num_1;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
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
	
	/**
	 * Retrieves the total count of the sum. <br>
	 * This value is stored in the {@code luna_sum_count} field.
	 * @return The integer value of the sum count.
	 */
	public int getSumCount()
	{
		return luna_sum_count;
	}
	
	/**
	 * Retrieves the cost required for a gacha pull.<br>
	 * This value is stored in the {@code gacha_cost} field.
	 * @return The integer value representing the gacha cost.
	 */
	public int getGachaCost()
	{
		return gacha_cost;
	}
	
	/**
	 * Retrieves the unique identifier for the item created by this template.<br>
	 * This value corresponds to the {@code create_1} attribute.
	 * @return The {@code int} ID of the created item.
	 */
	public int getCreateItemId()
	{
		return create_1;
	}
	
	/**
	 * Retrieves the total count of items created.<br>
	 * This value corresponds to the {@code num_1} attribute.
	 * @return The number of items created.
	 */
	public int getCreateItemCount()
	{
		return num_1;
	}
}
