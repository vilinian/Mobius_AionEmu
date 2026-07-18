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

import java.util.List;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.house.HouseBidEntry;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HOUSE_BIDS;
import com.aionemu.gameserver.services.HousingBidService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.collections.ListSplitter;

/**
 * Handles the client request to retrieve current house bids.<br>
 * This packet triggers a request to {@link HousingBidService} to fetch relevant data.<br>
 * The server then responds using {@link SM_HOUSE_BIDS}.
 * @author Rolandas
 */
public class CM_GET_HOUSE_BIDS extends AionClientPacket
{
	/**
	 * This method handles the request to retrieve house bids.<br>
	 * It initializes the packet with the required network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional connection states if needed.
	 */
	public CM_GET_HOUSE_BIDS(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		// No data
	}
	
	@Override
	protected void runImpl()
	{
		boolean isFirst = true;
		final Player player = getConnection().getActivePlayer();
		final HouseBidEntry playerBid = HousingBidService.getInstance().getLastPlayerBid(player.getObjectId());
		
		final List<HouseBidEntry> houseBids = HousingBidService.getInstance().getHouseBidEntries(player.getRace());
		final ListSplitter<HouseBidEntry> splitter = new ListSplitter<>(houseBids, 181);
		while (!splitter.isLast())
		{
			final List<HouseBidEntry> packetBids = splitter.getNext();
			final HouseBidEntry playerData = splitter.isLast() ? playerBid : null;
			PacketSendUtility.sendPacket(player, new SM_HOUSE_BIDS(isFirst, splitter.isLast(), playerData, packetBids));
			isFirst = false;
		}
	}
}
