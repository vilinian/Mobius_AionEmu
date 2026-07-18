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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.TradeService;

/**
 * Handles the client request to purchase an item from another player's trade window.<br>
 * This packet is processed by the {@link TradeService} to validate and execute the transaction.
 * @author MrPoke
 * @author GiGatR00n, Raziel
 */
public class CM_BUY_TRADE_IN_TRADE extends AionClientPacket
{
	private int sellerObjId; // NPC Object Id
	@SuppressWarnings("unused")
	private int BuyMask; // v4.7.5.7 Maybe implemented at future
	private int itemId;
	private int BuyCount;
	
	private int TradeinListCount; // They can be used for implementing Anti-Cheat System
	private int TradeinItemObjectId1;
	private int TradeinItemObjectId2;
	private int TradeinItemObjectId3;
	
	/**
	 * This constructor initializes a new {@link CM_BUY_TRADE_IN_TRADE} packet.<br>
	 * It sets the basic network properties for the trade request.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_BUY_TRADE_IN_TRADE(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		sellerObjId = readD();
		BuyMask = readC();
		itemId = readD();
		BuyCount = readD();
		TradeinListCount = readH();
		
		switch (TradeinListCount)
		{
			case 1:
				TradeinItemObjectId1 = readD();
				break;
			case 2:
				TradeinItemObjectId1 = readD();
				TradeinItemObjectId2 = readD();
				break;
			case 3:
				TradeinItemObjectId1 = readD();
				TradeinItemObjectId2 = readD();
				TradeinItemObjectId3 = readD();
				break;
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (BuyCount < 1)
		{
			return;
		}
		
		TradeService.performBuyFromTradeInTrade(player, sellerObjId, itemId, BuyCount, TradeinListCount, TradeinItemObjectId1, TradeinItemObjectId2, TradeinItemObjectId3);
	}
}
