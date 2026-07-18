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
package com.aionemu.gameserver.dataholders;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.model.templates.item.ItemEnchantTable;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the configuration data for various item enchantment tables.<br>
 * It maps {@link ItemEnchantTable} templates to their specific properties and requirements.
 * @author Alcapwnd
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "enchant_tables")
public class ItemEnchantTableData
{
	@XmlElement(name = "enchant_table", required = true)
	protected List<ItemEnchantTable> enchantTables;
	
	@SuppressWarnings(
	{
		"rawtypes",
		"unchecked"
	})
	@XmlTransient
	private final TIntObjectHashMap<ItemEnchantTable> enchants = new TIntObjectHashMap();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code enchantMap} using the list of {@link ItemEnchantTable} objects.<br>
	 * The {@code enchantMap} is cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (ItemEnchantTable it : enchantTables)
		{
			getEnchantMap().put(it.getId(), it);
		}
	}
	
	/**
	 * Retrieves the internal map of enchantment tables.<br>
	 * This method provides a mapping from IDs to {@link ItemEnchantTable} objects.
	 * @return A {@code TIntObjectHashMap} containing all loaded enchantment tables.
	 */
	private TIntObjectHashMap<ItemEnchantTable> getEnchantMap()
	{
		return enchants;
	}
	
	/**
	 * Retrieves the enchantment table for a specific weapon category.<br>
	 * This method searches through the {@code enchantTables} list.<br>
	 * It compares the category name with the provided {@code cType}.
	 * @param cType The {@link ItemCategory} to search for.
	 * @return The matching {@link ItemEnchantTable} object or {@code null} if not found.
	 */
	public ItemEnchantTable getTableWeapon(ItemCategory cType)
	{
		for (ItemEnchantTable it : enchantTables)
		{
			if (it.getType().equalsIgnoreCase(cType.toString()))
			{
				return it;
			}
			
		}
		
		return null;
	}
	
	/**
	 * Retrieves the specific enchantment table for a given armor type and category.<br>
	 * This method searches through the {@code enchantTables} list to find a matching entry.<br>
	 * It returns {@code null} if no matching table is found.
	 * @param aType The {@link ArmorType} of the equipment.
	 * @param cType The {@link ItemCategory} of the equipment.
	 * @return The corresponding {@link ItemEnchantTable} object or {@code null}.
	 */
	public ItemEnchantTable getTableArmor(ArmorType aType, ItemCategory cType)
	{
		for (ItemEnchantTable it : enchantTables)
		{
			if (it.getPart() == null)
			{
				continue;
			}
			else if (aType == ArmorType.NO_ARMOR)
			{
				continue;
			}
			
			if (it.getType().equalsIgnoreCase(aType.toString()) && it.getPart().equalsIgnoreCase(cType.toString()))
			{
				return it;
			}
			
		}
		
		return null;
	}
	
	/**
	 * Retrieves the enchantment table data for plumes.<br>
	 * This method searches through the {@code enchantTables} list.<br>
	 * It returns the first entry where the type is {@code PLUME}.
	 * @return The {@link ItemEnchantTable} for plumes, or {@code null} if not found.
	 */
	public ItemEnchantTable getTablePlume()
	{
		for (ItemEnchantTable it : enchantTables)
		{
			if (it.getType() != "PLUME")
			{
				continue;
			}
			
			return it;
			
		}
		
		return null;
	}
	
	/**
	 * Retrieves the authorization enchant table.<br>
	 * This method searches for an {@link ItemEnchantTable} with the type {@code AUTHORIZE}.<br>
	 * It returns the first matching table found in the list.
	 * @return The {@code ItemEnchantTable} object if found, or {@code null} if no such table exists.
	 */
	public ItemEnchantTable getTableAuthorize()
	{
		for (ItemEnchantTable it : enchantTables)
		{
			if (it.getType() != "AUTHORIZE")
			{
				continue;
			}
			
			return it;
			
		}
		
		return null;
	}
	
	/**
	 * Returns the total number of enchant tables.<br>
	 * This method calls {@code size} to get the count.
	 * @return The size of the internal list.
	 */
	public int size()
	{
		return enchants.size();
	}
}
