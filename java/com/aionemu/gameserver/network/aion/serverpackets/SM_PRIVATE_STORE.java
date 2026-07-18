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

import java.util.LinkedHashMap;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PrivateStore;
import com.aionemu.gameserver.model.trade.TradePSItem;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;

/**
 * This packet handles the synchronization of a {@link PrivateStore} for a player.<br>
 * It sends the list of items available in the store to the client.
 * @author Simple
 */
public class SM_PRIVATE_STORE extends AionServerPacket
{
	private final Player player;
	/**
	 * Private store Information *
	 */
	private final PrivateStore store;
	
	/**
	 * Creates a new {@code SM_PRIVATE_STORE} packet.<br>
	 * This packet contains information about a private store for a specific player.<br>
	 * It links the {@link PrivateStore} data to the {@link Player} who owns it.
	 * @param store The {@code PrivateStore} object containing the items and details.
	 * @param player The {@link Player} associated with this store.
	 */
	public SM_PRIVATE_STORE(PrivateStore store, Player player)
	{
		this.player = player;
		this.store = store;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		if (store != null)
		{
			final Player storePlayer = store.getOwner();
			final LinkedHashMap<Integer, TradePSItem> soldItems = store.getSoldItems();
			
			writeD(storePlayer.getObjectId());
			writeH(soldItems.size());
			for (Integer itemObjId : soldItems.keySet())
			{
				final Item item = storePlayer.getInventory().getItemByObjId(itemObjId);
				final TradePSItem tradeItem = store.getTradeItemByObjId(itemObjId);
				final long price = tradeItem.getPrice();
				writeD(itemObjId);
				writeD(item.getItemTemplate().getTemplateId());
				writeH((int) tradeItem.getCount());
				writeQ((int) price);
				
				final ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
				itemInfoBlob.writeMe(getBuf());
			}
		}
	}
}
