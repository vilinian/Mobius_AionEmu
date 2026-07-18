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
 * Handles the client request to enter a world directly.<br>
 * This packet is processed when a {@link Player} attempts to join a game world immediately.
 */
public class CM_DIRECT_ENTER_WORLD extends AionClientPacket
{
	private int accountId;
	
	/**
	 * Handles a request to enter the game world directly.<br>
	 * This packet processes the transition between different connection states.
	 * @param opcode The unique identifier for this network operation.
	 * @param state The primary {@link State} being transitioned into.
	 * @param restStates Additional {@link State} objects associated with the request.
	 */
	public CM_DIRECT_ENTER_WORLD(int opcode, State state, State... restStates)
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
