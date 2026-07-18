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
package com.aionemu.gameserver.model.templates.item.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.configs.main.EnchantsConfig;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.enchant.EnchantGrindService;
import com.aionemu.gameserver.services.enchant.EnchantService;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the logic for players who use an item to perform enchantment grinding.<br>
 * This action interacts with {@link EnchantGrindService} to process the upgrade attempts.
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "EnchantGrindingAction")
public class EnchantGrindingAction extends AbstractItemAction
{
	@XmlAttribute(name = "count")
	private int count;
	
	/**
	 * Checks if a {@link Player} can perform this action.<br>
	 * This method validates the requirements for interacting with items.<br>
	 * It checks for null items, enchantment limits, and sufficient Kinah.
	 * @param player The {@link Player} attempting the action.
	 * @param parentItem The item that triggers the action.
	 * @param targetItem The item being acted upon.
	 * @return {@code true} if the action is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem)
	{
		if ((parentItem == null) || (targetItem == null))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return false;
		}
		
		if (targetItem.getEnchantOrAuthorizeLevel() >= 15)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_ENCHANT_GRIND_LIMIT(targetItem.getNameId()));
			return false;
		}
		
		if (parentItem.getItemTemplate().isGrindEnchant() && (player.getInventory().getKinah() < EnchantService.EnchantKinah(targetItem)))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Executes the enchantment grinding action.<br>
	 * This method handles the logic when a {@link Player} uses an item to grind another item's enchantment.
	 * @param player The {@code Player} who is performing the action.
	 * @param parentItem The {@code Item} that triggers this action.
	 * @param targetItem The {@code Item} being acted upon.
	 */
	@Override
	public void act(Player player, Item parentItem, Item targetItem)
	{
		if (player.getInventory().getKinah() < EnchantService.EnchantKinah(targetItem))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
			return;
		}
		
		int enchantCast = 0;
		enchantCast = player.getGameStats().getStat(StatEnum.ENCHANT_BOOST, 0).getCurrent() != 0 ? (EnchantsConfig.ENCHANT_SPEED / 2) - ((EnchantsConfig.ENCHANT_SPEED * player.getGameStats().getStat(StatEnum.ENCHANT_BOOST, 0).getCurrent()) / 100) : EnchantsConfig.ENCHANT_SPEED;
		final boolean isSuccess = isSuccess(player, parentItem, targetItem);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), enchantCast, 0, 0));
		final ItemUseObserver moveObserver = new ItemUseObserver()
		{
			@Override
			public void abort()
			{
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(this);
				PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), targetItem.getObjectId(), targetItem.getItemTemplate().getTemplateId(), 0, 3, 0));
				ItemPacketService.updateItemAfterInfoChange(player, targetItem);
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_ENCHANT_GRIND_CANCEL(targetItem.getItemTemplate().getNameId()));
			}
		};
		player.getObserveController().attach(moveObserver);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(() ->
		{
			player.getController().cancelTask(TaskId.ITEM_USE);
			player.getObserveController().removeObserver(moveObserver);
			EnchantGrindService.enchantGrindItemAct(player, parentItem, targetItem, targetItem.getEnchantOrAuthorizeLevel(), isSuccess);
			PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, isSuccess ? 1 : 2, 384));
		}, enchantCast));
	}
	
	/**
	 * Checks if the enchantment grinding action was successful.<br>
	 * It verifies that the {@code parentItem} has a valid template.<br>
	 * Then it calls {@code enchantGrindItem}.
	 * @param player The {@link Player} performing the action.
	 * @param parentItem The item being used to perform the grind.
	 * @param targetItem The item receiving the enchantment.
	 * @return {@code true} if the action succeeded, otherwise {@code false}.
	 */
	private boolean isSuccess(Player player, Item parentItem, Item targetItem)
	{
		if (parentItem.getItemTemplate() != null)
		{
			return EnchantGrindService.enchantGrindItem(player, parentItem, targetItem);
		}
		
		return false;
	}
	
}
