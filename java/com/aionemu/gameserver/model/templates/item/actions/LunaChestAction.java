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

import javax.xml.bind.annotation.XmlAttribute;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LUNA_SYSTEM_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the logic for interacting with a {@link Item} of type Luna Chest.<br>
 * This action processes the opening mechanics and associated rewards for the player.<br>
 * It manages system messages and animations related to the Luna system.
 */
public class LunaChestAction extends AbstractItemAction
{
	@XmlAttribute
	protected int count;
	
	/**
	 * Checks if a {@link Player} can perform this action.<br>
	 * This method validates the requirements for interacting with items.<br>
	 * It returns {@code false} if the {@code parentItem} is {@code null}.
	 * @param player The {@link Player} attempting the action.
	 * @param parentItem The item that triggers the action.
	 * @param targetItem The item being acted upon.
	 * @return {@code true} if the action is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem)
	{
		if (parentItem == null)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return false;
		}
		
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
		player.getController().cancelUseItem();
		PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId().intValue(), 0, parentItem.getObjectId().intValue(), parentItem.getItemTemplate().getTemplateId(), 1000, 0));
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				final boolean succ = player.getInventory().decreaseByObjectId(parentItem.getObjectId().intValue(), 1);
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId().intValue(), 0, parentItem.getObjectId().intValue(), parentItem.getItemId(), 0, 1));
				if (succ)
				{
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300423, new Object[]
					{
						new DescriptionId(parentItem.getItemTemplate().getNameId())
					}));
					player.setLunaAccount(player.getLunaAccount() + count);
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_GETLUNA(player.getName(), count));
					PacketSendUtility.sendPacket(player, new SM_LUNA_SYSTEM_INFO(0, player.getLunaAccount()));
				}
			}
		}, 1000));
	}
}
