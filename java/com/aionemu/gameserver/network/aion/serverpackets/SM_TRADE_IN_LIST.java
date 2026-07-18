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
import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is sent to the client to provide a list of items for trading.<br>
 * It contains information regarding the {@code TradeListTemplate} and associated data.
 */
public class SM_TRADE_IN_LIST extends AionServerPacket
{
	private final Npc npc;
	private final TradeListTemplate tlist;
	private final int buyPriceModifier;
	
	/**
	 * Creates a new trade list packet for an NPC.<br>
	 * This method initializes the data needed to send a trade list to the client.
	 * @param npc The {@link Npc} object that owns the trade list.
	 * @param tlist The {@link TradeListTemplate} containing the items.
	 * @param buyPriceModifier The integer value used to adjust the purchase price.
	 */
	public SM_TRADE_IN_LIST(Npc npc, TradeListTemplate tlist, int buyPriceModifier)
	{
		this.npc = npc;
		this.tlist = tlist;
		this.buyPriceModifier = buyPriceModifier;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		if ((tlist != null) && (tlist.getNpcId() != 0) && (tlist.getCount() != 0))
		{
			writeD(npc.getObjectId().intValue());
		}
		
		writeC(tlist.getTradeNpcType().index());
		writeD(buyPriceModifier);
		writeD(buyPriceModifier);
		writeH(tlist.getCount());
		for (TradeListTemplate.TradeTab tradeTabl : tlist.getTradeTablist())
		{
			writeD(tradeTabl.getId());
		}
	}
}
