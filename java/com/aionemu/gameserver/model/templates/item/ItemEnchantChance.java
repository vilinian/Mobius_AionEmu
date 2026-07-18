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
package com.aionemu.gameserver.model.templates.item;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the enchantment success chances for specific items.<br>
 * This class stores data used to determine probabilities during the item enhancement process.
 */
@XmlType(name = "ItemEnchantChance")
public class ItemEnchantChance
{
	@XmlAttribute(name = "id")
	protected int enchantChanceId;
	@XmlAttribute(name = "name")
	protected String name;
	@XmlAttribute(name = "type")
	protected String type;
	@XmlAttribute(name = "quality")
	protected String quality;
	@XmlAttribute(name = "target_quality")
	protected String target_quality;
	@XmlElement(name = "item")
	private List<ItemEnchantChanceList> itemEnchantChanceList;
	
	/**
	 * Retrieves the unique identifier for this {@link ItemEnchantChance}.<br>
	 * This value corresponds to the internal ID of the enchantment chance.
	 * @return The integer ID of the enchantment chance.
	 */
	public int getId()
	{
		return enchantChanceId;
	}
	
	/**
	 * Retrieves the display name of the enchantment chance.<br>
	 * This method returns the {@code name} field associated with this object.
	 * @return The name as a {@code String}.
	 */
	public String name()
	{
		return name;
	}
	
	/**
	 * Retrieves the type of the task.<br>
	 * This method returns the value stored in the {@code type} field.
	 * @return The string representation of the task type.
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 * Retrieves the quality level of the item.<br>
	 * This value is stored as a {@code String}.
	 * @return The current quality of the item.
	 */
	public String getQuality()
	{
		return quality;
	}
	
	/**
	 * Retrieves the target quality of the enchantment.<br>
	 * This value is stored in the {@code target_quality} field.
	 * @return The {@code String} representing the target quality.
	 */
	public String getTargetQuality()
	{
		return target_quality;
	}
	
	/**
	 * Retrieves a specific enchantment chance list based on its unique identifier.<br>
	 * This method searches the internal {@code itemEnchantChanceList}.
	 * @param id The unique integer ID of the enchantment chance to find.
	 * @return The {@link ItemEnchantChanceList} matching the provided {@code id}, or {@code null} if not found.
	 */
	public ItemEnchantChanceList getChancesById(int id)
	{
		if (itemEnchantChanceList != null)
		{
			return itemEnchantChanceList.get(id);
		}
		
		return null;
	}
	
	/**
	 * Retrieves the list of enchantment chances for this item.<br>
	 * This method returns all {@link ItemEnchantChanceList} objects associated with the current template.
	 * @return A {@code List} of {@code ItemEnchantChanceList} objects.
	 */
	public List<ItemEnchantChanceList> getItemEnchantChanceList()
	{
		return itemEnchantChanceList;
	}
}
