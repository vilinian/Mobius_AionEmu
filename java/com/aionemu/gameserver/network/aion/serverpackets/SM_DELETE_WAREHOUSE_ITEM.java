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
 * This packet handles the deletion of an item from a player's warehouse.<br>
 * It is sent by the server to confirm that an item has been successfully removed.
 * @author kosyachok
 */
public class SM_DELETE_WAREHOUSE_ITEM extends AionServerPacket
{
	private final int warehouseType;
	private final int itemObjId;
	private final ItemDeleteType deleteType;
	
	/**
	 * This packet handles the deletion of an item from a warehouse.<br>
	 * It sends information about which warehouse and item to remove.
	 * @param warehouseType The type of the warehouse where the item is stored.
	 * @param itemObjId The unique identifier for the item object.
	 * @param deleteType The specific method used to delete the item, defined by {@link ItemDeleteType}.
	 */
	public SM_DELETE_WAREHOUSE_ITEM(int warehouseType, int itemObjId, ItemDeleteType deleteType)
	{
		this.warehouseType = warehouseType;
		this.itemObjId = itemObjId;
		this.deleteType = deleteType;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(warehouseType);
		writeD(itemObjId);
		writeC(deleteType.getMask());
	}
}
