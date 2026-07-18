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
package com.aionemu.gameserver.model.items;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.aionemu.gameserver.dataholders.loadingutils.adapters.NpcEquipmentList;
import com.aionemu.gameserver.dataholders.loadingutils.adapters.NpcEquippedGearAdapter;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * Represents the equipment worn by an {@code NpcTemplate}.<br>
 * It maps specific {@code ItemSlot} types to their corresponding {@link ItemTemplate}.
 * @author Luno
 */
@XmlJavaTypeAdapter(NpcEquippedGearAdapter.class)
public class NpcEquippedGear implements Iterable<Entry<ItemSlot, ItemTemplate>>
{
	private Map<ItemSlot, ItemTemplate> items;
	private short mask;
	private NpcEquipmentList v;
	
	/**
	 * Creates a new instance of {@link NpcEquippedGear}.<br>
	 * This constructor initializes the object using the provided data.
	 * @param v The {@code NpcEquipmentList} containing the gear information.
	 */
	public NpcEquippedGear(NpcEquipmentList v)
	{
		this.v = v;
	}
	
	/**
	 * Retrieves the bitmask for equipped items.<br>
	 * This method ensures that {@code init} is called if the internal map is null.
	 * @return The current item mask as a {@code short}.
	 */
	public short getItemsMask()
	{
		if (items == null)
		{
			init();
		}
		
		return mask;
	}
	
	/**
	 * Provides an {@link Iterator} to loop through all equipped gear.<br>
	 * This method ensures the internal data is initialized before returning.
	 * @return An {@code Iterator} containing {@code Entry} objects of {@code ItemSlot} and {@code ItemTemplate}.
	 */
	@Override
	public Iterator<Entry<ItemSlot, ItemTemplate>> iterator()
	{
		if (items == null)
		{
			init();
		}
		
		return items.entrySet().iterator();
	}
	
	/**
	 * Initializes the internal data structures for this object.<br>
	 * It populates the {@code items} map from the source list.<br>
	 * This method sets the {@code v} field to {@code null} after completion.
	 */
	public void init()
	{
		synchronized (this)
		{
			if (items == null)
			{
				items = new TreeMap<>();
				for (ItemTemplate item : v.items)
				{
					final ItemSlot[] itemSlots = ItemSlot.getSlotsFor(item.getItemSlot());
					for (ItemSlot itemSlot : itemSlots)
					{
						if (items.get(itemSlot) == null)
						{
							items.put(itemSlot, item);
							mask |= itemSlot.getSlotIdMask();
							break;
						}
					}
				}
			}
			
			v = null;
		}
	}
	
	/**
	 * Retrieves the {@link ItemTemplate} for a specific slot.<br>
	 * This method checks if the {@code itemSlot} exists in the current equipment set.<br>
	 * It returns {@code null} if no item is found or if the map is empty.
	 * @param itemSlot The specific slot to look up.
	 * @return The {@link ItemTemplate} associated with the slot, or {@code null}.
	 */
	public ItemTemplate getItem(ItemSlot itemSlot)
	{
		return items != null ? items.get(itemSlot) : null;
	}
}
