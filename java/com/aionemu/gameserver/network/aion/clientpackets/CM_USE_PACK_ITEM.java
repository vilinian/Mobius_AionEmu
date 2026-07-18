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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the client request to use an item from a pack.<br>
 * This packet triggers the logic for consuming or activating items within a specific container.<br>
 * It interacts with {@link Player} and {@link Item} objects to process the usage.
 * @author Ever'
 * @rework FrozenKiller
 */
public class CM_USE_PACK_ITEM extends AionClientPacket
{
	private int uniqueItemId;
	
	/**
	 * Handles a request to use an item from a pack.<br>
	 * This packet is sent by the client to the server.<br>
	 * It initializes the packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_USE_PACK_ITEM(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		uniqueItemId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if ((player == null) || (uniqueItemId == 0))
		{
			return;
		}
		
		if (player.isProtectionActive())
		{
			player.getController().stopProtectionActiveTask();
		}
		
		final Item item = player.getInventory().getItemByObjId(uniqueItemId);
		if (item == null)
		{
			return;
		}
		
		// check use item multicast delay exploit cast (spam)
		if (player.isCasting())
		{
			player.getController().cancelCurrentSkill();
		}
		
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, item.getObjectId(), item.getItemTemplate().getTemplateId(), 5000, 0), true);
		final ItemUseObserver observer = new ItemUseObserver()
		{
			@Override
			public void abort()
			{
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.removeItemCoolDown(item.getItemTemplate().getUseLimits().getDelayId());
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300427)); // Item use cancel
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, item.getObjectId(), item.getItemTemplate().getTemplateId(), 0, 2), true);
				player.getObserveController().removeObserver(this);
			}
		};
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule((Runnable) () ->
		{
			item.setPacked(false);
			player.getObserveController().removeObserver(observer);
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, item.getObjectId(), item.getItemTemplate().getTemplateId(), 0, 1), true);
			item.setPersistentState(PersistentState.UPDATE_REQUIRED);
			PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402968, new DescriptionId(item.getItemTemplate().getNameId())));
		}, 5000));
	}
}
