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

import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the logic for clearing instance time when a specific item is used.<br>
 * This action ensures that the current instance timer is reset to {@code 0}.<br>
 * It is typically triggered by items designed to refresh the remaining time in an instance.
 * @author Tiger
 * @rework FrozenKiller
 */
public class InstanceTimeClear extends AbstractItemAction
{
	/**
	 * Checks if a {@link Player} can perform this action.<br>
	 * This method validates the requirements for interacting with items.<br>
	 * It currently always returns {@code true}.
	 * @param player The {@link Player} attempting the action.
	 * @param parentItem The item that triggers the action.
	 * @param targetItem The item being acted upon.
	 * @return {@code true} if the action is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem)
	{
		return true;
	}
	
	/**
	 * Executes the action for adopting a pet.<br>
	 * This method handles the logic when a {@link Player} interacts with an item to adopt it.
	 * @param player The {@code Player} who is performing the action.
	 * @param parentItem The {@code Item} that triggers this action.
	 * @param targetItem The {@code Item} being acted upon.
	 */
	@Override
	public void act(Player player, Item parentItem, Item targetItem)
	{
	}
	
	/**
	 * Executes the action for an item used to clear instance time.<br>
	 * This method checks if the portal cooldown is active before proceeding.<br>
	 * It handles the animation, task scheduling, and item consumption logic.
	 * @param player The {@link Player} who is using the item.
	 * @param parentItem The {@link Item} being used by the player.
	 * @param SelectedSyncId The unique identifier for the specific instance sync.
	 */
	public void act(Player player, Item parentItem, int SelectedSyncId)
	{
		final int mapid = DataManager.INSTANCE_COOLTIME_DATA.getWorldId(SelectedSyncId);
		if (player.getPortalCooldownList().getPortalCooldown(mapid) == 0)
		{
			player.getController().cancelTask(TaskId.ITEM_USE);
			player.removeItemCoolDown(parentItem.getItemTemplate().getUseLimits().getDelayId());
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_COOL_TIME_INIT);
			return;
		}
		
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, parentItem.getObjectId(), parentItem.getItemId(), 1000, 0));
		
		final ItemUseObserver observer = new ItemUseObserver()
		{
			@Override
			public void abort()
			{
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.removeItemCoolDown(parentItem.getItemTemplate().getUseLimits().getDelayId());
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(parentItem.getItemTemplate().getNameId())));
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, 2), true);
				player.getObserveController().removeObserver(this);
			}
		};
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				player.getObserveController().removeObserver(observer);
				if (parentItem.getActivationCount() > 1)
				{
					parentItem.setActivationCount(parentItem.getActivationCount() - 1);
				}
				else
				{
					player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1);
				}
				
				final int mapid = DataManager.INSTANCE_COOLTIME_DATA.getWorldId(SelectedSyncId);
				player.getPortalCooldownList().reduceEntry(mapid);
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, parentItem.getObjectId(), parentItem.getItemId(), 0, 1));
			}
		}, 1000));
	}
}
