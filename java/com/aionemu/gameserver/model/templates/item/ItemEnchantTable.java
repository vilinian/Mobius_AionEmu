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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Represents the configuration data for an item enchantment table.<br>
 * This class defines the properties and behaviors used when a player interacts with an enchantment station.
 * @author Alcapwnd
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ItemEnchantTable")
public class ItemEnchantTable
{
	@XmlAttribute(name = "id")
	private int id;
	@XmlAttribute(name = "type")
	private String type;
	@XmlAttribute(name = "part")
	private String part;
	@XmlElement(name = "item_enchant", required = false)
	private List<ItemEnchantBonus> item_enchant;
	
	@SuppressWarnings(
	{
		"rawtypes",
		"unchecked"
	})
	@XmlTransient
	private final TIntObjectHashMap<List<StatFunction>> enchants = new TIntObjectHashMap();
	
	/**
	 * Retrieves the list of statistics for a specific enchantment level.<br>
	 * This method searches through all available bonuses.<br>
	 * It returns the modifiers that match the provided {@code level}.
	 * @param level The enchantment level to look up.
	 * @return A {@code List} of {@link StatFunction} objects, or {@code null} if no match is found.
	 */
	public List<StatFunction> getStats(int level)
	{
		for (ItemEnchantBonus ib : getItemEnchant())
		{
			if (ib.getLevel() != level)
			{
				continue;
			}
			
			return ib.getModifiers();
		}
		
		return null;
	}
	
	/**
	 * Retrieves the list of enchantment bonuses for this item.<br>
	 * This method returns all {@link ItemEnchantBonus} objects associated with the table.
	 * @return a {@code List} of {@code ItemEnchantBonus} objects.
	 */
	public List<ItemEnchantBonus> getItemEnchant()
	{
		return item_enchant;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
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
	 * Retrieves the part identifier for this item enchantment table.<br>
	 * This value is used to distinguish different components of a set.
	 * @return The {@code String} representing the part name.
	 */
	public String getPart()
	{
		return part;
	}
	
}
