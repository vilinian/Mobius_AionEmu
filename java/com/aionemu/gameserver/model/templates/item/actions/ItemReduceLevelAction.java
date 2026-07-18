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

import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the logic for reducing the level of an {@link Item}.<br>
 * This action is triggered when a player uses an item to decrease its current level.<br>
 * It updates the item's state and notifies the client of the changes.
 * @author Ghostfur
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemReduceLevelAction")
public class ItemReduceLevelAction extends AbstractItemAction
{
	@XmlAttribute(name = "count")
	protected Integer reduceCount;
	
	/**
	 * Checks if a {@link Player} is allowed to perform this action.<br>
	 * It verifies that the {@code targetItem} is not {@code null}.
	 * @param player The {@link Player} attempting the action.
	 * @param item The {@link Item} being used by the player.
	 * @param targetItem The {@link Item} that is the target of the action.
	 * @return {@code true} if the action can be performed, otherwise {@code false}.
	 */
	@Override
	public boolean canAct(Player player, Item item, Item targetItem)
	{
		if (targetItem == null)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Executes the action to reduce the level of a target item.<br>
	 * This method handles the animation, item consumption, and stat updates.
	 * @param player The {@link Player} who is performing the action.
	 * @param item The {@link Item} being used as a reagent.
	 * @param targetItem The {@link Item} whose level will be increased.
	 */
	@Override
	public void act(Player player, Item item, Item targetItem)
	{
		player.getController().cancelUseItem();
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), targetItem.getObjectId(), item.getObjectId(), item.getItemId(), 3000, 0), true);
		final ItemUseObserver observer = new ItemUseObserver()
		{
			@Override
			public void abort()
			{
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.removeItemCoolDown(item.getItemTemplate().getUseLimits().getDelayId());
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(targetItem.getItemTemplate().getNameId())));
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), targetItem.getObjectId(), item.getObjectId(), item.getItemId(), 0, 2), true);
				player.getObserveController().removeObserver(this);
			}
		};
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				if (!player.getInventory().decreaseByObjectId(item.getObjectId(), 1))
				{
					return;
				}
				
				player.getObserveController().removeObserver(observer);
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), targetItem.getObjectId(), item.getObjectId(), item.getItemId(), 0, 1), true);
				targetItem.setReductionLevel(targetItem.getReductionLevel() + 1);
				PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, targetItem));
				player.getObserveController().removeObserver(observer);
				if (targetItem.isEquipped())
				{
					player.getGameStats().updateStatsVisually();
				}
				
				ItemPacketService.updateItemAfterInfoChange(player, targetItem);
				
				if (targetItem.isEquipped())
				{
					player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
				}
				else
				{
					player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
				}
			}
		}, 3000));
	}
}
