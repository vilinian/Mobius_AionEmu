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

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.item.ItemEnchantChance;
import com.aionemu.gameserver.model.templates.item.ItemEnchantChanceList;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for item enchantment chances.<br>
 * It maps {@code ItemEnchantChance} objects to their respective IDs using a {@code TIntObjectHashMap}.<br>
 * It is used by the server to determine the success rates of various item upgrades.
 */
@XmlRootElement(name = "enchant_chances")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemEnchantChancesData
{
	@XmlElement(name = "enchant_chance")
	private List<ItemEnchantChance> ItemEnchantChance;
	private final TIntObjectHashMap<List<ItemEnchantChanceList>> ItemEnchantChanceList = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the object is unmarshalled from XML.<br>
	 * It populates the internal {@code ItemEnchantChanceList} map.<br>
	 * It clears any existing data before processing the new list.
	 * @param Unmarshaller the {@link Unmarshaller} used to read the data.
	 * @param Object the object being unmarshalled.
	 */
	void afterUnmarshal(Unmarshaller Unmarshaller, Object Object)
	{
		ItemEnchantChanceList.clear();
		final Iterator<ItemEnchantChance> Iterator = ItemEnchantChance.iterator();
		while (Iterator.hasNext())
		{
			final ItemEnchantChance itemEnchantChance = Iterator.next();
			ItemEnchantChanceList.put(itemEnchantChance.getId(), itemEnchantChance.getItemEnchantChanceList());
		}
	}
	
	/**
	 * Returns the total number of enchant chance lists.<br>
	 * This method calls {@code size} to get the count.
	 * @return The size of the internal map.
	 */
	public int size()
	{
		return ItemEnchantChanceList.size();
	}
	
	/**
	 * Retrieves an {@link ItemEnchantChance} based on a specific ID.<br>
	 * This method searches through the list of available chances.<br>
	 * It returns {@code null} if no match is found.
	 * @param id The unique identifier for the item enchantment chance.
	 * @return The matching {@code ItemEnchantChance} object or {@code null}.
	 */
	public ItemEnchantChance getChanceById(int id)
	{
		final Iterator<ItemEnchantChance> Iterator = ItemEnchantChance.iterator();
		while (Iterator.hasNext())
		{
			final ItemEnchantChance itemEnchantChance = Iterator.next();
			if (itemEnchantChance.getId() == id)
			{
				return itemEnchantChance;
			}
		}
		
		return null;
	}
}
