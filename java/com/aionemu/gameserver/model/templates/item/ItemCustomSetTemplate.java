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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a template for custom item sets in the game.<br>
 * This class defines the properties and requirements for grouped items.<br>
 * It is used by the {@link com.aionemu.gameserver.model.templates.item.ItemTemplate} system to manage set bonuses.
 * @author xjplay@yahoo
 */
@XmlType(name = "ItemCustomSetTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemCustomSetTemplate
{
	@XmlAttribute(name = "item_id")
	private Integer item_id;
	@XmlAttribute(name = "name")
	private String name;
	@XmlAttribute(name = "enchant_level")
	private int enchant_level;
	@XmlElement(name = "mana_stone")
	private List<Integer> mana_stone;
	
	/**
	 * Retrieves the unique identifier for this item.<br>
	 * This value is used to identify the specific item in the database.
	 * @return The {@code Integer} ID of the item, or {@code null} if not set.
	 */
	public Integer getItem_id()
	{
		return item_id;
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
	 * Retrieves the current enchantment level of the item.<br>
	 * This value is stored in the {@code enchant_level} field.
	 * @return The integer value representing the enchantment level.
	 */
	public int getEnchant_level()
	{
		return enchant_level;
	}
	
	/**
	 * Retrieves the list of {@code mana_stone} IDs.<br>
	 * This method returns the collection associated with this template.
	 * @return a {@code List<Integer>} containing the mana stone values.
	 */
	public List<Integer> getMana_stone()
	{
		return mana_stone;
	}
}
