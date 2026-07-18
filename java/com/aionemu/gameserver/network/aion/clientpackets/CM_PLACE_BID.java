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

import com.aionemu.gameserver.configs.main.HousingConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.HousingBidService;

/**
 * Handles the client request to place a bid on an item.<br>
 * This packet is processed by the {@link HousingBidService} to manage auction interactions.
 * @author Rolandas
 */
public class CM_PLACE_BID extends AionClientPacket
{
	int listIndex = 0;
	long bidOffer = 0;
	
	/**
	 * This method creates a new {@code CM_PLACE_BID} packet.<br>
	 * It initializes the packet with the required network states.<br>
	 * Use this constructor to handle player bidding actions.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates A variable number of additional connection states.
	 */
	public CM_PLACE_BID(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		listIndex = readD();
		bidOffer = readQ();
	}
	
	@Override
	protected void runImpl()
	{
		if (HousingConfig.ENABLE_HOUSE_AUCTIONS)
		{
			final Player player = getConnection().getActivePlayer();
			HousingBidService.getInstance().placeBid(player, listIndex, bidOffer);
		}
	}
}
