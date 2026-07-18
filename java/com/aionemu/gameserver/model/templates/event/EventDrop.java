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
package com.aionemu.gameserver.model.templates.event;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * Represents a loot drop associated with a specific game event.<br>
 * This class defines the items and quantities that can be rewarded during an event.<br>
 * It links to {@link ItemTemplate} to identify the objects being dropped.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventDrop")
public class EventDrop
{
	@XmlAttribute(name = "loc_id")
	protected int locId;
	@XmlAttribute(name = "npc_id")
	protected int npcId;
	@XmlAttribute(name = "item_id", required = true)
	protected int itemId;
	@XmlAttribute(name = "count", required = true)
	protected long count;
	@XmlAttribute(name = "chance", required = true)
	protected float chance;
	@XmlAttribute(name = "minDiff")
	protected int minDiff;
	@XmlAttribute(name = "maxDiff")
	protected int maxDiff;
	@XmlAttribute(name = "minLvl")
	protected int minLvl;
	@XmlAttribute(name = "maxLvl")
	protected int maxLvl;
	
	@XmlTransient
	private ItemTemplate template;
	
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
	 * Retrieves the current quantity of the item.<br>
	 * This value is updated by the {@code calculateCount} method.
	 * @return The total number of items as a {@code long}.
	 */
	public long getCount()
	{
		return count;
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
	 * Retrieves the minimum difference value for this event drop.<br>
	 * This value is used to calculate variations in item properties.
	 * @return The {@code int} value of the minimum difference.
	 */
	public int getMinDiff()
	{
		return minDiff;
	}
	
	/**
	 * Retrieves the maximum difference value.<br>
	 * This value is used to determine the upper limit of a range.
	 * @return The {@code int} value of {@code maxDiff}.
	 */
	public int getMaxDiff()
	{
		return maxDiff;
	}
	
	/**
	 * Retrieves the location identifier for this event drop.<br>
	 * This value corresponds to the {@code loc_id} attribute.
	 * @return The unique integer ID of the location.
	 */
	public int getLocId()
	{
		return locId;
	}
	
	/**
	 * Retrieves the unique identifier for the NPC.<br>
	 * This value is stored as an {@code int}.
	 * @return The unique integer ID of the NPC.
	 */
	public int getNpcId()
	{
		return npcId;
	}
	
	/**
	 * Retrieves the minimum level required for this group.<br>
	 * This value is used to filter eligible players.
	 * @return The {@code int} value of the minimum level.
	 */
	public int getMinLvl()
	{
		return minLvl;
	}
	
	/**
	 * Retrieves the maximum level allowed for this group.<br>
	 * This value is stored in the {@code maxLvl} field.
	 * @return The maximum level as an {@code int}.
	 */
	public int getMaxLvl()
	{
		return maxLvl;
	}
	
	/**
	 * Retrieves the {@link ItemTemplate} for this drop.<br>
	 * It returns the cached {@code template} if it exists.<br>
	 * If the {@code template} is {@code null}, it fetches the data from {@link DataManager}.
	 * @return The {@link ItemTemplate} associated with this drop.
	 */
	public ItemTemplate getItemTemplate()
	{
		if (template == null)
		{
			template = DataManager.ITEM_DATA.getItemTemplate(itemId);
		}
		
		return template;
	}
}
