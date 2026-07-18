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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;
import com.aionemu.gameserver.services.RepurchaseService;

/**
 * This packet handles the request for a player to repurchase an item.<br>
 * It communicates with the {@link RepurchaseService} to process the transaction.
 * @author xTz, KID
 */
public class SM_REPURCHASE extends AionServerPacket
{
	private final Player player;
	private final int targetObjectId;
	private final Collection<Item> items;
	
	/**
	 * Handles the repurchase request for a specific player.<br>
	 * It identifies the target NPC and retrieves the available items.<br>
	 * This packet is used to process item exchanges with NPCs.
	 * @param player The {@link Player} who is making the purchase.
	 * @param npcId The unique ID of the NPC being interacted with.
	 */
	public SM_REPURCHASE(Player player, int npcId)
	{
		this.player = player;
		targetObjectId = npcId;
		items = RepurchaseService.getInstance().getRepurchaseItems(player.getObjectId());
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(targetObjectId);
		writeD(1);
		writeH(items.size());
		
		for (Item item : items)
		{
			final ItemTemplate itemTemplate = item.getItemTemplate();
			
			writeD(item.getObjectId());
			writeD(itemTemplate.getTemplateId());
			writeNameId(itemTemplate.getNameId());
			
			final ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
			itemInfoBlob.writeMe(getBuf());
			
			writeQ(item.getRepurchasePrice());
		}
	}
}
