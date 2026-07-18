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

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.motion.Motion;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This class handles the logic for playing a specific animation on a {@link Player} when an item is used.<br>
 * It sends the required {@link SM_MOTION} and {@link SM_ITEM_USAGE_ANIMATION} packets to the client.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnimationAddAction")
public class AnimationAddAction extends AbstractItemAction
{
	@XmlAttribute
	protected Integer idle;
	@XmlAttribute
	protected Integer run;
	@XmlAttribute
	protected Integer jump;
	@XmlAttribute
	protected Integer rest;
	@XmlAttribute
	protected Integer shop;
	@XmlAttribute
	protected Integer minutes;
	
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
			// no item selected.
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
		PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 1000, 0));
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				if (player.getInventory().decreaseItemCount(parentItem, 1) != 0)
				{
					return;
				}
				
				if (idle != null)
				{
					addMotion(player, idle);
				}
				
				if (run != null)
				{
					addMotion(player, run);
				}
				
				if (jump != null)
				{
					addMotion(player, jump);
				}
				
				if (rest != null)
				{
					addMotion(player, rest);
				}
				
				if (shop != null)
				{
					addMotion(player, shop);
				}
				
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, parentItem.getObjectId(), parentItem.getItemId(), 0, 1));
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300423, new DescriptionId(parentItem.getItemTemplate().getNameId())));
				PacketSendUtility.broadcastPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()), false);
			}
		}, 1000));
	}
	
	/**
	 * Adds a specific motion to the {@link Player}.<br>
	 * This method creates a new {@code Motion} object.<br>
	 * It then updates the player's active motions and sends a packet.
	 * @param player The {@code Player} who will perform the motion.
	 * @param motionId The unique identifier for the motion to be added.
	 */
	private void addMotion(Player player, int motionId)
	{
		final Motion motion = new Motion(motionId, minutes == null ? 0 : (int) (System.currentTimeMillis() / 1000) + (minutes * 60), true);
		player.getMotions().add(motion, true);
		PacketSendUtility.sendPacket(player, new SM_MOTION((short) motion.getId(), motion.getRemainingTime()));
	}
}
