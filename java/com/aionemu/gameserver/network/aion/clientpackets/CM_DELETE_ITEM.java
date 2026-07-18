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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.RealItemRndBonusDAO;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemDeleteType;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the request from a client to delete an item.<br>
 * This packet processes the removal of items based on the {@code ItemDeleteType}.<br>
 * It ensures that the {@link Player} successfully removes the specified {@link Item} from their inventory or storage.
 * @author Avol
 */
public class CM_DELETE_ITEM extends AionClientPacket
{
	public int itemObjectId;
	
	/**
	 * This method creates a new {@code CM_DELETE_ITEM} packet.<br>
	 * It initializes the packet with the required network states.<br>
	 * Use this to handle requests for deleting items from the game world.
	 * @param opcode The operation code for the packet.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_DELETE_ITEM(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		itemObjectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		final Storage inventory = player.getInventory();
		final Item item = inventory.getItemByObjId(itemObjectId);
		
		if (item != null)
		{
			if (!item.getItemTemplate().isBreakable())
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_UNBREAKABLE_ITEM(new DescriptionId(item.getNameId())));
			}
			else
			{
				inventory.delete(item, ItemDeleteType.DISCARD);
				DAOManager.getDAO(RealItemRndBonusDAO.class).deleteAllRandomBonuses(item);
			}
		}
	}
}
