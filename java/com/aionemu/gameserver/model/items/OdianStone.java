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
 * Represents the {@code OdianStone} item in the game world.<br>
 * This class extends {@link ItemStone} to handle specific properties for this stone type.
 */
public class OdianStone extends ItemStone
{
	
	@SuppressWarnings("unused")
	private final int odianSkill;
	@SuppressWarnings("unused")
	private final int odianSkillLevel;
	private final ItemTemplate odianItem;
	
	/**
	 * Creates a new instance of an {@link OdianStone}.<br>
	 * This constructor initializes the stone with its unique IDs and state.<br>
	 * It also loads specific skill data from the {@code DataManager}.
	 * @param itemObjId The unique object ID for this specific instance.
	 * @param itemId The template ID used to look up item properties.
	 * @param persistentState The current state of the game object.
	 */
	public OdianStone(int itemObjId, int itemId, PersistentState persistentState)
	{
		super(itemObjId, itemId, 0, persistentState);
		
		ItemTemplate itemTemplate;
		odianItem = itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		odianSkill = itemTemplate.getOdianSkillId();
		odianSkillLevel = itemTemplate.getOdianSkillLevel();
	}
	
	/**
	 * Handles the logic when an {@code OdianStone} is equipped by a {@link Player}.<br>
	 * It checks if the associated {@code odianItem} is valid before proceeding.
	 * @param player The {@link Player} who equipped the stone.
	 */
	public void onEquip(Player player)
	{
		if (odianItem == null)
		{
			return;
		}
	}
}
