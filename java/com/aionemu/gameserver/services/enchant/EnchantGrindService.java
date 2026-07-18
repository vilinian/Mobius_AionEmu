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
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemEnchantChance;
import com.aionemu.gameserver.model.templates.item.ItemEnchantChanceList;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service handles the logic for grinding item enchantments.<br>
 * It manages the repetitive process of attempting to upgrade an {@link Item} until a specific success condition is met.
 */
public class EnchantGrindService
{
	/**
	 * This method calculates the success of an item enchantment grind.<br>
	 * It checks a random value against the chance data for the {@code targetItem}.<br>
	 * If the {@link Player} is a GM, it always returns {@code true}.
	 * @param player The {@link Player} attempting the action.
	 * @param parentItem The base item being used in the process.
	 * @param targetItem The specific item being enchanted.
	 * @return {@code true} if the enchantment succeeds, otherwise {@code false}.
	 */
	public static boolean enchantGrindItem(Player player, Item parentItem, Item targetItem)
	{
		boolean result = false;
		final float random = Rnd.get(1, 1000) / 10.0f;
		final int chanceId = 21;
		final ItemEnchantChance eItem = DataManager.ITEM_ENCHANT_CHANCES_DATA.getChanceById(chanceId);
		final ItemEnchantChanceList eData = eItem.getChancesById(targetItem.getEnchantOrAuthorizeLevel());
		result = player.isGM() ? true : random <= eData.getChance();
		return result;
	}
	
	/**
	 * Executes the logic for an item enchantment grind action.<br>
	 * This method handles currency deduction, item consumption, and updates the target item state.<br>
	 * It sends the appropriate success or failure messages to the {@link Player}.
	 * @param player The {@link Player} performing the action.
	 * @param parentItem The {@link Item} being consumed as a material.
	 * @param targetItem The {@link Item} being enchanted.
	 * @param currentEnchant The current enchantment level of the {@code targetItem}.
	 * @param result A boolean indicating if the grind attempt was successful.
	 */
	public static void enchantGrindItemAct(Player player, Item parentItem, Item targetItem, int currentEnchant, boolean result)
	{
		final int EnchantKinah = EnchantService.EnchantKinah(targetItem);
		currentEnchant = targetItem.getEnchantOrAuthorizeLevel();
		if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1L))
		{
			return;
		}
		
		if (player.getInventory().getKinah() >= EnchantKinah)
		{
			player.getInventory().decreaseKinah(EnchantKinah);
		}
		
		if (player.getInventory().getKinah() < EnchantKinah)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
			return;
		}
		
		if (result)
		{
			++currentEnchant;
		}
		else
		{
			targetItem.setContaminated(true);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_ENCHANT_GRIND_CANT_STATUS(new DescriptionId(targetItem.getNameId())));
		}
		
		targetItem.setEnchantOrAuthorizeLevel(currentEnchant);
		if (targetItem.isEquipped())
		{
			player.getGameStats().updateStatsVisually();
		}
		
		ItemPacketService.updateItemAfterInfoChange(player, targetItem, ItemPacketService.ItemUpdateType.STATS_CHANGE);
		if (targetItem.isEquipped())
		{
			player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
		else
		{
			player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
		
		if (result)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_ENCHANT_GRIND_SUCCEEDED(new DescriptionId(targetItem.getNameId()), targetItem.getEnchantOrAuthorizeLevel()));
		}
		else
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_ENCHANT_GRIND_FAIL(new DescriptionId(targetItem.getNameId())));
		}
	}
}
