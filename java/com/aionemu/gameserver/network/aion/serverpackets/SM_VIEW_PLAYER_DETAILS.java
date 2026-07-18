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
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;

/**
 * This packet is sent to the client to display detailed information about a specific {@link Player}.<br>
 * It contains data such as character stats, equipment, and other relevant profile details.
 * @author Avol, xTz
 */
public class SM_VIEW_PLAYER_DETAILS extends AionServerPacket
{
	private final List<Item> items;
	private final int itemSize;
	private final int targetObjId;
	private final Player player;
	
	/**
	 * Creates a new packet to display player details.<br>
	 * This method initializes the data for a specific {@link Player}.<br>
	 * It sets the required list of {@code Item} objects and the target ID.
	 * @param items The list of items to be displayed.
	 * @param player The player whose details are being viewed.
	 */
	public SM_VIEW_PLAYER_DETAILS(List<Item> items, Player player)
	{
		this.player = player;
		targetObjId = player.getObjectId();
		this.items = items;
		itemSize = items.size();
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(targetObjId);
		writeC(11);
		writeH(itemSize);
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
		final ItemTemplate template = item.getItemTemplate();
		
		writeD(0);
		writeD(template.getTemplateId());
		writeH(0x24); // Testing
		writeNameId(template.getNameId());
		
		final ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
		itemInfoBlob.writeMe(getBuf());
	}
}
