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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;

/**
 * This packet updates the client's inventory with information about a specific {@link Item}.<br>
 * It is used to synchronize item changes such as quantity, durability, or other properties.<br>
 * The server sends this to ensure the player's UI reflects the current state of their items.
 * @author ATracer
 * @author -Nemesiss-
 * @update FrozenKiller
 */
public class SM_INVENTORY_UPDATE_ITEM extends AionServerPacket
{
	private final Player player;
	private final Item item;
	private final ItemUpdateType updateType;
	
	/**
	 * Creates a packet to update an item in a player's inventory.<br>
	 * This constructor uses the default {@code DEC_ITEM_USE} update type.
	 * @param player The {@link Player} who owns the inventory.
	 * @param item The {@link Item} being updated.
	 */
	public SM_INVENTORY_UPDATE_ITEM(Player player, Item item)
	{
		this(player, item, ItemUpdateType.DEC_ITEM_USE);
	}
	
	/**
	 * Updates a specific item in a player's inventory.<br>
	 * This packet informs the client about changes to an {@link Item}.<br>
	 * It uses the provided {@code ItemUpdateType} to specify the change type.
	 * @param player The {@link Player} who owns the inventory.
	 * @param item The {@link Item} being updated.
	 * @param updateType The type of update to apply to the item.
	 */
	public SM_INVENTORY_UPDATE_ITEM(Player player, Item item, ItemUpdateType updateType)
	{
		this.player = player;
		this.item = item;
		this.updateType = updateType;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		final ItemTemplate itemTemplate = item.getItemTemplate();
		
		writeD(item.getObjectId());
		writeNameId(itemTemplate.getNameId()); // Includes writeH(0x24) + writeD(itemNameId) + writeH(0x00);
		
		ItemInfoBlob itemInfoBlob;
		switch (updateType)
		{
			case EQUIP_UNEQUIP:
				itemInfoBlob = new ItemInfoBlob(player, item);
				itemInfoBlob.addBlobEntry(ItemBlobType.EQUIPPED_SLOT);
				break;
			case CHARGE:
				itemInfoBlob = new ItemInfoBlob(player, item);
				itemInfoBlob.addBlobEntry(ItemBlobType.CONDITIONING_INFO);
			default:
				itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
				break;
		}
		
		itemInfoBlob.writeMe(getBuf());
		
		if (updateType.isSendable())
		{
			writeH(updateType.getMask());
		}
	}
}
