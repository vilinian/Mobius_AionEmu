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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.limiteditems.LimitedItem;
import com.aionemu.gameserver.model.limiteditems.LimitedTradeNpc;
import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.LimitedItemTradeService;

/**
 * This packet handles the transmission of trade list data to the client.<br>
 * It provides information about items available for trading between players or NPCs.<br>
 * Use this class to synchronize {@link TradeListTemplate} data with the game client.
 */
public class SM_TRADELIST extends AionServerPacket
{
	private final Integer playerObj;
	private final int npcObj;
	private final int npcId;
	private final TradeListTemplate tlist;
	private final int buyPriceModifier;
	
	/**
	 * Creates a new {@code SM_TRADELIST} packet for a specific player and NPC.<br>
	 * This packet sends the trade list data to the client.
	 * @param player The {@link Player} who will receive the packet.
	 * @param npc The {@link Npc} that owns the trade list.
	 * @param tlist The {@link TradeListTemplate} containing the items for sale.
	 * @param buyPriceModifier A numeric value to adjust the purchase price of items.
	 */
	public SM_TRADELIST(Player player, Npc npc, TradeListTemplate tlist, int buyPriceModifier)
	{
		playerObj = player.getObjectId();
		npcObj = npc.getObjectId().intValue();
		npcId = npc.getNpcId();
		this.tlist = tlist;
		this.buyPriceModifier = buyPriceModifier;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		if ((tlist != null) && (tlist.getNpcId() != 0) && (tlist.getCount() != 0))
		{
			writeD(npcObj);
			writeC(tlist.getTradeNpcType().index());
			writeD(buyPriceModifier);
			writeD(buyPriceModifier);
			writeC(1);
			writeC(1);
			writeH(tlist.getCount());
			for (TradeListTemplate.TradeTab tradeTabl : tlist.getTradeTablist())
			{
				writeD(tradeTabl.getId());
			}
			
			int i = 0;
			LimitedTradeNpc limitedTradeNpc = null;
			if (LimitedItemTradeService.getInstance().isLimitedTradeNpc(npcId))
			{
				limitedTradeNpc = LimitedItemTradeService.getInstance().getLimitedTradeNpc(npcId);
				i = limitedTradeNpc.getLimitedItems().size();
			}
			
			writeH(i);
			if (limitedTradeNpc != null)
			{
				for (LimitedItem limitedItem : limitedTradeNpc.getLimitedItems())
				{
					writeD(limitedItem.getItemId());
					writeH(limitedItem.getBuyCount().get(playerObj.intValue()) == null ? 0 : limitedItem.getBuyCount().get(playerObj.intValue()).intValue());
					writeH(limitedItem.getSellLimit());
				}
			}
		}
	}
}
