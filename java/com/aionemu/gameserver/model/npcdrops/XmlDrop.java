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
package com.aionemu.gameserver.model.npcdrops;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a loot drop item defined within an {@code XML} configuration file.<br>
 * This class maps the data structure used to determine what items an NPC can drop.
 * @author Falke_34
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "drop")
public class XmlDrop
{
	@XmlAttribute(name = "item_id", required = true)
	protected int itemId;
	@XmlAttribute(name = "min_amount", required = true)
	protected int minAmount;
	@XmlAttribute(name = "max_amount", required = true)
	protected int maxAmount;
	@XmlAttribute(required = true)
	protected float chance;
	@XmlAttribute(name = "no_reduce")
	protected boolean noReduce = false;
	@XmlAttribute(name = "eachmember")
	protected boolean eachMember = false;
	@XmlAttribute(name = "name", required = false)
	protected String name;
	
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
	 * Retrieves the minimum quantity of an item that can be dropped.<br>
	 * This value is used by {@code int, float, com.aionemu.gameserver.model.Race, java.util.Collection)}.
	 * @return The minimum amount as an {@code int}.
	 */
	public int getMinAmount()
	{
		return minAmount;
	}
	
	/**
	 * Retrieves the maximum quantity of an item that can drop.<br>
	 * This value is used by {@code int, float, com.aionemu.gameserver.model.Race, java.util.Collection)}.
	 * @return The maximum amount as an {@code int}.
	 */
	public int getMaxAmount()
	{
		return maxAmount;
	}
	
	/**
	 * Retrieves the probability of this drop occurring.<br>
	 * The value is stored as a {@code float}.
	 * @return The drop chance value.
	 */
	public float getChance()
	{
		return chance;
	}
	
	/**
	 * Checks if the drop amount is exempt from reductions.<br>
	 * This method returns {@code true} if the {@code noReduce} flag is set.<br>
	 * It helps determine if modifiers should be applied to this specific drop.
	 * @return {@code true} if no reduction is applied, {@code false} otherwise.
	 */
	public boolean isNoReduction()
	{
		return noReduce;
	}
	
	/**
	 * Checks if the drop applies to every member.<br>
	 * This method returns the value of the {@code eachMember} attribute.
	 * @return {@code true} if it applies to each member, {@code false} otherwise.
	 */
	public boolean isEachMember()
	{
		return eachMember;
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
