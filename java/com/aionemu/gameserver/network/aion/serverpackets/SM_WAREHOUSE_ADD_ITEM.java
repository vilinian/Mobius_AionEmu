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

import java.util.Collections;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemAddType;

/**
 * This packet handles the request to add an item to a player's warehouse.<br>
 * It processes the transfer of items from the character inventory to storage.
 * @author kosyachok
 * @author -Nemesiss-
 */
public class SM_WAREHOUSE_ADD_ITEM extends AionServerPacket
{
	private final int warehouseType;
	private final List<Item> items;
	private final Player player;
	private ItemAddType addType;
	
	/**
	 * This packet adds a single {@code Item} to a specific warehouse.<br>
	 * It identifies the target warehouse by its type.<br>
	 * The action is performed on behalf of the specified {@link Player}.
	 * @param item The {@code Item} object to be added.
	 * @param warehouseType The integer ID representing the warehouse category.
	 * @param player The {@link Player} who owns the items.
	 */
	public SM_WAREHOUSE_ADD_ITEM(Item item, int warehouseType, Player player)
	{
		this.player = player;
		this.warehouseType = warehouseType;
		items = Collections.singletonList(item);
		addType = ItemAddType.ALL_SLOT;
	}
	
	/**
	 * This packet handles adding an item to a warehouse.<br>
	 * It includes specific details about the addition type.
	 * @param item The {@code Item} object being added.
	 * @param warehouseType The integer ID of the warehouse type.
	 * @param player The {@link Player} who owns the item.
	 * @param addType The {@code ItemAddType} used for this operation.
	 */
	public SM_WAREHOUSE_ADD_ITEM(Item item, int warehouseType, Player player, ItemAddType addType)
	{
		this(item, warehouseType, player);
		this.addType = addType;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(warehouseType);
		writeH(addType.getMask());
		writeH(items.size());
		
		for (Item item : items)
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
