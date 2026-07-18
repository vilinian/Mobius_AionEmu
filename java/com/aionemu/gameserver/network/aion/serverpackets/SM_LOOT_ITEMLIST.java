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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is sent to the client to provide a list of items available for looting.<br>
 * It contains information about {@link DropItem} objects found in the game world.
 * @author alexa026, Avol, Corrected by Metos modified by ATracer, KID
 */
public class SM_LOOT_ITEMLIST extends AionServerPacket
{
	private int targetObjectId;
	private List<DropItem> dropItems;
	
	/**
	 * Creates a loot item list packet for a specific object.<br>
	 * This method filters the provided items based on ownership and validity.<br>
	 * It populates the internal {@code dropItems} list with valid data.
	 * @param targetObjectId The unique ID of the object being looted.
	 * @param setItems A set of {@link DropItem} objects to be processed.
	 * @param player The {@link Player} instance who is receiving the loot.
	 */
	public SM_LOOT_ITEMLIST(int targetObjectId, Set<DropItem> setItems, Player player)
	{
		this.targetObjectId = targetObjectId;
		dropItems = new ArrayList<>();
		if (setItems == null)
		{
			LoggerFactory.getLogger(SM_LOOT_ITEMLIST.class).warn("null Set<DropItem>, skip");
			return;
		}
		
		for (DropItem item : setItems)
		{
			if ((item.getPlayerObjId() == 0) || (player.getObjectId() == item.getPlayerObjId()))
			{
				if (DataManager.ITEM_DATA.getItemTemplate(item.getDropTemplate().getItemId()) != null)
				{
					dropItems.add(item);
				}
			}
		}
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(targetObjectId);
		writeC(dropItems.size());
		
		for (DropItem dropItem : dropItems)
		{
			final Drop drop = dropItem.getDropTemplate();
			writeC(dropItem.getIndex()); // index in droplist
			writeC(0); // TODO
			writeH(0); // TODO
			writeD(drop.getItemId());
			writeD((int) dropItem.getCount());
			writeH(dropItem.getOptionalSocket());
			writeC(0);
			final ItemTemplate template = drop.getItemTemplate();
			writeC(!template.getCategory().equals(ItemCategory.QUEST) && !template.isTradeable() ? 1 : 0);
		}
	}
}
