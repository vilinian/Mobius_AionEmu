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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This service handles the logic for tuning and modifying item properties.<br>
 * It manages specific adjustments to {@link Item} attributes during gameplay.<br>
 * Use this class to apply dynamic changes to items based on server rules.
 */
public class ItemTuningService
{
	/**
	 * Handles the logic for tuning an item owned by a player.<br>
	 * This method checks if the player has enough Kinah to perform the action.<br>
	 * It triggers a visual animation and updates the item stats after a delay.
	 * @param player The {@link Player} who is performing the action.
	 * @param item The {@link Item} being tuned by the player.
	 */
	public void onTuneItem(Player player, Item item)
	{
		if ((player.getInventory().getKinah() < ItemTuningService.getReTuningByQuality(item)) && (item.getOptionalSocket() != -1))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
			return;
		}
		
		final ItemTemplate template = item.getItemTemplate();
		final int nameId = template.getNameId();
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 2000, 0, 0), true);
		final ItemUseObserver moveObserver = new ItemUseObserver()
		{
			@Override
			public void abort()
			{
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(this);
				ItemPacketService.updateItemAfterInfoChange(player, item);
				player.removeItemCoolDown(template.getUseLimits().getDelayId());
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300427)); // Item use cancel
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 0, 2, 0), true);
			}
		};
		player.getObserveController().attach(moveObserver);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(() ->
		{
			player.getController().cancelTask(TaskId.ITEM_USE);
			player.getObserveController().removeObserver(moveObserver);
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 0, 1, 1), true);
			if (item.getOptionalSocket() != -1)
			{
				player.getInventory().decreaseKinah(ItemTuningService.getReTuningByQuality(item));
			}
			
			int rndCount = item.getRandomCount();
			if ((rndCount >= item.getItemTemplate().getRandomBonusCount()) || item.isEquipped())
			{
				return;
			}
			
			item.setRandomStats(null);
			item.setBonusNumber(0);
			item.setRandomCount(++rndCount);
			item.setOptionalSocket(-1);
			item.setOptionalSocket(Rnd.get(0, item.getItemTemplate().getOptionSlotBonus()));
			item.setRndBonus();
			item.setPersistentState(PersistentState.UPDATE_REQUIRED);
			player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
			PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401626, new DescriptionId(nameId)));
		}, 5000));
	}
	
	/**
	 * Calculates the cost to re-tune an item based on its quality.<br>
	 * This method checks the {@code ItemTemplate} of the provided {@code Item}.<br>
	 * It returns a specific price for {@code ANCIENT}, {@code RELIC}, or {@code FINALITY} qualities.
	 * @param item The {@code Item} object to check.
	 * @return The integer cost required for re-tuning.
	 */
	public static int getReTuningByQuality(Item item)
	{
		int price = 0;
		switch (item.getItemTemplate().getItemQuality())
		{
			case ANCIENT:
			{
				price = 10000;
				break;
			}
			case RELIC:
			{
				price = 50000;
				break;
			}
			case FINALITY:
			{
				price = 150000;
				break;
			}
			default:
				break;
		}
		
		return price;
	}
	
	/**
	 * Provides access to the singleton instance of {@link ItemTuningService}.<br>
	 * Use this method to get the global service for item tuning.
	 * @return The singleton instance of {@code ItemTuningService}.
	 */
	public static ItemTuningService getInstance()
	{
		return NewSingletonHolder.INSTANCE;
	}
	
	private static class NewSingletonHolder
	{
		private static final ItemTuningService INSTANCE = new ItemTuningService();
	}
}
