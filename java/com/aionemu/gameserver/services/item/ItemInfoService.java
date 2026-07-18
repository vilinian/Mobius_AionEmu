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
package com.aionemu.gameserver.services.item;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * This service provides centralized access to item information.<br>
 * It handles the retrieval and management of {@link ItemTemplate} data from the {@link DataManager}.<br>
 * Use this class to fetch details about items based on their unique identifiers.
 * @author ATracer
 */
public class ItemInfoService
{
	/**
	 * Retrieves the quality of an item based on its unique ID.<br>
	 * This method uses {@code getItemTemplate} to find the correct data.
	 * @param itemId The unique identifier for the item.
	 * @return The {@code ItemQuality} associated with the given {@code itemId}.
	 */
	public static ItemQuality getQuality(int itemId)
	{
		return getItemTemplate(itemId).getItemQuality();
	}
	
	/**
	 * Retrieves the name identifier for a specific item.<br>
	 * This method uses {@code getItemTemplate} to find the data.
	 * @param itemId The unique ID of the item to look up.
	 * @return The integer ID representing the item's name.
	 */
	public static int getNameId(int itemId)
	{
		return getItemTemplate(itemId).getNameId();
	}
	
	/**
	 * Retrieves the {@link ItemTemplate} for a specific item.<br>
	 * This method looks up data using the provided {@code itemId}.
	 * @param itemId The unique identifier of the item to find.
	 * @return The {@code ItemTemplate} associated with the given ID.
	 */
	public static ItemTemplate getItemTemplate(int itemId)
	{
		return DataManager.ITEM_DATA.getItemTemplate(itemId);
	}
}
