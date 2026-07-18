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
package com.aionemu.gameserver.services.enchant;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.grind.GrindCombine;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for combining items using {@link GrindCombine} templates.<br>
 * This service manages the requirements and outcomes of item crafting processes.
 */
public class GrindCombineService
{
	/**
	 * Combines two grind items into a new reward.<br>
	 * This method checks for required materials and the correct price.<br>
	 * It deducts the cost from the {@code Player} inventory.<br>
	 * A random reward is given if all conditions are met.
	 * @param player The {@link Player} who is performing the combination.
	 * @param mat The first {@link Item} required for the recipe.
	 * @param mat2 The second {@link Item} required for the recipe.
	 */
	public static void combineGrind(Player player, Item mat, Item mat2)
	{
		int index;
		if ((mat == null) || (mat2 == null))
		{
			return;
		}
		
		final GrindCombine combine = DataManager.GRIND_COMBINE_DATA.getCombine(player, mat.getItemTemplate().getGrindColor(), mat2.getItemTemplate().getGrindColor());
		if (combine == null)
		{
			return;
		}
		
		if (player.getInventory().getKinah() >= combine.getPrice())
		{
			player.getInventory().decreaseKinah(combine.getPrice());
		}
		
		if (player.getInventory().getKinah() < combine.getPrice())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
			return;
		}
		
		if (player.getInventory().decreaseByObjectId(mat.getObjectId(), 1) && player.getInventory().decreaseByObjectId(mat2.getObjectId(), 1) && ((index = Rnd.get(0, combine.getRewards().size() - 1)) != -1))
		{
			final int itemId = combine.getRewards().get(index).getItemId();
			ItemService.addItem(player, itemId, 1);
		}
	}
}
