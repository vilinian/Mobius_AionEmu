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

import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate;
import com.aionemu.gameserver.model.templates.tradelist.TradeNpcType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the request to sell an item to a merchant.<br>
 * It processes the transaction between the player and a {@link TradeListTemplate}.
 * @author orz, Sarynth, modified by Artur
 */
public class SM_SELL_ITEM extends AionServerPacket
{
	private final int targetObjectId;
	private final int sellPercentage;
	private TradeListTemplate tradeListTemplate;
	@SuppressWarnings("unused")
	private byte action = 0x01; // ?! WTF
	
	/**
	 * Creates a new {@code SM_SELL_ITEM} packet.<br>
	 * This packet is used to handle selling an item to an NPC.
	 * @param targetObjectId The unique ID of the target object.
	 * @param sellPercentage The percentage value used for the sale.
	 */
	public SM_SELL_ITEM(int targetObjectId, int sellPercentage)
	{
		this.sellPercentage = sellPercentage;
		this.targetObjectId = targetObjectId;
	}
	
	/**
	 * Creates a new {@code SM_SELL_ITEM} packet for selling an item.<br>
	 * This constructor sets the target object and uses the template to determine the sell price.<br>
	 * It automatically adjusts the action byte based on the NPC type.
	 * @param targetObjectId The unique ID of the object being sold.
	 * @param tradeListTemplate The template containing trade details like prices and NPC types.
	 */
	public SM_SELL_ITEM(int targetObjectId, TradeListTemplate tradeListTemplate)
	{
		this.targetObjectId = targetObjectId;
		this.tradeListTemplate = tradeListTemplate;
		sellPercentage = tradeListTemplate.getBuyPriceRate();
		if (tradeListTemplate.getTradeNpcType() == TradeNpcType.ABYSS)
		{
			action = 0x02;
		}
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		if ((tradeListTemplate != null) && (tradeListTemplate.getNpcId() != 0) && (tradeListTemplate.getCount() != 0))
		{
			writeD(targetObjectId);
			writeC(tradeListTemplate.getTradeNpcType().index());
			writeD(sellPercentage);
			writeH(256);
			writeH(tradeListTemplate.getCount());
			for (TradeListTemplate.TradeTab tradeTabl : tradeListTemplate.getTradeTablist())
			{
				writeD(tradeTabl.getId());
			}
		}
		else
		{
			writeD(targetObjectId);
			writeD(5121);
			writeD(65792);
			writeC(0);
		}
	}
}
