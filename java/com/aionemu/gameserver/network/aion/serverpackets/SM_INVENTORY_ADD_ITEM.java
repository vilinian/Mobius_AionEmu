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

import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.ItemStorage;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemAddType;

/**
 * This packet is sent to the client to notify it that an item has been added to a player's inventory.<br>
 * It handles the synchronization of new {@link Item} objects for the user.
 * @author ATracer
 */
public class SM_INVENTORY_ADD_ITEM extends AionServerPacket
{
	private final List<Item> items;
	private final int size;
	private final Player player;
	private ItemAddType addType;
	
	/**
	 * Adds a list of items to a player's inventory.<br>
	 * This packet uses the {@code ITEM_COLLECT} type by default.
	 * @param items The list of {@link Item} objects to be added.
	 * @param player The {@link Player} who will receive the items.
	 */
	public SM_INVENTORY_ADD_ITEM(List<Item> items, Player player)
	{
		this.player = player;
		this.items = items;
		size = items.size();
		addType = ItemAddType.ITEM_COLLECT;
	}
	
	/**
	 * This packet adds items to a player's inventory.<br>
	 * It specifies the list of items and the type of addition.
	 * @param items The list of {@link Item} objects to be added.
	 * @param player The {@link Player} receiving the items.
	 * @param addType The {@link ItemAddType} defining how the items are added.
	 */
	public SM_INVENTORY_ADD_ITEM(List<Item> items, Player player, ItemAddType addType)
	{
		this(items, player);
		this.addType = addType;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		// TODO: Rework it, who knows where it could be bugged else.
		int mask = addType.getMask();
		if (addType == ItemAddType.ITEM_COLLECT)
		{
			// TODO: if size != 1, then it's buy item, should not specify any slot in other places then !!!
			if ((size == 1) && (items.get(0).getEquipmentSlot() != ItemStorage.FIRST_AVAILABLE_SLOT))
			{
				mask = ItemAddType.PARTIAL_WITH_SLOT.getMask();
			}
		}
		
		writeH(mask); //
		writeH(size); // number of entries
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
		writeH(0x24); // Testing
		writeNameId(itemTemplate.getNameId());
		
		final ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
		itemInfoBlob.writeMe(getBuf());
		
		writeH(-1);
		writeC(item.getItemTemplate().isCloth() ? 1 : 0);
	}
}
