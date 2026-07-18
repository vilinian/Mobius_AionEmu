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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * Represents a {@link ItemStone} specifically used as a rune stone.<br>
 * This class handles the data and behavior for stones that contain magical runes.
 */
public class RuneStone extends ItemStone
{
	
	@SuppressWarnings("unused")
	private final int runeTransformGroup;
	private final ItemTemplate runeItem;
	
	/**
	 * Creates a new instance of a {@link RuneStone}.<br>
	 * This constructor initializes the stone with its unique IDs and state.<br>
	 * It also loads the required template data from the {@code DataManager}.
	 * @param itemObjId The unique object ID for this specific instance.
	 * @param itemId The base item ID used to look up the template.
	 * @param persistentState The state information for the stone.
	 */
	public RuneStone(int itemObjId, int itemId, PersistentState persistentState)
	{
		super(itemObjId, itemId, 0, persistentState);
		
		ItemTemplate itemTemplate;
		runeItem = itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		runeTransformGroup = itemTemplate.getRunTransformTableId();
	}
	
	/**
	 * Handles the logic when a {@code RuneStone} is equipped by a {@link Player}.<br>
	 * It ensures that the associated {@code runeItem} template is not {@code null}.<br>
	 * If the template is missing, the method returns immediately.
	 * @param player The {@link Player} who equipped the stone.
	 */
	public void onEquip(Player player)
	{
		if (runeItem == null)
		{
			return;
		}
	}
}
