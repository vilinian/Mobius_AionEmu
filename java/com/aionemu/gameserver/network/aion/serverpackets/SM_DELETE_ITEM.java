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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemDeleteType;

/**
 * This packet is sent to the client to notify it that an item has been deleted.<br>
 * It handles the removal of items from the player's inventory or world.<br>
 * Use this class when processing {@code ItemDeleteType} actions.
 * @author Avol
 */
public class SM_DELETE_ITEM extends AionServerPacket
{
	private final int itemObjectId;
	private final ItemDeleteType deleteType;
	
	/**
	 * This packet is used to remove an item from a player.<br>
	 * It defaults the deletion type to {@code QUEST_REWARD}.
	 * @param itemObjectId The unique identifier of the item to be deleted.
	 */
	public SM_DELETE_ITEM(int itemObjectId)
	{
		this(itemObjectId, ItemDeleteType.QUEST_REWARD);
	}
	
	/**
	 * Creates a new {@code SM_DELETE_ITEM} packet.<br>
	 * This packet is used to notify the client about an item removal.
	 * @param itemObjectId The unique ID of the item to be deleted.
	 * @param deleteType The specific type of deletion for the item.
	 */
	public SM_DELETE_ITEM(int itemObjectId, ItemDeleteType deleteType)
	{
		this.itemObjectId = itemObjectId;
		this.deleteType = deleteType;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(itemObjectId);
		writeC(deleteType.getMask());
	}
}
