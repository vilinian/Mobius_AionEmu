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

import java.util.Collection;
import java.util.Collections;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;

/**
 * This packet sends warehouse information to the client.<br>
 * It contains a list of {@link Item} objects stored in the player's warehouse.
 * @author kosyachok
 */
public class SM_WAREHOUSE_INFO extends AionServerPacket
{
	private final int warehouseType;
	private Collection<Item> itemList;
	private final boolean firstPacket;
	private final int expandLvl;
	private final Player player;
	
	/**
	 * This constructor initializes a new {@link SM_WAREHOUSE_INFO} packet.<br>
	 * It prepares the warehouse data to be sent to a client.
	 * @param items The collection of {@link Item} objects to include in the packet. If {@code null}, it defaults to an empty list.
	 * @param warehouseType The specific type identifier for the warehouse.
	 * @param expandLvl The expansion level associated with the warehouse.
	 * @param firstPacket A boolean flag indicating if this is the first part of the data transmission.
	 * @param player The {@link Player} object who owns or is viewing the warehouse.
	 */
	public SM_WAREHOUSE_INFO(Collection<Item> items, int warehouseType, int expandLvl, boolean firstPacket, Player player)
	{
		this.warehouseType = warehouseType;
		this.expandLvl = expandLvl;
		this.firstPacket = firstPacket;
		if (items == null)
		{
			itemList = Collections.emptyList();
		}
		else
		{
			itemList = items;
		}
		
		this.player = player;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(warehouseType);
		writeC(firstPacket ? 1 : 0);
		writeC(expandLvl); // warehouse expand (0 - 9)
		writeH(0);
		writeH(itemList.size());
		for (Item item : itemList)
		{
			writeItemInfo(item);
		}
	}
	
	/**
	 * Writes the details of a single {@link Item} to the packet buffer.<br>
	 * This method handles the serialization of IDs, names, and blobs.<br>
	 * It is used internally by {@code writeImpl}.
	 * @param item The {@code Item} object containing the data to be written.
	 */
	private void writeItemInfo(Item item)
	{
		final ItemTemplate itemTemplate = item.getItemTemplate();
		
		writeD(item.getObjectId());
		writeD(itemTemplate.getTemplateId());
		writeC(0); // some item info (4 - weapon, 7 - armor, 8 - rings, 17 - bottles)
		writeNameId(itemTemplate.getNameId());
		
		final ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
		itemInfoBlob.writeMe(getBuf());
		
		writeH((int) (item.getEquipmentSlot() & 0xFFFF));
	}
}
