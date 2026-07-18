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

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RANK_LIST;
import com.aionemu.gameserver.services.ranking.PlayerRankingUpdateService;

/**
 * This packet handles requests from the client to retrieve a ranking list.<br>
 * It interacts with {@link PlayerRankingUpdateService} to fetch and send data via {@link SM_RANK_LIST}.
 * @author Falke_34
 */
public class CM_RANK_LIST extends AionClientPacket
{
	private int tableId;
	// private int serverSwitch;
	
	/**
	 * This constructor initializes a new {@link CM_RANK_LIST} packet.<br>
	 * It sets the required network states for the packet.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@code State} of the connection.
	 * @param restStates A variable number of additional {@code State} objects.
	 */
	public CM_RANK_LIST(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		tableId = readD();
		// serverSwitch = readC();
	}
	
	@Override
	protected void runImpl()
	{
		final List<SM_RANK_LIST> results = PlayerRankingUpdateService.getInstance().getPlayers(tableId);
		for (SM_RANK_LIST packet : results)
		{
			sendPacket(packet);
		}
	}
}
