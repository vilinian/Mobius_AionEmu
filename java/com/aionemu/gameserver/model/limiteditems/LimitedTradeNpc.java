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
package com.aionemu.gameserver.model.limiteditems;

import java.util.List;

/**
 * Represents a non-player character (NPC) that handles the trading of limited items.<br>
 * This class manages the logic for specific trade interactions involving restricted goods.
 * @author xTz
 */
public class LimitedTradeNpc
{
	private final List<LimitedItem> limitedItems;
	
	/**
	 * Creates a new instance of {@link LimitedTradeNpc}.<br>
	 * This constructor initializes the NPC with a specific list of items.
	 * @param limitedItems The {@code List} of {@link LimitedItem} objects to assign.
	 */
	public LimitedTradeNpc(List<LimitedItem> limitedItems)
	{
		this.limitedItems = limitedItems;
		
	}
	
	/**
	 * Adds a list of items to the current collection.<br>
	 * This method updates the internal {@code limitedItems} list.
	 * @param limitedItems The {@code List} of {@link LimitedItem} objects to add.
	 */
	public void putLimitedItems(List<LimitedItem> limitedItems)
	{
		this.limitedItems.addAll(limitedItems);
	}
	
	/**
	 * Retrieves the list of items available for this NPC.<br>
	 * This method returns all {@link LimitedItem} objects stored in the collection.
	 * @return a {@code List} containing the {@code LimitedItem} objects.
	 */
	public List<LimitedItem> getLimitedItems()
	{
		return limitedItems;
	}
}
