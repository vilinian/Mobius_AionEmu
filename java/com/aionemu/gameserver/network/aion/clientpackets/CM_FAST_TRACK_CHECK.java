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
import com.aionemu.gameserver.services.FastTrackService;

/**
 * Handles the client request to check for fast track availability.<br>
 * This packet interacts with the {@link FastTrackService} to verify if a player can use the feature.
 * @author Alcapwnd
 */
public class CM_FAST_TRACK_CHECK extends AionClientPacket
{
	private int accountId;
	
	/**
	 * This method initializes a new {@code CM_FAST_TRACK_CHECK} packet.<br>
	 * It sets the required network properties for the request.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state of the client.
	 * @param restStates A variable number of additional states associated with the packet.
	 */
	public CM_FAST_TRACK_CHECK(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		accountId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (player.isOnFastTrack())
		{
			FastTrackService.getInstance().checkFastTrackMove(player, accountId, true);
		}
		else
		{
			FastTrackService.getInstance().checkFastTrackMove(player, accountId, false);
		}
	}
	
}
