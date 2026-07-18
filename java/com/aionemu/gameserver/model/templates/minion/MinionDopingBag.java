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
package com.aionemu.gameserver.model.templates.minion;

import java.util.Arrays;

/**
 * Represents a doping bag item used by minions.<br>
 * This class stores the configuration and properties for minion-specific buffs.
 */
public class MinionDopingBag
{
	private int[] itemBag;
	private boolean isDirty;
	
	/**
	 * Creates a new instance of {@link MinionDopingBag}.<br>
	 * Initializes the internal state to default values.<br>
	 * Sets the {@code itemBag} to {@code null}.<br>
	 * Sets {@code isDirty} to {@code false}.
	 */
	public MinionDopingBag()
	{
		itemBag = null;
		isDirty = false;
	}
	
	/**
	 * Sets the primary food item for this {@link MinionDopingBag}.<br>
	 * This method updates the item in slot {@code 0}.
	 * @param itemId The unique identifier of the food item.
	 */
	public void setFoodItem(int itemId)
	{
		setItem(itemId, 0);
	}
	
	/**
	 * Retrieves the ID of the food item from the bag.<br>
	 * This method checks if the {@code itemBag} is valid before accessing it.<br>
	 * It returns the value stored in the first slot.
	 * @return The integer ID of the food item, or 0 if the bag is empty or null.
	 */
	public int getFoodItem()
	{
		if ((itemBag == null) || (itemBag.length < 1))
		{
			return 0;
		}
		
		return itemBag[0];
	}
	
	/**
	 * Sets the drink item for this {@link MinionDopingBag}.<br>
	 * This method updates the first slot of the bag.
	 * @param itemId The unique identifier of the drink item.
	 */
	public void setDrinkItem(int itemId)
	{
		setItem(itemId, 1);
	}
	
	/**
	 * Retrieves the ID of the drink item from the bag.<br>
	 * This method checks if the {@code itemBag} is valid before accessing it.<br>
	 * It returns the value stored at index 1.
	 * @return The integer ID of the drink item, or 0 if the bag is empty or null.
	 */
	public int getDrinkItem()
	{
		if ((itemBag == null) || (itemBag.length < 2))
		{
			return 0;
		}
		
		return itemBag[1];
	}
	
	/**
	 * Places an item into a specific slot in the bag.<br>
	 * This method updates the {@code itemBag} array and marks the state as dirty.<br>
	 * It automatically expands the array if the {@code slot} index is out of bounds.
	 * @param itemId The unique identifier for the item to store.
	 * @param slot The index position where the item should be placed.
	 */
	public void setItem(int itemId, int slot)
	{
		if (itemBag == null)
		{
			itemBag = new int[slot + 1];
			isDirty = true;
		}
		else if (slot > (itemBag.length - 1))
		{
			itemBag = Arrays.copyOf(itemBag, slot + 1);
			isDirty = true;
		}
		
		if (itemBag[slot] != itemId)
		{
			itemBag[slot] = itemId;
			isDirty = true;
		}
	}
	
	/**
	 * Retrieves the list of scrolls used by this {@link MinionDopingBag}.<br>
	 * It returns all items located after the first two slots in the bag.<br>
	 * If the bag is empty or too small, it returns an empty array.
	 * @return An {@code int[]} containing the IDs of the scrolls used.
	 */
	public int[] getScrollsUsed()
	{
		if ((itemBag == null) || (itemBag.length < 3))
		{
			return new int[0];
		}
		
		return Arrays.copyOfRange(itemBag, 2, itemBag.length);
	}
	
	/**
	 * Checks if the data in this object has been modified.<br>
	 * Returns {@code true} if any changes were made since the last save.<br>
	 * Returns {@code false} if the state is clean.
	 * @return The current dirty status of the bag.
	 */
	public boolean isDirty()
	{
		return isDirty;
	}
}
